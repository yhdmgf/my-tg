//! Canonical, resource-bounded Telegram TL wire encoding and decoding.
//!
//! The codec is colocated with generated declarations so every published type
//! uses one trait set and one constructor-dispatch implementation. [`Limits`]
//! bounds untrusted allocations, recursion, vector lengths, and aggregate input.

#![allow(
    clippy::missing_errors_doc,
    clippy::missing_panics_doc,
    clippy::must_use_candidate
)]

mod decoder;
mod encoder;
mod error;
mod traits;
mod value;

pub use decoder::*;
pub use encoder::*;
pub use error::*;
pub use traits::*;
pub use value::*;

#[cfg(test)]
mod tests;
