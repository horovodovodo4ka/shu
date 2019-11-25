import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
}

group = "pro.horovodovodo4ka"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(rootProject)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

    implementation("pro.horovodovodo4ka.astaroth:astaroth:0.4.1")
    kapt("pro.horovodovodo4ka.kodable:processor:2.0.12")
    api ("pro.horovodovodo4ka.kodable:core:2.0.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}