int ? = Int;
long ? = Long;
double ? = Double;
string ? = String;

// @description A universal vector constructor.
vector {t:Type} # [ t ] = Vector t;

int128 4*[ int ] = Int128;
// @description Represents a 256-bit integer.
int256 8*[ int ] = Int256;

resPQ#05162463 nonce:int128 server_nonce:int128 pq:bytes server_public_key_fingerprints:Vector<long> = ResPQ;

p_q_inner_data_dc#a9f55f95 pq:bytes p:bytes q:bytes nonce:int128 server_nonce:int128 new_nonce:int256 dc:int = P_Q_inner_data;
p_q_inner_data_temp_dc#56fddf88 pq:bytes p:bytes q:bytes nonce:int128 server_nonce:int128 new_nonce:int256 dc:int expires_in:int = P_Q_inner_data;

server_DH_params_ok#d0e8075c nonce:int128 server_nonce:int128 encrypted_answer:bytes = Server_DH_Params;

server_DH_inner_data#b5890dba nonce:int128 server_nonce:int128 g:int dh_prime:bytes g_a:bytes server_time:int = Server_DH_inner_data;

client_DH_inner_data#6643b654 nonce:int128 server_nonce:int128 retry_id:long g_b:bytes = Client_DH_Inner_Data;

dh_gen_ok#3bcbf734 nonce:int128 server_nonce:int128 new_nonce_hash1:int128 = Set_client_DH_params_answer;
dh_gen_retry#46dc1fb9 nonce:int128 server_nonce:int128 new_nonce_hash2:int128 = Set_client_DH_params_answer;
dh_gen_fail#a69dae02 nonce:int128 server_nonce:int128 new_nonce_hash3:int128 = Set_client_DH_params_answer;

bind_auth_key_inner#75a3f765 nonce:long temp_auth_key_id:long perm_auth_key_id:long temp_session_id:long expires_at:int = BindAuthKeyInner;

rpc_result#f35c6d01 req_msg_id:long result:Object = RpcResult;
rpc_error#2144ca19 error_code:int error_message:string = RpcError;

rpc_answer_unknown#5e2ad36e = RpcDropAnswer;
rpc_answer_dropped_running#cd78e586 = RpcDropAnswer;
rpc_answer_dropped#a43ad8b7 msg_id:long seq_no:int bytes:int = RpcDropAnswer;

future_salt#0949d9dc valid_since:int valid_until:int salt:long = FutureSalt;
future_salts#ae500895 req_msg_id:long now:int salts:vector<future_salt> = FutureSalts;

pong#347773c5 msg_id:long ping_id:long = Pong;

destroy_session_ok#e22045fc session_id:long = DestroySessionRes;
destroy_session_none#62d350c9 session_id:long = DestroySessionRes;

new_session_created#9ec20908 first_msg_id:long unique_id:long server_salt:long = NewSession;

