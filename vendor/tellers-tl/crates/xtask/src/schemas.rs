//! Reproducible Telegram Desktop schema synchronization.

use crate::{cli::SchemaCommand, documentation, layered, paths, process};
use eyre::{Context, ContextCompat, Result, bail};
use futures::{StreamExt, TryStreamExt, stream};
use reqwest::{Client, StatusCode};
use scraper::{Html, Selector};
use semver::Version;
use serde::{Deserialize, Serialize};
use sha2::{Digest, Sha256};
use std::{
    collections::{BTreeMap, HashSet},
    fmt::Write as _,
    path::Path,
};

const REPOSITORY: &str = "https://github.com/telegramdesktop/tdesktop.git";
const RAW_ROOT: &str = "https://raw.githubusercontent.com/telegramdesktop/tdesktop";
const OFFICIAL_API: &str = "https://core.telegram.org/schema?raw=1";
const OFFICIAL_MTPROTO: &str = "https://core.telegram.org/schema/mtproto?raw=1";
const OFFICIAL_SECRET_CHAT: &str = "https://core.telegram.org/schema/end-to-end?raw=1";
const NETWORK_CONCURRENCY: usize = 24;
const CURRENT_API: &str = "Telegram/SourceFiles/mtproto/scheme/api.tl";
const CURRENT_LAYER: &str = "Telegram/SourceFiles/mtproto/scheme/layer.tl";
const MIDDLE_API: &str = "Telegram/Resources/tl/api.tl";
const OLD_COMBINED: &str = "Telegram/SourceFiles/mtproto/scheme.tl";

#[derive(Debug, Default, Deserialize, Serialize)]
struct Manifest {
    repository: String,
    layers: BTreeMap<u32, LayerSource>,
    transport: Option<FileSource>,
    secret_chat: Option<LayeredSource>,
}

#[derive(Clone, Debug, Deserialize, Serialize)]
struct LayerSource {
    layer: u32,
    revision: String,
    tag: String,
    url: String,
    sha256: String,
    combined_with_transport: bool,
}

#[derive(Clone, Debug, Deserialize, Serialize)]
struct FileSource {
    revision: String,
    url: String,
    sha256: String,
}

#[derive(Clone, Debug, Deserialize, Serialize)]
struct LayeredSource {
    revision: String,
    url: String,
    sha256: String,
    current_layer: u32,
    layers: BTreeMap<u32, String>,
}

#[derive(Clone, Debug)]
struct Tag {
    revision: String,
    name: String,
    version: Version,
}

#[derive(Debug)]
struct Snapshot {
    source: LayerSource,
    text: String,
}

pub(crate) async fn run(command: &SchemaCommand) -> Result<()> {
    match command {
        SchemaCommand::SyncCurrent => sync_current().await,
        SchemaCommand::SyncLayers {
            min_layer,
            max_tags,
        } => sync_layers(*min_layer, *max_tags).await,
        SchemaCommand::Verify => verify().await,
    }
}

