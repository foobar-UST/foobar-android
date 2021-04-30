// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version by extra("1.4.21")
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(Dependencies.GRADLE_PLUGIN)
        classpath(Dependencies.KOTLIN_PLUGIN)
        classpath(Dependencies.NAVIGATION_SAFE_ARGS_PLUGIN)
        classpath(Dependencies.HILT_PLUGIN)
        classpath(Dependencies.GOOGLE_SERVICES)
        classpath(Dependencies.FIREBASE_CRASHLYTICS_PLUGIN)
        classpath("com.jaredsburrows:gradle-license-plugin:0.8.90")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven("https://jitpack.io")
    }
}

tasks.register("clean").configure {
    delete("build")
}