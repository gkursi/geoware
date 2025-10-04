import org.gradle.kotlin.dsl.project

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}

buildCache {
    local.isEnabled = false
}

includeBuild("../multirender")