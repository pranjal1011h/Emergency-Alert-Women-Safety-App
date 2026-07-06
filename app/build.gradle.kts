plugins {
    id("com.android.application")
 id("com.google.gms.google-services")
}

android {


    namespace = "com.example.safetyapp"

    compileSdk = 34

    defaultConfig {

        applicationId = "com.example.safetyapp"

        minSdk = 24
        targetSdk = 34

        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {

        getByName("release") {

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

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {


    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation(
        "com.google.android.material:material:1.11.0"
    )

    implementation(
        "androidx.constraintlayout:constraintlayout:2.1.4"
    )

    implementation(
        "androidx.recyclerview:recyclerview:1.3.2"
    )

    implementation(
        "androidx.core:core-ktx:1.12.0"
    )

    implementation(
        "com.google.android.gms:play-services-location:21.0.1"
    )
    implementation("androidx.biometric:biometric:1.1.0")
    implementation(libs.activity)

    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))

    implementation("com.google.firebase:firebase-storage")

    implementation("com.google.firebase:firebase-database")

    implementation("com.google.firebase:firebase-auth")
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}
