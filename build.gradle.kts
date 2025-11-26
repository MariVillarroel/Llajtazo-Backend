plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "1.9.22"

    id("io.ktor.plugin") version "2.3.5"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // --- KTOR & PROJECT DEPENDENCIES ---
    implementation("io.ktor:ktor-server-core-jvm:2.3.5")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.5")
    implementation("mysql:mysql-connector-java:8.0.33")

    // --- TEST DEPENDENCIES ---
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    implementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(24)
}