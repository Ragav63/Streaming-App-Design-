plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.streamingapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.streamingapp"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

    implementation("com.github.bumptech.glide:glide:5.0.5")
    annotationProcessor("com.github.bumptech.glide:compiler:5.0.5")

    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation("androidx.navigation:navigation-fragment:2.9.6")
    implementation("androidx.navigation:navigation-ui:2.9.6")

    implementation("com.mig35:carousellayoutmanager:1.4.6")

    implementation("com.google.code.gson:gson:2.13.2")

    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-core:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.19.1")
    implementation("com.google.android.exoplayer:exoplayer-smoothstreaming:2.19.1")

    compileOnly ("org.projectlombok:lombok:1.18.42")
    annotationProcessor ("org.projectlombok:lombok:1.18.42")

    implementation("com.hbb20:ccp:2.7.3")

    implementation("com.webtoonscorp.android:readmore-view:1.4.1")

}