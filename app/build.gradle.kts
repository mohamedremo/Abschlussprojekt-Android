plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")version "2.0.10-1.0.24"
    id("androidx.navigation.safeargs.kotlin")
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.abschlussprojekt"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.abschlussprojekt"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.ui.desktop)
    implementation(libs.androidx.datastore.core.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //VIEWMODEL
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v262)
    //LIVE-DATA
    implementation(libs.androidx.lifecycle.livedata.ktx)
    //FRAGMENT
    implementation(libs.androidx.fragment.ktx)

    //COIL
    implementation(libs.coil)

    //ROOM
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //MOSHI
    implementation(libs.moshi.kotlin)

    //Moshi Codegen (Annotation Processor)
    ksp(libs.moshi.kotlin.codegen)

    //Retrofit-Moshi Konverter
    implementation(libs.converter.moshi)

    //FIREBASE
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)

    //logger
    implementation(libs.logging.interceptor)

    //GSON
    implementation(libs.gson)

}