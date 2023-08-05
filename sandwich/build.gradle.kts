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

import com.github.skydoves.sandwich.Configuration

plugins {
  id("kotlin")
  id(libs.plugins.kotlin.serialization.get().pluginId)
}

rootProject.extra.apply {
  set("PUBLISH_GROUP_ID", Configuration.artifactGroup)
  set("PUBLISH_ARTIFACT_ID", "sandwich")
  set("PUBLISH_VERSION", rootProject.extra.get("rootVersionName"))
}

apply(from = "${rootDir}/scripts/publish-module.gradle")

dependencies {
  implementation(libs.coroutines)
  api(libs.retrofit)
  api(libs.okhttp)

  // unit test
  testImplementation(libs.junit)
  testImplementation(libs.mockito.core)
  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.mock.webserver)
  testImplementation(libs.retrofit.moshi)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.serialization)
}
