package com.jumso.registry

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class SessionRegistry {
    private val sessions = ConcurrentHashMap<String, WebSocketSession>()

    fun addSession(session: WebSocketSession) {
        sessions[session.id] = session
    }

    fun removeSession(session: WebSocketSession) {
        sessions.remove(session.id)
    }

    fun getSessionCount() = sessions.size.toDouble()
}