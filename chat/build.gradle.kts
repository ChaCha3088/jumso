plugins {
}

dependencies {
    // was
    implementation(project(":was"))

    // Web Socket
        implementation("org.springframework.boot:spring-boot-starter-websocket")

    // DataBase
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("com.mysql:mysql-connector-j:8.0.33")

    // Kafka
        implementation("org.springframework.kafka:spring-kafka")
}
