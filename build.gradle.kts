val ktorVersion: String by project

plugins {
    kotlin("multiplatform") version "1.4.10"
}

group = "me.lasta.lambdaruntime"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    @Suppress("UNUSED_VARIABLE") val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-network-tls:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
                implementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
            }
        }
        val nativeMain by getting {
            dependencies {
                // Use cio client when ktor supports TLS connection on native target.
                implementation("io.ktor:ktor-client-curl:$ktorVersion")
            }
        }
        val nativeTest by getting {
            dependencies {
                implementation("io.ktor:ktor-client-mock-native:$ktorVersion")
            }
        }
    }
}

tasks {
    wrapper {
        gradleVersion = "6.6.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}
