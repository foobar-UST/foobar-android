plugins {
    id(Plugins.JAVA_LIBRARY)
    id(Plugins.KOTLIN)
    id(Plugins.KOTLIN_SERIALIZATION) version Versions.SERIALIZATION
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":domain"))

    implementation(Dependencies.KOTLIN_STDLIB)
    implementation(Dependencies.JUNIT)
    implementation(Dependencies.KOTLIN_SERIALIZATION)
    implementation(Dependencies.PAGING_COMMON)
    api(Dependencies.COROUTINE_TEST)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}