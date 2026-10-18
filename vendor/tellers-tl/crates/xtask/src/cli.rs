//! Command-line definitions.

use clap::{Args, Parser, Subcommand};

/// Maintenance commands run through `cargo xtask`.
#[derive(Debug, Parser)]
#[command(version, about)]
pub(crate) struct Cli {
    #[command(subcommand)]
    pub(crate) command: Command,
}

#[derive(Debug, Subcommand)]
pub(crate) enum Command {
    /// Fetch, archive, and verify upstream schemas.
    #[command(subcommand)]
    Schemas(SchemaCommand),
    /// Generate the official current API, or optionally every archived layer.
    Generate(GenerateArgs),
    /// Verify schema hashes and checked-in generated output.
    Verify,
}

#[derive(Clone, Copy, Debug, Subcommand)]
pub(crate) enum SchemaCommand {
    /// Fetch current official schemas, then regenerate code and its layer version.
    SyncCurrent,
    /// Build a third-party historical archive from stable Telegram Desktop tags.
    SyncLayers {
        /// Oldest layer to retain. Layer 100 starts the modern archive by default.
        #[arg(long, default_value_t = 100)]
        min_layer: u32,
        /// Limit processed tags for diagnostics; omitted means every stable tag.
        #[arg(long)]
        max_tags: Option<usize>,
    },
    /// Verify every archived source against its manifest hash and parser.
    Verify,
}

#[derive(Debug, Clone, Copy, Args)]
pub(crate) struct GenerateArgs {
    /// Compare generated output without changing files.
    #[arg(long)]
    pub(crate) check: bool,
    /// Materialize every archived historical layer instead of only official current.
    #[arg(long)]
    pub(crate) all_layers: bool,
}
