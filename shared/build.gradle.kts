import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    // Future: iOS targets
    // iosX64()
    // iosArm64()
    // iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            // Coroutines & Serialization
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            // Koin DI (módulos shared exportam definições de módulo)
            implementation(libs.koin.core)

            // Ktor Client (core + plugins)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)

            // Supabase (backend: auth, database, realtime, storage)
            // Versões gerenciadas via version catalog (supabase = "3.0.0") — sem BOM
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.auth)
            implementation(libs.supabase.realtime)
            implementation(libs.supabase.storage)

            // SQLDelight (Flow extensions para queries reativas)
            implementation(libs.sqldelight.coroutines)
        }

        androidMain.dependencies {
            // SQLDelight driver para Android
            implementation(libs.sqldelight.android.driver)

            // Ktor engine para Android (OkHttp)
            implementation(libs.ktor.client.okhttp)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }

        // MockK é JVM-only — não usar em commonTest (quebra KMP)
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.mockk)
                implementation(libs.sqldelight.sqlite.driver)
            }
        }
    }
}

sqldelight {
    databases {
        create("KanoasDatabase") {
            packageName.set("br.com.kanoas.shared.core.database")
        }
    }
}

android {
    namespace = "br.com.kanoas.shared"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
