plugins {
    id(Plugins.JAVA_LIBRARY)
    id(Plugins.KOTLIN)
    id(Plugins.KOTLIN_KAPT)
    id(Plugins.CHECK_DEPENDENCY_UPDATES) version "1.0.211"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(Dependencies.KOTLIN_STDLIB)
    implementation(Dependencies.COROUTINE_CORE)

    implementation(Dependencies.DAGGER)
    kapt(Annotation.DAGGER_COMPILER)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xuse-experimental=kotlinx.coroutines.FlowPreview"
        )
    }
}

