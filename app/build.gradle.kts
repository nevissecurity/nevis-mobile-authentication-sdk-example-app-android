plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.jetbrains.dokka) apply true
}

fun readProperty(name: String): Any? {
    if (project.hasProperty(name)) {
        return project.property(name)
    }
    println("Getting project property failed, returning null. Set $name as a project property in your ${project.projectDir}/gradle.properties")
    return null
}

fun readVersionCode(): Int {
    val property = readProperty("VERSION_CODE") as? String
    if (property != null) {
        return property.toInt()
    }
    return 1
}

fun readVersionName(): String {
    val property = readProperty("VERSION_NAME")
    if (property != null) {
        return "$property"
    }
    return ""
}

android {
    namespace = "ch.nevis.exampleapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "ch.nevis.exampleapp"
        minSdk = 24
        targetSdk = 35
        versionCode = readVersionCode()
        versionName = readVersionName()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        configureEach {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // Barcode Scanning, to scan QR codes
    implementation(libs.mlkit.barcode.scanning)

    // CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)

    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)

    // Dagger Hilt (dependency injection)
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Nevis Mobile Authentication SDK
    implementation(libs.nevis.mobile.authentication.sdk.android)

    // Logging
    implementation(libs.timber)

    // Retrofit (HTTP requests)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.json)
}

dokka {
    moduleName.set("${rootProject.name}-${project.name}")

    dokkaSourceSets.configureEach {
        reportUndocumented.set(true)
        includes.from("module.md")
        externalDocumentationLinks.register("nma-sdk") {
            url("https://docs.nevis.net/mobilesdk/api-references/javadoc/")
            packageListUrl("https://docs.nevis.net/mobilesdk/api-references/javadoc/element-list")
        }
    }
}
