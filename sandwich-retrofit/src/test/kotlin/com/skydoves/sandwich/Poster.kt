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
package com.skydoves.sandwich

import com.squareup.moshi.JsonClass
import kotlin.random.Random

@JsonClass(generateAdapter = false)
internal data class Poster(
  val id: Long,
  val name: String,
  val release: String,
  val playtime: String,
  val description: String,
  val plot: String,
  val poster: String,
) {

  companion object {

    fun create(): Poster = Poster(
      id = Random.nextLong(),
      "Frozen II",
      "2019",
      "1 h 43 min",
      "Frozen II, also known as Frozen 2, is a 2019 American 3D computer-animated " +
        "musical fantasy film produced by Walt Disney Animation Studios. " +
        "The 58th animated film " +
        "produced by the studio, it is the sequel to the 2013 film " +
        "Frozen and features the return " +
        "of directors Chris Buck and Jennifer Lee, producer Peter Del Vecho, " +
        "songwriters Kristen Anderson-Lopez and Robert Lopez, and composer Christophe Beck. " +
        "Lee also returns as screenwriter, penning the screenplay from a story by her, Buck, " +
        "Marc E. Smith, Anderson-Lopez, and Lopez,[2] while Byron Howard executive-produced the" +
        " film.[a][1] Veteran voice cast Kristen Bell, Idina Menzel, Josh Gad, Jonathan Groff," +
        " and Ciarán Hinds return as their previous characters, " +
        "and they are joined by newcomers " +
        "Sterling K. Brown, Evan Rachel Wood, Alfred Molina, Martha Plimpton, Jason Ritter, " +
        "Rachel Matthews, and Jeremy Sisto.",
      "https://user-images.githubusercontent.com/" +
        "24237865/75087936-5c1d9f80-553e-11ea-81d3-a912634dd8f7.jpg",
      "",
    )
  }
}
