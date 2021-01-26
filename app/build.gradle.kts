import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    id(Plugins.ANDROID_APPLICATION)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KOTLIN_KAPT)
    id(Plugins.KOTLIN_PARCELIZE)
    id(Plugins.HILT_ANDROID)
    id(Plugins.NAVIGATION_SAFEARGS)
    id(Plugins.CHECK_DEPENDENCY_UPDATES) version Versions.CHECK_DEPENDENCY_UPDATES
    id(Plugins.FIREBASE_CRASHLYTICS)
    kotlin(Plugins.KOTLIN_SERIALIZATION) version Versions.SERIALIZATION
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

        // Get Maps API
        val properties = Properties()
        val localProperties = rootProject.file("local.properties")
        properties.load(localProperties.inputStream())

        // Expose API to manifest
        setManifestPlaceholders(
            mapOf(
                "mapsApiKey" to properties.getProperty("MAPS_API_KEY", "")
                //"crashlyticsCollectionEnabled" to true
            )
        )
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("debug") {

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
                "-Xuse-experimental=kotlin.ExperimentalStdlibApi",
                "-Xuse-experimental=androidx.paging.ExperimentalPagingApi"
            )
        }
    }

    buildFeatures {
        dataBinding = true
    }

    sourceSets {
        /*
        val test by getting
        val androidTest by getting
        test.java.srcDirs("src/sharedTest/java")
        androidTest.java.srcDirs("src/sharedTest/java")
         */
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation(Dependencies.KOTLIN_STDLIB)
    implementation(Dependencies.KOTLIN_SERIALIZATION)
    implementation(Dependencies.COROUTINE_CORE)
    implementation(Dependencies.COROUTINE_ANDROID)
    implementation(Dependencies.COROUTINE_PLAY_SERVICES)
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
    implementation(Dependencies.SWIPE_REFRESH_LAYOUT)
    implementation(Dependencies.BROWSER)
    implementation(Dependencies.FIREBASE_CRASHLYTICS)
    implementation(Dependencies.FIREBASE_MESSAGING)
    implementation(Dependencies.FIREBASE_ANALYTICS)
    implementation(Dependencies.PLAY_SERVICES_MAP)
    implementation(Dependencies.BANNER_VIEW_PAGER)
    implementation(Dependencies.MATERIAL)
    implementation(Dependencies.GLIDE)
    implementation(Dependencies.SPINKIT)
    implementation(Dependencies.MAP)
    implementation(Dependencies.WORK)
    implementation(Dependencies.HILT_WORK)
    implementation(Dependencies.ROOM_RUNTIME)
    implementation(Dependencies.ROOM_KTX)

    // Annotation Processors
    kapt(Annotation.ROOM_COMPILER)
    kapt(Annotation.HILT_ANDROID_COMPILER)

    // Debug
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

    androidTestAnnotationProcessor(Annotation.HILT_ANDROID_COMPILER)
}