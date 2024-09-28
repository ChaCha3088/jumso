package kr.co.jumso.domain.chat.enumstorage

enum class MessageType {
    SYSTEM, // 시스템 메시지
    ERROR, // 에러 메시지

    // 요청
    REQUEST_SELECT_CHAT_ROOM_LIST, // 채팅방 목록 조회 요청
    REQUEST_CREATE_CHAT_ROOM, // 채팅방 생성 요청
    REQUEST_DELETE_CHAT_ROOM, // 채팅방 삭제 요청
    REQUEST_SELECT_CHAT_ROOM_MESSAGES, // 채팅방의 메시지 조회 요청

    REQUEST_SEND_CHAT_MESSAGE, // 채팅 메시지 전송 요청

    // 응답
    RESPONSE_SELECT_CHAT_ROOM_LIST, // 채팅방 목록 조회 응답
    RESPONSE_CREATE_CHAT_ROOM, // 채팅방 생성 응답
    RESPONSE_DELETE_CHAT_ROOM, // 채팅방 삭제 응답
    RESPONSE_SELECT_CHAT_ROOM_MESSAGES, // 채팅방의 메시지 조회 응답

    RESPONSE_SEND_CHAT, // 채팅 메시지 전송 응답
}
