//! Checked-in Rust generation for every archived layer.

use crate::{cli::GenerateArgs, paths};
use eyre::{Context, ContextCompat, Result, bail};
use semver::{BuildMetadata, Version};
use std::{fmt::Write as _, fs, path::Path};
use tellers_tl_rust::{Options, generate};
use tellers_tl_schema::Source;
use toml_edit::{Array, DocumentMut, Item, Value, value};
use walkdir::WalkDir;

pub(crate) fn run(arguments: GenerateArgs) -> Result<()> {
    let archived = archived_layers();
    let current_text = fs::read_to_string(paths::schemas().join("upstream/api.tl"))?;
    let current = tellers_tl_parser::parse(&current_text)?
        .layer
        .context("official current schema has no layer")?;
    let layers = if arguments.all_layers {
        archived
    } else {
        vec![current]
    };
    let latest = current;
    let secret_layers = secret_chat_layers();
    let latest_secret = secret_layers.last().copied();
    let generated = paths::generated_crate();

    generate_transport(&generated, arguments.check)?;
    generate_api_layers(&generated, &layers, current, arguments.check)?;
    generate_secret_chat_layers(&generated, &secret_layers, arguments.check)?;
    if let Some(latest_secret) = latest_secret {
        let secret_library = render_secret_chat_library(&secret_layers, latest_secret);
        output(
            &generated.join("src/secret_chat/mod.rs"),
            &secret_library,
            arguments.check,
        )?;
    }

    let library = render_library(&layers, latest, latest_secret.is_some());
    output(&generated.join("src/lib.rs"), &library, arguments.check)?;
    let manifest_path = generated.join("Cargo.toml");
    let manifest = render_manifest(&manifest_path, &layers, latest, latest_secret.is_some())?;
    output(&manifest_path, &manifest, arguments.check)?;
    println!(
        "{} {} generated API layers (latest: {latest})",
        if arguments.check { "verified" } else { "wrote" },
        layers.len()
    );
    if let Some(latest_secret) = latest_secret {
        println!(
            "{} {} generated Secret Chat layers (latest: {latest_secret})",
            if arguments.check { "verified" } else { "wrote" },
            secret_layers.len()
        );
    }
    Ok(())
}

fn generate_transport(generated: &Path, check: bool) -> Result<()> {
    let transport_text = fs::read_to_string(paths::schemas().join("upstream/mtproto.tl"))?;
    let transport_schema = tellers_tl_schema::parse(
        &transport_text,
        Source {
            name: "mtproto.tl".into(),
            url: Some("https://core.telegram.org/schema/mtproto".into()),
        },
    )?;
    let transport = generate(
        &transport_schema,
        &Options {
            module_documentation: "Shared Telegram MTProto transport schema; see https://core.telegram.org/mtproto/mtproto-transports for framing, obfuscation, and MTProxy connection details.".into(),
            emit_codec: true,
            ..Options::default()
        },
    )?;
    output(&generated.join("src/transport.rs"), &transport, check)?;
    Ok(())
}

fn generate_api_layers(generated: &Path, layers: &[u32], current: u32, check: bool) -> Result<()> {
    for layer in layers {
        let source_path = paths::schemas().join(format!("layers/{layer}/api.tl"));
        let text = fs::read_to_string(&source_path)?;
        let schema = tellers_tl_schema::parse(
            &text,
            Source {
                name: source_path.display().to_string(),
                url: (*layer == current).then(|| "https://core.telegram.org/schema".into()),
            },
        )?;
        let code = generate(
            &schema,
            &Options {
                module_documentation: format!("Telegram API schema layer {layer}."),
                prelude: Vec::new(),
                emit_codec: true,
                ..Options::default()
            },
        )?;
        output(
            &generated.join(format!("src/layer_{layer}/api.rs")),
            &code,
            check,
        )?;
    }
    Ok(())
}

