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
package com.skydoves.sandwichdemo.model

import com.squareup.moshi.JsonClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@JsonClass(generateAdapter = true)
data class Poster(
  @SerialName("id") val id: Long,
  @SerialName("name") val name: String,
  @SerialName("release") val release: String,
  @SerialName("playtime") val playtime: String,
  @SerialName("description") val description: String,
  @SerialName("plot") val plot: String,
  @SerialName("poster") val poster: String,
)
