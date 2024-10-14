package kr.co.jumso.util

import org.springframework.stereotype.Component

@Component
class CoordinateConverter {
    fun coordinates(
        latitude: Double,
        longitude: Double,
        howFarCanYouGo: Byte
    ): Array<Pair<Double, Double>> {
        val latitudeDifference = howFarCanYouGo / 111
        val longitudeDifference = howFarCanYouGo / 111

        return arrayOf(
            Pair(latitude - latitudeDifference, longitude - longitudeDifference),
            Pair(latitude + latitudeDifference, longitude + longitudeDifference)
        )
    }
}
