package kr.co.jumso.enumstorage

enum class RedisKeys(
    val key: String
) {
    MEMBER_ID_TO_SERVER_PORT_AND_SESSION_ID("member-id-to-server-port-and-session-id"),

    CHAT_SERVER_LOAD("chat-server-load"),

    CHAT_MESSAGE_STORAGE("chat-message-storage-"),

    CHAT_ROOM_MEMBERS("chat-room-members-"),
}
