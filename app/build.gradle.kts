import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile


plugins {
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
    id("com.gradleup.shadow") version "9.2.2"
    kotlin("jvm")
}

group = "cu.adroid.not.gradleoffline"
version = "1.1.3"
var kotlinVersion = "2.1.20"

repositories {
    //mavenCentral()
}

dependencies {
  implementation("junit:junit:4.13.2")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
  implementation("com.google.code.gson:gson:2.9.0")
  implementation("com.squareup.okhttp3:okhttp:4.9.0")
  implementation(compose.desktop.currentOs)
  implementation("org.jetbrains.compose.material:material-icons-core:1.7.3")
  implementation("commons-codec:commons-codec:1.17.1")
  implementation("org.apache.commons:commons-text:1.12.0")
  implementation("org.apache.maven:maven-artifact:3.9.10")
  //testImplementation 'org.jetbrains.kotlin:kotlin-test-junit'
}


tasks.apply{
    withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        }
    }
    withType<Jar> {
        manifest{
            attributes["Main-Class"] = "cu.adroid.not.gradleoffline.MainKt"
        }
    }
    withType<Test> {
        useJUnit()
    }
}

compose.desktop {
    application {
        mainClass = "cu.adroid.not.gradleoffline.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Gradle Offline Manager"
            packageVersion = "$version"
        }
    }
}