async fn sync_current() -> Result<()> {
    let client = Client::new();
    let (api_html, mtproto_html, secret_chat_html) = tokio::try_join!(
        download(&client, OFFICIAL_API),
        download(&client, OFFICIAL_MTPROTO),
        download(&client, OFFICIAL_SECRET_CHAT),
    )?;
    let (layer, api_body) = extract_official_api(&api_html)?;
    let api = format!("// LAYER {layer}\n{api_body}");
    let mtproto = extract_schema_code(&mtproto_html, "pre code")?;
    let secret_chat = extract_schema_code(&secret_chat_html, "pre.page_scheme code")?;
    let secret_snapshots = layered::snapshots(&secret_chat)?;
    let secret_layer = secret_snapshots
        .last()
        .context("official end-to-end schema contains no layers")?
        .layer;
    let latest_secret = &secret_snapshots
        .last()
        .context("official end-to-end schema contains no layers")?
        .text;
    let docs = documentation::fetch(
        &client,
        [api.as_str(), mtproto.as_str(), latest_secret.as_str()],
    )
    .await?;
    let api = documentation::enrich(&api, &docs, true)?;
    let mtproto = documentation::enrich(&mtproto, &docs, true)?;
    let secret_snapshots = secret_snapshots
        .into_iter()
        .map(|snapshot| {
            Ok(layered::Snapshot {
                layer: snapshot.layer,
                text: documentation::enrich(&snapshot.text, &docs, false)?,
            })
        })
        .collect::<Result<Vec<_>>>()?;
    parse_model(&api, OFFICIAL_API)?;
    parse_model(&mtproto, OFFICIAL_MTPROTO)?;

    let schemas = paths::schemas();
    write(&schemas.join("upstream/api.tl"), &api).await?;
    write(&schemas.join("upstream/mtproto.tl"), &mtproto).await?;
    write(&schemas.join("upstream/end-to-end.tl"), &secret_chat).await?;
    write(&schemas.join(format!("layers/{layer}/api.tl")), &api).await?;
    let mut secret_layers = BTreeMap::new();
    for snapshot in &secret_snapshots {
        write(
            &schemas.join(format!("secret-chat/layers/{}.tl", snapshot.layer)),
            &snapshot.text,
        )
        .await?;
        secret_layers.insert(snapshot.layer, digest(&snapshot.text));
    }

    let mut manifest = load_manifest().await?;
    manifest.repository = "https://core.telegram.org/schema".into();
    manifest.layers.insert(
        layer,
        LayerSource {
            layer,
            revision: "official-current".into(),
            tag: "official".into(),
            url: OFFICIAL_API.into(),
            sha256: digest(&api),
            combined_with_transport: false,
        },
    );
    manifest.transport = Some(FileSource {
        revision: "official-current".into(),
        url: OFFICIAL_MTPROTO.into(),
        sha256: digest(&mtproto),
    });
    manifest.secret_chat = Some(LayeredSource {
        revision: "official-current".into(),
        url: OFFICIAL_SECRET_CHAT.into(),
        sha256: digest(&secret_chat),
        current_layer: secret_layer,
        layers: secret_layers,
    });
    save_manifest(&manifest).await?;
    println!("synchronized Telegram API layer {layer} and Secret Chat layer {secret_layer}");
    Ok(())
}

async fn sync_layers(min_layer: u32, max_tags: Option<usize>) -> Result<()> {
    let mut tags = stable_tags().await?;
    tags.sort_by(|left, right| right.version.cmp(&left.version));
    if let Some(limit) = max_tags {
        tags.truncate(limit);
    }
    println!("inspecting {} stable Telegram Desktop tags", tags.len());
    let client = Client::new();
    let snapshots = stream::iter(tags.iter())
        .map(|tag| async {
            match fetch_tag(&client, tag).await {
                Ok(snapshot) => snapshot.filter(|value| value.source.layer >= min_layer),
                Err(error) => {
                    eprintln!("warning: {}: {error:#}", tag.name);
                    None
                }
            }
        })
        .buffer_unordered(NETWORK_CONCURRENCY)
        .filter_map(async move |snapshot| snapshot)
        .collect::<Vec<_>>()
        .await;

    let mut by_layer = BTreeMap::new();
    for snapshot in snapshots {
        by_layer.entry(snapshot.source.layer).or_insert(snapshot);
    }
    if by_layer.is_empty() {
        bail!("no API layers at or above {min_layer} were discovered");
    }

    let mut manifest = load_manifest().await?;
    if manifest.repository.is_empty() {
        manifest.repository = "https://core.telegram.org/schema".into();
    }
    for (layer, snapshot) in by_layer {
        let path = paths::schemas().join(format!("layers/{layer}/api.tl"));
        write(&path, &snapshot.text).await?;
        manifest.layers.entry(layer).or_insert(snapshot.source);
    }
    save_manifest(&manifest).await?;
    println!(
        "archive now contains {} distinct layers",
        manifest.layers.len()
    );
    Ok(())
}

