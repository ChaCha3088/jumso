package kr.co.jumso.domain.member.entity

import com.example.jumso.domain.AuditingEntity
import com.example.jumso.domain.auth.entity.RefreshToken
import com.example.jumso.domain.chat.entity.MemberChatRoom
import com.example.jumso.domain.company.entity.Company
import com.example.jumso.domain.member.enumstorage.*
import com.example.jumso.enumstrorage.MemberRole
import com.example.jumso.enumstrorage.MemberRole.USER
import jakarta.persistence.*
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.EnumType.ORDINAL
import jakarta.persistence.FetchType.LAZY
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Entity
class Member(
    email: String,
    password: String,
    nickname: String,
    companyId: Long,
): AuditingEntity() {
    @Column(nullable = false, unique = true)
    @NotBlank
    var email: String = email
        protected set

    @Column(nullable = false)
    @NotBlank
    var password: String = password
        protected set

    @Column(nullable = false)
    @NotBlank
    var nickname: String = nickname
        protected set

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
    var company: Company? = null
        protected set

    @Column(name = "company_id")
    var companyId: Long = companyId
        protected set

    @Column(nullable = false)
    @NotNull
    var didYouWriteProfile: Boolean = false
        protected set

    // Member의 채팅
    @OneToMany(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var membersChatRooms: MutableList<MemberChatRoom> = mutableListOf()
        protected set

    @OneToMany(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var properties: MutableList<MemberProperty> = mutableListOf()
        protected set

    var bornAt: LocalDateTime? = null
        protected set

    @Enumerated(ORDINAL)
    var sex: Sex? = null
        protected set

    var height: Int? = null
        protected set

    @Enumerated(ORDINAL)
    var bodyType: BodyType? = null
        protected set

    @Column(length = 50)
    var job: String? = null
        protected set

    var marry: Marry? = null
        protected set

    @Enumerated(ORDINAL)
    var religion: Religion? = null
        protected set

    var smoke: Boolean? = null
        protected set

    @Enumerated(ORDINAL)
    var drink: Drink? = null
        protected set

    // x 좌표, 위도
    var latitude: Double? = null
        protected set

    // y 좌표, 경도
    var longitude: Double? = null
        protected set

    @Column(length = 1000)
    var introduction: String? = null
        protected set

    @Enumerated(ORDINAL)
    var whatSexDoYouWant: Sex? = null
        protected set

    var howOldDoYouWantMin: Int? = null
        protected set

    var howOldDoYouWantMax: Int? = null
        protected set

    @Enumerated(ORDINAL)
    var howFarCanYouGo: HowFar? = null
        protected set

    @Enumerated(ORDINAL)
    var whatKindOfBodyTypeDoYouWant: BodyType? = null
        protected set

    @Enumerated(ORDINAL)
    var whatKindOfMarryDoYouWant: Marry? = null
        protected set

    @Enumerated(ORDINAL)
    var whatKindOfReligionDoYouWant: Religion? = null
        protected set

    var whatKindOfSmokeDoYouWant: Boolean? = null
        protected set

    @Enumerated(ORDINAL)
    var whatKindOfDrinkDoYouWant: Drink? = null
        protected set

    @OneToMany(mappedBy = "member", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var notTheseCompanies: MutableList<MemberNotTheseCompany> = mutableListOf()
        protected set

    fun updateLastSignIn() {
        lastSignIn = now()
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    fun updateIntroduction(introduction: String) {
        this.introduction = introduction
    }
}
