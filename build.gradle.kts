
import org.jetbrains.dokka.gradle.DokkaTaskPartial
import java.io.FileInputStream
import java.net.URL
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

subprojects {
    apply(plugin = "org.jetbrains.dokka")

    tasks.dokkaHtml {
        moduleName.set("${rootProject.name}-${project.name}")
        outputDirectory.set(file("build/dokka/${project.name}"))
        failOnWarning.set(false)
        suppressInheritedMembers.set(true)
        suppressObviousFunctions.set(true)

        dokkaSourceSets.configureEach {
            reportUndocumented.set(true)
            includes.from("module.md")
            externalDocumentationLink(
                url = URL("https://docs.nevis.net/mobilesdk/${getConfig("VERSION_API_REFERENCE")}/api-references/javadoc/"),
                packageListUrl = URL("https://docs.nevis.net/mobilesdk/${getConfig("VERSION_API_REFERENCE")}/api-references/javadoc/element-list")
            )
        }

        pluginsMapConfiguration.set(
            mapOf(
                "org.jetbrains.dokka.base.DokkaBase" to """{ "customStyleSheets": ["$customLogoFile"], "footerMessage": "$customFooterMessage"}"""
            )
        )
    }

    tasks.withType<DokkaTaskPartial>().configureEach {
        moduleName.set("${rootProject.name}-${project.name}")
        failOnWarning.set(false)
        suppressInheritedMembers.set(true)
        suppressObviousFunctions.set(true)

        dokkaSourceSets.configureEach {
            reportUndocumented.set(true)
            includes.from("module.md")
            externalDocumentationLink(
                url = URL("https://docs.nevis.net/mobilesdk/${getConfig("VERSION_API_REFERENCE")}/api-references/javadoc/"),
                packageListUrl = URL("https://docs.nevis.net/mobilesdk/${getConfig("VERSION_API_REFERENCE")}/api-references/javadoc/element-list")
            )
        }

        pluginsMapConfiguration.set(
            mapOf(
                "org.jetbrains.dokka.base.DokkaBase" to """{ "customStyleSheets": ["$customLogoFile"], "footerMessage": "$customFooterMessage"}"""
            )
        )
    }
}

afterEvaluate {
    tasks.dokkaHtmlMultiModule {
        pluginsMapConfiguration.set(
            mapOf(
                "org.jetbrains.dokka.base.DokkaBase" to """{ "customStyleSheets": ["$customLogoFile"], "footerMessage": "$customFooterMessage"}"""
            )
        )
    }
}
