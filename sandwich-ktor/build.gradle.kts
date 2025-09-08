/*
 * Designed and developed by 2020 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.skydoves.sandwich.Configuration
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  id(libs.plugins.kotlin.serialization.get().pluginId)
  id(libs.plugins.nexus.plugin.get().pluginId)
  id(libs.plugins.baseline.profile.get().pluginId)
}

apply(from = "${rootDir}/scripts/publish-module.gradle.kts")

mavenPublishing {
  pom {
    version = rootProject.extra.get("libVersion").toString()
    group = Configuration.artifactGroup
  }
}

kotlin {
  androidTarget { publishLibraryVariants("release") }

  jvm {
    libs.versions.jvmTarget.get().toInt()
    compilerOptions {
      jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get().toString()))
    }
  }

  wasmJs {
    browser {
      testTask {
        enabled = false
      }
    }
    nodejs {
      testTask {
        enabled = false
      }
    }
    binaries.library()
  }

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  macosX64()
  macosArm64()

  @Suppress("OPT_IN_USAGE")
  applyHierarchyTemplate {
    common {
      group("jvm") {
        withAndroidTarget()
        withJvm()
      }
      group("skia") {
        group("darwin") {
          group("apple") {
            group("ios") {
              withIosX64()
              withIosArm64()
              withIosSimulatorArm64()
            }
            group("macos") {
              withMacosX64()
              withMacosArm64()
            }
          }
          withJs()
          withWasmJs()
        }
      }
    }
  }

  sourceSets {
    all {
      languageSettings.optIn("com.skydoves.sandwich.annotations.InternalSandwichApi")
    }
    val commonMain by getting {
      dependencies {
        api(project(":sandwich"))
        api(libs.ktor.core)
        implementation(libs.coroutines)
      }
    }
  }

  explicitApi()
}

android {
  compileSdk = Configuration.compileSdk
  namespace = "com.skydoves.sandwich.ktor"
  defaultConfig {
    minSdk = Configuration.minSdk
    consumerProguardFiles("consumer-rules.pro")
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

baselineProfile {
  baselineProfileOutputDir = "../../src/androidMain"
  filter {
    include("com.skydoves.sandwich.ktor.**")
  }
}

dependencies {
  baselineProfile(project(":baselineprofile"))
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType(JavaCompile::class.java).configureEach {
  this.targetCompatibility = JavaVersion.VERSION_11.toString()
  this.sourceCompatibility = JavaVersion.VERSION_11.toString()
}
