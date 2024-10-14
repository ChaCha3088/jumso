package kr.co.jumso.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.SC_OK
import kr.co.jumso.domain.auth.filter.AuthenticationProcessFilter
import kr.co.jumso.domain.auth.handler.JwtLogoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.LogoutFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationProcessFilter: AuthenticationProcessFilter,
    private val jwtLogoutHandler: JwtLogoutHandler,
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .cors { obj: CorsConfigurer<HttpSecurity> -> obj.disable() }

        // 기본 페이지, css, image, js 하위 폴더에 있는 자료들은 모두 접근 가능
        http
            .authorizeHttpRequests { authorize ->
                    authorize
                        .requestMatchers(
                            "/",
                            "/css/**",
                            "/img/**",
                            "/js/**",
                            "/favicon.ico",
                            "/error/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                }

        // 세션
        http
            .sessionManagement { sessionManagement: SessionManagementConfigurer<HttpSecurity?> ->
                sessionManagement.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            } // 세션을 사용하지 않겠다.


        // Basic 인증
        http // http header에 username, password를 넣어서 전송하는 방법을
            .httpBasic() // 사용하지 않겠다.
        { obj: HttpBasicConfigurer<HttpSecurity> -> obj.disable() }

        // Filter
        http
            .addFilterAfter(authenticationProcessFilter, LogoutFilter::class.java)

        //                .addFilterAfter(memberAuthenticationFilter(), AuthenticationProcessFilter.class);

        // 인증
        http
            .formLogin { obj: FormLoginConfigurer<HttpSecurity> -> obj.disable() } // form login 비활성화


        // 로그아웃
        http
            .logout { logout: LogoutConfigurer<HttpSecurity?> ->
                logout.permitAll()
                    .logoutUrl("/api/auth/signout/v1")
                    .logoutSuccessHandler { request: HttpServletRequest?, response: HttpServletResponse, authentication: Authentication? ->
                        response.status = SC_OK
                    }
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .addLogoutHandler(jwtLogoutHandler)
            }

        return http.build()
    }
}
