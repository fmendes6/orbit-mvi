import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    kotlin("kapt")
    id("com.android.library")
    id("kotlin-parcelize")
    kotlin("plugin.serialization") version "1.5.21"
}

kotlin {
    android()
    ios {
        binaries {
            framework {
                baseName = "shared"
            }
        }
    }
    sourceSets {

        val commonMain by getting {
            dependencies {
                api("org.orbit-mvi:orbit-core:4.1.3")
                api("dev.icerock.moko:mvvm-core:0.11.0") // only ViewModel, EventsDispatcher, Dispatchers.UI
                implementation("dev.icerock.moko:parcelize:0.7.1")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

                implementation("io.ktor:ktor-client-core:1.6.2")
                implementation("io.ktor:ktor-client-serialization:1.6.2")

                implementation("io.insert-koin:koin-core:3.1.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:1.6.2")

                implementation("org.orbit-mvi:orbit-viewmodel:4.1.3")

                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0-alpha03")

                // Dependency Injection
                implementation("io.insert-koin:koin-android:3.1.2")


            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting {
            //kotlin.srcDir("${buildDir.absolutePath}/generated/source/kaptKotlin/")

            dependencies {
                implementation("io.ktor:ktor-client-ios:1.6.2")
            }
        }
        val iosTest by getting
    }
}

android {
    compileSdk = 30
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 30
    }
}

val xcFrameworkPath = "$buildDir/xcode-frameworks/${project.name}.xcframework"

tasks.create<Delete>("deleteXcFramework") { delete = setOf(xcFrameworkPath) }

val buildXcFramework by tasks.registering {
    dependsOn("deleteXcFramework")
    group = "build"
    val mode = "Release"
    val frameworks = arrayOf("iosArm64", "iosX64")
        .map { kotlin.targets.getByName<KotlinNativeTarget>(it).binaries.getFramework(mode) }
    inputs.property("mode", mode)
    dependsOn(frameworks.map { it.linkTask })
    doLast { buildXcFramework(frameworks) }
}

fun Task.buildXcFramework(frameworks: List<org.jetbrains.kotlin.gradle.plugin.mpp.Framework>) {
    val buildArgs: () -> List<String> = {
        val arguments = mutableListOf("-create-xcframework")
        frameworks.forEach {
            arguments += "-framework"
            arguments += "${it.outputDirectory}/${project.name}.framework"
        }
        arguments += "-output"
        arguments += xcFrameworkPath
        arguments
    }
    exec {
        executable = "xcodebuild"
        args = buildArgs()
    }
}

tasks.getByName("build").dependsOn(buildXcFramework)