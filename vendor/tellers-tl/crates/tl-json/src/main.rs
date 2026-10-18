//! Command-line JSON exporter.

use clap::Parser;
use schemars::JsonSchema;
use serde::Serialize;
use std::{
    fs,
    io::{self, Read},
    path::PathBuf,
};
use tellers_tl_schema::{Schema, Source};

#[derive(Debug, Parser)]
#[command(version, about)]
struct Args {
    /// TL input path; omit or use `-` for standard input.
    input: Option<PathBuf>,
    /// Write here instead of standard output.
    #[arg(short, long)]
    output: Option<PathBuf>,
    /// Canonical upstream URL recorded as provenance.
    #[arg(long)]
    source_url: Option<String>,
    /// Emit compact JSON instead of pretty-printed JSON.
    #[arg(long)]
    compact: bool,
}

#[derive(Serialize, JsonSchema)]
struct Document {
    /// JSON Schema describing `schema`.
    json_schema: schemars::Schema,
    /// Parsed and validated TL schema.
    schema: Schema,
}

fn main() {
    if let Err(error) = run() {
        eprintln!("error: {error}");
        std::process::exit(1);
    }
}

fn run() -> Result<(), Box<dyn std::error::Error>> {
    let args = Args::parse();
    let (text, name) = match args.input.as_deref() {
        None => {
            let mut s = String::new();
            io::stdin().read_to_string(&mut s)?;
            (s, "<stdin>".to_owned())
        }
        Some(p) if p.as_os_str() == "-" => {
            let mut s = String::new();
            io::stdin().read_to_string(&mut s)?;
            (s, "<stdin>".to_owned())
        }
        Some(path) => (fs::read_to_string(path)?, path.display().to_string()),
    };
    let schema = tellers_tl_schema::parse(
        &text,
        Source {
            name,
            url: args.source_url,
        },
    )?;
    let document = Document {
        json_schema: schemars::schema_for!(Schema),
        schema,
    };
    let bytes = if args.compact {
        serde_json::to_vec(&document)?
    } else {
        serde_json::to_vec_pretty(&document)?
    };
    if let Some(path) = args.output {
        fs::write(path, bytes)?;
    } else {
        use std::io::Write;
        io::stdout().write_all(&bytes)?;
        io::stdout().write_all(b"\n")?;
    }
    Ok(())
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn document_contains_model_and_machine_readable_contract() {
        let schema = tellers_tl_schema::parse(
            "item value:int = Item;",
            Source {
                name: "test.tl".into(),
                url: None,
            },
        )
        .unwrap();
        let document = Document {
            json_schema: schemars::schema_for!(Schema),
            schema,
        };
        let json = serde_json::to_value(document).unwrap();
        assert!(json.get("json_schema").is_some());
        assert_eq!(json["schema"]["constructors"][0]["name"], "item");
        assert_eq!(json["schema"]["constructors"][0]["id_explicit"], false);
    }
}
