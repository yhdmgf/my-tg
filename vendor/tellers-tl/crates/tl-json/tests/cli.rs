//! Black-box command-line contract tests.

use assert_cmd::Command;
use predicates::prelude::*;
use std::fs;

#[test]
fn exports_self_describing_json_from_a_file() {
    let directory = tempfile::tempdir().unwrap();
    let input = directory.path().join("schema.tl");
    fs::write(&input, "// LAYER 7\nitem#01020304 value:int = Item;").unwrap();

    Command::cargo_bin("tellers-tl-json")
        .unwrap()
        .arg(&input)
        .arg("--compact")
        .assert()
        .success()
        .stdout(predicate::str::contains("\"json_schema\""))
        .stdout(predicate::str::contains("\"layer\":7"))
        .stdout(predicate::str::contains("\"name\":\"item\""));
}

#[test]
fn reports_invalid_input_without_panicking() {
    Command::cargo_bin("tellers-tl-json")
        .unwrap()
        .write_stdin("not a valid declaration")
        .assert()
        .failure()
        .stderr(predicate::str::starts_with("error:"));
}
