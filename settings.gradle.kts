pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
val properties = java.util.Properties()
file("local.properties").inputStream().use { properties.load(it) }

// add to local.properties mapboxPrivateKey= ""
val mapboxPrivateKey: String? = properties.getProperty("mapboxPrivateKey")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = mapboxPrivateKey
                //password = "sk.eyJ1IjoieGJhcmFuZWNkIiwiYSI6ImNsbzM1OW0yODBlaTIybXVkZmp2aWRrZmcifQ.Eq9Y6VGWGVN2gkoJ3Yl1TQ"
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "Cvicenie2"
include(":app")

 