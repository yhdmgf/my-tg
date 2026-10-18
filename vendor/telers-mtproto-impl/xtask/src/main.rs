//! Repository automation that needs sequencing or dependency-aware behavior.

#![forbid(unsafe_code)]

use std::env;
use std::ffi::OsStr;
use std::process::{Command, ExitCode};

const ROOT_PACKAGES: [&str; 3] = [
    "tellers-mtproto-codec",
    "tellers-mtproto-crypto",
    "tellers-mtproto-session",
];
const CODEC_PATCH: [&str; 2] = [
    "--config",
    "patch.crates-io.tellers-mtproto-codec.path=\"crates/tellers-mtproto-codec\"",
];
const CRYPTO_PATCH: [&str; 2] = [
    "--config",
    "patch.crates-io.tellers-mtproto-crypto.path=\"crates/tellers-mtproto-crypto\"",
];
const SESSION_PATCH: [&str; 2] = [
    "--config",
    "patch.crates-io.tellers-mtproto-session.path=\"crates/tellers-mtproto-session\"",
];
const TRANSPORT_PATCH: [&str; 2] = [
    "--config",
    "patch.crates-io.tellers-mtproto-transport.path=\"crates/tellers-mtproto-transport\"",
];
const ENGINE_PATCH: [&str; 2] = [
    "--config",
    "patch.crates-io.tellers-mtproto-engine.path=\"crates/tellers-mtproto-engine\"",
];

fn run(program: &str, arguments: &[&str]) -> Result<(), String> {
    eprintln!("+ {program} {}", arguments.join(" "));
    let status = Command::new(program)
        .args(arguments.iter().map(OsStr::new))
        .status()
        .map_err(|error| format!("failed to start {program}: {error}"))?;
    if status.success() {
        Ok(())
    } else {
        Err(format!("{program} exited with {status}"))
    }
}

fn package() -> Result<(), String> {
    for package in ROOT_PACKAGES {
        run("cargo", &["package", "-p", package, "--allow-dirty"])?;
    }
    // These archives cannot be independently verified until the new root
    // package versions exist in the registry. CI still builds their exact
    // publish archives, and release automation verifies them after publishing
    // dependencies in order.
    let dependent_packages: [(&str, &[&str]); 3] = [
        ("tellers-mtproto-transport", &CRYPTO_PATCH),
        (
            "tellers-mtproto-engine",
            &[
                CODEC_PATCH[0],
                CODEC_PATCH[1],
                CRYPTO_PATCH[0],
                CRYPTO_PATCH[1],
                SESSION_PATCH[0],
                SESSION_PATCH[1],
                TRANSPORT_PATCH[0],
                TRANSPORT_PATCH[1],
            ],
        ),
        (
            "tellers-mtproto-testkit",
            &[
                CODEC_PATCH[0],
                CODEC_PATCH[1],
                CRYPTO_PATCH[0],
                CRYPTO_PATCH[1],
                SESSION_PATCH[0],
                SESSION_PATCH[1],
                TRANSPORT_PATCH[0],
                TRANSPORT_PATCH[1],
                ENGINE_PATCH[0],
                ENGINE_PATCH[1],
            ],
        ),
    ];
    for (package, patches) in dependent_packages {
        let mut arguments = vec!["package", "-p", package, "--allow-dirty", "--no-verify"];
        arguments.extend_from_slice(patches);
        run("cargo", &arguments)?;
    }
    Ok(())
}

fn ci() -> Result<(), String> {
    run("cargo", &["fmt", "--all", "--", "--check"])?;
    run(
        "cargo",
        &[
            "clippy",
            "--workspace",
            "--all-targets",
            "--all-features",
            "--",
            "-D",
            "warnings",
        ],
    )?;
    run(
        "cargo",
        &["nextest", "run", "--workspace", "--all-features"],
    )?;
    run("cargo", &["test", "--workspace", "--all-features", "--doc"])?;
    package()
}

fn help() {
    eprintln!("Usage: cargo xtask <ci|package>");
}

fn main() -> ExitCode {
    let result = match env::args().nth(1).as_deref() {
        Some("ci") => ci(),
        Some("package") => package(),
        Some("help" | "-h" | "--help") => {
            help();
            return ExitCode::SUCCESS;
        }
        _ => {
            help();
            return ExitCode::from(2);
        }
    };
    match result {
        Ok(()) => ExitCode::SUCCESS,
        Err(error) => {
            eprintln!("xtask: {error}");
            ExitCode::FAILURE
        }
    }
}
