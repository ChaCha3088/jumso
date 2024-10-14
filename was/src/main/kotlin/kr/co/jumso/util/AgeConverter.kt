package kr.co.jumso.util

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Component
class AgeConverter {
    fun convertAgeToBirthYearMax(minAge: Byte): LocalDateTime {
        val currentYear = now().year
        return LocalDateTime.of(currentYear - minAge.toInt(), 12, 31, 23, 59, 59)
    }

    fun convertAgeToBirthYearMin(maxAge: Byte): LocalDateTime {
        val currentYear = now().year
        return LocalDateTime.of(currentYear - maxAge.toInt(), 1, 1, 0, 0, 0)
    }
}
