import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  kotlin("jvm")
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
}

group = "com.r0adkll.cadence"
version = "1.0"

dependencies {
  implementation(projects.core)

  implementation(compose.material3)
  implementation(compose.runtime)
  implementation(compose.ui)
  implementation(compose.desktop.currentOs)

  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(17)
}

compose.desktop {
  application {
    mainClass = "com.r0adkll.kimchi.restaurant.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "com.r0adkll.cadence.app"
      packageVersion = "1.0.0"
    }
  }
}
