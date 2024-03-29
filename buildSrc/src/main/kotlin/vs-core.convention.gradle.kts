plugins {
    kotlin("jvm")
    kotlin("kapt")
    `java-library`
    `maven-publish`
}

group = "org.valkyrienskies.core"
// Determine the version
val gitRevision = "git rev-parse HEAD".execute()
version = "1.1.0+" + gitRevision.substring(0, 10)

repositories {
    mavenCentral()
    maven {
        name = "VS Maven"
        url = uri(project.findProperty("vs_maven_url") ?: "https://maven.valkyrienskies.org/")

        val vsMavenUsername = project.findProperty("vs_maven_username") as String?
        val vsMavenPassword = project.findProperty("vs_maven_password") as String?

        if (vsMavenPassword != null && vsMavenUsername != null) {
            credentials {
                username = vsMavenUsername
                password = vsMavenPassword
            }
        }
    }
}

kapt {
    correctErrorTypes = true
}

publishing {
    repositories {
        val ghpUser = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")) as String?
        val ghpPassword = (project.findProperty("gpr.key") ?: System.getenv("TOKEN")) as String?
        // Publish to Github Packages
        if (ghpUser != null && ghpPassword != null) {
            println("Publishing to GitHub Packages ($version)")
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/ValkyrienSkies/vs-core")
                credentials {
                    username = ghpUser
                    password = ghpPassword
                }
            }
        }

        val vsMavenUsername = project.findProperty("vs_maven_username") as String?
        val vsMavenPassword = project.findProperty("vs_maven_password") as String?
        val vsMavenUrl = project.findProperty("vs_maven_url") as String?
        if (vsMavenUrl != null && vsMavenPassword != null && vsMavenUsername != null) {
            println("Publishing to VS Maven ($version)")
            maven {
                url = uri(vsMavenUrl)
                credentials {
                    username = vsMavenUsername
                    password = vsMavenPassword
                }
            }
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs += listOf("-Xjvm-default=all")
        }

        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions {
            freeCompilerArgs += listOf("-Xjvm-default=all")
        }
        kotlinOptions.jvmTarget = "1.8"
    }
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    compileTestJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

// region Automatically accept Gradle build scan TOS

if (hasProperty("buildScan")) {
    extensions.findByName("buildScan")?.withGroovyBuilder {
        setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
        setProperty("termsOfServiceAgree", "yes")
    }
}

// endregion

// region Util functions

fun String.execute(envp: Array<String>? = null, dir: File = projectDir): String {
    val process = Runtime.getRuntime().exec(this, envp, projectDir)
    return process.inputStream.reader().readText()
}

// endregion

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}

