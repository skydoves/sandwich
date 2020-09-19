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

/** SandwichInitializer is a rules and strategies initializer of the network response. */
object SandwichInitializer {

  /**
   * determines the success code range of network responses.
   * if a network request is successful and the response code is in the [successCodeRange],
   * its response will be a [ApiResponse.Success].
   *
   * if a network request is successful and the response code is out of the [successCodeRange],
   * its response will be a [ApiResponse.Failure.Error].
   * */
  @JvmStatic
  var successCodeRange: IntRange = 200..299
}
