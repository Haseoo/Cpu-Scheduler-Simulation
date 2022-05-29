import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"


}
group = "com.github.haseoo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

var currentOS: OperatingSystem = OperatingSystem.current()

var platform = "win"
if (currentOS.isLinux) {
    platform = "linux"
} else if (currentOS.isMacOsX) {
    platform = "mac"
}

dependencies {
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.openjfx:javafx-base:11:${platform}")
    implementation("org.openjfx:javafx-graphics:11:${platform}")
    implementation("org.openjfx:javafx-controls:11:${platform}")
    implementation("org.openjfx:javafx-fxml:11:${platform}")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}
application {
    mainClassName = "com.github.haseoo.cpuschedsim.MainKt"
}