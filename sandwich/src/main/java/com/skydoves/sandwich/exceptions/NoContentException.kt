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

package com.skydoves.sandwich.exceptions

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Thrown by various accessor methods to indicate that the response body being requested
 * does not exist. e.g., 204 (NoContent), 205 (ResetContent).
 *
 * The server has successfully fulfilled the request with the 2xx code
 * and that there is no additional content to send in the response payload body.
 *
 * If we want to handle empty body instead of throwing [NoContentException] for the 204 and 205 request case,
 * use the [com.skydoves.sandwich.interceptors.EmptyBodyInterceptor] for your interceptor.
 *
 * ```
 * OkHttpClient.Builder()
 *   .addInterceptor(EmptyBodyInterceptor())
 *   .build()
 * ```
 */
class NoContentException constructor(
  val code: Int,
  override val message: String? =
    "The server has successfully fulfilled the request with the code ($code) and that there is " +
      "no additional content to send in the response payload body."
) : Throwable(message)
