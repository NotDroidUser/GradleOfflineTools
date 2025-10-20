buildscript{
    repositories {
//        maven {
//            url= uri("https://plugins.gradle.org/m2/")
//        }
    }
    dependencies {
        // __LATEST_COMPOSE_RELEASE_VERSION__
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.8.0")
        // __KOTLIN_COMPOSE_VERSION__
        classpath(kotlin("gradle-plugin", version = "2.1.20"))
        classpath("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.1.20")
    }
}
