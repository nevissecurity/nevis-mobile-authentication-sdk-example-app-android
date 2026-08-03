// Top-level build file where you can add configuration options common to all subprojects/modules.
val customFooterMessage = "© 2025 made with ❤️ by Nevis"
val customLogoFile = "$projectDir/logo-style.css"

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.parcelize) apply false
    alias(libs.plugins.jetbrains.ksp) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
    alias(libs.plugins.jetbrains.dokka) apply true
    alias(libs.plugins.ktlint) apply true
}

fun getConfig(name: String): String {
    val localPropertiesFile = rootDir.resolve("local.properties")
    if (localPropertiesFile.exists()) {
        val localProperties = java.util.Properties()
        localPropertiesFile.inputStream().use { localProperties.load(it) }
        localProperties.getProperty(name)?.let { return it }
    }
    System.getenv(name)?.let { return it }
    System.getProperty(name)?.let { return it }
    providers.gradleProperty(name).orNull?.let { return it }

    throw GradleException(
        "Getting configuration with name $name failed! Set it as environment variable or as local/project/system property."
    )
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

// Workaround for https://github.com/JLLeitschuh/ktlint-gradle/issues/1037:
// the ktlint Gradle plugin only auto-detects the version from ktlint-plugins.properties
// for the root project, not for subprojects, so it needs to be propagated explicitly.
ktlint.version.let { ktlintVersion ->
    subprojects {
        pluginManager.withPlugin("org.jlleitschuh.gradle.ktlint") {
            configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
                version.set(ktlintVersion)
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
