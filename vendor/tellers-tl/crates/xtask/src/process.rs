//! Checked external process execution.

use eyre::{Context, Result, bail};
use tokio::process::Command;

pub(crate) async fn output(program: &str, arguments: &[&str]) -> Result<String> {
    let result = Command::new(program)
        .args(arguments)
        .output()
        .await
        .with_context(|| format!("failed to start `{program}`"))?;
    if !result.status.success() {
        bail!(
            "`{program}` failed: {}",
            String::from_utf8_lossy(&result.stderr).trim()
        );
    }
    String::from_utf8(result.stdout).context("process output was not UTF-8")
}
