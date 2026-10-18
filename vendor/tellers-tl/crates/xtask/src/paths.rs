//! Workspace path discovery independent of the caller's current directory.

use std::path::{Path, PathBuf};

pub(crate) fn root() -> PathBuf {
    Path::new(env!("CARGO_MANIFEST_DIR"))
        .join("../..")
        .canonicalize()
        .expect("workspace root must exist")
}

pub(crate) fn schemas() -> PathBuf {
    root().join("schemas")
}

pub(crate) fn generated_crate() -> PathBuf {
    root().join("crates/mtproto-types")
}