msg_container#73f1f8dc messages:vector<%Message> = MessageContainer;
// @description A message
// @param date Date of the message
// @param edit_date Last edit date of this message
// @param edit_hide Whether the message should be shown as not modified to the user, even if an edit date is present
// @param effect A message effect that should be played as specified here » .
// @param entities Message entities for styled text
// @param factcheck Represents a fact-check » .
// @param flags Flags, see TL conditional fields
// @param flags2 Flags, see TL conditional fields
// @param forwards Forward counter
// @param from_boosts_applied Supergroups only, contains the number of boosts this user has given the current supergroup, and should be shown in the UI in the header of the message. Only present for incoming messages from non-anonymous supergroup members that have boosted the supergroup. Note that this counter should be locally overridden for non-anonymous outgoing messages, according to the current value of channelFull . boosts_applied , to ensure the value is correct even for messages sent by the current user before a supergroup was boosted (or after a boost has expired or the number of boosts has changed); do not update this value for incoming messages from other users, even if their boosts have changed.
// @param from_id ID of the sender of the message
// @param from_rank Only in supergroups » (never basic groups, where this information is contained elsewhere » ), contains the sender's tag » .
// @param from_scheduled Whether this is a scheduled message
// @param fwd_from Info about forwarded messages
// @param grouped_id Multiple media messages sent using messages.sendMultiMedia with the same grouped ID indicate an album or media group
// @param guestchat_via_from If the message was posted by a guest bot » , the peer on whose behalf the bot sent the message.
// @param id ID of the message
// @param invert_media If set, any eventual webpage preview will be shown on top of the message instead of at the bottom.
// @param legacy This is a legacy message: it has to be refetched with the new layer
// @param media Media attachment
// @param media_unread Whether there are unread media attachments in this message, see here » for more info on how to clear this flag.
// @param mentioned Whether we were mentioned in this message
// @param message The message
// @param noforwards Whether this message is protected and thus cannot be forwarded; clients should also prevent users from saving attached media (i.e. videos should only be streamed, photos should be kept in RAM, et cetera).
// @param offline If set, the message was sent because of a scheduled action by the message sender, for example, as away, or a greeting service message.
// @param out Is this an outgoing message
// @param paid_message_stars The amount of stars the sender has paid to send the message, see here » for more info.
// @param paid_suggested_post_stars Set if this is a suggested channel post » that was paid using Telegram Stars .
// @param paid_suggested_post_ton Set if this is a suggested channel post » that was paid using Grams.
// @param peer_id Peer ID, the chat where this message was sent
// @param pinned Whether this message is pinned
// @param post Whether this is a channel post
// @param post_author Name of the author of this message for channel posts (with signatures enabled)
// @param quick_reply_shortcut_id If set, this message is a quick reply shortcut message » (note that quick reply shortcut messages sent to a private chat will not have this field set).
// @param reactions Reactions to this message
// @param replies Info about post comments (for channels) or message replies (for groups)
// @param reply_markup Reply markup (bot/inline keyboards)
// @param reply_to Reply information
// @param report_delivery_until_date Used for Telegram Gateway verification messages : if set and the current unixtime is bigger than the specified unixtime, invoke messages.reportMessagesDelivery passing the ID and the peer of this message as soon as it is received by the client (optionally batching requests for the same peer).
// @param restriction_reason Contains the reason why access to this message must be restricted.
// @param saved_peer_id Messages from a saved messages dialog » will have peer = inputPeerSelf and the saved_peer_id flag set to the ID of the saved dialog. Messages from a monoforum » will have peer =ID of the monoforum and the saved_peer_id flag set to the ID of a topic.
// @param schedule_repeat_period Once sent, this message will be automatically re-scheduled to be re-sent again this many seconds in the future, see here » for more info on repeating scheduled messages.
// @param silent Whether this is a silent message (no notification triggered)
// @param suggested_post Used to suggest a post to a channel, see here » for more info on the full flow.
// @param summary_from_language If set, clients should offer a summarization button » for this message; contains the two-letter ISO 639-1 language code of the inferred language of the current message.
// @param ttl_period Time To Live of the message, once message.date+message.ttl_period === time(), the message will be deleted on the server, and must be deleted locally as well.
// @param via_bot_id ID of the inline bot that generated the message
// @param via_business_bot_id Whether the message was sent by the business bot specified in via_bot_id on behalf of the user.
// @param video_processing_pending The video contained in the message is currently being processed by the server (i.e. to generate alternative qualities, that will be contained in the final messageMediaDocument . alt_document ), and will be sent once the video is processed, which will happen approximately at the specified date (i.e. messages with this flag set should be treated similarly to scheduled messages , but instead of the scheduled date, date contains the estimated conversion date). See here » for more info.
// @param views View count for channel posts
// @see https://core.telegram.org/api/ai#summarize-messages
// @see https://core.telegram.org/api/boost
// @see https://core.telegram.org/api/bots/connected-business-bots
// @see https://core.telegram.org/api/bots/guest-mode#guest-messages
// @see https://core.telegram.org/api/business#quick-reply-shortcuts
// @see https://core.telegram.org/api/channel#supergroups
// @see https://core.telegram.org/api/effects
// @see https://core.telegram.org/api/entities
// @see https://core.telegram.org/api/factcheck
// @see https://core.telegram.org/api/files#albums-grouped-media
// @see https://core.telegram.org/api/files#video-qualities
// @see https://core.telegram.org/api/mentions
// @see https://core.telegram.org/api/monoforum
// @see https://core.telegram.org/api/paid-messages
// @see https://core.telegram.org/api/pin
// @see https://core.telegram.org/api/rank
// @see https://core.telegram.org/api/saved-messages
// @see https://core.telegram.org/api/scheduled-messages
// @see https://core.telegram.org/api/scheduled-messages#repeating-scheduled-messages
// @see https://core.telegram.org/api/stars
// @see https://core.telegram.org/api/suggested-posts
// @see https://core.telegram.org/api/threads
// @see https://core.telegram.org/api/views#read-message-contents
// @see https://core.telegram.org/constructor/channelFull
// @see https://core.telegram.org/constructor/inputPeerSelf
// @see https://core.telegram.org/constructor/messageMediaDocument
// @see https://core.telegram.org/constructor/true
// @see https://core.telegram.org/method/messages.reportMessagesDelivery
// @see https://core.telegram.org/method/messages.sendMultiMedia
// @see https://core.telegram.org/mtproto/TL-combinators#conditional-fields
// @see https://core.telegram.org/type/%23
// @see https://core.telegram.org/type/FactCheck
// @see https://core.telegram.org/type/MessageEntity
// @see https://core.telegram.org/type/MessageFwdHeader
// @see https://core.telegram.org/type/MessageMedia
// @see https://core.telegram.org/type/MessageReactions
// @see https://core.telegram.org/type/MessageReplies
// @see https://core.telegram.org/type/MessageReplyHeader
// @see https://core.telegram.org/type/Peer
// @see https://core.telegram.org/type/ReplyMarkup
// @see https://core.telegram.org/type/RestrictionReason
// @see https://core.telegram.org/type/SuggestedPost
// @see https://core.telegram.org/type/Vector%20t
// @see https://core.telegram.org/type/int
// @see https://core.telegram.org/type/long
// @see https://core.telegram.org/type/string
// @see https://telegram.org/blog/content-protection-delete-by-date-and-more
// @see https://telegram.org/blog/star-messages-gateway-2-0-and-more#save-even-more-on-user-verification
message msg_id:long seqno:int bytes:int body:Object = Message;
msg_copy#e06046b2 orig_message:Message = MessageCopy;

