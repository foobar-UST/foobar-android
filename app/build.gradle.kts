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
}

android {
    compileSdkVersion(Application.COMPILE_SDK)

    defaultConfig {
        applicationId = Application.APPLICATION_ID
        minSdkVersion(Application.MIN_SDK)
        targetSdkVersion(Application.TARGET_SDK)
        versionCode = Application.VERSION_CODE
        versionName = Application.VERSION_NAME
        testInstrumentationRunner = Dependencies.INSTRUMENTAL_RUNNER
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
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
                "-Xuse-experimental=androidx.paging.ExperimentalPagingApi"
            )
        }
    }

    buildFeatures {
        dataBinding = true
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

    // Firebase
    implementation(Dependencies.FIREBASE_CRASHLYTICS)
    implementation(Dependencies.FIREBASE_MESSAGING)
    implementation(Dependencies.FIREBASE_ANALYTICS)


    // Dependencies
    implementation(Dependencies.MATERIAL)
    implementation(Dependencies.GLIDE)
    implementation(Dependencies.SPINKIT)

    // Annotation Processors
    kapt(Annotation.HILT_COMPILER)
    kapt(Annotation.ANDROIDX_HILT_COMPILER)

    // Debug
    debugImplementation(Dependencies.LEAK_CANARY)

    // Unit Test
    testImplementation(Dependencies.ARCH_CORE_TESTING)
    testImplementation(Dependencies.COROUTINE_TEST)
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKITO_CORE)

    // Android Test
    //androidTestImplementation(Dependencies.TEST_CORE)
    //androidTestImplementation(Dependencies.TEST_RUNNER)
    //androidTestImplementation(Dependencies.TEST_RULES)
    androidTestImplementation(Dependencies.TEST_EXT_JUNIT)
    androidTestImplementation(Dependencies.TEST_ESPRESSO_CORE)
    androidTestImplementation(Dependencies.HILT_TESTING)


    // Android Test Annotation Processors
    androidTestAnnotationProcessor(Annotation.HILT_COMPILER)
}