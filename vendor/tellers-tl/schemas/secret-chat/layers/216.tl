// LAYER 216
// @introduced 73
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
decryptedMessage#91cc4674 flags:# no_webpage:flags.1?true silent:flags.5?true random_id:long ttl:int message:string media:flags.9?DecryptedMessageMedia entities:flags.7?Vector<MessageEntity> via_bot_name:flags.11?string reply_to_random_id:flags.3?long grouped_id:flags.17?long = DecryptedMessage;
// @introduced 17
// @description Contents of an encrypted service message.
// @param action Action relevant to the service message
// @param random_bytes Random bytes, removed in Layer 17.
// @param random_id Random message ID, assigned by the message author. Must be equal to the ID passed to the sending method. See here » for more info on random ID deduplication and updateMessageID mapping.
// @see https://core.telegram.org/api/updates#updatemessageid-updates
// @see https://core.telegram.org/type/DecryptedMessageAction
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/long
decryptedMessageService#73164160 random_id:long action:DecryptedMessageAction = DecryptedMessage;
// @introduced 8
// @description Empty constructor, no media content.
decryptedMessageMediaEmpty#89f5c4a = DecryptedMessageMedia;
// @introduced 45
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
decryptedMessageMediaPhoto#f1fa8d78 thumb:bytes thumb_w:int thumb_h:int w:int h:int size:int key:bytes iv:bytes caption:string = DecryptedMessageMedia;
// @introduced 45
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
decryptedMessageMediaVideo#970c8c0e thumb:bytes thumb_w:int thumb_h:int duration:int mime_type:string w:int h:int size:int key:bytes iv:bytes caption:string = DecryptedMessageMedia;
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
// @introduced 143
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
decryptedMessageMediaDocument#6abd9782 thumb:bytes thumb_w:int thumb_h:int mime_type:string size:long key:bytes iv:bytes attributes:Vector<DocumentAttribute> caption:string = DecryptedMessageMedia;
// @introduced 17
// @description Audio file attached to a secret chat message.
// @param duration Audio duration in seconds
// @param iv Initialization vector
// @param key Key to decrypt the attached media file
// @param mime_type MIME-type of the audio file Parameter added in Layer 13.
// @param size File size
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
decryptedMessageMediaAudio#57e0a9cb duration:int mime_type:string size:int key:bytes iv:bytes = DecryptedMessageMedia;
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
// @introduced 17
// @description Sets the layer number for the contents of an encrypted message.
// @param in_seq_no 2x the number of messages in the sender's inbox (including deleted and service messages), incremented by 1 if current user was not the chat creator Parameter added in Layer 17.
// @param layer Layer number. Mimimal value - 17 (the layer in which the constructor was added).
// @param message The content of message itself
// @param out_seq_no 2x the number of messages in the recipient's inbox (including deleted and service messages), incremented by 1 if current user was the chat creator Parameter added in Layer 17.
// @param random_bytes Set of random bytes to prevent content recognition in short encrypted messages. Clients are required to check that there are at least 15 random bytes included in each message. Messages with less than 15 random bytes must be ignored. Parameter moved here from decryptedMessage in Layer 17.
// @see https://core.telegram.org/constructor/decryptedMessage
// @see https://core.telegram.org/type/DecryptedMessage
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/int
decryptedMessageLayer#1be31789 random_bytes:bytes layer:int in_seq_no:int out_seq_no:int message:DecryptedMessage = DecryptedMessageLayer;
// @introduced 17
// @description User is typing.
sendMessageTypingAction#16bf744e = SendMessageAction;
// @introduced 17
// @description Invalidate all previous action updates. E.g. when user deletes entered text or aborts a video upload.
sendMessageCancelAction#fd5ec8f5 = SendMessageAction;
// @introduced 17
// @description User is recording a video.
sendMessageRecordVideoAction#a187d66f = SendMessageAction;
// @introduced 17
// @description User is uploading a video.
// @param progress Progress percentage
// @see https://core.telegram.org/type/int
sendMessageUploadVideoAction#92042ff7 = SendMessageAction;
// @introduced 17
// @description User is recording a voice message.
sendMessageRecordAudioAction#d52f73f7 = SendMessageAction;
// @introduced 17
// @description User is uploading a voice message.
// @param progress Progress percentage
// @see https://core.telegram.org/type/int
sendMessageUploadAudioAction#e6ac8a6f = SendMessageAction;
// @introduced 17
// @description User is uploading a photo.
// @param progress Progress percentage
// @see https://core.telegram.org/type/int
sendMessageUploadPhotoAction#990a3c1a = SendMessageAction;
// @introduced 17
// @description User is uploading a file.
// @param progress Progress percentage
// @see https://core.telegram.org/type/int
sendMessageUploadDocumentAction#8faee98e = SendMessageAction;
// @introduced 17
// @description User is selecting a location to share.
sendMessageGeoLocationAction#176f8ba1 = SendMessageAction;
// @introduced 17
// @description User is selecting a contact to share.
sendMessageChooseContactAction#628cbc6f = SendMessageAction;
// @introduced 17
// @description Request for the other party in a Secret Chat to automatically resend a contiguous range of previously sent messages, as explained in Sequence number is Secret Chats .
// @param end_seq_no out_seq_no of the last message to be resent, with same parity.
// @param start_seq_no out_seq_no of the first message to be resent, with correct parity
// @see https://core.telegram.org/api/end-to-end/seq_no
// @see https://core.telegram.org/type/int
decryptedMessageActionResend#511110b0 start_seq_no:int end_seq_no:int = DecryptedMessageAction;
// @introduced 17
// @description A notification stating the API layer that is used by the client. You should use your current layer and take notice of the layer used on the other side of a conversation when sending messages.
// @param layer Layer number, must be 17 or higher (this constructor was introduced in Layer 17.
// @see https://core.telegram.org/type/int
decryptedMessageActionNotifyLayer#f3048883 layer:int = DecryptedMessageAction;
// @introduced 17
// @description User is preparing a message: typing, recording, uploading, etc.
// @param action Type of action
// @see https://core.telegram.org/type/SendMessageAction
decryptedMessageActionTyping#ccb27641 action:SendMessageAction = DecryptedMessageAction;
// @introduced 20
// @description Request rekeying, see rekeying process
// @param exchange_id Exchange ID
// @param g_a g_a, see rekeying process
// @see https://core.telegram.org/api/end-to-end/pfs
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/long
decryptedMessageActionRequestKey#f3c9611b exchange_id:long g_a:bytes = DecryptedMessageAction;
// @introduced 20
// @description Accept new key
// @param exchange_id Exchange ID
// @param g_b B parameter, see rekeying process
// @param key_fingerprint Key fingerprint, see rekeying process
// @see https://core.telegram.org/api/end-to-end/pfs
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/long
decryptedMessageActionAcceptKey#6fe1735b exchange_id:long g_b:bytes key_fingerprint:long = DecryptedMessageAction;
// @introduced 20
// @description Abort rekeying
// @param exchange_id Exchange ID
// @see https://core.telegram.org/type/long
decryptedMessageActionAbortKey#dd05ec6b exchange_id:long = DecryptedMessageAction;
// @introduced 20
// @description Commit new key, see rekeying process
// @param exchange_id Exchange ID, see rekeying process
// @param key_fingerprint Key fingerprint, see rekeying process
// @see https://core.telegram.org/api/end-to-end/pfs
// @see https://core.telegram.org/type/long
decryptedMessageActionCommitKey#ec2e0b9b exchange_id:long key_fingerprint:long = DecryptedMessageAction;
// @introduced 20
// @description NOOP action
decryptedMessageActionNoop#a82fdd63 = DecryptedMessageAction;
// @introduced 23
// @description Defines the width and height of an image uploaded as document
// @param h Height of image
// @param w Width of image
// @see https://core.telegram.org/type/int
documentAttributeImageSize#6c37c15c w:int h:int = DocumentAttribute;
// @introduced 23
// @description Defines an animated GIF
documentAttributeAnimated#11b58939 = DocumentAttribute;
// @introduced 45
// @description Defines a sticker
// @param alt Alternative emoji representation of sticker
// @param flags Flags, see TL conditional fields
// @param mask Whether this is a mask sticker
// @param mask_coords Mask coordinates (if this is a mask sticker, attached to a photo)
// @param stickerset Associated stickerset
// @see https://core.telegram.org/constructor/true
// @see https://core.telegram.org/mtproto/TL-combinators#conditional-fields
// @see https://core.telegram.org/type/%23
// @see https://core.telegram.org/type/InputStickerSet
// @see https://core.telegram.org/type/MaskCoords
// @see https://core.telegram.org/type/string
documentAttributeSticker#3a556302 alt:string stickerset:InputStickerSet = DocumentAttribute;
// @introduced 66
// @description Defines a video
// @param duration Duration in seconds
// @param flags Flags, see TL conditional fields
// @param h Video height
// @param nosound Whether the specified document is a video file with no audio tracks
// @param preload_prefix_size Number of bytes to preload when preloading videos (particularly video stories ).
// @param round_message Whether this is a round video
// @param supports_streaming Whether the video supports streaming
// @param video_codec Codec used for the video, i.e. "h264", "h265", or "av1"
// @param video_start_ts Floating point UNIX timestamp in seconds, indicating the frame of the video that should be used as static preview and thumbnail.
// @param w Video width
// @see https://core.telegram.org/api/stories
// @see https://core.telegram.org/constructor/true
// @see https://core.telegram.org/mtproto/TL-combinators#conditional-fields
// @see https://core.telegram.org/type/%23
// @see https://core.telegram.org/type/double
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
documentAttributeVideo#ef02ce6 flags:# round_message:flags.0?true duration:int w:int h:int = DocumentAttribute;
// @introduced 46
// @description Represents an audio file
// @param duration Duration in seconds
// @param flags Flags, see TL conditional fields
// @param performer Performer
// @param title Name of song
// @param voice Whether this is a voice message
// @param waveform Waveform: consists in a series of bitpacked 5-bit values. Example implementation: android .
// @see https://core.telegram.org/constructor/true
// @see https://core.telegram.org/mtproto/TL-combinators#conditional-fields
// @see https://core.telegram.org/type/%23
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
// @see https://github.com/DrKLO/Telegram/blob/4d7a3a40c88ce28a211aa2814e9569389fc40567/TMessagesProj/jni/audio.c#L562
documentAttributeAudio#9852f9c6 flags:# voice:flags.10?true duration:int title:flags.0?string performer:flags.1?string waveform:flags.2?bytes = DocumentAttribute;
// @introduced 23
// @description A simple document with a file name
// @param file_name The file name
// @see https://core.telegram.org/type/string
documentAttributeFilename#15590068 file_name:string = DocumentAttribute;
// @introduced 23
// @description Empty constructor. Image with this thumbnail is unavailable.
// @param type PhotoSize.type value »
// @see https://core.telegram.org/api/files#photosizetype-values
// @see https://core.telegram.org/type/string
photoSizeEmpty#e17e23c type:string = PhotoSize;
// @introduced 23
// @description Image description.
// @param h Image height
// @param size File size
// @param type PhotoSize.type value »
// @param w Image width
// @see https://core.telegram.org/api/files#photosizetype-values
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
photoSize#77bfb61b type:string location:FileLocation w:int h:int size:int = PhotoSize;
// @introduced 23
// @description Description of an image and its content.
// @param bytes Binary data, file content
// @param h Image height
// @param type PhotoSize.type value »
// @param w Image width
// @see https://core.telegram.org/api/files#photosizetype-values
// @see https://core.telegram.org/type/bytes
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
photoCachedSize#e9a734fa type:string location:FileLocation w:int h:int bytes:bytes = PhotoSize;
// @introduced 23
// @description File is currently unavailable.
// @param local_id Local ID
// @param secret Secret
// @param volume_id Volume ID
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/long
fileLocationUnavailable#7c596b46 volume_id:long local_id:int secret:long = FileLocation;
// @introduced 23
// @description File location.
// @param dc_id DC ID
// @param local_id Local ID
// @param secret Secret
// @param volume_id Volume ID
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/long
fileLocation#53d69076 dc_id:int volume_id:long local_id:int secret:long = FileLocation;
// @introduced 23
// @description Non-e2e documented forwarded from non-secret chat
// @param access_hash access hash
// @param attributes Attributes for media types
// @param date Date
// @param dc_id DC ID
// @param id Document ID
// @param mime_type Mime type
// @param size Size
// @param thumb Thumbnail
// @see https://core.telegram.org/type/DocumentAttribute
// @see https://core.telegram.org/type/PhotoSize
// @see https://core.telegram.org/type/Vector%20t
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/long
// @see https://core.telegram.org/type/string
decryptedMessageMediaExternalDocument#fa95b0dd id:long access_hash:long date:int mime_type:string size:int thumb:PhotoSize dc_id:int attributes:Vector<DocumentAttribute> = DecryptedMessageMedia;
// @introduced 45
// @description Unknown message entity
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
messageEntityUnknown#bb92ba95 offset:int length:int = MessageEntity;
// @introduced 45
// @description Message entity mentioning a user by @username ; messageEntityMentionName can also be used to mention users by their ID.
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/api/mentions
// @see https://core.telegram.org/constructor/messageEntityMentionName
// @see https://core.telegram.org/type/int
messageEntityMention#fa04579d offset:int length:int = MessageEntity;
// @introduced 45
// @description #hashtag message entity
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
messageEntityHashtag#6f635b0d offset:int length:int = MessageEntity;
// @introduced 45
// @description Message entity representing a bot /command
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
messageEntityBotCommand#6cef8ac7 offset:int length:int = MessageEntity;
// @introduced 45
// @description Message entity representing an in-text url: https://google.com ; for text urls , use messageEntityTextUrl .
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/constructor/messageEntityTextUrl
// @see https://core.telegram.org/type/int
// @see https://google.com
messageEntityUrl#6ed02538 offset:int length:int = MessageEntity;
// @introduced 45
// @description Message entity representing an email@example.com .
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
messageEntityEmail#64e475c2 offset:int length:int = MessageEntity;
// @introduced 45
// @description Message entity representing bold text .
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
messageEntityBold#bd610bc9 offset:int length:int = MessageEntity;
// @introduced 45
// @description Message entity representing italic text .
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
messageEntityItalic#826f8b60 offset:int length:int = MessageEntity;
// @introduced 45
// @description Message entity representing a codeblock .
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
messageEntityCode#28a20571 offset:int length:int = MessageEntity;
// @introduced 45
// @description Message entity representing a preformatted codeblock , allowing the user to specify a programming language for the codeblock.
// @param language Programming language of the code
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
messageEntityPre#73924be0 offset:int length:int language:string = MessageEntity;
// @introduced 45
// @description Message entity representing a text url : for in-text urls like https://google.com use messageEntityUrl .
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @param url The actual URL
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/constructor/messageEntityUrl
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/string
// @see https://google.com
messageEntityTextUrl#76a6d327 offset:int length:int url:string = MessageEntity;
// @introduced 45
// @description Stickerset by short name, from a stickerset deep link »
// @param short_name Short name from a stickerset deep link »
// @see https://core.telegram.org/api/links#stickerset-links
// @see https://core.telegram.org/type/string
inputStickerSetShortName#861cc8a0 short_name:string = InputStickerSet;
// @introduced 45
// @description Empty constructor
inputStickerSetEmpty#ffb62b95 = InputStickerSet;
// @introduced 45
// @description Venue
// @param address Address
// @param lat Latitude of venue
// @param long Longitude of venue
// @param provider Venue provider: currently only "foursquare" and "gplaces" (Google Places) need to be supported
// @param title Venue name
// @param venue_id Venue ID in the provider's database
// @see https://core.telegram.org/type/double
// @see https://core.telegram.org/type/string
decryptedMessageMediaVenue#8a0df56f lat:double long:double title:string address:string provider:string venue_id:string = DecryptedMessageMedia;
// @introduced 45
// @description Webpage preview
// @param url URL of webpage
// @see https://core.telegram.org/type/string
decryptedMessageMediaWebPage#e50511d8 url:string = DecryptedMessageMedia;
// @introduced 66
// @description User is recording a round video to share
sendMessageRecordRoundAction#88f27fbc = SendMessageAction;
// @introduced 66
// @description User is uploading a round video
// @param progress Progress percentage
// @see https://core.telegram.org/type/int
sendMessageUploadRoundAction#bb718624 = SendMessageAction;
// @introduced 101
// @description Message entity representing underlined text.
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
messageEntityUnderline#9c4e7e8b offset:int length:int = MessageEntity;
// @introduced 101
// @description Message entity representing strikethrough text.
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
messageEntityStrike#bf0693d4 offset:int length:int = MessageEntity;
// @introduced 101
// @description Message entity representing a block quote.
// @param collapsed Whether the quote is collapsed by default.
// @param flags Flags, see TL conditional fields
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/constructor/true
// @see https://core.telegram.org/mtproto/TL-combinators#conditional-fields
// @see https://core.telegram.org/type/%23
// @see https://core.telegram.org/type/int
messageEntityBlockquote#20df5d0 offset:int length:int = MessageEntity;
// @introduced 144
// @description Message entity representing a spoiler
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/type/int
messageEntitySpoiler#32ca960f offset:int length:int = MessageEntity;
// @introduced 144
// @description Represents a custom emoji. Note that this entity must wrap exactly one regular emoji (the one contained in documentAttributeCustomEmoji . alt ) in the related text, otherwise the server will ignore it.
// @param document_id Document ID of the custom emoji , use messages.getCustomEmojiDocuments to fetch the emoji animation and the actual emoji it represents.
// @param length Length of message entity within message (in UTF-16 code units )
// @param offset Offset of message entity within message (in UTF-16 code units )
// @see https://core.telegram.org/api/custom-emoji
// @see https://core.telegram.org/api/entities#entity-length
// @see https://core.telegram.org/constructor/documentAttributeCustomEmoji
// @see https://core.telegram.org/method/messages.getCustomEmojiDocuments
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/long
messageEntityCustomEmoji#c8cf05f8 offset:int length:int document_id:long = MessageEntity;
// @introduced 216
// @description JSON key: value pair
// @param key Key
// @param value Value
// @see https://core.telegram.org/type/JSONValue
// @see https://core.telegram.org/type/string
jsonObjectValue#c0de1bd9 key:string value:JSONValue = JSONObjectValue;
// @introduced 216
// @description null JSON value
jsonNull#3f6d7b68 = JSONValue;
// @introduced 216
// @description JSON boolean value
// @param value Value
// @see https://core.telegram.org/type/Bool
jsonBool#c7345e6a value:Bool = JSONValue;
// @introduced 216
// @description JSON numeric value
// @param value Value
// @see https://core.telegram.org/type/double
jsonNumber#2be0dfa4 value:double = JSONValue;
// @introduced 216
// @description JSON string
// @param value Value
// @see https://core.telegram.org/type/string
jsonString#b71e767a value:string = JSONValue;
// @introduced 216
// @description JSON array
// @param value JSON values
// @see https://core.telegram.org/type/JSONValue
// @see https://core.telegram.org/type/Vector%20t
jsonArray#f7444763 value:Vector<JSONValue> = JSONValue;
// @introduced 216
// @description JSON object value
// @param value Values
// @see https://core.telegram.org/type/JSONObjectValue
// @see https://core.telegram.org/type/Vector%20t
jsonObject#99c1d49d value:Vector<JSONObjectValue> = JSONValue;
// @introduced 216
// @description Styled text with message entities
// @param entities Message entities for styled text
// @param text Text
// @see https://core.telegram.org/api/entities
// @see https://core.telegram.org/type/MessageEntity
// @see https://core.telegram.org/type/Vector%20t
// @see https://core.telegram.org/type/string
textWithEntities#751f3146 text:string entities:Vector<MessageEntity> = TextWithEntities;
// @introduced 216
// @description Represents an in-call message » , emoji reaction, paid live story comment or standalone paid live story donation.
// @param date Message date
// @param flags Flags, see TL conditional fields
// @param from_admin Whether the message was sent by a group call admin
// @param from_id Displayed message author
// @param id Message ID
// @param message Message text or emoji reaction; empty for standalone paid live story donations. Must be at most group_call_message_length_limit » UTF-8 characters long. For paid chats, use stars_groupcall_message_limits » to source limits according to the passed value of paid_message_stars , instead.
// @param paid_message_stars Number of Telegram Stars donated with the message or standalone donation
// @see https://core.telegram.org/api/config#group-call-message-length-limit
// @see https://core.telegram.org/api/config#stars-groupcall-message-limits
// @see https://core.telegram.org/api/group-calls#in-call-messages
// @see https://core.telegram.org/constructor/true
// @see https://core.telegram.org/mtproto/TL-combinators#conditional-fields
// @see https://core.telegram.org/type/%23
// @see https://core.telegram.org/type/Peer
// @see https://core.telegram.org/type/TextWithEntities
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/long
groupCallMessage#907ce88e random_id:long message:TextWithEntities = GroupCallMessage;
