package com.example.simple_blog.converter

import com.example.simple_blog.enumstrorage.MemberRole
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class MemberRoleConverter : AttributeConverter<MemberRole, String> {
    override fun convertToDatabaseColumn(attribute: MemberRole): String = attribute.name

    override fun convertToEntityAttribute(dbData: String): MemberRole =
        MemberRole.entries.first { it.name.equals(dbData) }
}