async fn fetch_tag(client: &Client, tag: &Tag) -> Result<Option<Snapshot>> {
    let candidates: &[(&str, bool)] = if tag.version >= Version::new(4, 9, 0) {
        &[
            (CURRENT_API, false),
            (MIDDLE_API, false),
            (OLD_COMBINED, true),
        ]
    } else if tag.version >= Version::new(2, 0, 0) {
        &[
            (MIDDLE_API, false),
            (CURRENT_API, false),
            (OLD_COMBINED, true),
        ]
    } else {
        &[
            (OLD_COMBINED, true),
            (MIDDLE_API, false),
            (CURRENT_API, false),
        ]
    };
    for (path, combined) in candidates {
        let url = raw_url(&tag.revision, path);
        let Some(mut text) = download_optional(client, &url).await? else {
            continue;
        };
        if tellers_tl_parser::parse(&text)?.layer.is_none() {
            if let Some(marker) =
                download_optional(client, &raw_url(&tag.revision, CURRENT_LAYER)).await?
            {
                text = format!("{marker}\n{text}");
            }
        }
        let Some(layer) = parse_layer(&text) else {
            continue;
        };
        return Ok(Some(Snapshot {
            source: LayerSource {
                layer,
                revision: tag.revision.clone(),
                tag: tag.name.clone(),
                url,
                sha256: digest(&text),
                combined_with_transport: *combined,
            },
            text,
        }));
    }
    Ok(None)
}

async fn stable_tags() -> Result<Vec<Tag>> {
    let output = process::output("git", &["ls-remote", "--refs", "--tags", REPOSITORY]).await?;
    let mut revisions = HashSet::new();
    let mut tags = Vec::new();
    for line in output.lines() {
        let Some((revision, reference)) = line.split_once('\t') else {
            continue;
        };
        let Some(name) = reference.strip_prefix("refs/tags/v") else {
            continue;
        };
        let Ok(version) = Version::parse(name) else {
            continue;
        };
        if !version.pre.is_empty() || !revisions.insert(revision.to_owned()) {
            continue;
        }
        tags.push(Tag {
            revision: revision.into(),
            name: format!("v{name}"),
            version,
        });
    }
    Ok(tags)
}

pub(crate) async fn verify() -> Result<()> {
    let manifest = load_manifest().await?;
    if manifest.layers.is_empty() {
        bail!("schema manifest contains no API layers");
    }
    stream::iter(&manifest.layers)
        .map(|(layer, source)| async move {
            let path = paths::schemas().join(format!("layers/{layer}/api.tl"));
            let text = tokio::fs::read_to_string(&path)
                .await
                .with_context(|| format!("failed to read {}", path.display()))?;
            if digest(&text) != source.sha256 {
                bail!("SHA-256 mismatch for {}", path.display());
            }
            let parsed_layer =
                parse_layer(&text).with_context(|| format!("{} has no layer", path.display()))?;
            if parsed_layer != *layer {
                bail!(
                    "{} declares layer {parsed_layer}, expected {layer}",
                    path.display()
                );
            }
            parse_model(&text, &source.url)
        })
        .buffer_unordered(NETWORK_CONCURRENCY)
        .try_collect::<Vec<_>>()
        .await?;
    if let Some(source) = &manifest.transport {
        let path = paths::schemas().join("upstream/mtproto.tl");
        let text = tokio::fs::read_to_string(&path).await?;
        if digest(&text) != source.sha256 {
            bail!("SHA-256 mismatch for {}", path.display());
        }
        parse_model(&text, &source.url)?;
    }
    if let Some(source) = &manifest.secret_chat {
        let upstream = paths::schemas().join("upstream/end-to-end.tl");
        let text = tokio::fs::read_to_string(&upstream)
            .await
            .with_context(|| format!("failed to read {}", upstream.display()))?;
        if digest(&text) != source.sha256 {
            bail!("SHA-256 mismatch for {}", upstream.display());
        }
        let snapshots = layered::snapshots(&text)?;
        if snapshots.last().map(|snapshot| snapshot.layer) != Some(source.current_layer) {
            bail!("Secret Chat current layer does not match the manifest");
        }
        for snapshot in snapshots {
            let path = paths::schemas().join(format!("secret-chat/layers/{}.tl", snapshot.layer));
            let expected = source.layers.get(&snapshot.layer).with_context(|| {
                format!(
                    "Secret Chat layer {} is absent from manifest",
                    snapshot.layer
                )
            })?;
            let checked_in = tokio::fs::read_to_string(&path)
                .await
                .with_context(|| format!("failed to read {}", path.display()))?;
            if digest(&checked_in) != *expected {
                bail!("modified Secret Chat schema {}", path.display());
            }
            parse_model(&checked_in, &source.url)?;
        }
        println!("verified {} Secret Chat layers", source.layers.len());
    }
    println!("verified {} archived API layers", manifest.layers.len());
    Ok(())
}

