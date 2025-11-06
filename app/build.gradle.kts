plugins {
    id("com.android.application")
}

android {
    namespace = "com.yary.blockapps"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yary.blockapps"
        minSdk = 21
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")
    
    // Критические зависимости для нового API
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.fragment:fragment:1.6.1")
    
    // Явно исключаем конфликтующие Kotlin зависимости
    implementation("androidx.appcompat:appcompat:1.6.1") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }
    implementation("com.google.android.material:material:1.9.0") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }
    implementation("androidx.activity:activity:1.8.0") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }
    implementation("androidx.fragment:fragment:1.6.1") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }
}

configurations.all {
    // Глобально исключаем конфликтующие Kotlin модули
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    resolutionStrategy {
        eachDependency {
            // Принудительно используем одну версию Kotlin если она все равно подтягивается
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion("1.8.22")
            }
        }
    }
}