package kr.co.jumso.registry

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class SessionRegistry {
    private val sessions = ConcurrentHashMap<Long, WebSocketSession>()

    fun addSession(memberId: Long, session: WebSocketSession) {
        sessions[memberId] = session
    }

    fun removeSession(memberId: Long) {
        sessions.remove(memberId)
    }

    fun containsSession(memberId: Long) = sessions.containsKey(memberId)

    fun getSessionCount() = sessions.size.toDouble()
}
