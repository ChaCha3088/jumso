//package com.example.simple_blog.dummy
//
//import com.example.simple_blog.domain.member.entity.Member
//import com.example.simple_blog.domain.member.repository.MemberPropertyRepository
//import com.example.simple_blog.domain.member.repository.MemberRepository
//import com.example.simple_blog.domain.member.entity.MemberProperty
//import com.example.simple_blog.domain.property.entity.Property
//import com.example.simple_blog.domain.property.repository.PropertyRepository
//import io.github.serpro69.kfaker.Faker
//import org.springframework.boot.context.event.ApplicationReadyEvent
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.event.EventListener
//import org.springframework.dao.DataIntegrityViolationException
//
//@Configuration
//class DummyData(
//    private val faker: Faker,
//
//    private val memberRepository: MemberRepository,
//    private val propertyRepository: PropertyRepository,
//    private val memberPropertyRepository: MemberPropertyRepository
//) {
//    @EventListener(ApplicationReadyEvent::class)
//    private fun init() {
//        // Member 10000명 생성
//        val members = mutableListOf<Member>()
//
//        for (i in 1..10_000) {
//            members.add(
//                Member(
//                    email = faker.internet.safeEmail(),
//                    password = faker.name.name(),
//                    name = faker.name.name(),
//                    nickname = faker.name.name()
//                )
//            )
//        }
//
//        memberRepository.saveAll(members)
//
//
//
//        // Property 생성
//        val properties = mutableListOf<Property>()
//
//        for (t in 1..10) {
//            val from = (t - 1) * 100 + 1
//            val to = from + 9
//            for (i in from..to) {
//                properties.add(
//                    Property(
//                        id = i.toLong(),
//                        value = faker.adjective.positive().let {
//                            // 50자 이상이면 50자로 자르기
//                            when {
//                                it.length > 50 -> it.substring(0, 50)
//                                else -> it
//                            }
//                        }
//                    )
//                )
//            }
//        }
//
//        propertyRepository.saveAll(properties)
//
//
//
//        // MemberProperty 생성
//        for (i in 1..100_000) {
//            try {
//                memberPropertyRepository.save(
//                    MemberProperty(
//                        memberId = members.random().id!!,
//                        propertyId = properties.random().id
//                    )
//                )
//            }
//            catch (e: DataIntegrityViolationException) {
//                // 중복 데이터가 발생할 수 있으므로 무시
//            }
//        }
//    }
//}
