plugins {
}

dependencies {
    // domain
        implementation(project(":domain"))

    // DataBase
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("com.mysql:mysql-connector-j:8.0.33")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Security
        implementation("org.springframework.boot:spring-boot-starter-security")

    // Kotlin JDSL
        implementation("com.linecorp.kotlin-jdsl:spring-data-kotlin-jdsl-starter-jakarta:2.2.1.RELEASE")
        implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.5.2")
        implementation("com.linecorp.kotlin-jdsl:jpql-render:3.5.2")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
