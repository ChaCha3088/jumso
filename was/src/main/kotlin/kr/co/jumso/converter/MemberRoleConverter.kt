package kr.co.jumso.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.jumso.enumstrorage.MemberRole

@Converter(autoApply = true)
class MemberRoleConverter : AttributeConverter<MemberRole, String> {
    override fun convertToDatabaseColumn(attribute: MemberRole): String = attribute.name

    override fun convertToEntityAttribute(dbData: String): MemberRole =
        MemberRole.entries.first { it.name == dbData }
}
