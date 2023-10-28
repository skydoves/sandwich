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
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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

plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  id(libs.plugins.kotlin.serialization.get().pluginId)
  id(libs.plugins.nexus.plugin.get().pluginId)
  java
}

apply(from = "${rootDir}/scripts/publish-module.gradle.kts")

mavenPublishing {
  pom {
    version = rootProject.extra.get("libVersion").toString()
    group = Configuration.artifactGroup
  }
}

kotlin {
  jvmToolchain(11)
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
    }
    withJava()
  }
  ios()
  js(IR) {
    browser()
    binaries.executable()
  }
  iosSimulatorArm64()
  macosArm64()
  macosX64()

  sourceSets {
    all { languageSettings.optIn("kotlin.contracts.ExperimentalContracts") }
    val commonMain by getting {
      dependencies {
        api(libs.retrofit)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(libs.junit)
        implementation(libs.mockito.core)
        implementation(libs.mockito.inline)
        implementation(libs.mockito.kotlin)
        implementation(libs.retrofit.moshi)
        implementation(libs.coroutines.test)
        implementation(libs.serialization)
      }
    }

    val jvmMain by getting {
      dependencies {
        api(libs.okhttp)
        implementation(libs.coroutines)
      }
    }
    val jvmTest by getting {
      dependencies {
        implementation(libs.mock.webserver)
      }
    }
    val appleTest by creating {
      dependsOn(commonTest)
    }
    val appleMain by creating {
      dependsOn(commonMain)
    }
    val iosMain by getting {
      dependsOn(appleMain)
    }
    val macosArm64Main by getting {
      dependsOn(appleMain)
    }
    val macosX64Main by getting {
      dependsOn(appleMain)
    }
    val iosSimulatorArm64Main by getting {
      dependsOn(appleMain)
    }

    val iosArm64Main by getting {
      dependsOn(appleTest)
    }
    val iosArm64Test by getting {
      dependsOn(appleTest)
    }

    val iosX64Main by getting {
      dependsOn(appleTest)
    }
    val iosX64Test by getting {
      dependsOn(appleTest)
    }

    val iosTest by getting {
      dependsOn(appleTest)
    }
    val iosSimulatorArm64Test by getting {
      dependsOn(appleTest)
    }
    val macosX64Test by getting {
      dependsOn(appleTest)
    }
    val macosArm64Test by getting {
      dependsOn(appleTest)
    }
  }

  explicitApi()
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType(JavaCompile::class.java).configureEach {
  this.targetCompatibility = JavaVersion.VERSION_11.toString()
  this.sourceCompatibility = JavaVersion.VERSION_11.toString()
}
