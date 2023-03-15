plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("kapt") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.soberanacraft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    implementation(kotlin("reflect"))
    kapt("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.4") {
        exclude(group = "org.slf4j")
    }

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation("io.ktor:ktor-client-core:2.2.3")
    implementation("io.ktor:ktor-client-cio:2.2.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.3")

    implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("io.arrow-kt:arrow-core:1.1.2")

}

kotlin {
    jvmToolchain(17)
}

tasks.build {
    finalizedBy("shadowJar")
}