fn generate_secret_chat_layers(generated: &Path, layers: &[u32], check: bool) -> Result<()> {
    for layer in layers {
        let source_path = paths::schemas().join(format!("secret-chat/layers/{layer}.tl"));
        let text = fs::read_to_string(&source_path)?;
        let schema = tellers_tl_schema::parse(
            &text,
            Source {
                name: source_path.display().to_string(),
                url: Some("https://core.telegram.org/schema/end-to-end".into()),
            },
        )?;
        let code = generate(
            &schema,
            &Options {
                module_documentation: format!("Telegram Secret Chat schema layer {layer}."),
                prelude: vec!["use crate::latest::api::*;".into()],
                emit_codec: true,
                ..Options::default()
            },
        )?;
        output(
            &generated.join(format!("src/secret_chat/layer_{layer}.rs")),
            &code,
            check,
        )?;
    }
    Ok(())
}

fn archived_layers() -> Vec<u32> {
    let root = paths::schemas().join("layers");
    let mut layers = WalkDir::new(root)
        .min_depth(2)
        .max_depth(2)
        .into_iter()
        .filter_map(Result::ok)
        .filter(|entry| entry.file_name() == "api.tl")
        .filter_map(|entry| {
            entry
                .path()
                .parent()?
                .file_name()?
                .to_str()?
                .parse::<u32>()
                .ok()
        })
        .collect::<Vec<_>>();
    layers.sort_unstable();
    layers.dedup();
    layers
}

fn secret_chat_layers() -> Vec<u32> {
    let root = paths::schemas().join("secret-chat/layers");
    let mut layers = WalkDir::new(root)
        .min_depth(1)
        .max_depth(1)
        .into_iter()
        .filter_map(Result::ok)
        .filter_map(|entry| entry.path().file_stem()?.to_str()?.parse::<u32>().ok())
        .collect::<Vec<_>>();
    layers.sort_unstable();
    layers.dedup();
    layers
}

fn render_library(layers: &[u32], latest: u32, secret_chat: bool) -> String {
    let mut output = String::from(
        "//! Generated, feature-gated Telegram API and MTProto declarations.\n\
         //!\n\
         //! API layers are isolated modules. Enable one layer for a pinned client,\n\
         //! or `all-layers` for compatibility and migration tooling.\n\n\
         #![allow(clippy::large_enum_variant, clippy::doc_markdown)]\n\
         #![forbid(unsafe_code)]\n\n\
         /// Canonical bounded TL wire codec used by every generated declaration.\n\
         pub mod codec;\n\n\
         pub use codec::{\n    Boxed, BoxedDecode, Decoder, Encoder, Error as CodecError, Limits, RpcMethod, TlDecode,\n    TlEncode,\n};\n\n\
         /// Shared core MTProto transport and authorization declarations.\n\
         #[rustfmt::skip]\n\
         #[allow(missing_docs, clippy::all, clippy::pedantic)]\n\
         pub mod transport;\n\n",
    );
    write!(
        output,
        "/// API layers materialized in this build of the generated crate.\npub const GENERATED_API_LAYERS: &[u32] = &{layers:?};\n\n/// Current officially published API layer.\npub const LATEST_API_LAYER: u32 = {latest};\n\n"
    )
    .expect("writing to a String cannot fail");
    for layer in layers {
        write!(
            output,
            "/// Telegram API schema layer {layer}.\n#[cfg(feature = \"layer-{layer}\")]\npub mod layer_{layer} {{\n    /// Public Telegram API constructors and methods.\n    #[rustfmt::skip]\n    #[allow(missing_docs, clippy::all, clippy::pedantic)]\n    pub mod api;\n}}\n\n"
        )
        .expect("writing to a String cannot fail");
    }
    write!(
        output,
        "/// The current officially published API layer.\n\
         #[cfg(feature = \"layer-{latest}\")]\n\
         pub use layer_{latest} as latest;\n"
    )
    .expect("writing to a String cannot fail");
    if secret_chat {
        output.push_str(
            "\n/// Independently versioned end-to-end payloads for Secret Chats.\n\
             #[cfg(feature = \"secret-chat\")]\n\
             pub mod secret_chat;\n",
        );
    }
    output
}

