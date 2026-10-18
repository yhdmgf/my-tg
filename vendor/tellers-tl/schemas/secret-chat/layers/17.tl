// LAYER 17
// @introduced 17
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
decryptedMessage#204d3878 random_id:long ttl:int message:string media:DecryptedMessageMedia = DecryptedMessage;
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
// @introduced 17
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
decryptedMessageMediaVideo#524a415d thumb:bytes thumb_w:int thumb_h:int duration:int mime_type:string w:int h:int size:int key:bytes iv:bytes = DecryptedMessageMedia;
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
