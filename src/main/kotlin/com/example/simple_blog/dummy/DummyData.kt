package com.example.simple_blog.dummy

import com.example.simple_blog.domain.member.entity.Member
import com.example.simple_blog.domain.member.repository.MemberRepository
import io.github.serpro69.kfaker.Faker
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener

@Configuration
class DummyData(
    private val memberRepository: MemberRepository
) {
    val faker = Faker()

    @EventListener(ApplicationReadyEvent::class)
    private fun init() {
        val member = Member(
            email = faker.internet.safeEmail(),
            password = faker.name.name(),
            name = faker.name.name(),
            nickname = faker.name.name()
        )

        memberRepository.save(member)
    }
}