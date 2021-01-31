import java.util.*

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
        // Get local.properties
        val properties = Properties()
        val localProperties = rootProject.file("local.properties")
        properties.load(localProperties.inputStream())

        // Config for release build
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        // Config for debug build
        getByName("debug") {
            buildConfigField("Boolean", "USE_FIREBASE_EMULATOR", "true")
            buildConfigField("String", "FIREBASE_EMULATOR_HOST", "\"192.168.128.66\"")
            buildConfigField("String", "FIREBASE_EMULATOR_FIRESTORE_PORT", "\"8080\"")
            buildConfigField("String", "FIREBASE_EMULATOR_FUNCTIONS_PORT", "\"5001\"")
            buildConfigField("int", "FIREBASE_EMULATOR_AUTH_PORT", "9099")

            val mapsApiKey = properties.getProperty("MAPS_API_KEY", "")
            buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"$mapsApiKey\"")
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
    implementation(project(":domain"))

    implementation(Dependencies.KOTLIN_STDLIB)
    implementation(Dependencies.COROUTINE_CORE)
    implementation(Dependencies.COROUTINE_ANDROID)
    implementation(Dependencies.COROUTINE_PLAY_SERVICES)
    implementation(Dependencies.CORE)
    implementation(Dependencies.APPCOMPAT)
    implementation(Dependencies.ROOM_RUNTIME)
    implementation(Dependencies.ROOM_KTX)
    implementation(Dependencies.HILT)
    implementation(Dependencies.PAGING_RUNTIME)
    // TODO: fix api exposure
    api(Dependencies.FIREBASE_AUTH)
    api(Dependencies.FIREBASE_FIRESTORE)
    api(Dependencies.FIREBASE_STORAGE)
    implementation(Dependencies.PLAY_SERVICES_AUTH)
    implementation(Dependencies.RETROFIT)
    implementation(Dependencies.RETROFIT_CONVERTER_GSON)
    implementation(Dependencies.OKHTTP_LOGGING_INTERCEPTOR)
    implementation(Dependencies.PLAY_SERVICES_MAP)
    implementation(Dependencies.MAP_UTILS)

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