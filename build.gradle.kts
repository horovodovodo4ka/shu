import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    kotlin("kapt") version "1.3.50"
}


group = "pro.horovodovodo4ka"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

    implementation("pro.horovodovodo4ka.astaroth:astaroth:0.4.1")

    kapt("pro.horovodovodo4ka.kodable:processor:1.2.11")
    api ("pro.horovodovodo4ka.kodable:core:1.2.11")

    api("com.github.kittinunf.fuel:fuel:2.2.1") {
        exclude(group = "org.jetbrains.kotlin")
    }
    api("com.github.kittinunf.fuel:fuel-coroutines:2.2.1") {
        exclude(group = "org.jetbrains.kotlin")
    }

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}