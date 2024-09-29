package kr.co.jumso.registry

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Component
class SessionRegistry {
    private val sessions = ConcurrentHashMap<Long, WebSocketSession>()

    fun getSession(memberId: Long): WebSocketSession? {
        // 있으면 WebSocketSession을 반환하고, 없으면 null을 반환한다.
        return sessions[memberId]
    }

    fun addSession(memberId: Long, session: WebSocketSession) {
        sessions[memberId] = session
    }

    fun removeSession(memberId: Long) {
        sessions.remove(memberId)
    }

    fun containsSession(memberId: Long) = sessions.containsKey(memberId)

    fun getSessionCount() = sessions.size.toDouble()
}
