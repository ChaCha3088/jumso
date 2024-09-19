plugins {
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    // Email
        // Spring Email
        implementation("org.springframework.boot:spring-boot-starter-mail")
}
