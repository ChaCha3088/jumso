plugins {
}

dependencies {
    // DataBase
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.mysql:mysql-connector-j:8.0.33")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // domain
        implementation(project(":domain"))

    // was
        implementation(project(":was"))

    // Web Socket
        implementation("org.springframework.boot:spring-boot-starter-websocket")
}
