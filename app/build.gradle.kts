import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.firebase.sneakov"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.firebase.sneakov"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
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

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.3")
    // Retrofit + Moshi
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Moshi (KSP codegen)
    implementation("com.squareup.moshi:moshi:1.15.2")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.2")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")
    // Koin DI
    implementation("io.insert-koin:koin-core:3.4.3")
    implementation("io.insert-koin:koin-android:3.4.3")
    implementation("io.insert-koin:koin-androidx-compose:3.4.3")
    // Icon pack
    //implementation("br.com.devsrsouza.compose.icons.android:material:1.1.0")
    implementation("br.com.devsrsouza.compose.icons:font-awesome:1.1.1")
    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    // Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics:23.0.0")
    // Scene View
    implementation("io.github.sceneview:sceneview:2.3.0")
}