gzip_packed#3072cfa1 packed_data:bytes = Object;

msgs_ack#62d6b459 msg_ids:Vector<long> = MsgsAck;

bad_msg_notification#a7eff811 bad_msg_id:long bad_msg_seqno:int error_code:int = BadMsgNotification;
bad_server_salt#edab447b bad_msg_id:long bad_msg_seqno:int error_code:int new_server_salt:long = BadMsgNotification;

msg_resend_req#7d861a08 msg_ids:Vector<long> = MsgResendReq;
msgs_state_req#da69fb52 msg_ids:Vector<long> = MsgsStateReq;
msgs_state_info#04deb57d req_msg_id:long info:bytes = MsgsStateInfo;
msgs_all_info#8cc0d131 msg_ids:Vector<long> info:bytes = MsgsAllInfo;
msg_detailed_info#276d3ec6 msg_id:long answer_msg_id:long bytes:int status:int = MsgDetailedInfo;
msg_new_detailed_info#809db6df answer_msg_id:long bytes:int status:int = MsgDetailedInfo;

destroy_auth_key_ok#f660e1d4 = DestroyAuthKeyRes;
destroy_auth_key_none#0a9f2259 = DestroyAuthKeyRes;
destroy_auth_key_fail#ea109b13 = DestroyAuthKeyRes;

http_wait#9299359f max_delay:int wait_after:int max_wait:int = HttpWait;

---functions---

req_pq_multi#be7e8ef1 nonce:int128 = ResPQ;

req_DH_params#d712e4be nonce:int128 server_nonce:int128 p:bytes q:bytes public_key_fingerprint:long encrypted_data:bytes = Server_DH_Params;

set_client_DH_params#f5045f1f nonce:int128 server_nonce:int128 encrypted_data:bytes = Set_client_DH_params_answer;

rpc_drop_answer#58e4a740 req_msg_id:long = RpcDropAnswer;
get_future_salts#b921bd04 num:int = FutureSalts;
ping#7abe77ec ping_id:long = Pong;
ping_delay_disconnect#f3427b8c ping_id:long disconnect_delay:int = Pong;
destroy_session#e7512126 session_id:long = DestroySessionRes;

destroy_auth_key#d1435160 = DestroyAuthKeyRes;
