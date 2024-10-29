package kr.co.jumso.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.jumso.enumstorage.member.MemberRole

@Converter(autoApply = true)
class MemberRoleConverter : AttributeConverter<kr.co.jumso.enumstorage.member.MemberRole, String> {
    override fun convertToDatabaseColumn(attribute: kr.co.jumso.enumstorage.member.MemberRole): String = attribute.name

    override fun convertToEntityAttribute(dbData: String): kr.co.jumso.enumstorage.member.MemberRole =
        kr.co.jumso.enumstorage.member.MemberRole.entries.first { it.name == dbData }
}
