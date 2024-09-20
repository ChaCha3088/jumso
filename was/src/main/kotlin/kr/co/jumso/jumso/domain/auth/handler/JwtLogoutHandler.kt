package kr.co.jumso.domain.auth.handler

import com.example.jumso.domain.auth.service.AuthService
import com.fasterxml.jackson.core.JsonProcessingException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class JwtLogoutHandler(
    private val authService: AuthService
): LogoutHandler {
    /**
     * 로그아웃 할 때는 accessToken과 refreshToken을 모두 보내야 함
     * @param request the HTTP request
     * @param response the HTTP response
     * @param authentication the current principal details
     */
    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        try {
            authService.signOut(request, response)

            response.characterEncoding = "UTF-8"
            response.contentType = APPLICATION_JSON_VALUE
            response.status = HttpStatus.OK.value()
        } // 들어오는 값이 이상할 때
        catch (e: IllegalArgumentException) {
            // 400 Bad Request
            response.status = HttpStatus.BAD_REQUEST.value()
        } catch (e: JsonProcessingException) {
            response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        } catch (e: IOException) {
            response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        }
    }
}
