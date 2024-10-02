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
        implementation("com.mysql:mysql-connector-j:8.0.33")

    // Kotlin JDSL
        implementation("com.linecorp.kotlin-jdsl:spring-data-kotlin-jdsl-starter-jakarta:2.2.1.RELEASE")
        implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.5.2")
        implementation("com.linecorp.kotlin-jdsl:jpql-render:3.5.2")

    // Kafka
        implementation("org.springframework.kafka:spring-kafka")
}
