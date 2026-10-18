//! Repository task runner.

mod cli;
mod documentation;
mod generate;
mod layered;
mod paths;
mod process;
mod schemas;

use clap::Parser;
use eyre::Result;

#[tokio::main]
async fn main() {
    if let Err(error) = run().await {
        eprintln!("error: {error:#}");
        std::process::exit(1);
    }
}

async fn run() -> Result<()> {
    let command = cli::Cli::parse().command;
    match command {
        cli::Command::Schemas(command) => {
            schemas::run(&command).await?;
            if matches!(command, cli::SchemaCommand::SyncCurrent) {
                schemas::verify().await?;
                run_generator(cli::GenerateArgs {
                    check: false,
                    all_layers: false,
                })
                .await?;
            }
            Ok(())
        }
        cli::Command::Generate(arguments) => {
            schemas::verify().await?;
            run_generator(arguments).await
        }
        cli::Command::Verify => {
            schemas::verify().await?;
            run_blocking(|| {
                generate::run(cli::GenerateArgs {
                    check: true,
                    all_layers: false,
                })
            })
            .await
        }
    }
}

async fn run_generator(arguments: cli::GenerateArgs) -> Result<()> {
    run_blocking(move || generate::run(arguments)).await?;
    if !arguments.check {
        refresh_lockfile().await?;
    }
    Ok(())
}

async fn refresh_lockfile() -> Result<()> {
    let manifest = paths::root().join("Cargo.toml");
    let manifest = manifest.to_string_lossy();
    process::output(
        "cargo",
        &[
            "metadata",
            "--no-deps",
            "--format-version=1",
            "--manifest-path",
            &manifest,
        ],
    )
    .await?;
    Ok(())
}

async fn run_blocking(operation: impl FnOnce() -> Result<()> + Send + 'static) -> Result<()> {
    tokio::task::spawn_blocking(operation)
        .await
        .map_err(eyre::Report::from)?
}
