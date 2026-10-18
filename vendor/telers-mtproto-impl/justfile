set shell := ["bash", "-euo", "pipefail", "-c"]

# Show available project commands.
default:
    @just --list

# Format all Rust sources.
fmt:
    cargo fmt --all

# Check formatting without changing files.
fmt-check:
    cargo fmt --all -- --check

# Run strict workspace lints.
lint:
    cargo clippy --workspace --all-targets --all-features -- -D warnings

# Run the complete test suite with nextest.
test:
    cargo nextest run --workspace --all-features

# Run documentation tests.
docs:
    cargo test --workspace --all-features --doc

# Run all source checks, excluding packaging.
check: fmt-check lint test docs

# Build and verify publishable crate archives in dependency order.
package:
    cargo xtask package

# Reproduce the complete CI pipeline locally.
ci:
    cargo xtask ci
