plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "me.basehub.templatecompose"
    compileSdk = 37

    defaultConfig {
        applicationId = "me.basehub.templatecompose"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Select the API environment at build time without adding separate app entry points.
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            // TODO(template): Replace with the development API URL for your app.
            buildConfigField("String", "API_BASE_URL", "\"https://dummyjson.com/\"")
        }
        create("staging") {
            dimension = "environment"
            // TODO(template): Replace with the staging API URL for your app.
            buildConfigField("String", "API_BASE_URL", "\"https://dummyjson.com/\"")
        }
        create("production") {
            dimension = "environment"
            // TODO(template): Replace with the production API URL for your app.
            buildConfigField("String", "API_BASE_URL", "\"https://dummyjson.com/\"")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
