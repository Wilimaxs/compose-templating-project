plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "me.basehub.templatecompose"
    compileSdk = 37

    defaultConfig {
        applicationId = "me.basehub.templatecompose"
        minSdk = 28
        targetSdk = 37
        versionCode = 1 // Up this number avery distribution of the app
        versionName =
            "1.0.0" // Up this number every after app distribution and want to update the app

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix =
                "-alpha.1" // Internal = alpha, revisi alpha +1, external = beta, revisi beta +1
            // TODO(template): Update this URL if you want to use a different API endpoint.
            buildConfigField("String", "BASE_URL", "\"https://dummyjson.com/\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            versionNameSuffix =
                "-alpha.1" // Internal = alpha, revisi alpha +1, external = beta, revisi beta +1
            // TODO(template): Update this URL if you want to use a different API endpoint.
            buildConfigField("String", "BASE_URL", "\"https://dummyjson.com/\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(jdkVersion = 17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

    // Testing & Debugging
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Hilt (Dependency Injection)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    // Retrofit & Network
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    // Room Database
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.paging)

    // Utilities
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.timber)

    // Datastore
    implementation(libs.androidx.datastore.preferences)

    // Carautines (Non Blocking)
    implementation(libs.kotlinx.coroutines.android)

    // Pagination
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Auth
    implementation(libs.androidx.biometric)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

}
