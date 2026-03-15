plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.molo4ko.navigationapi"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.molo4ko.navigationapi"
        minSdk = 24
        targetSdk = 33
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
}
dependencies {

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // Material
    implementation("com.google.android.material:material:1.9.0")

    // AndroidX
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Legacy support (только если реально используется старый код)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")

    // Local libs
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Unit tests
    testImplementation("junit:junit:4.13.2")

    // Android tests
    androidTestImplementation("androidx.navigation:navigation-testing:2.5.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}