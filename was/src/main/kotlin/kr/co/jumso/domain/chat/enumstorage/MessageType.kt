package kr.co.jumso.domain.chat.enumstorage

enum class MessageType {
    SYSTEM, // 시스템 메시지
    ERROR, // 에러 메시지

    SELECT_CHAT_ROOM_LIST, // 채팅방 목록 조회 응답
    CREATE_CHAT_ROOM, // 채팅방 생성 응답
    DELETE_CHAT_ROOM, // 채팅방 삭제 응답
    SELECT_CHAT_ROOM_MESSAGES, // 채팅방의 메시지 조회 응답

    CHAT_MESSAGE, // 채팅 메시지 전송 응답
}