fn render_secret_chat_library(layers: &[u32], latest: u32) -> String {
    let mut output = String::from(
        "//! Generated Secret Chat payload schemas.\n\
         //!\n\
         //! Select a module using the peer layer negotiated by the Secret Chat protocol.\n\n",
    );
    write!(
        output,
        "/// Schema change layers reconstructed from Telegram's official end-to-end schema.\npub const SUPPORTED_LAYERS: &[u32] = &{layers:?};\n\n/// Newest available Secret Chat schema layer.\npub const LATEST_LAYER: u32 = {latest};\n\n/// Select the newest generated schema not newer than the peer's layer.\n#[must_use]\npub const fn schema_layer_for_peer(peer_layer: u32) -> Option<u32> {{\n    let mut index = 0;\n    let mut selected = None;\n    while index < SUPPORTED_LAYERS.len() {{\n        let layer = SUPPORTED_LAYERS[index];\n        if layer > peer_layer {{\n            break;\n        }}\n        selected = Some(layer);\n        index += 1;\n    }}\n    selected\n}}\n\n"
    )
    .expect("writing to a String cannot fail");
    for layer in layers {
        write!(
            output,
            "/// Secret Chat payload schema at layer {layer}.\n#[rustfmt::skip]\n#[allow(missing_docs, clippy::all, clippy::pedantic)]\npub mod layer_{layer};\n\n"
        )
        .expect("writing to a String cannot fail");
    }
    write!(
        output,
        "/// Newest available Secret Chat payload schema.\npub use layer_{latest} as latest;\n"
    )
    .expect("writing to a String cannot fail");
    output
}

fn render_manifest(path: &Path, layers: &[u32], latest: u32, secret_chat: bool) -> Result<String> {
    let source = fs::read_to_string(path)?;
    let mut document = source.parse::<DocumentMut>()?;
    let mut package_version = workspace_version()?;
    package_version.build = BuildMetadata::new(&format!("layer.{latest}"))?;
    document["package"]["version"] = value(package_version.to_string());
    let features = document["features"]
        .as_table_mut()
        .context("generated crate has no [features] table")?;
    let selected_layers = layers
        .iter()
        .map(|layer| format!("layer-{layer}"))
        .collect::<Vec<_>>();
    let obsolete_layer_keys = features
        .iter()
        .map(|(key, _)| key.to_owned())
        .filter(|key| key.starts_with("layer-") && !selected_layers.contains(key))
        .collect::<Vec<_>>();
    for key in obsolete_layer_keys {
        features.remove(&key);
    }
    features.insert("default", array_item([format!("layer-{latest}")]));
    for layer in layers {
        features.insert(&format!("layer-{layer}"), array_item([]));
    }
    features.insert("all-layers", array_item(selected_layers));
    if secret_chat {
        features.insert("secret-chat", array_item([format!("layer-{latest}")]));
    } else {
        features.remove("secret-chat");
    }
    Ok(document.to_string())
}

fn workspace_version() -> Result<Version> {
    let source = fs::read_to_string(paths::root().join("Cargo.toml"))?;
    let document = source.parse::<DocumentMut>()?;
    let version = document["workspace"]["package"]["version"]
        .as_str()
        .context("workspace.package.version is not a string")?;
    Version::parse(version).context("workspace.package.version is not valid SemVer")
}

fn array_item(values: impl IntoIterator<Item = String>) -> Item {
    let mut array = Array::new();
    for value in values {
        array.push(value);
    }
    Item::Value(Value::Array(array))
}

fn output(path: &Path, expected: &str, check: bool) -> Result<()> {
    if check {
        let actual = fs::read_to_string(path)
            .with_context(|| format!("missing generated file {}", path.display()))?;
        if actual != expected {
            bail!("generated file is stale: {}", path.display());
        }
    } else {
        if let Some(parent) = path.parent() {
            fs::create_dir_all(parent)?;
        }
        fs::write(path, expected)?;
    }
    Ok(())
}
