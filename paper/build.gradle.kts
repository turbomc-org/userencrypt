plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "org.turbomc.userencrypt"
version = rootProject.findProperty("version")?.toString() ?: "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation(project(":core"))
    implementation("net.java.dev.jna:jna:5.14.0")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        dependencies {
            include(project(":core"))
            include(dependency("net.java.dev.jna:jna"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
