package kr.co.jumso.company.entity

import jakarta.persistence.CascadeType.ALL
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotNull
import kr.co.jumso.AuditingEntity
import kr.co.jumso.auth.entity.CompanyEmail

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
