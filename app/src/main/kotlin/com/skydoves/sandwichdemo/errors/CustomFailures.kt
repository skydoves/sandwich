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
package com.skydoves.sandwichdemo.errors

import com.skydoves.sandwich.ApiResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

data object UnKnownError : ApiResponse.Failure.Exception(
  throwable = RuntimeException("unknwon error")
)

data object LimitedRequest : ApiResponse.Failure.Exception(
  throwable = RuntimeException("your request is limited")
)

data object WrongArgument : ApiResponse.Failure.Exception(
  throwable = RuntimeException("wrong argument")
)

data object HttpException : ApiResponse.Failure.Exception(
  throwable = RuntimeException("http exception"),
)

data class RetrofitCustomError(
  val response: Response<String> = Response.error(
    10001,
    "This is a custom Retrofit error".toResponseBody(contentType = "text/plain".toMediaType()),
  ),
) : ApiResponse.Failure.Error(
  payload = response,
)
