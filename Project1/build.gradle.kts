plugins {
    kotlin("jvm") version "1.3.70"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.vertx:vertx-core:4.0.0-milestone4")
    implementation("io.vertx:vertx-lang-kotlin:4.0.0-milestone4")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:4.0.0-milestone4")

    implementation("io.vertx:vertx-jdbc-client:4.0.0-milestone4")
    implementation("io.vertx:vertx-pg-client:4.0.0-milestone4")

    implementation("org.postgresql:postgresql:42.2.10")

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}