package kr.co.jumso.domain.member.repository

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.query.spec.ExpressionOrderSpec
import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.querydsl.from.fetch
import com.linecorp.kotlinjdsl.querymodel.jpql.expression.Expressions
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.deleteQuery
import com.linecorp.kotlinjdsl.spring.data.listQuery
import com.linecorp.kotlinjdsl.spring.data.selectQuery
import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import jakarta.persistence.criteria.JoinType.LEFT
import kr.co.jumso.domain.auth.entity.RefreshToken
import kr.co.jumso.domain.member.entity.Member
import kr.co.jumso.domain.member.enumstorage.*
import kr.co.jumso.domain.member.enumstorage.BodyType.*
import kr.co.jumso.domain.member.enumstorage.BodyType.NONE
import kr.co.jumso.domain.member.enumstorage.Drink.NEVER
import kr.co.jumso.domain.member.enumstorage.RelationshipStatus.DOLSING
import kr.co.jumso.domain.member.enumstorage.RelationshipStatus.SINGLE
import kr.co.jumso.domain.member.enumstorage.Religion.*
import kr.co.jumso.domain.member.enumstorage.Smoke.*
import kr.co.jumso.util.AgeConverter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository: JpaRepository<Member, Long>, MemberCustomRepository

interface MemberCustomRepository {
    fun findNotDeletedById(memberId: Long): Member?
    fun findNotDeletedByEmail(email: String): Member?
    fun findNotDeletedByVerificationCode(verificationCode: String): Member?
    fun findNotDeletedMembers(): List<Member>
    fun findNotDeletedByEmailWithRefreshToken(email: String): Member?

    fun findNotDeletedByIdWithRefreshToken(id: Long): Member?
    fun findNotDeletedWithRefreshTokenByIdAndRefreshToken(id: Long, refreshToken: String): Member?

    fun findNotDeletedByIdWithMemberProperties(memberId: Long): Member?
    fun findNotDeletedByIdWithNotTheseCompaniesAndNoMatch(memberId: Long): Member?

    fun existsByEmail(email: String): Boolean

    fun deleteByMemberId(memberId: Long)

    fun findNotDeletedByCriteria(
        memberIds: Set<Long>,
        notTheseCompanyIds: List<Long>,
        noMatchIds: List<Long>,
        whatSexDoYouWant: Sex,

        // height
        howTallDoYouWantMin: Short,
        howTallDoYouWantMax: Short,

        // age
        howOldDoYouWantMin: Byte,
        howOldDoYouWantMax: Byte,

        // coordinates
        coordinates: Array<Pair<Double, Double>>,

        // bodyType
        whatKindOfBodyTypeDoYouWant: BodyType,

        // relationshipStatus
        whatKindOfRelationshipStatusDoYouWant: RelationshipStatus,

        // religion
        whatKindOfReligionDoYouWant: Religion,

        // smoke
        whatKindOfSmokeDoYouWant: Smoke,

        // drink
        whatKindOfDrinkDoYouWant: Drink,
    ): List<Member>
}

class MemberCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory,

    private val entityManager: EntityManager,

    private val context: JpqlRenderContext,
    private val renderer: JpqlRenderer,

    private val ageConverter: AgeConverter,
): MemberCustomRepository {
    override fun findNotDeletedById(memberId: Long): Member? {
        return queryFactory.selectQuery<Member> {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::id).equal(memberId))
            where(column(Member::isDeleted).equal(false))
        }.resultList.firstOrNull()
    }

    override fun findNotDeletedByEmail(email: String): Member? {
        return queryFactory.selectQuery<Member> {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::email).equal(email))
            where(column(Member::isDeleted).equal(false))
        }.resultList.firstOrNull()
    }

    override fun findNotDeletedByVerificationCode(verificationCode: String): Member? {
        return queryFactory.selectQuery<Member> {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::verificationCode).equal(verificationCode))
            where(column(Member::isDeleted).equal(false))
        }.resultList.firstOrNull()
    }

    override fun findNotDeletedMembers(): List<Member> {
        return queryFactory.listQuery {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::isDeleted).equal(false))
            orderBy(ExpressionOrderSpec(column(Member::id), true))
        }
    }

    override fun findNotDeletedByEmailWithRefreshToken(email: String): Member? {
        return queryFactory.selectQuery {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::email).equal(email))
            where(column(Member::isDeleted).equal(false))
            fetch(Member::refreshToken, joinType = LEFT)
        }.resultList.firstOrNull()
    }

    override fun findNotDeletedByIdWithRefreshToken(id: Long): Member? {
        return queryFactory.selectQuery {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::id).equal(id))
            where(column(Member::isDeleted).equal(false))
            fetch(Member::refreshToken, joinType = LEFT)
        }.resultList.firstOrNull()
    }

    override fun findNotDeletedWithRefreshTokenByIdAndRefreshToken(id: Long, refreshToken: String): Member? {
        return queryFactory.selectQuery {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::id).equal(id))
            where(column(Member::isDeleted).equal(false))
            where(column(Member::refreshToken).isNotNull().and(column(RefreshToken::token).equal(refreshToken)))
            fetch(Member::refreshToken, joinType = LEFT)
        }.resultList.firstOrNull()
    }

    override fun findNotDeletedByIdWithMemberProperties(memberId: Long): Member? {
        return queryFactory.selectQuery {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::id).equal(memberId))
            where(column(Member::isDeleted).equal(false))
            fetch(Member::memberProperties, joinType = LEFT)
        }.resultList.firstOrNull()
    }

    override fun findNotDeletedByIdWithNotTheseCompaniesAndNoMatch(memberId: Long): Member? {
        return queryFactory.selectQuery {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::id).equal(memberId))
            where(column(Member::isDeleted).equal(false))
            fetch(Member::notTheseCompanies, joinType = LEFT)
            fetch(Member::noMatch, joinType = LEFT)
        }.resultList.firstOrNull()
    }

    override fun existsByEmail(email: String): Boolean {
        return queryFactory.selectQuery<Member> {
            select(entity(Member::class))
            from(entity(Member::class))
            where(column(Member::email).equal(email))
        }.resultList.isNotEmpty()
    }

    override fun deleteByMemberId(memberId: Long) {
        queryFactory.deleteQuery<Member> {
            where(column(Member::id).equal(memberId))
        }
    }

    override fun findNotDeletedByCriteria(
        // In memberIds
        memberIds: Set<Long>,

        // NotIn NotTheseCompanies
        notTheseCompanyIds: List<Long>,

        // NotIn No Match
        noMatchIds: List<Long>,

        // sex
        whatSexDoYouWant: Sex,

        // height
        howTallDoYouWantMin: Short,
        howTallDoYouWantMax: Short,

        // age
        howOldDoYouWantMin: Byte,
        howOldDoYouWantMax: Byte,

        // coordinates
        coordinates: Array<Pair<Double, Double>>,

        // bodyType
        whatKindOfBodyTypeDoYouWant: BodyType,

        // relationshipStatus
        whatKindOfRelationshipStatusDoYouWant: RelationshipStatus,

        // religion
        whatKindOfReligionDoYouWant: Religion,

        // smoke
        whatKindOfSmokeDoYouWant: Smoke,

        // drink
        whatKindOfDrinkDoYouWant: Drink,
    ): List<Member> {
        val query = jpql {
            select(path(Member::id))
                .from(entity(Member::class))
                .whereAnd(
                    path(Member::isDeleted).equal(false),
                    path(Member::id).`in`(memberIds),
                    path(Member::id).notIn(notTheseCompanyIds),
                    path(Member::id).notIn(noMatchIds),
                    path(Member::sex).equal(whatSexDoYouWant),
                    path(Member::height).between(howTallDoYouWantMin, howTallDoYouWantMax),
                    // 2024 - 30 = 1994
                    // 2024 - 24 = 2000
                    path(Member::bornAt).between(
                        ageConverter.convertAgeToBirthYearMin(howOldDoYouWantMax),
                        ageConverter.convertAgeToBirthYearMax(howOldDoYouWantMin)
                    ),
                    path(Member::latitude).between(coordinates[0].first, coordinates[1].first),
                    path(Member::longitude).between(coordinates[0].second, coordinates[1].second),
                    // ToDo: BodyType 동적 쿼리 작동 확인
                    path(Member::bodyType).equal(
                        when (whatKindOfBodyTypeDoYouWant) {
                            NONE -> Expressions.nullValue()
                            SKINNY -> Expressions.value(SKINNY)
                            SLIM -> Expressions.value(SLIM)
                            NORMAL -> Expressions.value(NORMAL)
                            FAT -> Expressions.value(FAT)
                            MUSCLE -> Expressions.value(MUSCLE)
                        }
                    ),
                    path(Member::relationshipStatus).equal(
                        when (whatKindOfRelationshipStatusDoYouWant) {
                            SINGLE -> Expressions.value(SINGLE)
                            DOLSING -> Expressions.value(DOLSING)
                        }
                    ),
                    path(Member::religion).equal(
                        when (whatKindOfReligionDoYouWant) {
                            Religion.NONE -> Expressions.nullValue()
                            CHRISTIAN -> Expressions.value(CHRISTIAN)
                            CATHOLIC -> Expressions.value(CATHOLIC)
                            ORTHODOX -> Expressions.value(ORTHODOX)
                            JUDAISM -> Expressions.value(JUDAISM)
                            BUDDHISM -> Expressions.value(BUDDHISM)
                            ISLAM -> Expressions.value(ISLAM)
                            HINDUISM -> Expressions.value(HINDUISM)
                            OTHER -> Expressions.value(OTHER)
                        }
                    ),
                    path(Member::smoke).equal(
                        when (whatKindOfSmokeDoYouWant) {
                            Smoke.NONE -> Expressions.nullValue()
                            NO -> Expressions.value(NO)
                            SOMETIMES -> Expressions.value(SOMETIMES)
                            OFTEN -> Expressions.value(OFTEN)
                        }
                    ),
                    path(Member::drink).equal(
                        when (whatKindOfDrinkDoYouWant) {
                            Drink.NONE -> Expressions.nullValue()
                            NEVER -> Expressions.nullValue()
                            Drink.SOMETIMES -> Expressions.value(Drink.SOMETIMES)
                            Drink.OFTEN -> Expressions.value(Drink.OFTEN)
                        }
                    ),
                )
        }

        val rendered = renderer.render(query, context)

        val jpaQuery: Query = entityManager.createQuery(rendered.query).apply {
            rendered.params.forEach { (key, value) ->
                setParameter(key, value)
            }
            // 최대 10개 조회
            maxResults = 10
        }

        return jpaQuery.resultList as List<Member>
    }
}
