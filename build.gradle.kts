import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Designed and developed by 2020 skydoves (Jaewoong Eum)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
//     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.android.test) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.kotlin.kapt) apply false
  alias(libs.plugins.ktorfit) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.baseline.profile) apply false
  alias(libs.plugins.kotlin.binary.compatibility)
  alias(libs.plugins.nexus.plugin)
  alias(libs.plugins.spotless)
  alias(libs.plugins.dokka)
}

apiValidation {
  ignoredProjects.addAll(listOf("app"))
}

subprojects {
  apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("$buildDir/**/*.kt")
      ktlint().editorConfigOverride(
        mapOf(
          "indent_size" to "2",
          "continuation_indent_size" to "2"
        )
      )
      licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
      trimTrailingWhitespace()
      endWithNewline()
    }
    format("kts") {
      target("**/*.kts")
      targetExclude("$buildDir/**/*.kts")
      licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(^(?![\\/ ]\\*).*$)")
      trimTrailingWhitespace()
      endWithNewline()
    }
  }

  if (!name.contains("app")) {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
      compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get().toString()))
        freeCompilerArgs.addAll(
          listOf(
            "-opt-in=com.skydoves.sandwich.annotations.InternalSandwichApi",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xexplicit-api=strict",
          )
        )
      }
    }
  }

  tasks.withType(JavaCompile::class.java).configureEach {
    this.targetCompatibility = libs.versions.jvmTarget.get()
    this.sourceCompatibility = libs.versions.jvmTarget.get()
  }
}
