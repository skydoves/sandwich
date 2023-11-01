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
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

data object LimitedRequest : ApiResponse.Failure.Error(
  payload = "your request is limited",
)

data object WrongArgument : ApiResponse.Failure.Error(
  payload = "wrong argument",
)

data object HttpException : ApiResponse.Failure.Exception(
  throwable = RuntimeException("http exception"),
)

data class RetrofitCustomError(
  val response: Response<String> = Response.error(
    403,
    (
      """{"code":10001, "message":"This is a custom error message"}"""
        .trimIndent()
      ).toResponseBody(
      contentType = "text/plain".toMediaType(),
    ),
  ),
) : ApiResponse.Failure.Error(
  payload = response,
)

class CustomHttpResponse(
  private val statusCode: Int,
  private val description: String,
) :
  HttpResponse() {
  @InternalAPI
  override val content: ByteReadChannel
    get() = ByteReadChannel("")

  override val call: HttpClientCall
    get() = HttpClientCall(HttpClient())

  override val coroutineContext: CoroutineContext
    get() = EmptyCoroutineContext

  override val headers: Headers
    get() = Headers.Empty

  override val requestTime: GMTDate
    get() = GMTDate()

  override val responseTime: GMTDate
    get() = GMTDate()

  override val status: HttpStatusCode
    get() = HttpStatusCode(statusCode, description)

  override val version: HttpProtocolVersion
    get() = HttpProtocolVersion(name = "HTTP", major = 1, minor = 1)
}

data class KtorCustomError(
  val response: HttpResponse = CustomHttpResponse(statusCode = 123, description = ""),
) : ApiResponse.Failure.Error(
  payload = response,
)
