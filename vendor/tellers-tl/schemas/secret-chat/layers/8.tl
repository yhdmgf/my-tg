// LAYER 8
// @introduced 8
// @description Contents of an encrypted message.
// @param entities Message entities for styled text (parameter added in layer 45)
// @param flags Flags, see TL conditional fields (added in layer 45)
// @param grouped_id Random group ID, assigned by the author of message. Multiple encrypted messages with a photo attached and with the same group ID indicate an album or grouped media (parameter added in layer 45)
// @param media Media content
// @param message Message text
// @param random_bytes Random bytes, removed in layer 17.
// @param random_id Random message ID, assigned by the author of message. Must be equal to the ID passed to sending method. See here » for more info on random ID deduplication and updateMessageID mapping.
// @param reply_to_random_id Random message ID of the message this message replies to (parameter added in layer 45)
// @param ttl Message lifetime. Has higher priority than decryptedMessageActionSetMessageTTL . Parameter added in Layer 17.
// @param via_bot_name Specifies the ID of the inline bot that generated the message (parameter added in layer 45)
// @see https://core.telegram.org/api/entities
// @see https://core.telegram.org/api/files#albums-grouped-media
// @see https://core.telegram.org/api/updates#updatemessageid-updates
// @see https://core.telegram.org/constructor/decryptedMessageActionSetMessageTTL
// @see https://core.telegram.org/mtproto/TL-combinators#conditional-fields
// @see https://core.telegram.org/type/%23
// @see https://core.telegram.org/type/DecryptedMessageMedia
// @see https://core.telegram.org/type/MessageEntity
// @see https://core.telegram.org/type/Vector%20t
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/long
// @see https://core.telegram.org/type/string
decryptedMessage#1f814f1f random_id:long random_bytes:bytes message:string media:DecryptedMessageMedia = DecryptedMessage;
// @introduced 8
// @description Contents of an encrypted service message.
// @param action Action relevant to the service message
// @param random_bytes Random bytes, removed in Layer 17.
// @param random_id Random message ID, assigned by the message author. Must be equal to the ID passed to the sending method. See here » for more info on random ID deduplication and updateMessageID mapping.
// @see https://core.telegram.org/api/updates#updatemessageid-updates
// @see https://core.telegram.org/type/DecryptedMessageAction
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/long
decryptedMessageService#aa48327d random_id:long random_bytes:bytes action:DecryptedMessageAction = DecryptedMessage;
// @introduced 8
// @description Empty constructor, no media content.
decryptedMessageMediaEmpty#89f5c4a = DecryptedMessageMedia;
// @introduced 8
// @description Photo attached to an encrypted message.
// @param caption Caption
// @param h Photo height
// @param iv Initialization vector
// @param key Key to decrypt an attached file with a full version
// @param size Size of the photo in bytes
// @param thumb Content of thumbnail file (JPEGfile, quality 55, set in a square 90x90)
// @param thumb_h Thumbnail height
// @param thumb_w Thumbnail width
// @param w Photo width
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
decryptedMessageMediaPhoto#32798a8c thumb:bytes thumb_w:int thumb_h:int w:int h:int size:int key:bytes iv:bytes = DecryptedMessageMedia;
// @introduced 8
// @description Video attached to an encrypted message.
// @param caption Caption
// @param duration Duration of video in seconds
// @param h Image height
// @param iv Initialization vector
// @param key Key to decrypt the attached video file
// @param mime_type MIME-type of the video file Parameter added in Layer 17.
// @param size File size
// @param thumb Content of thumbnail file (JPEG file, quality 55, set in a square 90x90)
// @param thumb_h Thumbnail height
// @param thumb_w Thumbnail width
// @param w Image width
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
decryptedMessageMediaVideo#4cee6ef3 thumb:bytes thumb_w:int thumb_h:int duration:int w:int h:int size:int key:bytes iv:bytes = DecryptedMessageMedia;
// @introduced 8
// @description GeoPoint attached to an encrypted message.
// @param lat Latitude of point
// @param long Longitude of point
// @see https://core.telegram.org/type/double
decryptedMessageMediaGeoPoint#35480a59 lat:double long:double = DecryptedMessageMedia;
// @introduced 8
// @description Contact attached to an encrypted message.
// @param first_name Contact's first name
// @param last_name Contact's last name
// @param phone_number Phone number
// @param user_id Telegram User ID of signed-up contact
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
decryptedMessageMediaContact#588a0a97 phone_number:string first_name:string last_name:string user_id:int = DecryptedMessageMedia;
// @introduced 8
// @description Setting of a message lifetime after reading.
// @param ttl_seconds Lifetime in seconds
// @see https://core.telegram.org/type/int
decryptedMessageActionSetMessageTTL#a1733aec ttl_seconds:int = DecryptedMessageAction;
// @introduced 8
// @description Document attached to a message in a secret chat.
// @param attributes Document attributes for media types
// @param caption Caption
// @param file_name File name, moved to attributes in Layer 45.
// @param iv Initialization
// @param key Key to decrypt the attached document file
// @param mime_type File MIME-type
// @param size Document size ( int on layer <143, long on layer >=143)
// @param thumb Thumbnail-file contents (JPEG-file, quality 55, set in a 90x90 square)
// @param thumb_h Thumbnail height
// @param thumb_w Thumbnail width
// @see https://core.telegram.org/type/DocumentAttribute
// @see https://core.telegram.org/type/Vector%20t
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/long
// @see https://core.telegram.org/type/string
decryptedMessageMediaDocument#b095434b thumb:bytes thumb_w:int thumb_h:int file_name:string mime_type:string size:int key:bytes iv:bytes = DecryptedMessageMedia;
// @introduced 8
// @description Audio file attached to a secret chat message.
// @param duration Audio duration in seconds
// @param iv Initialization vector
// @param key Key to decrypt the attached media file
// @param mime_type MIME-type of the audio file Parameter added in Layer 13.
// @param size File size
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
decryptedMessageMediaAudio#6080758f duration:int size:int key:bytes iv:bytes = DecryptedMessageMedia;
// @introduced 8
// @description Messages marked as read.
// @param random_ids List of message IDs
// @see https://core.telegram.org/type/Vector%20t
// @see https://core.telegram.org/type/long
decryptedMessageActionReadMessages#c4f40be random_ids:Vector<long> = DecryptedMessageAction;
// @introduced 8
// @description Deleted messages.
// @param random_ids List of deleted message IDs
// @see https://core.telegram.org/type/Vector%20t
// @see https://core.telegram.org/type/long
decryptedMessageActionDeleteMessages#65614304 random_ids:Vector<long> = DecryptedMessageAction;
// @introduced 8
// @description A screenshot was taken.
// @param random_ids List of affected message ids (that appeared on the screenshot)
// @see https://core.telegram.org/type/Vector%20t
// @see https://core.telegram.org/type/long
decryptedMessageActionScreenshotMessages#8ac1f475 random_ids:Vector<long> = DecryptedMessageAction;
// @introduced 8
// @description The entire message history has been deleted.
decryptedMessageActionFlushHistory#6719e45c = DecryptedMessageAction;
