package kr.co.jumso.member.entity

import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.ORDINAL
import jakarta.persistence.FetchType.LAZY
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kr.co.jumso.AuditingEntity
import kr.co.jumso.auth.entity.RefreshToken
import kr.co.jumso.chat.entity.MemberChatRoom
import kr.co.jumso.company.entity.Company
import kr.co.jumso.enumstorage.member.*
import kr.co.jumso.enumstorage.member.MemberRole.USER
import kr.co.jumso.member.exception.InvalidMemberPropertyIdsException
import kr.co.jumso.recommend.entity.MemberNoMatch
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.util.UUID.randomUUID

@Entity
class Member(
    email: String,
    password: String,
    nickname: String,
    companyId: Long,
    bornAt: LocalDateTime,
    sex: Sex,
    height: Short,
    bodyType: BodyType,
    job: String,
    relationshipStatus: RelationshipStatus,
    religion: Religion,
    smoke: Smoke,
    drink: Drink,
    latitude: Double,
    longitude: Double,
    introduction: String,
    whatSexDoYouWant: Sex,
    howTallDoYouWantMin: Short,
    howTallDoYouWantMax: Short,
    howOldDoYouWantMin: Byte,
    howOldDoYouWantMax: Byte,
    howFarCanYouGo: Byte,
    whatKindOfBodyTypeDoYouWant: BodyType,
    whatKindOfRelationshipStatusDoYouWant: RelationshipStatus,
    whatKindOfReligionDoYouWant: Religion,
    whatKindOfSmokeDoYouWant: Smoke,
    whatKindOfDrinkDoYouWant: Drink,
): AuditingEntity() {
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "이메일을 입력해주세요.")
    @Max(value = 50, message = "이메일은 50자 이하로 입력해주세요.")
    var email: String = email
        protected set

    @Column(nullable = false)
    @NotBlank
    var password: String = password
        protected set

    @Column(nullable = false, length = 50)
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Max(value = 50, message = "닉네임은 50자 이하로 입력해주세요.")
    var nickname: String = nickname
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull
    var role: MemberRole = USER
        protected set

    @Column(nullable = false)
    @NotNull
    var isDeleted: Boolean = false
        protected set

    @Column(nullable = false)
    @NotNull
    var lastSignIn: LocalDateTime = now()
        protected set

    @OneToOne(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var refreshToken: RefreshToken? = null
        internal set

    @JoinColumn(nullable = false, insertable = false, updatable = false)
    @ManyToOne(fetch = LAZY, targetEntity = Company::class)
    @NotNull(message = "회사를 선택해주세요.")
    var company: Company? = null
        protected set

    @Column(name = "company_id", nullable = false)
    @NotNull(message = "회사를 선택해주세요.")
    var companyId: Long = companyId
        protected set

    var verificationCode: String? = null
        protected set

    var verificationCodeExpiration: LocalDateTime? = null
        protected set

    var toBeCompanyId: Long? = null
        protected set

    var toBeDomain: String? = null
        protected set

    var toBeUsername: String? = null
        protected set

    // Member의 채팅
    @OneToMany(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var membersChatRooms: MutableList<MemberChatRoom> = mutableListOf()
        protected set

    @OneToMany(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var memberProperties: MutableSet<MemberProperty> = mutableSetOf()
        protected set

    @OneToMany(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var noMatch: MutableSet<MemberNoMatch> = mutableSetOf()
        protected set

    @Column(nullable = false)
    @NotNull
    @Min(value = 0L)
    var newChatTicket: Long = 0
        protected set

    @Column(nullable = false)
    @NotNull(message = "생년월일을 입력해주세요.")
    var bornAt: LocalDateTime = bornAt
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "성별을 선택해주세요.")
    var sex: Sex = sex
        protected set

    @Column(nullable = false)
    @NotNull
    @Min(value = 100, message = "키는 100cm 이상으로 입력해주세요.")
    @Max(value = 300, message = "키는 300cm 이하로 입력해주세요.")
    var height: Short = height
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "체형을 선택해주세요.")
    var bodyType: BodyType = bodyType
        protected set

    @Column(nullable = false, length = 50)
    @NotBlank(message = "직업을 입력해주세요.")
    @Max(value = 50, message = "직업은 50자 이하로 입력해주세요.")
    var job: String = job
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "교제 상태를 선택해주세요.")
    var relationshipStatus: RelationshipStatus = relationshipStatus
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "종교를 선택해주세요.")
    var religion: Religion = religion
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "흡연 여부를 선택해주세요.")
    var smoke: Smoke = smoke
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "음주 여부를 선택해주세요.")
    var drink: Drink = drink
        protected set

    // x 좌표, 위도
    @Column(nullable = false)
    @NotNull(message = "위치를 선택해주세요.")
    var latitude: Double = latitude
        protected set

    // y 좌표, 경도
    @Column(nullable = false)
    @NotNull(message = "위치를 선택해주세요.")
    var longitude: Double = longitude
        protected set

    @Column(nullable = false, length = 1000)
    @NotBlank(message = "자기소개를 입력해주세요.")
    @Max(value = 1000, message = "자기소개는 1000자 이하로 입력해주세요.")
    var introduction: String = introduction
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "원하는 성별을 선택해주세요.")
    var whatSexDoYouWant: Sex = whatSexDoYouWant
        protected set

    @Column(nullable = false)
    @NotNull
    @Min(value = 100, message = "키는 100cm 이상으로 입력해주세요.")
    @Max(value = 300, message = "키는 300cm 이하로 입력해주세요.")
    var howTallDoYouWantMin: Short = howTallDoYouWantMin
        protected set

    @Column(nullable = false)
    @NotNull
    @Min(value = 100, message = "키는 100cm 이상으로 입력해주세요.")
    @Max(value = 300, message = "키는 300cm 이하로 입력해주세요.")
    var howTallDoYouWantMax: Short = howTallDoYouWantMax
        protected set

    @Column(nullable = false)
    @NotNull(message = "원하는 나이를 선택해주세요.")
    @Max(value = 127, message = "나이는 127 이하로 입력해주세요.")
    @Min(value = 0, message = "나이는 0 이상으로 입력해주세요.")
    var howOldDoYouWantMin: Byte = if (howOldDoYouWantMin < howOldDoYouWantMax) howOldDoYouWantMin else throw IllegalArgumentException("최소 나이보다 최대 나이가 작습니다.")
        protected set

    @Column(nullable = false)
    @NotNull
    @Max(value = 127, message = "나이는 127 이하로 입력해주세요.")
    @Min(value = 0, message = "나이는 0 이상으로 입력해주세요.")
    var howOldDoYouWantMax: Byte = if (howOldDoYouWantMax > howOldDoYouWantMin) howOldDoYouWantMax else throw IllegalArgumentException("최대 나이보다 최소 나이가 큽니다.")
        protected set

    @Column(nullable = false)
    @NotNull(message = "원하는 거리를 입력해주세요.")
    @Max(value = 127, message = "거리는 127 이하로 입력해주세요.")
    @Min(value = 0, message = "거리는 0 이상으로 입력해주세요.")
    var howFarCanYouGo: Byte = howFarCanYouGo
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "원하는 체형을 선택해주세요.")
    var whatKindOfBodyTypeDoYouWant: BodyType = whatKindOfBodyTypeDoYouWant
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "원하는 교제 상태를 선택해주세요.")
    var whatKindOfRelationshipStatusDoYouWant: RelationshipStatus = whatKindOfRelationshipStatusDoYouWant
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "원하는 종교를 선택해주세요.")
    var whatKindOfReligionDoYouWant: Religion = whatKindOfReligionDoYouWant
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "원하는 흡연 여부를 선택해주세요.")
    var whatKindOfSmokeDoYouWant: Smoke = whatKindOfSmokeDoYouWant
        protected set

    @Enumerated(ORDINAL)
    @Column(nullable = false)
    @NotNull(message = "원하는 음주 여부를 선택해주세요.")
    var whatKindOfDrinkDoYouWant: Drink = whatKindOfDrinkDoYouWant
        protected set

    @OneToMany(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var notTheseCompanies: MutableSet<MemberNotTheseCompany> = mutableSetOf()
        protected set

    fun addMemberProperties(propertyIds: Set<Long>) {
        // enrollRequest의 memberPropertyIds가 1000번대, 2000번대, 3000번대가 하나 이상 포함되어 있는지 확인
        // memberPropertyIds가 1000번대, 2000번대, 3000번대가 하나 이상, 5개 이하인지 확인, 총 최대 15개
        val propertyIds1000 = mutableSetOf<Long>()
        val propertyIds2000 = mutableSetOf<Long>()
        val propertyIds3000 = mutableSetOf<Long>()

        for (propertyId in propertyIds) {
            when (propertyId) {
                in 1000..1999 -> propertyIds1000.add(propertyId)
                in 2000..2999 -> propertyIds2000.add(propertyId)
                in 3000..3999 -> propertyIds3000.add(propertyId)
                else -> {}
            }
        }

        if (!(propertyIds1000.size in 1..5 && propertyIds2000.size in 1..5 && propertyIds3000.size in 1..5)) {
            throw InvalidMemberPropertyIdsException()
        }

        // 회원이 요청한 propertyIds에 없는 propertyIds를 추가
        for (propertyId in propertyIds) {
            val memberProperty = MemberProperty(
                memberId = id!!,
                propertyId = propertyId,
            )

            this.memberProperties.add(memberProperty)
        }

        // 회원이 요청한 propertyIds에 없는 propertyIds를 삭제
        this.memberProperties.removeIf { it.propertyId !in propertyIds }
    }

    fun addMemberNotTheseCompany(memberNotTheseCompanyIds: Set<Long>) {
        for (notTheseCompanyId in memberNotTheseCompanyIds) {
            val memberNotTheseCompany = MemberNotTheseCompany(
                memberId = id!!,
                companyId = notTheseCompanyId,
            )

            this.notTheseCompanies.add(memberNotTheseCompany)
        }

        // 회원이 요청한 notTheseCompanyIds에 없는 notTheseCompanyIds를 삭제
        this.notTheseCompanies.removeIf { it.companyId !in memberNotTheseCompanyIds }
    }

    fun updateLastSignIn() {
        lastSignIn = now()
    }

    fun requestResetPassword(): String {
        val newCode = randomUUID().toString()
        this.verificationCode = newCode
        this.verificationCodeExpiration = now().plusMinutes(10)

        return newCode
    }

    fun resetPassword(newPassword: String) {
        // verificationCode의 만료 시간이 지났는지 확인
        if (verificationCode == null || verificationCodeExpiration == null || verificationCodeExpiration!!.isBefore(now())) {
            throw IllegalArgumentException("인증 코드가 만료되었습니다.")
        }

        this.password = newPassword
        this.verificationCode = null
        this.verificationCodeExpiration = null
    }

    fun requestChangeCompany(
        companyId: Long,
        toBeDomain: String,
        toBeUsername: String,
    ): String {
        val newCode = randomUUID().toString()
        this.verificationCode = newCode
        this.verificationCodeExpiration = now().plusMinutes(10)

        this.toBeCompanyId = companyId
        this.toBeDomain = toBeDomain
        this.toBeUsername = toBeUsername

        return newCode
    }

    fun changeCompany(
        verificationCode: String,
    ) {
        if (
            this.toBeCompanyId == null ||
            this.toBeDomain == null ||
            this.toBeUsername == null ||
            this.verificationCode == null ||
            this.verificationCodeExpiration == null ||
            this.verificationCodeExpiration!!.isBefore(now())
        ) {
            throw IllegalArgumentException("인증 코드가 만료되었거나 회사 변경 요청이 없습니다.")
        }

        this.companyId = toBeCompanyId!!
        this.email = "${toBeUsername!!}@${toBeDomain!!}"

        this.toBeCompanyId = null
        this.toBeDomain = null
        this.toBeUsername = null
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun updateIntroduction(introduction: String) {
        this.introduction = introduction
    }

    fun delete() {
        this.isDeleted = true
    }
}
