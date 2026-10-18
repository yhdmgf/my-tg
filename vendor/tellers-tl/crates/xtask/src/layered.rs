//! Expansion of Telegram's official `===layer===` schema-delta format.

use eyre::{Context, Result, bail};
use std::{collections::HashMap, fmt::Write as _};
use tellers_tl_parser::{DeclarationKind, Section, TypeExpr};

/// One cumulative TL schema reconstructed from the official layer deltas.
#[derive(Debug, Eq, PartialEq)]
pub(crate) struct Snapshot {
    pub(crate) layer: u32,
    pub(crate) text: String,
}

#[derive(Clone, Debug, Eq, Hash, PartialEq)]
enum DeclarationKey {
    Combinator(Section, String),
    Finalization(Section, String, String),
    PartialApplication(Section, String),
}

#[derive(Debug)]
struct Declaration {
    key: DeclarationKey,
    id: Option<u32>,
    introduced: u32,
    section: Section,
    source: String,
}

/// Apply every official delta and return a complete schema at each change layer.
pub(crate) fn snapshots(source: &str) -> Result<Vec<Snapshot>> {
    let deltas = split_deltas(source)?;
    let mut declarations: Vec<Option<Declaration>> = Vec::new();
    let mut positions = HashMap::<DeclarationKey, usize>::new();
    let mut ids = HashMap::<u32, usize>::new();
    let mut output = Vec::with_capacity(deltas.len());

    for (layer, delta) in deltas {
        let parsed = tellers_tl_parser::parse(delta)
            .with_context(|| format!("invalid end-to-end TL delta at layer {layer}"))?;
        if parsed.declarations.is_empty() {
            bail!("end-to-end TL layer {layer} contains no declarations");
        }
        for item in parsed.declarations {
            let source = delta[item.span.start..item.span.end].trim().to_owned();
            let (key, id) = declaration_identity(item.section, &item.kind);
            let replacement = positions.get(&key).copied();
            if let Some(previous) = replacement {
                if let Some(previous_id) = declarations[previous]
                    .as_ref()
                    .and_then(|declaration| declaration.id)
                {
                    ids.remove(&previous_id);
                }
            }
            if let Some(id) = id {
                if let Some(previous) = ids.insert(id, replacement.unwrap_or(declarations.len())) {
                    if Some(previous) != replacement {
                        if let Some(old) = declarations[previous].take() {
                            positions.remove(&old.key);
                        }
                    }
                }
            }
            let declaration = Declaration {
                key: key.clone(),
                id,
                introduced: layer,
                section: item.section,
                source,
            };
            if let Some(position) = replacement {
                declarations[position] = Some(declaration);
            } else {
                let position = declarations.len();
                positions.insert(key, position);
                declarations.push(Some(declaration));
                if let Some(id) = id {
                    ids.insert(id, position);
                }
            }
        }

        let text = render(layer, declarations.iter().flatten());
        tellers_tl_schema::parse(&text, tellers_tl_schema::Source::default())
            .with_context(|| format!("invalid cumulative end-to-end schema at layer {layer}"))?;
        output.push(Snapshot { layer, text });
    }
    Ok(output)
}

fn declaration_identity(
    section: Section,
    declaration: &DeclarationKind,
) -> (DeclarationKey, Option<u32>) {
    match declaration {
        DeclarationKind::Combinator(value) => (
            DeclarationKey::Combinator(section, value.name.clone()),
            value.id,
        ),
        DeclarationKind::Finalization { mode, ty } => (
            DeclarationKey::Finalization(section, format!("{mode:?}"), display_expression(ty)),
            None,
        ),
        DeclarationKind::PartialApplication(value) => (
            DeclarationKey::PartialApplication(section, display_expression(value)),
            None,
        ),
    }
}

fn display_expression(expression: &TypeExpr) -> String {
    expression.to_string()
}

fn render<'a>(layer: u32, declarations: impl Iterator<Item = &'a Declaration>) -> String {
    let mut output = format!("// LAYER {layer}\n");
    let mut section = Section::Types;
    for declaration in declarations {
        if declaration.section != section {
            section = declaration.section;
            output.push_str(match section {
                Section::Types => "\n---types---\n",
                Section::Functions => "\n---functions---\n",
            });
        }
        write!(
            output,
            "// @introduced {}\n{}\n",
            declaration.introduced, declaration.source
        )
        .expect("writing to a String cannot fail");
    }
    output
}

fn split_deltas(source: &str) -> Result<Vec<(u32, &str)>> {
    let mut markers = Vec::new();
    let mut offset = 0;
    for line in source.split_inclusive('\n') {
        let trimmed = line.trim();
        if let Some(layer) = trimmed
            .strip_prefix("===")
            .and_then(|value| value.strip_suffix("==="))
            .and_then(|value| value.parse::<u32>().ok())
        {
            markers.push((layer, offset, offset + line.len()));
        }
        offset += line.len();
    }
    if markers.is_empty() {
        bail!("official end-to-end schema contains no `===layer===` markers");
    }
    if !source[..markers[0].1].trim().is_empty() {
        bail!("unexpected content before the first end-to-end layer marker");
    }
    let mut output = Vec::with_capacity(markers.len());
    for (index, &(layer, _, body_start)) in markers.iter().enumerate() {
        if index > 0 && markers[index - 1].0 >= layer {
            bail!("end-to-end schema layers are not strictly increasing at {layer}");
        }
        let body_end = markers
            .get(index + 1)
            .map_or(source.len(), |marker| marker.1);
        output.push((layer, &source[body_start..body_end]));
    }
    Ok(output)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn applies_replacements_and_preserves_introduction_layers() {
        let source = "===1===\nold#00000001 value:int = Item;\nkeep#00000002 = Keep;\n\
                      ===3===\nold#00000003 value:long = Item;\nnew#00000004 = New;\n";
        let values = snapshots(source).unwrap();
        assert_eq!(
            values.iter().map(|value| value.layer).collect::<Vec<_>>(),
            [1, 3]
        );
        assert!(values[1].text.contains("old#00000003"));
        assert!(!values[1].text.contains("old#00000001"));
        assert!(values[1].text.contains("// @introduced 1\nkeep"));
        assert!(values[1].text.contains("// @introduced 3\nnew"));
    }

    #[test]
    fn rejects_non_monotonic_layers() {
        let error = snapshots("===2===\na#1 = A;\n===2===\nb#2 = B;").unwrap_err();
        assert!(error.to_string().contains("strictly increasing"));
    }
}
