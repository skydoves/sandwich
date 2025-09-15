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
package com.skydoves.sandwich.ktor

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ApiResponse.Companion.maps
import com.skydoves.sandwich.ApiResponse.Companion.operate
import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.SuspensionFunction
import com.skydoves.sandwich.exceptions.NoContentException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets
import kotlinx.coroutines.CancellationException
import kotlin.jvm.JvmSynthetic

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns a status code from the [HttpResponse].
 *
 * @return A [StatusCode] from the network callback response.
 */
public fun HttpResponse.getStatusCode(): StatusCode = StatusCode.entries.find {
  it.code == status.value
}
  ?: StatusCode.Unknown

@PublishedApi
internal val <T> ApiResponse.Success<T>.tagResponse: HttpResponse
  inline get() = (tag as? HttpResponse) ?: throw IllegalArgumentException(
    "You can access the `tag` only for the encapsulated ApiResponse.Success<T> " +
      "using the Response class.",
  )

@PublishedApi
internal val ApiResponse.Failure.Error.payloadResponse: HttpResponse
  inline get() = (payload as? HttpResponse) ?: throw IllegalArgumentException(
    "You can access the `payload` only for the encapsulated ApiResponse.Failure.Error " +
      "using the Response class.",
  )

/** The de-serialized response body of a successful data. */
public suspend inline fun <reified T> ApiResponse.Success<T>.body(): T =
  tagResponse.body() ?: throw NoContentException(tagResponse.getStatusCode().code)

/** [StatusCode] is Hypertext Transfer Protocol (HTTP) response status codes. */
public val <T> ApiResponse.Success<T>.statusCode: StatusCode
  inline get() = tagResponse.getStatusCode()

/** The header fields of a single HTTP message. */
public val <T> ApiResponse.Success<T>.headers: Headers
  inline get() = tagResponse.headers

/** Take out the [HttpResponse] from the tag property. */
public val <T> ApiResponse.Success<T>.httpResponse: HttpResponse
  inline get() = tagResponse

/**
 * The [ByteReadChannel] can be consumed only once. */
public suspend fun ApiResponse.Failure.Error.bodyChannel(): ByteReadChannel =
  payloadResponse.bodyAsChannel()

/**
 * The [ByteReadChannel] can be consumed only once. */
public suspend fun ApiResponse.Failure.Error.bodyString(
  fallbackCharset: Charset = Charsets.UTF_8,
): String = payloadResponse.bodyAsText(fallbackCharset = fallbackCharset)

/** [StatusCode] is Hypertext Transfer Protocol (HTTP) response status codes. */
public val ApiResponse.Failure.Error.statusCode: StatusCode
  inline get() = payloadResponse.getStatusCode()

/** The header fields of a single HTTP message. */
public val ApiResponse.Failure.Error.headers: Headers
  inline get() = payloadResponse.headers

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponse Factory.
 *
 * @param successCodeRange A success code range for determining the response is successful or failure.
 * @param [f] Create [ApiResponse] from [HttpResponse] returning from the block.
 * If [HttpResponse] has no errors, it creates [ApiResponse.Success].
 * If [HttpResponse] has errors, it creates [ApiResponse.Failure.Error].
 * If [HttpResponse] has occurred exceptions, it creates [ApiResponse.Failure.Exception].
 *
 * @return An [ApiResponse] model which holds information about the response.
 */
@JvmSynthetic
@SuspensionFunction
@Throws(CancellationException::class)
public suspend inline fun <reified T> apiResponseOf(
  successCodeRange: IntRange = SandwichInitializer.successCodeRange,
  crossinline f: suspend () -> HttpResponse,
): ApiResponse<T> = try {
  val response = f()
  if (response.status.value in successCodeRange) {
    ApiResponse.Success(
      data = response.body() ?: Unit as T,
      tag = response,
    )
  } else {
    ApiResponse.Failure.Error(response)
  }
} catch (e: CancellationException) {
  throw e
} catch (ex: Exception) {
  ApiResponse.Failure.Exception(ex)
}.operate().maps()

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponse Factory.
 *
 * @param successCodeRange A success code range for determining the response is successful or failure.
 * @param [f] Create [ApiResponse] from [HttpResponse] returning from the block.
 * If [HttpResponse] has no errors, it creates [ApiResponse.Success].
 * If [HttpResponse] has errors, it creates [ApiResponse.Failure.Error].
 * If [HttpResponse] has occurred exceptions, it creates [ApiResponse.Failure.Exception].
 *
 * @return An [ApiResponse] model which holds information about the response.
 */
@SuspensionFunction
public suspend inline fun <reified T> ApiResponse.Companion.responseOf(
  successCodeRange: IntRange = SandwichInitializer.successCodeRange,
  crossinline f: suspend () -> HttpResponse,
): ApiResponse<T> = apiResponseOf(successCodeRange, f)
