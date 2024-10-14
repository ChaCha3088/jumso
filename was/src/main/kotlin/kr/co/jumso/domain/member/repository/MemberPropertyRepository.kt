package kr.co.jumso.domain.member.repository

import com.linecorp.kotlinjdsl.querydsl.expression.column
import com.linecorp.kotlinjdsl.spring.data.SpringDataQueryFactory
import com.linecorp.kotlinjdsl.spring.data.listQuery
import kr.co.jumso.domain.member.entity.MemberProperty
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberPropertyRepository: JpaRepository<MemberProperty, Long>, MemberPropertyCustomRepository

interface MemberPropertyCustomRepository {
    fun findByMemberId(memberId: Long): MutableSet<MemberProperty>

    fun findByCriteria(
        memberId: Long,
        propertyIds: Set<Long>,
    ): Set<Long>
}

class MemberPropertyCustomRepositoryImpl(
    private val queryFactory: SpringDataQueryFactory
): MemberPropertyCustomRepository {
    override fun findByMemberId(memberId: Long): MutableSet<MemberProperty> {
        return queryFactory.listQuery<MemberProperty> {
            select(entity(MemberProperty::class))
            from(entity(MemberProperty::class))
            where(column(MemberProperty::memberId).equal(memberId))
        }.toMutableSet()
    }

    override fun findByCriteria(
        memberId: Long,
        propertyIds: Set<Long>
    ): Set<Long> {
        //SELECT member_id
        //FROM member_property
        //WHERE property_id IN (property_id_1, property_id_2, property_id_3)
        //GROUP BY member_id
        //HAVING COUNT(DISTINCT property_id) = 3;  -- 여기에 주어진 property_id의 개수를 입력하세요.
        return queryFactory.listQuery<Long> {
            select(column(MemberProperty::memberId))
            from(entity(MemberProperty::class))
            // 본인 제외
            where(column(MemberProperty::memberId).notEqual(memberId))
            // propertyIds에 속한 propertyId들 중 하나라도 가지고 있는 memberIds
            where(column(MemberProperty::propertyId).`in`(propertyIds))
            // memberIds로 group
            groupBy(column(MemberProperty::memberId))
            // propertyIds의 개수와 같은 propertyId를 가진 memberIds
            having(countDistinct(column(MemberProperty::propertyId)).equal(propertyIds.size.toLong()))
        }.toSet()
    }
}
