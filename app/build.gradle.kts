plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
}

android {
    namespace = "ru.mkruglikov.annotationsdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.mkruglikov.annotationsdemo"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // через implementation дотягиваемся до самой аннотации
    implementation(project(":annotations"))

    // через ksp дотягиваемся до процессора
    // Для разных типов процессинга можно делать разные модули. Например annotations-ksp или annotations-kapt
    ksp(project(":annotations"))
}