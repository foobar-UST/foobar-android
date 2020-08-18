// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val kotlin_version by extra("1.3.72")
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath(Dependencies.BUILD_TOOLS_PLUGIN)
        classpath(Dependencies.KOTLIN_PLUGIN)
        classpath(Dependencies.NAVIGATION_SAFE_ARGS_PLUGIN)
        classpath(Dependencies.HILT_PLUGIN)
        "classpath"("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.register("clean").configure {
    delete("build")
}