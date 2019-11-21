import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    kotlin("kapt") version "1.3.50"
    maven
}

group = "pro.horovodovodo4ka"
version = "1.1.3"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("pro.horovodovodo4ka.astaroth:astaroth:0.4.1")

    kapt("pro.horovodovodo4ka.kodable:processor:1.2.11")
    implementation ("pro.horovodovodo4ka.kodable:core:1.2.11")

    implementation("com.github.kittinunf.fuel:fuel:2.2.1")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.2.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

apply(from = "$rootDir/mavenizer/gradle-mavenizer.gradle")