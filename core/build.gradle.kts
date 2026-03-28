plugins {
    kotlin("jvm") version "2.3.0"
    signing
    `maven-publish`
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "org.turbomc"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.java.dev.jna:jna:5.18.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates("org.turbomc", "core", "0.1.0")

    pom {
        name.set("UserEncrypt Core")
        description.set("Core library for UserEncrypt")
        url.set("https://github.com/turbomc-org/userencrypt")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("harihar-nautiyal")
                name.set("Harihar Nautiyal")
                email.set("me@hariharnautiyal.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/turbomc-org/userencrypt.git")
            developerConnection.set("scm:git:ssh://github.com:turbomc-org/userencrypt.git")
            url.set("https://github.com/turbomc-org/userencrypt")
        }
    }
}
