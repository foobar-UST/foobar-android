plugins {
    id(Plugins.ANDROID_LIBRARY)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KOTLIN_KAPT)
    id(Plugins.HILT_ANDROID)
    id(Plugins.GOOGLE_SERVICES)
    id(Plugins.CHECK_DEPENDENCY_UPDATES) version Versions.CHECK_DEPENDENCY_UPDATES
}

android {
    compileSdkVersion(Application.COMPILE_SDK)

    defaultConfig {
        minSdkVersion(Application.MIN_SDK)
        targetSdkVersion(Application.TARGET_SDK)
        versionCode = Application.VERSION_CODE
        versionName = Application.VERSION_NAME
        testInstrumentationRunner = Dependencies.HILT_TEST_RUNNER

        consumerProguardFiles("consumer-proguard-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            buildConfigField("Boolean", "USE_FIREBASE_EMULATOR", "false")
            buildConfigField("String", "FIREBASE_EMULATOR_HOST", "\"192.168.128.66:8080\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
                "-Xuse-experimental=androidx.paging.ExperimentalPagingApi"
            )
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    api(project(":domain"))

    // Kotlin
    implementation(Dependencies.KOTLIN_STDLIB)
    implementation(Dependencies.COROUTINE_CORE)
    implementation(Dependencies.COROUTINE_ANDROID)
    implementation(Dependencies.COROUTINE_PLAY_SERVICES)

    // Android
    implementation(Dependencies.CORE)
    implementation(Dependencies.APPCOMPAT)
    implementation(Dependencies.ROOM_RUNTIME)
    implementation(Dependencies.ROOM_KTX)
    implementation(Dependencies.HILT)
    implementation(Dependencies.PAGING_RUNTIME)
    //implementation(Dependencies.PREFERENCES_DATASTORE)
    //implementation(Dependencies.PROTO_DATASTORE)

    // Firebase
    api(Dependencies.FIREBASE_AUTH)
    api(Dependencies.FIREBASE_FIRESTORE)
    api(Dependencies.FIREBASE_STORAGE)

    // Play Services
    implementation(Dependencies.PLAY_SERVICES_AUTH)

    implementation(Dependencies.GSON)

    // Annotation Processors
    kapt(Annotation.ROOM_COMPILER)
    kapt(Annotation.HILT_ANDROID_COMPILER)

    // Unit Test
    testImplementation(Dependencies.ARCH_CORE_TESTING)
    testImplementation(Dependencies.COROUTINE_TEST)
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKITO_CORE)
    testImplementation(Dependencies.MOCKITO_CORE)

    // Android Test
    //androidTestImplementation(Dependencies.TEST_CORE)
    //androidTestImplementation(Dependencies.TEST_RUNNER)
    //androidTestImplementation(Dependencies.TEST_RULES)
    androidTestImplementation(Dependencies.TEST_EXT_JUNIT_KTX)
    androidTestImplementation(Dependencies.TEST_ESPRESSO_CORE)
    androidTestImplementation(Dependencies.HILT_TESTING)


    // Android Test Annotation Processors
    androidTestAnnotationProcessor(Annotation.HILT_ANDROID_COMPILER)
}