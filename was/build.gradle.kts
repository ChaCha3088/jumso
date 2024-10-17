plugins {
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"

    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.jpa") version "1.9.24"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.24"
}

repositories {
    mavenCentral()
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
    // JWT
    implementation("com.auth0:java-jwt:4.4.0")
    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // DataBase
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.mysql:mysql-connector-j:8.0.33")

    // Tweeter Snowflake
    implementation("com.littlenb:snowflake:1.0.5")

    // Logging
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.1")

    // ETC
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // Kotlin Faker
    implementation("io.github.serpro69:kotlin-faker:1.16.0")

    // AWS Parameter Store
    implementation("io.awspring.cloud:spring-cloud-starter-aws-parameter-store-config:2.3.3")

    // Test
    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Security
        implementation("org.springframework.boot:spring-boot-starter-security")


    // Kotlin JDSL
        implementation("com.linecorp.kotlin-jdsl:spring-data-kotlin-jdsl-starter-jakarta:2.2.1.RELEASE")
        implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.5.2")
        implementation("com.linecorp.kotlin-jdsl:jpql-render:3.5.2")

    // Kafka
        implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
