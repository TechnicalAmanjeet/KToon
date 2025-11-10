plugins {
    kotlin("jvm") version "1.9.21"
    id("maven-publish")
    id("signing")
}

group = "com.amanjeet"
version = "0.1.0"
description = "Token-Oriented Object Notation (TOON) - A compact, human-readable format for LLM contexts (Kotlin implementation)"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.1")
    
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

// Configure Java sources jar
java {
    withSourcesJar()
    withJavadocJar()
}

// Publishing configuration for JitPack
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.TechnicalAmanjeet"
            artifactId = "ktoon"
            version = project.version.toString()
            
            from(components["java"])
            
            pom {
                name.set("KToon")
                description.set("Token-Oriented Object Notation (TOON) - A compact, human-readable format for LLM contexts (Kotlin implementation)")
                url.set("https://github.com/TechnicalAmanjeet/KToon")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("technicalamanjeet")
                        name.set("Amanjeet")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/TechnicalAmanjeet/KToon.git")
                    developerConnection.set("scm:git:ssh://github.com/TechnicalAmanjeet/KToon.git")
                    url.set("https://github.com/TechnicalAmanjeet/KToon")
                }
            }
        }
    }
}
