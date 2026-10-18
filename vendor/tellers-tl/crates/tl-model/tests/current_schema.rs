//! Integration tests against the checked-in Telegram schemas.
// SPDX-License-Identifier: MPL-2.0
#[test]
fn current_upstream_schemas_parse_and_validate() {
    for (name, source, constructors, functions) in [
        (
            "api.tl",
            include_str!("../../../schemas/upstream/api.tl"),
            1546,
            757,
        ),
        (
            "mtproto.tl",
            include_str!("../../../schemas/upstream/mtproto.tl"),
            45,
            9,
        ),
    ] {
        let parsed = tellers_tl_schema::parse(
            source,
            tellers_tl_schema::Source {
                name: name.into(),
                url: None,
            },
        )
        .unwrap_or_else(|error| panic!("{name}: {error}"));
        assert_eq!(
            parsed.constructors.len(),
            constructors,
            "constructor coverage changed"
        );
        assert_eq!(
            parsed.functions.len(),
            functions,
            "function coverage changed"
        );
        let json = serde_json::to_value(&parsed).expect("model must serialize");
        assert!(
            !json.to_string().contains("\"Tuple\""),
            "repetition groups must not invent a Tuple type"
        );
    }
}

#[test]
fn current_secret_chat_schema_preserves_documented_layer_history() {
    let parsed = tellers_tl_schema::parse(
        include_str!("../../../schemas/secret-chat/layers/216.tl"),
        tellers_tl_schema::Source {
            name: "end-to-end.tl".into(),
            url: Some("https://core.telegram.org/schema/end-to-end".into()),
        },
    )
    .unwrap();

    assert_eq!(parsed.layer, Some(216));
    assert_eq!(parsed.constructors.len(), 76);
    assert!(
        parsed
            .constructors
            .iter()
            .all(|constructor| constructor.layer.introduced.is_some())
    );
}
