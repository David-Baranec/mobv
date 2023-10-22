pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = "eyJ1IjoieGJhcmFuZWNkIiwiYSI6ImNsbm13MWozejAwOTAydnBmZnZrcWl2eTMifQ.EJhgjDiQTbbtYbPLYw_5BQ"
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "Cvicenie2"
include(":app")

 