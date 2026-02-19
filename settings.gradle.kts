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

// I develop these alongside each other
// you can get it from https://github.com/gkursi/multirender
includeBuild("../multirender")