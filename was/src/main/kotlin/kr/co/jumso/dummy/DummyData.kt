package kr.co.jumso.dummy

import kr.co.jumso.domain.company.repository.CompanyRepository
import kr.co.jumso.domain.member.repository.MemberPropertyRepository
import kr.co.jumso.domain.member.repository.MemberRepository
import kr.co.jumso.domain.property.repository.PropertyRepository
import io.github.serpro69.kfaker.Faker
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

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
//        // 역직렬화
//        val mapper = ObjectMapper().registerKotlinModule()
//
//        val companies: MutableList<Company> = mutableListOf()
//
//        // src/main/resources/companies 안의 json들을 읽고, Company 엔티티로 변환하여 저장
//        val fileList = File("src/main/resources/companies").listFiles()
//
//        for (file in fileList) {
//            val deserialized = mapper.readValue<TemporaryResponse>(file)
//
//            // Company 생성
//            deserialized.data.companies.forEach { it ->
//                companies.add(Company(it.name).apply {
//                    addCompanyEmails(it.emails)
//                })
//            }
//        }
//
//        companyRepository.saveAll(companies)

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
