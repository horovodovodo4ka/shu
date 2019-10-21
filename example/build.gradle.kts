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

    implementation("pro.horovodovodo4ka.astaroth:astaroth:0.4.1")
    kapt("pro.horovodovodo4ka.kodable:processor:1.2.11")
    api ("pro.horovodovodo4ka.kodable:core:1.2.11")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}