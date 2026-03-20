import java.util.Properties

rootProject.name = "angelos-project-secrand"

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven {
            val localProps = Properties()
            val localPropsFile = file("${rootProject.projectDir.path}/local.properties")
            if (localPropsFile.exists()) {
                localProps.load(localPropsFile.inputStream())
            }
            val repsyUsername = localProps.getProperty("repsy.username") ?: System.getenv("REPSY_USERNAME") ?: ""
            val repsyPassword = localProps.getProperty("repsy.password") ?: System.getenv("REPSY_PASSWORD") ?: ""
            credentials {
                username = repsyUsername
                password = repsyPassword
            }
            url = uri("https://repo.repsy.io/$repsyUsername/angelos-project")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":library")
include(":benchmark")
include(":jazzer")