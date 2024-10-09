package kr.co.jumso.domain.member.entity

import kr.co.jumso.domain.AuditingEntity
import kr.co.jumso.domain.auth.entity.RefreshToken
import kr.co.jumso.domain.chat.entity.MemberChatRoom
import kr.co.jumso.domain.company.entity.Company
import kr.co.jumso.domain.member.enumstorage.*
import kr.co.jumso.enumstrorage.MemberRole
import kr.co.jumso.enumstrorage.MemberRole.USER
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.ORDINAL
import jakarta.persistence.FetchType.LAZY
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
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

    fun addMemberProperty(memberProperty: MemberProperty) {
        memberProperties.add(memberProperty)
    }

    fun addNotTheseCompany(memberNotTheseCompany: MemberNotTheseCompany) {
        notTheseCompanies.add(memberNotTheseCompany)
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
