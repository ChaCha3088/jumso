plugins {
}

dependencies {
    // root
    implementation(project(":root"))

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Kotlin JDSL
        implementation("com.linecorp.kotlin-jdsl:spring-data-kotlin-jdsl-starter-jakarta:2.2.1.RELEASE")
        implementation("com.linecorp.kotlin-jdsl:jpql-dsl:3.5.2")
        implementation("com.linecorp.kotlin-jdsl:jpql-render:3.5.2")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
