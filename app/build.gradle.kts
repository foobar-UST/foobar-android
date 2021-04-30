import com.jaredsburrows.license.LicenseReportTask
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
    id(Plugins.LICENSE)
}

android {
    compileSdkVersion(Application.COMPILE_SDK)

    defaultConfig {
        applicationId = Application.MAIN_APPLICATION_ID
        minSdkVersion(Application.MIN_SDK)
        targetSdkVersion(Application.TARGET_SDK)
        versionCode = Application.MAIN_VERSION_CODE
        versionName = Application.MAIN_VERSION_NAME
        testInstrumentationRunner = Dependencies.HILT_TEST_RUNNER

        // Read local properties
        val localProperties = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }
        val mapsApiKey = localProperties.getProperty("MAPS_API_KEY", null)

        addManifestPlaceholders(mapOf("mapsApiKey" to mapsApiKey))
    }

    buildTypes {
        // Release build
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            addManifestPlaceholders(
                mapOf(
                    "clearTextTraffic" to false,
                    "disableAnalytics" to false
                )
            )
        }

        // Debug build
        getByName("debug") {
            addManifestPlaceholders(
                mapOf(
                    "clearTextTraffic" to true,
                    "disableAnalytics" to false
                )
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
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

    tasks.withType(LicenseReportTask::class).configureEach {
        generateCsvReport = false
        generateHtmlReport = true
        generateJsonReport = false
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":data"))
    implementation(project(":domain"))
    testImplementation(project(":test-shared"))

    implementation(Dependencies.KOTLIN_STDLIB)
    implementation(Dependencies.COROUTINE_CORE)
    implementation(Dependencies.COROUTINE_ANDROID)
    implementation(Dependencies.COROUTINE_PLAY_SERVICES)
    implementation(Dependencies.CORE_KTX)
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
    implementation(Dependencies.HILT_NAVIGATION)
    implementation(Dependencies.HILT_WORK)
    implementation(Dependencies.PAGING_RUNTIME)
    implementation(Dependencies.SWIPE_REFRESH_LAYOUT)
    implementation(Dependencies.BROWSER)
    implementation(Dependencies.FIREBASE_CRASHLYTICS)
    implementation(Dependencies.FIREBASE_DYNAMIC_LINKS)
    implementation(Dependencies.FIREBASE_ANALYTICS)
    implementation(Dependencies.PLAY_SERVICES_MAP)
    implementation(Dependencies.BANNER_VIEW_PAGER)
    implementation(Dependencies.MATERIAL)
    implementation(Dependencies.GLIDE)
    implementation(Dependencies.SPINKIT)
    implementation(Dependencies.MAPS_KTX)
    implementation(Dependencies.WORK)
    implementation(Dependencies.ZXING_CORE)
    implementation(Dependencies.ZXING_EMBEDDED) { isTransitive = false }

    // Annotation Processors
    kapt(Annotation.HILT_ANDROID_COMPILER)
    kapt(Annotation.HILT_ANDROIDX_EXT_COMPILER)

    // Debug
    //debugImplementation(Dependencies.LEAK_CANARY)

    // Unit Test
    testImplementation(Dependencies.ARCH_CORE_TESTING)
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKK)

    // Android Test
    androidTestImplementation(Dependencies.HILT_TESTING)

    debugImplementation(Dependencies.FRAGMENT_TESTING)

    kaptAndroidTest(Annotation.HILT_ANDROID_COMPILER)
}