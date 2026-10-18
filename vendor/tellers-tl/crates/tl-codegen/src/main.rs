//! Command-line frontend for deterministic Rust generation.

use std::{env, fs, process};
use tellers_tl_rust::{Options, generate};

fn main() {
    if let Err(error) = run() {
        eprintln!("error: {error}");
        process::exit(1);
    }
}

fn run() -> Result<(), Box<dyn std::error::Error>> {
    let mut arguments = env::args_os().skip(1);
    let input = arguments
        .next()
        .ok_or("usage: tellers-tl-rust INPUT.tl OUTPUT.rs [MODULE-DOC] [PRELUDE]")?;
    let output = arguments
        .next()
        .ok_or("usage: tellers-tl-rust INPUT.tl OUTPUT.rs [MODULE-DOC] [PRELUDE]")?;
    let module_documentation = arguments.next().map_or_else(
        || "Generated Telegram TL declarations.".to_owned(),
        |value| value.to_string_lossy().into_owned(),
    );
    let prelude = arguments
        .next()
        .map(|value| value.to_string_lossy().into_owned())
        .into_iter()
        .collect();
    if arguments.next().is_some() {
        return Err("too many arguments".into());
    }
    let text = fs::read_to_string(&input)?;
    let schema = tellers_tl_schema::parse(
        &text,
        tellers_tl_schema::Source {
            name: input.to_string_lossy().into_owned(),
            url: None,
        },
    )?;
    fs::write(
        output,
        generate(
            &schema,
            &Options {
                module_documentation,
                prelude,
                ..Options::default()
            },
        )?,
    )?;
    Ok(())
}
