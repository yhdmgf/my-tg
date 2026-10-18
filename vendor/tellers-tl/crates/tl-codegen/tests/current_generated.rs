//! Ensures checked-in generated modules exactly match the official TL inputs.

use pretty_assertions::assert_eq;
use std::{fs, path::PathBuf};
use syn::{Fields, Item};
use tellers_tl_rust::{Options, generate};
use tellers_tl_schema::{Schema, Source};

fn assert_complete_generated_contract(schema: &Schema, generated: &str) {
    let expected_methods = schema
        .functions
        .iter()
        .filter(|method| !method.builtin)
        .count();
    let syntax = syn::parse_file(generated).expect("generated output is valid Rust");
    let actual_methods = syntax
        .items
        .iter()
        .filter(|item| {
            let Item::Impl(item) = item else {
                return false;
            };
            item.trait_
                .as_ref()
                .and_then(|(path, _)| path.segments.last())
                .is_some_and(|segment| segment.ident == "RpcMethod")
        })
        .count();
    assert_eq!(
        actual_methods, expected_methods,
        "Every non-builtin TL function must implement RpcMethod"
    );

    for item in syntax.items {
        match item {
            Item::Struct(item) if matches!(item.vis, syn::Visibility::Public(_)) => {
                assert!(has_docs(&item.attrs), "Missing docs on `{}`", item.ident);
                for field in item.fields {
                    assert!(
                        has_docs(&field.attrs),
                        "Missing docs on a field of `{}`",
                        item.ident
                    );
                }
            }
            Item::Enum(item) if matches!(item.vis, syn::Visibility::Public(_)) => {
                assert!(has_docs(&item.attrs), "Missing docs on `{}`", item.ident);
                for variant in item.variants {
                    assert!(
                        has_docs(&variant.attrs),
                        "Missing docs on `{}::{}`",
                        item.ident,
                        variant.ident
                    );
                    if let Fields::Named(fields) = variant.fields {
                        for field in fields.named {
                            assert!(
                                has_docs(&field.attrs),
                                "Missing docs on a field of `{}::{}`",
                                item.ident,
                                variant.ident
                            );
                        }
                    }
                }
            }
            _ => {}
        }
    }
}

fn has_docs(attributes: &[syn::Attribute]) -> bool {
    attributes
        .iter()
        .any(|attribute| attribute.path().is_ident("doc"))
}

#[test]
fn official_current_api_is_fresh() {
    let workspace = PathBuf::from(env!("CARGO_MANIFEST_DIR")).join("../..");
    let schema_text = fs::read_to_string(workspace.join("schemas/upstream/api.tl")).unwrap();
    let schema = tellers_tl_schema::parse(
        &schema_text,
        Source {
            name: "api".into(),
            url: Some("https://core.telegram.org/schema".into()),
        },
    )
    .unwrap();
    let layer = schema
        .layer
        .expect("the official API schema declares a layer");
    let checked_in = fs::read_to_string(
        workspace.join(format!("crates/mtproto-types/src/layer_{layer}/api.rs")),
    )
    .unwrap();
    let generated = generate(
        &schema,
        &Options {
            module_documentation: format!("Telegram API schema layer {layer}."),
            prelude: Vec::new(),
            emit_codec: true,
            ..Options::default()
        },
    )
    .unwrap();

    assert_eq!(generated, checked_in, "Regenerate API layer {layer}");
    assert_complete_generated_contract(&schema, &generated);
}

#[test]
fn official_transport_is_fresh() {
    let workspace = PathBuf::from(env!("CARGO_MANIFEST_DIR")).join("../..");
    let schema_text = fs::read_to_string(workspace.join("schemas/upstream/mtproto.tl")).unwrap();
    let schema = tellers_tl_schema::parse(
        &schema_text,
        Source {
            name: "mtproto".into(),
            url: Some("https://core.telegram.org/schema/mtproto".into()),
        },
    )
    .unwrap();
    let checked_in =
        fs::read_to_string(workspace.join("crates/mtproto-types/src/transport.rs")).unwrap();
    let generated = generate(
        &schema,
        &Options {
            module_documentation: "Shared Telegram MTProto transport schema; see https://core.telegram.org/mtproto/mtproto-transports for framing, obfuscation, and MTProxy connection details.".into(),
            emit_codec: true,
            ..Options::default()
        },
    )
    .unwrap();

    assert_eq!(generated, checked_in, "Regenerate the MTProto module");
    assert_complete_generated_contract(&schema, &generated);
}

#[test]
fn official_secret_chat_layers_are_fresh() {
    let workspace = PathBuf::from(env!("CARGO_MANIFEST_DIR")).join("../..");
    let schemas = workspace.join("schemas/secret-chat/layers");
    for entry in fs::read_dir(schemas).unwrap() {
        let path = entry.unwrap().path();
        if path.extension().and_then(|value| value.to_str()) != Some("tl") {
            continue;
        }
        let schema_text = fs::read_to_string(&path).unwrap();
        let schema = tellers_tl_schema::parse(
            &schema_text,
            Source {
                name: path.display().to_string(),
                url: Some("https://core.telegram.org/schema/end-to-end".into()),
            },
        )
        .unwrap();
        let layer = schema
            .layer
            .expect("a materialized Secret Chat schema declares its layer");
        let checked_in = fs::read_to_string(workspace.join(format!(
            "crates/mtproto-types/src/secret_chat/layer_{layer}.rs"
        )))
        .unwrap();
        let generated = generate(
            &schema,
            &Options {
                module_documentation: format!("Telegram Secret Chat schema layer {layer}."),
                prelude: vec!["use crate::latest::api::*;".into()],
                emit_codec: true,
                ..Options::default()
            },
        )
        .unwrap();

        assert_eq!(
            generated, checked_in,
            "Regenerate Secret Chat layer {layer}"
        );
        assert_complete_generated_contract(&schema, &generated);
    }
}
