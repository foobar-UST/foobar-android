object Dependencies {
    // Gradle Plugins
    const val BUILD_TOOLS_PLUGIN = "com.android.tools.build:gradle:${Versions.GRADLE}"
    const val KOTLIN_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
    const val NAVIGATION_SAFE_ARGS_PLUGIN = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}"
    const val HILT_PLUGIN = "com.google.dagger:hilt-android-gradle-plugin:${Versions.HILT}"

    // Kotlin
    const val KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk7"
    const val COROUTINE_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINE}"
    const val COROUTINE_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINE}"
    const val COROUTINE_PLAY_SERVICES = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:${Versions.COROUTINE_PLAY_SERVICES}"

    // Android
    const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
    const val ACTIVITY = "androidx.activity:activity-ktx:${Versions.ACTIVITY}"
    const val CORE = "androidx.core:core-ktx:${Versions.CORE}"
    const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:${Versions.CONSTRAINT_LAYOUT}"
    const val FRAGMENT = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT}"
    const val NAVIGATION_FRAGMENT = "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION}"
    const val NAVIGATION_UI = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}"
    const val NAVIGATION_FEATURES = "androidx.navigation:navigation-dynamic-features-fragment:${Versions.NAVIGATION}"
    const val LIFECYCLE_VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE}"
    const val LIFECYCLE_VIEWMODEL_SAVEDSTATE = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.LIFECYCLE}"
    const val LIFECYCLE_LIVEDATA = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE}"
    const val LIFECYCLE_RUNTIME = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
    const val LIFECYCLE_COMMON = "androidx.lifecycle:lifecycle-common-java8:${Versions.LIFECYCLE}"
    const val ROOM_RUNTIME = "androidx.room:room-runtime:${Versions.ROOM}"
    const val ROOM_KTX = "androidx.room:room-ktx:${Versions.ROOM}"
    const val HILT = "com.google.dagger:hilt-android:${Versions.HILT}"
    const val HILT_VIEWMODEL = "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.HILT_VIEWMODEL}"
    const val HILT_WORK = "androidx.hilt:hilt-work:${Versions.HILT_WORK}"
    const val PAGING_RUNTIME = "androidx.paging:paging-runtime:${Versions.PAGING}"
    const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
    const val GLIDE = "com.github.bumptech.glide:glide:${Versions.GLIDE}"
    const val LEAK_CANARY = "com.squareup.leakcanary:leakcanary-android:${Versions.LEAK_CANARY}"

    // Firebase
    const val FIREBASE_AUTH = "com.google.firebase:firebase-auth-ktx:${Versions.FIREBASE_AUTH}"
    const val FIREBASE_FIRESTORE = "com.google.firebase:firebase-firestore-ktx:${Versions.FIREBASE_FIRESTORE}"
    const val FIREBASE_FUNCTIONS = "com.google.firebase:firebase-functions-ktx:${Versions.FIREBASE_FUNCTIONS}"
    const val FIREBASE_MESSAGING = "com.google.firebase:firebase-messaging:${Versions.FIREBASE_MESSAGING}"
    const val FIREBASE_STORAGE = "com.google.firebase:firebase-storage-ktx:${Versions.FIREBASE_STORAGE}"

    // Test
    const val KOTLIN_TEST = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.KOTLIN}"
    const val INSTRUMENTAL_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
    const val JUNIT = "junit:junit:${Versions.JUNIT}"

    // Android Test
    const val EXT_JUNIT = "androidx.test.ext:junit:${Versions.EXT_JUNIT}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
    const val FRAGMENT_TESTING = "androidx.fragment:fragment-testing:${Versions.FRAGMENT}"
    const val NAVIGATION_TESTING = "androidx.navigation:navigation-testing:${Versions.NAVIGATION}"
    const val ROOM_TESTING = "androidx.room:room-testing:${Versions.ROOM}"
}