plugins { id("com.android.library"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "mx.edu.sabores.domain"; compileSdk = 36
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
dependencies { implementation(project(":core")); implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2") }
