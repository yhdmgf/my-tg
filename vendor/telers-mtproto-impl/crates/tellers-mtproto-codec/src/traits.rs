//! Schema-neutral bare and boxed codec traits.

use crate::{Decoder, Encoder, Error};

/// A value that can be emitted in bare TL form.
pub trait TlEncode {
    /// Append the bare representation to `encoder`.
    fn encode(&self, encoder: &mut Encoder) -> Result<(), Error>;
}

/// A value that can be decoded from bare TL form.
pub trait TlDecode: Sized {
    /// Read one bare value.
    fn decode(decoder: &mut Decoder<'_>) -> Result<Self, Error>;
}

/// Constructor metadata for a boxed TL value.
pub trait Boxed: TlEncode {
    /// Constructor number written before the bare body.
    const CONSTRUCTOR_ID: u32;

    /// Encode constructor number followed by the bare body.
    fn encode_boxed(&self, encoder: &mut Encoder) -> Result<(), Error> {
        encoder.write_u32(Self::CONSTRUCTOR_ID);
        self.encode(encoder)
    }
}

/// Decode a polymorphic boxed TL result without a hardcoded global table.
pub trait BoxedDecode: Sized {
    /// Dispatch a constructor number using the generated type's variants.
    fn decode_boxed(decoder: &mut Decoder<'_>) -> Result<Self, Error>;
}
