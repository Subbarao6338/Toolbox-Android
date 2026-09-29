plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

fun getSigningProperty(envName: String, keychainAccount: String, keychainService: String, fallback: String = ""): String {
    val envVal = System.getenv(envName)
    if (!envVal.isNullOrEmpty()) return envVal
    return try {
        Runtime.getRuntime()
            .exec(arrayOf("security", "find-generic-password", "-a", keychainAccount, "-s", keychainService, "-w"))
            .inputStream.bufferedReader().readLine()?.takeIf { it.isNotBlank() } ?: fallback
    } catch (_: Throwable) {
        fallback
    }
}

android {
    namespace = "com.toolbox"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vedtechnologies.toolbox"
        minSdk = 26
        targetSdk = 36
        versionCode = 14
        versionName = "1.7.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val userJks = file("${System.getProperty("user.home")}/.android/release.jks")
            val backupJks = file("${project.rootDir}/backup/release.jks")
            storeFile = if (userJks.exists()) userJks else backupJks
            val pwd = getSigningProperty("RELEASE_STORE_PASSWORD", "release-key", "android-release-keystore", "android")
            storePassword = pwd
            keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: "release-key"
            keyPassword = getSigningProperty("RELEASE_KEY_PASSWORD", "release-key", "android-release-keystore", pwd)
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk.debugSymbolLevel = "FULL"
        }
    }

    buildFeatures {
        compose = true
    }

    kotlin {
        jvmToolchain(21)
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.animation)
    implementation(libs.compose.foundation)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.navigation.compose)

    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.service)

    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)

    implementation(libs.datastore.preferences)

    implementation(libs.zxing.core)
    implementation(libs.mlkit.text.recognition)

    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.core.splashscreen)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.glance.appwidget)
    implementation(libs.glance.material3)

    implementation(libs.lottie.compose)
}
