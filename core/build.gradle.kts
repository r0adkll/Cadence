import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.mavenPublish)
}

kotlin {
  jvm()
  androidTarget { publishLibraryVariants("release") }

  applyDefaultHierarchyTemplate()

  iosX64()
  iosArm64()
  iosSimulatorArm64()
  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    moduleName = property("POM_ARTIFACT_ID").toString()
    browser()
  }

  jvmToolchain(17)

  sourceSets {
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
//            implementation(compose.ui)
    }
  }
}

android { namespace = "com.r0adkll.cadence.core" }
androidComponents { beforeVariants { variant -> variant.androidTest.enable = false } }
