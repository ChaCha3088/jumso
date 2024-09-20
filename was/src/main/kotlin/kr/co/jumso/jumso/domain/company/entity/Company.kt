package kr.co.jumso.domain.company.entity

import com.example.jumso.domain.AuditingEntity
import com.example.jumso.domain.auth.entity.CompanyEmail
import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotNull

@Entity
class Company(
    name: String,
): AuditingEntity() {
    var name: String = name
        protected set

    @NotNull
    @OneToMany(mappedBy = "company", cascade = [ALL], orphanRemoval = true, fetch = LAZY)
    var companyEmails: MutableSet<CompanyEmail> = mutableSetOf()
        protected set

    fun addCompanyEmails(emails: List<String>) {
        companyEmails.addAll(emails.map { CompanyEmail(it, this) })
    }
}
