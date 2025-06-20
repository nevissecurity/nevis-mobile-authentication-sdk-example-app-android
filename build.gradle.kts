import java.io.FileInputStream
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
val customFooterMessage = "© 2025 made with ❤️ by Nevis"
val customLogoFile = "$projectDir/logo-style.css"

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.parcelize) apply false
    alias(libs.plugins.jetbrains.kotlin.kapt) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
    alias(libs.plugins.jetbrains.dokka) apply true
}

fun getConfig(name: String): String {
    val localPropertiesFile = File(rootProject.rootDir, "local.properties")
    if (localPropertiesFile.exists()) {
        val localProperties = Properties().apply {
            load(FileInputStream(localPropertiesFile))
        }
        if (localProperties.containsKey(name)) {
            return localProperties.getProperty(name)
        }
    }
    val env = System.getenv(name)
    if (env != null) {
        return env
    }
    val prop = System.getProperty(name)
    if (prop != null) {
        return prop
    }
    (project.property(name) as? String)?.let {
        return it
    }

    println("Getting env variable failed, returning empty: set $name as environment variable or as system property in your ~/.gradle/gradle.properties")
    return ""
}

allprojects {
    repositories {
        mavenLocal()
        maven {
            url = uri("https://maven.pkg.github.com/nevissecurity/nevis-mobile-authentication-sdk-android-package")
            credentials {
                username = getConfig("GH_USERNAME")
                password = getConfig("GH_PERSONAL_ACCESS_TOKEN")
            }
        }
        google {
            content {
                excludeGroupByRegex("ch\\.nevis\\..*")
            }
        }
        mavenCentral {
            content {
                excludeGroupByRegex("ch\\.nevis\\..*")
            }
        }
    }
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(file("build/dokka/${project.name}"))
        failOnWarning.set(false)
        suppressInheritedMembers.set(true)
        suppressObviousFunctions.set(true)
    }

    pluginsConfiguration.html {
        customStyleSheets.from(customLogoFile)
        footerMessage.set(customFooterMessage)
    }
}

dependencies {
    dokka(project(":app"))
}
