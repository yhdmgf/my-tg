//! Structured documentation extraction from official constructor and method pages.

use eyre::{Context, Result};
use futures::{StreamExt, TryStreamExt, stream};
use reqwest::{Client, StatusCode};
use scraper::{Html, Selector};
use std::collections::{BTreeMap, BTreeSet, HashMap, HashSet};
use tellers_tl_parser::{DeclarationKind, Section};

const CONCURRENCY: usize = 24;

#[derive(Clone, Debug, Default, Eq, PartialEq)]
pub(crate) struct Documentation {
    description: Option<String>,
    parameters: BTreeMap<String, String>,
    links: BTreeSet<String>,
    introduced: Option<u32>,
}

pub(crate) async fn fetch(
    client: &Client,
    schemas: impl IntoIterator<Item = &str>,
) -> Result<HashMap<(Section, String), Documentation>> {
    let mut keys = HashSet::new();
    for schema in schemas {
        for declaration in tellers_tl_parser::parse(schema)?.declarations {
            if let DeclarationKind::Combinator(combinator) = declaration.kind {
                keys.insert((declaration.section, combinator.name));
            }
        }
    }
    let mut keys = keys.into_iter().collect::<Vec<_>>();
    keys.sort_by(|left, right| left.1.cmp(&right.1));
    stream::iter(keys)
        .map(|(section, name)| async move {
            let category = match section {
                Section::Types => "constructor",
                Section::Functions => "method",
            };
            let url = format!("https://core.telegram.org/{category}/{name}?raw=1");
            let response = client
                .get(&url)
                .send()
                .await
                .with_context(|| format!("failed downloading documentation {url}"))?;
            if response.status() == StatusCode::NOT_FOUND {
                return Ok(None);
            }
            let html = response
                .error_for_status()
                .with_context(|| format!("failed downloading documentation {url}"))?
                .text()
                .await
                .with_context(|| format!("failed reading documentation {url}"))?;
            Ok::<_, eyre::Report>(Some(((section, name), extract(&html)?)))
        })
        .buffer_unordered(CONCURRENCY)
        .try_filter_map(async move |value| Ok(value))
        .try_collect()
        .await
}

pub(crate) fn enrich(
    source: &str,
    documentation: &HashMap<(Section, String), Documentation>,
    include_introduced: bool,
) -> Result<String> {
    let parsed = tellers_tl_parser::parse(source)?;
    let mut output = String::with_capacity(source.len() + source.len() / 3);
    let mut cursor = 0;
    for declaration in parsed.declarations {
        output.push_str(&source[cursor..declaration.span.start]);
        if let DeclarationKind::Combinator(combinator) = &declaration.kind {
            if let Some(docs) = documentation.get(&(declaration.section, combinator.name.clone())) {
                if include_introduced {
                    if let Some(layer) = docs.introduced {
                        output.push_str("// @introduced ");
                        output.push_str(&layer.to_string());
                        output.push('\n');
                    }
                }
                if let Some(description) = &docs.description {
                    output.push_str("// @description ");
                    output.push_str(description);
                    output.push('\n');
                }
                for (name, description) in &docs.parameters {
                    output.push_str("// @param ");
                    output.push_str(name);
                    output.push(' ');
                    output.push_str(description);
                    output.push('\n');
                }
                for link in &docs.links {
                    output.push_str("// @see ");
                    output.push_str(link);
                    output.push('\n');
                }
            }
        }
        output.push_str(&source[declaration.span.start..declaration.span.end]);
        cursor = declaration.span.end;
    }
    output.push_str(&source[cursor..]);
    Ok(output)
}

fn extract(html: &str) -> Result<Documentation> {
    let document = Html::parse_document(html);
    let paragraph = selector("#dev_page_content > p")?;
    let table = selector("#dev_page_content table.table")?;
    let row = selector("tbody tr")?;
    let cell = selector("td")?;
    let anchor = selector("a[href]")?;
    let schema = selector("pre.page_scheme code")?;
    let paragraph = document.select(&paragraph).next();
    let table = document.select(&table).next();
    let description = paragraph
        .as_ref()
        .map(|element| normalized_text(element.text()))
        .filter(|text| !(text.is_empty() || text.starts_with('{') && text.ends_with('}')));
    let parameters = table
        .as_ref()
        .into_iter()
        .flat_map(|table| table.select(&row))
        .filter_map(|row| {
            let cells = row
                .select(&cell)
                .map(|value| normalized_text(value.text()))
                .collect::<Vec<_>>();
            (cells.len() >= 3 && !cells[0].is_empty()).then(|| (cells[0].clone(), cells[2].clone()))
        })
        .collect();
    let links = paragraph
        .into_iter()
        .chain(table)
        .flat_map(|element| element.select(&anchor))
        .filter_map(|anchor| absolute_url(anchor.value().attr("href")?))
        .collect();
    let introduced = document
        .select(&schema)
        .next()
        .into_iter()
        .flat_map(|schema| schema.text())
        .flat_map(str::lines)
        .filter_map(parse_layer_marker)
        .last();
    Ok(Documentation {
        description,
        parameters,
        links,
        introduced,
    })
}

fn parse_layer_marker(line: &str) -> Option<u32> {
    line.trim()
        .strip_prefix("===")?
        .strip_suffix("===")?
        .parse()
        .ok()
}

fn absolute_url(value: &str) -> Option<String> {
    if value.starts_with("https://") || value.starts_with("http://") {
        Some(value.to_owned())
    } else if let Some(path) = value.strip_prefix("//") {
        Some(format!("https://{path}"))
    } else {
        value
            .strip_prefix('/')
            .map(|path| format!("https://core.telegram.org/{path}"))
    }
}

fn selector(value: &str) -> Result<Selector> {
    Selector::parse(value).map_err(|error| eyre::eyre!("invalid selector {value}: {error}"))
}

fn normalized_text<'a>(parts: impl Iterator<Item = &'a str>) -> String {
    parts
        .flat_map(str::split_whitespace)
        .collect::<Vec<_>>()
        .join(" ")
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn extracts_and_injects_structured_documentation() {
        let html = r#"
            <div id="dev_page_content">
              <p>Does <a href="/thing">something</a> useful.</p>
              <pre class="page_scheme"><code>===5===
              item#01020304 value:int = Item;</code></pre>
              <table class="table"><tbody><tr>
                <td>value</td><td>int</td><td>The useful value.</td>
              </tr></tbody></table>
            </div>
        "#;
        let docs = extract(html).unwrap();
        let all = HashMap::from([((Section::Types, "item".into()), docs)]);
        let enriched = enrich("item#01020304 value:int = Item;", &all, true).unwrap();
        assert!(enriched.contains("// @description Does something useful."));
        assert!(enriched.contains("// @introduced 5"));
        assert!(enriched.contains("// @param value The useful value."));
        let model =
            tellers_tl_schema::parse(&enriched, tellers_tl_schema::Source::default()).unwrap();
        assert_eq!(
            model.constructors[0].arguments[0].description.as_deref(),
            Some("The useful value.")
        );
    }
}
