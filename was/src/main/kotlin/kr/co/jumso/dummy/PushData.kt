package kr.co.jumso.dummy

import kr.co.jumso.domain.property.entity.Property
import kr.co.jumso.domain.property.repository.PropertyRepository
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

@Configuration
class PushData(
    private val propertyRepository: PropertyRepository,
) {
//    @EventListener(ApplicationReadyEvent::class)
//    private fun init() {
//        // Property 생성
//        val properties: MutableSet<Property> = mutableSetOf()
//
//        // data/Properties.txt 파일 읽기
//        System.setIn(FileInputStream("data/Properties.txt"))
//        val br = BufferedReader(InputStreamReader(System.`in`))
//
//        var thousands = 1000L
//        var hundreds = 0L
//        var id = 0L
//
//        while (true) {
//            val text = br.readLine() ?: break
//
//            if (text == "") {
//                // 다음 forEach로 넘어가기
//                hundreds += 100L
//                id = 0L
//            }
//            else if (text == ".") {
//                thousands += 1000L
//                hundreds = 0L
//                id = 0L
//            }
//            else if (text == "---") {
//                // forEach 종료
//                break
//            }
//            else {
//                val newProperty = Property(
//                    id = thousands + hundreds + id,
//                    value = text,
//                )
//                properties.add(newProperty)
//                id += 1L
//            }
//        }
//
//        propertyRepository.saveAll(properties)
//    }
}
