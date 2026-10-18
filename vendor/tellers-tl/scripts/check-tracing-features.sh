#!/bin/sh
set -eu

# Exercise the ordinary defaults first. The tracing dependency is optional in
# each handwritten crate; xtask's HTTP stack may still include tracing
# transitively, so dependency-tree text cannot establish feature activation.
cargo check --locked --workspace --all-targets

# Exercise both opt-in modes from every workspace package boundary. Downstream
# tracing features forward to all direct Tellers dependencies.
for package in \
  tellers-mtproto \
  tellers-tl-parser \
  tellers-tl-schema \
  tellers-tl-rust \
  tellers-tl-json \
  tellers-xtask
do
  cargo check --locked -p "$package" --all-targets --features tracing
  cargo check --locked -p "$package" --all-targets --features dangerous-unredacted-logging
done

# These tests inspect events with a scoped test subscriber: safe mode must not
# contain controlled source/byte sentinels, while dangerous mode must contain
# them without changing parser or codec behavior.
for package in tellers-tl-parser tellers-mtproto; do
  cargo test --locked -p "$package" --features tracing --test tracing_capture
  cargo test --locked -p "$package" --features dangerous-unredacted-logging --test tracing_capture
done
