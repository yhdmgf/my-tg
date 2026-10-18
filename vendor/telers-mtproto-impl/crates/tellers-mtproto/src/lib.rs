//! Generated, feature-gated Telegram API and MTProto declarations.
//!
//! API layers are isolated modules. Enable one layer for a pinned client,
//! or `all-layers` for compatibility and migration tooling.

#![allow(clippy::large_enum_variant, clippy::doc_markdown)]
#![forbid(unsafe_code)]

/// Canonical bounded TL wire codec used by every generated declaration.
pub mod codec;

pub use codec::{
    Boxed, BoxedDecode, Decoder, Encoder, Error as CodecError, Limits, RpcMethod, TlDecode,
    TlEncode,
};

/// Shared core MTProto transport and authorization declarations.
#[rustfmt::skip]
#[allow(missing_docs, clippy::all, clippy::pedantic)]
pub mod transport;

/// API layers materialized in this build of the generated crate.
pub const GENERATED_API_LAYERS: &[u32] = &[223, 229];

/// Current officially published API layer.
pub const LATEST_API_LAYER: u32 = 229;

/// Telegram API schema layer 223.
#[cfg(feature = "layer-223")]
pub mod layer_223 {
    /// Public Telegram API constructors and methods.
    #[rustfmt::skip]
    #[allow(missing_docs, clippy::all, clippy::pedantic)]
    pub mod api;
}

/// Telegram API schema layer 229.
#[cfg(feature = "layer-229")]
pub mod layer_229 {
    /// Public Telegram API constructors and methods.
    #[rustfmt::skip]
    #[allow(missing_docs, clippy::all, clippy::pedantic)]
    pub mod api;
}

/// The current officially published API layer.
#[cfg(feature = "layer-229")]
pub use layer_229 as latest;

/// Independently versioned end-to-end payloads for Secret Chats.
#[cfg(feature = "secret-chat")]
pub mod secret_chat;
