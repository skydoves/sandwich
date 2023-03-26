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

package com.skydoves.sandwich.interceptors

import com.skydoves.sandwich.StatusCode
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * Related: https://github.com/square/retrofit/issues/2867
 *
 * An interceptor for bypassing the [com.skydoves.sandwich.exceptions.NoContentException]
 * when the server has successfully fulfilled the request with the 2xx code
 * and that there is no additional content to send in the response payload body.
 * e.g., 204 (NoContent), 205 (ResetContent).
 */
public object EmptyBodyInterceptor : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val response = chain.proceed(chain.request())
    if (!response.isSuccessful || response.code.let {
        it != StatusCode.NoContent.code && it != StatusCode.ResetContent.code
      }
    ) {
      return response
    }

    if ((response.body?.contentLength()?.takeIf { it >= 0 } != null)) {
      return response.newBuilder().code(StatusCode.OK.code).build()
    }

    val emptyBody = "".toResponseBody("text/plain".toMediaType())

    return response
      .newBuilder()
      .code(StatusCode.OK.code)
      .body(emptyBody)
      .build()
  }
}
