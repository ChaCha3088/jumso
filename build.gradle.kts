import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.3.2" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false

    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24" apply false
    kotlin("plugin.jpa") version "1.9.24" apply false
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.24" apply false
}

allprojects {
    group = "com.jumso"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    apply(plugin = "kotlin")

    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    apply(plugin = "kotlin-jpa")
//    apply(plugin = "kotlin-spring") //all-open

    dependencies {
        // Security
        implementation("org.springframework.boot:spring-boot-starter-security")
        // JWT
        implementation("com.auth0:java-jwt:4.4.0")
        // Validation
        implementation("org.springframework.boot:spring-boot-starter-validation")

        // Web
        implementation("org.springframework.boot:spring-boot-starter-web")

        // DataBase
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation("com.mysql:mysql-connector-j:8.0.33")
        // Kotlin JDSL
        implementation("com.linecorp.kotlin-jdsl:spring-data-kotlin-jdsl-starter-jakarta:2.2.1.RELEASE")
        // Tweeter Snowflake
        implementation("com.littlenb:snowflake:1.0.5")

        // Logging
        implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.1")

        // AWS Parameter Store
        implementation("io.awspring.cloud:spring-cloud-starter-aws-parameter-store-config:2.3.3")

        // ETC
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        // Kotlin Faker
        implementation("io.github.serpro69:kotlin-faker:1.16.0")

        // Test
        testImplementation(kotlin("test"))
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

// was 설정
project(":was") {
    val jar: Jar by tasks
    jar.enabled = true

    val bootJar: BootJar by tasks
    bootJar.enabled = false
}
