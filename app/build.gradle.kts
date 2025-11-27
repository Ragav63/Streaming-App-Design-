plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.streamingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.streamingapp"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.0.1")
    implementation("com.google.android.exoplayer:exoplayer:2.16.1")

    implementation ("androidx.navigation:navigation-fragment:2.7.0")
    implementation ("androidx.navigation:navigation-ui:2.7.0")

    implementation("com.mig35:carousellayoutmanager:1.4.6")

}