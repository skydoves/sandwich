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

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.android.application.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.kotlin.serialization.get().pluginId)
  id(libs.plugins.kotlin.kapt.get().pluginId)
  id(libs.plugins.ktorfit.get().pluginId)
  id(libs.plugins.ksp.get().pluginId)
}

android {
  namespace = "com.skydoves.sandwichdemo"
  compileSdk = Configuration.compileSdk
  defaultConfig {
    applicationId = "com.skydoves.sandwichdemo"
    minSdk = Configuration.minSdk
    targetSdk = Configuration.targetSdk
    versionCode = Configuration.versionCode
    versionName = Configuration.versionName
    multiDexEnabled = true
  }

  buildFeatures {
    buildConfig = true
    dataBinding = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  lint {
    abortOnError = false
  }

  buildTypes {
    create("benchmark") {
      initWith(buildTypes.getByName("release"))
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += listOf("release")
      isDebuggable = false
    }
  }
}

dependencies {
  implementation(project(":sandwich"))
  implementation(project(":sandwich-ktor"))
  implementation(project(":sandwich-ktorfit"))
  implementation(project(":sandwich-retrofit"))
  implementation(project(":sandwich-retrofit-datasource"))
  implementation(project(":sandwich-retrofit-serialization"))

  // android supports
  implementation(libs.material)
  implementation(libs.lifecycle.livedata)
  implementation(libs.lifecycle.viewmodel)

  implementation(libs.coroutines)
  implementation(libs.retrofit.moshi)
  implementation(libs.moshi)
  ksp(libs.moshi.codegen)

  implementation(libs.ktor.negotiation)
  implementation(libs.ktor.okhttp)
  implementation(libs.ktor.json)
  implementation(libs.serialization)

  implementation(libs.ktorfit)
  ksp(libs.ktorfit.ksp)

  implementation(libs.glide)
  implementation(libs.timber)

  implementation(libs.ktorfit)

  implementation("androidx.multidex:multidex:2.0.1")
}
