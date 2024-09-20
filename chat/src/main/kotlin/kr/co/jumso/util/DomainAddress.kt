package com.jumso.util

import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class DomainAddress(
    private val env: Environment
) {
    // Spring Profile이 Local이면 localhost, 아니면 jumso.co.kr
    val domainAddress: String
        get() = when (env.activeProfiles[0]) {
            "local" -> "*"
            else -> "jumso.co.kr"
        }
}