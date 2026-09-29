pluginManagement {
    repositories {
        maven("https://maven-central.storage-download.googleapis.com/maven2/")
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        maven("https://maven-central.storage-download.googleapis.com/maven2/")
        google()
        gradlePluginPortal()
    }
}

rootProject.name = "Toolbox"
include(":app")
