import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(Plugins.ANDROID_APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KOTLIN_ANDROID_EXTENSIONS)
    id(Plugins.KOTLIN_KAPT)
    id(Plugins.HILT_ANDROID)
    id(Plugins.NAVIGATION_SAFEARGS)
    id(Plugins.CHECK_DEPENDENCY_UPDATES) version "1.0.211"
    id(Plugins.FIREBASE_CRASHLYTICS)
    kotlin(Plugins.KOTLIN_SERIALIZATION) version "1.4.10"
}

android {
    compileSdkVersion(Application.COMPILE_SDK)

    defaultConfig {
        applicationId = Application.APPLICATION_ID
        minSdkVersion(Application.MIN_SDK)
        targetSdkVersion(Application.TARGET_SDK)
        versionCode = Application.VERSION_CODE
        versionName = Application.VERSION_NAME
        testInstrumentationRunner = Dependencies.HILT_TEST_RUNNER
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            //manifestPlaceholders = mapOf("crashlyticsCollectionEnabled" to true)
        }

        /*
        getByName("debug") {
            manifestPlaceholders = mapOf("crashlyticsCollectionEnabled" to false)
        }
         */
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
                "-Xuse-experimental=kotlin.ExperimentalStdlibApi",
                "-Xuse-experimental=androidx.paging.ExperimentalPagingApi"
            )
        }
    }

    buildFeatures {
        dataBinding = true
    }

    sourceSets {
        val test by getting
        val androidTest by getting
        test.java.srcDirs("src/sharedTest/java")
        androidTest.java.srcDirs("src/sharedTest/java")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":data"))
    api(project(":domain"))

    // Kotlin
    implementation(Dependencies.KOTLIN_STDLIB)
    implementation(Dependencies.COROUTINE_CORE)
    implementation(Dependencies.COROUTINE_ANDROID)
    implementation(Dependencies.COROUTINE_PLAY_SERVICES)
    implementation(Dependencies.KOTLIN_SERIALIZATION)

    // Android
    implementation(Dependencies.CORE)
    implementation(Dependencies.APPCOMPAT)
    implementation(Dependencies.CONSTRAINT_LAYOUT)
    implementation(Dependencies.ACTIVITY)
    implementation(Dependencies.FRAGMENT)
    implementation(Dependencies.NAVIGATION_FRAGMENT)
    implementation(Dependencies.NAVIGATION_UI)
    implementation(Dependencies.LIFECYCLE_RUNTIME)
    implementation(Dependencies.LIFECYCLE_LIVEDATA)
    implementation(Dependencies.LIFECYCLE_VIEWMODEL)
    implementation(Dependencies.LIFECYCLE_VIEWMODEL_SAVEDSTATE)
    implementation(Dependencies.LIFECYCLE_COMMON)
    implementation(Dependencies.HILT)
    implementation(Dependencies.HILT_VIEWMODEL)
    implementation(Dependencies.PAGING_RUNTIME)
    implementation(Dependencies.PREFERENCES)
    //implementation(Dependencies.WORK)
    implementation(Dependencies.SWIPE_REFRESH_LAYOUT)
    implementation(Dependencies.BROWSER)

    // Firebase
    implementation(Dependencies.FIREBASE_CRASHLYTICS)
    implementation(Dependencies.FIREBASE_MESSAGING)
    implementation(Dependencies.FIREBASE_ANALYTICS)

    // Play Services
    implementation(Dependencies.PLAY_SERVICES_MAP)

    // Others
    implementation(Dependencies.BANNER_VIEW_PAGER)
    implementation(Dependencies.MATERIAL)
    implementation(Dependencies.GLIDE)
    implementation(Dependencies.SPINKIT)
    implementation(Dependencies.MAP)

    // Annotation Processors
    kapt(Annotation.HILT_ANDROID_COMPILER)
    kapt(Annotation.HILT_ANDROIDX_COMPILER)

    // Debug
    // Who cares about memory leak
    //debugImplementation(Dependencies.LEAK_CANARY)

    // Unit Test
    testImplementation(Dependencies.ARCH_CORE_TESTING)
    testImplementation(Dependencies.COROUTINE_TEST)
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKITO_CORE)

    // Android Test
    //androidTestImplementation(Dependencies.TEST_CORE)
    //androidTestImplementation(Dependencies.TEST_RUNNER)
    //androidTestImplementation(Dependencies.TEST_RULES)
    androidTestImplementation(Dependencies.TEST_EXT_JUNIT_KTX)
    androidTestImplementation(Dependencies.TEST_ESPRESSO_CORE)
    androidTestImplementation(Dependencies.HILT_TESTING)

    kaptAndroidTest(Annotation.HILT_ANDROID_COMPILER)


    // Android Test Annotation Processors
    androidTestAnnotationProcessor(Annotation.HILT_ANDROID_COMPILER)
}