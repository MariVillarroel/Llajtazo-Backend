plugins {
        kotlin("jvm") version "1.9.10"
        id("org.springframework.boot") version "3.1.5"
        id("io.spring.dependency-management") version "1.1.3"
}

group = "org.llajtazo"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17


repositories {
    mavenCentral()
}

dependencies {

    // --- KTOR & PROJECT DEPENDENCIES ---
    implementation("org.springframework.security:spring-security-crypto")
    implementation("io.ktor:ktor-server-core-jvm:2.3.5")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.5")
    implementation("mysql:mysql-connector-java:8.0.33")

    // --- TEST DEPENDENCIES ---
testImplementation(kotlin("test"))
// Spring Boot core
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
implementation("org.springframework.boot:spring-boot-starter-validation")
// MySQL connector
runtimeOnly("com.mysql:mysql-connector-j")
runtimeOnly("com.h2database:h2")
// Kotlin support
implementation("org.jetbrains.kotlin:kotlin-reflect")
implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
// Testing
testImplementation("org.springframework.boot:spring-boot-starter-test")
testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
