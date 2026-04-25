pluginManagement {
    repositories {
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies" )
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
    }
}

rootProject.name = "ai-response-diff-viewer"