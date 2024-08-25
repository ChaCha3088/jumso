package com.example.simple_blog.dummy

import com.example.simple_blog.domain.company.entity.Company
import com.example.simple_blog.domain.company.repository.CompanyRepository
import com.example.simple_blog.domain.member.repository.MemberPropertyRepository
import com.example.simple_blog.domain.member.repository.MemberRepository
import com.example.simple_blog.domain.property.repository.PropertyRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.serpro69.kfaker.Faker
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import java.io.File

@Configuration
class DummyData(
    private val faker: Faker,

    private val memberRepository: MemberRepository,
    private val propertyRepository: PropertyRepository,
    private val memberPropertyRepository: MemberPropertyRepository,
    private val companyRepository: CompanyRepository,
) {
    @EventListener(ApplicationReadyEvent::class)
    private fun init() {
        // json의 형식은
//        {
//            "result":0,
//            "data":
//            {"companies":
//                [
//                    {"id":4600,"name":"11번가","alias":null,"color":"#D95757","emails":["11stcorp.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":4574,"name":"21gram","alias":null,"color":"#22628E","emails":["21gramcompany.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":6514,"name":"29cm","alias":null,"color":"#D95757","emails":["29cm.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":7741,"name":"3i","alias":null,"color":"#75C155","emails":["3i.ai"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":1428,"name":"3S소프트","alias":null,"color":"#D95757","emails":["3ssoft.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":10446,"name":"4th CREATIVE PARTY","alias":null,"color":"#75C155","emails":["4thparty.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":1002,"name":"8percent","alias":"에잇퍼센트","color":"#D95757","emails":["8percent.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":3291,"name":"a2zrobotics","alias":null,"color":"#75C155","emails":["a2zrobotics.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":1167,"name":"A3 시큐리티","alias":null,"color":"#1980C5","emails":["a3sc.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":7429,"name":"AAC TECHNOLOGIES","alias":null,"color":"#1980C5","emails":["aactechnologies.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":9604,"name":"AA아키그룹건축사사무소","alias":null,"color":"#75C155","emails":["aaarchigroup.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":10138,"name":"ABB 코리아","alias":null,"color":"#D95757","emails":["kr.abb.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":3270,"name":"abies","alias":null,"color":"#75C155","emails":["abies.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":3271,"name":"Able I\u0026C","alias":null,"color":"#1980C5","emails":["ableinc.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":10178,"name":"ABL생명보험","alias":null,"color":"#5C606D","emails":["abllife.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":4578,"name":"ABOV세미컨덕터","alias":null,"color":"#75C155","emails":["abov.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":5089,"name":"ACN코리아","alias":null,"color":"#5C606D","emails":["acnkr.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":3275,"name":"ADE","alias":null,"color":"#1980C5","emails":["adeinc.io"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":1510,"name":"Adecco","alias":null,"color":"#1980C5","emails":["adecco.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":1402,"name":"adient","alias":null,"color":"#75C155","emails":["adient.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":7282,"name":"ADM Korea","alias":null,"color":"#1980C5","emails":["admkorea.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":8738,"name":"Admaru","alias":null,"color":"#1980C5","emails":["admaru.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":4683,"name":"Adobe","alias":null,"color":"#75C155","emails":["adobe.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":9197,"name":"Adriel","alias":null,"color":"#75C155","emails":["adriel.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":3276,"name":"ADVANCE ELECTRIC KOREA","alias":null,"color":"#75C155","emails":["advancekr.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":4107,"name":"Aep코리아네트","alias":null,"color":"#22628E","emails":["kn.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":8291,"name":"Aeva","alias":null,"color":"#D95757","emails":["aeva.ai"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":2053,"name":"AGC디스플레이","alias":null,"color":"#E09E24","emails":["ado.agc.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":4486,"name":"Aggreko","alias":null,"color":"#5C606D","emails":["aggreko.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":5451,"name":"AgileSoDA","alias":null,"color":"#75C155","emails":["agilesoda.ai"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":7325,"name":"Agility","alias":null,"color":"#1980C5","emails":["agility.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":8295,"name":"Agoda","alias":null,"color":"#22628E","emails":["agoda.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":10291,"name":"Agora Inc.","alias":null,"color":"#1980C5","emails":["agora.io"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":2,"name":"AIA생명","alias":null,"color":"#D95757","emails":["aia.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":5774,"name":"Aidus","alias":null,"color":"#22628E","emails":["aidus.io"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":3,"name":"AIG손해보험","alias":null,"color":"#1980C5","emails":["aig.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":1489,"name":"AIR KOREA","alias":"에어코리아","color":"#5C606D","emails":["airkorea.biz"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":8704,"name":"AItheNutrigene","alias":null,"color":"#75C155","emails":["aithenutrigene.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":3280,"name":"aj park","alias":null,"color":"#E09E24","emails":["ajpark.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":1129,"name":"AJ네트웍스","alias":null,"color":"#E09E24","emails":["ajnet.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":1069,"name":"AJ렌터카","alias":null,"color":"#1980C5","emails":["ajrentacar.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":1130,"name":"AK플라자","alias":null,"color":"#E09E24","emails":["aekyung.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":3179,"name":"AK홀딩스","alias":null,"color":"#D95757","emails":["aekyunggroup.co.kr","aekyung.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":5571,"name":"ALLEGO Global","alias":null,"color":"#1980C5","emails":["allego.io"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":4711,"name":"Allergan","alias":null,"color":"#D95757","emails":["allergan.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":4484,"name":"Allm","alias":null,"color":"#5C606D","emails":["allm.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":7940,"name":"Amazon Web Services","alias":null,"color":"#75C155","emails":["amazon.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":4399,"name":"AMGEN","alias":null,"color":"#5C606D","emails":["amgen.com"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":3222,"name":"AMST","alias":null,"color":"#D95757","emails":["amst.co.kr"],"enabled":true,"scale":null,"order":null,"logo_url":null},
//                    {"id":7975,"name":"Applied Materials US","alias":null,"color":"#5C606D","emails":["amat.com"],"enabled":true,"scale":null,"order":null,"logo_url":null}
//                ],
//                "last_page":false,
//                "current_page":1,
//                "next_page":2,
//                "total_pages":203
//            }
//        }

        // 역직렬화
        val mapper = ObjectMapper().registerKotlinModule()

        val companies: MutableList<Company> = mutableListOf()

        // src/main/resources/companies 안의 json들을 읽고, Company 엔티티로 변환하여 저장
        val fileList = File("src/main/resources/companies").listFiles()

        for (file in fileList) {
            val deserialized = mapper.readValue<TemporaryResponse>(file)

            // Company 생성
            deserialized.data.companies.forEach { it ->
                companies.add(Company(it.name).apply {
                    addCompanyEmails(it.emails)
                })
            }
        }

        companyRepository.saveAll(companies)

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
    }
}
