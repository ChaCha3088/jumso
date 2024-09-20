plugins {
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    // Security
        implementation("org.springframework.boot:spring-boot-starter-security")

    // DataBase
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")

        // Kotlin JDSL
        implementation("com.linecorp.kotlin-jdsl:spring-data-kotlin-jdsl-starter-jakarta:2.2.1.RELEASE")

    // Email
        // Spring Email
        implementation("org.springframework.boot:spring-boot-starter-mail")
}
