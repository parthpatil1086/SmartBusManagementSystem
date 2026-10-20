plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {

    namespace = "com.example.smartbussystem"
    compileSdk = 36

    defaultConfig {

        applicationId =
            "com.example.smartbussystem"

        minSdk = 24
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {

        sourceCompatibility =
            JavaVersion.VERSION_11

        targetCompatibility =
            JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // FIREBASE BOM
    implementation(
        platform(
            "com.google.firebase:firebase-bom:34.0.0"
        )
    )

    implementation("org.osmdroid:osmdroid-android:6.1.20")
    // FIREBASE AUTH
    implementation(
        "com.google.firebase:firebase-auth"
    )

    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.1")

    // FIRESTORE DATABASE
    implementation(
        "com.google.firebase:firebase-firestore"
    )

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.credentials)

    implementation(
        libs.credentials.play.services.auth
    )

    implementation(libs.googleid)
    implementation(libs.play.services.location)

    testImplementation(libs.junit)

    androidTestImplementation(
        libs.ext.junit
    )

    androidTestImplementation(
        libs.espresso.core
    )
}