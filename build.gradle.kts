plugins {
    kotlin("jvm") version "1.9.10"
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("plugin.spring") version "1.9.10"

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
    //implementation("mysql:mysql-connector-java:8.0.33")

    // --- SPRING BOOT CORE ---
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // --- DATABASE CONNECTORS ---
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2")

    // --- KOTLIN SUPPORT ---
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // --- TEST DEPENDENCIES ---
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Mockito-Kotlin (para usar whenever/any en Kotlin)
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.mockito:mockito-inline:5.2.0") // permite mockear clases finales
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}