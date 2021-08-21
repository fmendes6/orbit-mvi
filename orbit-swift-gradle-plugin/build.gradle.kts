import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
    }
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.12.0"
}

apply(plugin = "org.jetbrains.kotlin.jvm")

gradlePlugin {
    plugins {
        create("org.orbitmvi.orbit.swift") {
            id = "org.orbitmvi.orbit.swift"
            displayName = "orbitswiftplugin"
            description = "Generate swift code for Orbit Multiplatform"
            implementationClass = "org.orbitmvi.orbit.swift.OrbitSwiftPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/orbit-mvi/orbit-mvi"
    vcsUrl = "https://github.com/orbit-mvi/orbit-mvi.git"
    tags = listOf("kotlin", "mvi", "swift", "multiplatform")
}

version = "1.0"//System.getenv("CIRCLE_TAG") ?: System.getProperty("CIRCLE_TAG") ?: "unknown"
group = "org.orbitmvi"

repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-klib:0.0.1")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.5.21")
    implementation("com.samskivert:jmustache:1.15")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}