fn parse_model(text: &str, url: &str) -> Result<()> {
    tellers_tl_schema::parse(
        text,
        tellers_tl_schema::Source {
            name: url.into(),
            url: Some(url.into()),
        },
    )?;
    Ok(())
}

fn parse_layer(text: &str) -> Option<u32> {
    tellers_tl_parser::parse(text).ok()?.layer
}

async fn download(client: &Client, url: &str) -> Result<String> {
    download_optional(client, url)
        .await?
        .with_context(|| format!("upstream file not found: {url}"))
}

async fn download_optional(client: &Client, url: &str) -> Result<Option<String>> {
    let response = client
        .get(url)
        .send()
        .await
        .with_context(|| format!("failed downloading {url}"))?;
    if response.status() == StatusCode::NOT_FOUND {
        return Ok(None);
    }
    let response = response
        .error_for_status()
        .with_context(|| format!("failed downloading {url}"))?;
    response
        .text()
        .await
        .map(Some)
        .with_context(|| format!("failed reading {url}"))
}

fn extract_official_api(html: &str) -> Result<(u32, String)> {
    let document = Html::parse_document(html);
    let layer_selector = Selector::parse("a.dropdown-toggle")
        .map_err(|error| eyre::eyre!("invalid layer selector: {error}"))?;
    let layer = document
        .select(&layer_selector)
        .map(|node| node.text().collect::<String>())
        .find_map(|text| {
            let text = text.trim();
            text.strip_prefix("Layer ")?
                .split_whitespace()
                .next()?
                .parse::<u32>()
                .ok()
        })
        .context("official schema page has no current layer marker")?;
    Ok((layer, extract_schema_code(html, "pre.page_scheme code")?))
}

fn extract_schema_code(html: &str, selector: &str) -> Result<String> {
    let document = Html::parse_document(html);
    let selector = Selector::parse(selector)
        .map_err(|error| eyre::eyre!("invalid schema selector: {error}"))?;
    let code = document
        .select(&selector)
        .next()
        .context("official page has no TL schema code block")?
        .text()
        .collect::<String>();
    let normalized = code.trim().replace("\r\n", "\n");
    if normalized.is_empty() {
        bail!("official TL schema code block is empty");
    }
    Ok(format!("{normalized}\n"))
}

fn raw_url(revision: &str, path: &str) -> String {
    format!("{RAW_ROOT}/{revision}/{path}")
}

fn digest(text: &str) -> String {
    Sha256::digest(text.as_bytes())
        .iter()
        .fold(String::with_capacity(64), |mut output, byte| {
            write!(output, "{byte:02x}").expect("writing to a String cannot fail");
            output
        })
}

fn manifest_path() -> std::path::PathBuf {
    paths::schemas().join("manifest.json")
}

async fn load_manifest() -> Result<Manifest> {
    let path = manifest_path();
    if !path.exists() {
        return Ok(Manifest::default());
    }
    serde_json::from_slice(&tokio::fs::read(&path).await?).context("invalid schemas/manifest.json")
}

async fn save_manifest(manifest: &Manifest) -> Result<()> {
    let mut bytes = serde_json::to_vec_pretty(manifest)?;
    bytes.push(b'\n');
    let path = manifest_path();
    if let Some(parent) = path.parent() {
        tokio::fs::create_dir_all(parent).await?;
    }
    tokio::fs::write(path, bytes).await?;
    Ok(())
}

async fn write(path: &Path, text: &str) -> Result<()> {
    if let Some(parent) = path.parent() {
        tokio::fs::create_dir_all(parent).await?;
    }
    tokio::fs::write(path, text)
        .await
        .with_context(|| format!("failed to write {}", path.display()))
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn extracts_official_html_without_schema_name_knowledge() {
        let html = r#"
            <a class="dropdown-toggle">Layer 321 <b>open</b></a>
            <pre class="page_scheme"><code>
            unfamiliar#01020304 value:Vector&lt;long&gt; = Unfamiliar;
            </code></pre>
        "#;
        let (layer, schema) = extract_official_api(html).unwrap();
        assert_eq!(layer, 321);
        assert!(schema.contains("value:Vector<long>"));
        tellers_tl_parser::parse(&schema).unwrap();
    }
}
