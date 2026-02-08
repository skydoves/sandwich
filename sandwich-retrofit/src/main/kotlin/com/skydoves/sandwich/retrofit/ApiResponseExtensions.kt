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
package com.skydoves.sandwich.retrofit

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ApiResponse.Companion.maps
import com.skydoves.sandwich.ApiResponse.Companion.operate
import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.SuspensionFunction
import com.skydoves.sandwich.exceptions.NoContentException
import kotlinx.coroutines.CancellationException
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns a status code from the [Response].
 *
 * @return A [StatusCode] from the network callback response.
 */
public fun <T> Response<T>.getStatusCode(): StatusCode = StatusCode.entries.find {
  it.code == code()
}
  ?: StatusCode.Unknown

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal val <T> ApiResponse.Success<T>.tagResponse: Response<T>
  inline get() = (tag as? Response<T>) ?: throw IllegalArgumentException(
    "You can access the `tag` only for the encapsulated ApiResponse.Success<T> " +
      "using the Response class.",
  )

@PublishedApi
internal val ApiResponse.Failure.Error.payloadResponse: Response<*>
  inline get() = (payload as? Response<*>) ?: throw IllegalArgumentException(
    "You can access the `payload` only for the encapsulated ApiResponse.Failure.Error " +
      "using the Response class.",
  )

/** The de-serialized response body of a successful data. */
public val <T> ApiResponse.Success<T>.body: T
  inline get() = tagResponse.body() ?: throw NoContentException(tagResponse.getStatusCode().code)

/** [StatusCode] is Hypertext Transfer Protocol (HTTP) response status codes. */
public val <T> ApiResponse.Success<T>.statusCode: StatusCode
  inline get() = tagResponse.getStatusCode()

/** The header fields of a single HTTP message. */
public val <T> ApiResponse.Success<T>.headers: Headers
  inline get() = tagResponse.headers()

/** The raw response from the HTTP client. */
public val <T> ApiResponse.Success<T>.raw: okhttp3.Response
  inline get() = tagResponse.raw()

/**
 * The [ResponseBody] can be consumed only once. */
public val ApiResponse.Failure.Error.errorBody: ResponseBody?
  inline get() = payloadResponse.errorBody()

/** [StatusCode] is Hypertext Transfer Protocol (HTTP) response status codes. */
public val ApiResponse.Failure.Error.statusCode: StatusCode
  inline get() = payloadResponse.getStatusCode()

/** The header fields of a single HTTP message. */
public val ApiResponse.Failure.Error.headers: Headers
  inline get() = payloadResponse.headers()

/** The raw response from the HTTP client. */
public val ApiResponse.Failure.Error.raw: okhttp3.Response
  inline get() = payloadResponse.raw()

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.3.2
 *
 *  Returns The error message or null depending on the type of [ApiResponse].
 */
public inline val ApiResponse<*>.apiMessage: String?
  get() = when (this) {
    is ApiResponse.Failure.Error -> payloadResponse.errorBody()?.string()
    is ApiResponse.Failure.Exception -> message
    else -> null
  }

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponse Factory.
 *
 * @param successCodeRange A success code range for determining the response is successful or failure.
 * @param [f] Create [ApiResponse] from [retrofit2.Response] returning from the block.
 * If [retrofit2.Response] has no errors, it creates [ApiResponse.Success].
 * If [retrofit2.Response] has errors, it creates [ApiResponse.Failure.Error].
 * If [retrofit2.Response] has occurred exceptions, it creates [ApiResponse.Failure.Exception].
 *
 * @return An [ApiResponse] model which holds information about the response.
 */
@JvmSynthetic
@Suppress("UNCHECKED_CAST")
public inline fun <T> apiResponseOf(
  successCodeRange: IntRange = SandwichInitializer.successCodeRange,
  crossinline f: () -> Response<T>,
): ApiResponse<T> = try {
  val response = f()
  if (response.raw().code in successCodeRange) {
    ApiResponse.Success(
      data = response.body() ?: Unit as T,
      tag = response,
    )
  } else {
    ApiResponse.Failure.Error(response)
  }
} catch (ex: CancellationException) {
  throw ex
} catch (ex: Exception) {
  ApiResponse.Failure.Exception(ex)
}.operate().maps()

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponse Factory.
 *
 * @param successCodeRange A success code range for determining the response is successful or failure.
 * @param [f] Create [ApiResponse] from [retrofit2.Response] returning from the block.
 * If [retrofit2.Response] has no errors, it creates [ApiResponse.Success].
 * If [retrofit2.Response] has errors, it creates [ApiResponse.Failure.Error].
 * If [retrofit2.Response] has occurred exceptions, it creates [ApiResponse.Failure.Exception].
 *
 * @return An [ApiResponse] model which holds information about the response.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> suspendApiResponseOf(
  successCodeRange: IntRange = SandwichInitializer.successCodeRange,
  crossinline f: suspend () -> Response<T>,
): ApiResponse<T> {
  val response = f()
  return apiResponseOf(successCodeRange) { response }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponse Factory.
 *
 * @param successCodeRange A success code range for determining the response is successful or failure.
 * @param [f] Create [ApiResponse] from [retrofit2.Response] returning from the block.
 * If [retrofit2.Response] has no errors, it creates [ApiResponse.Success].
 * If [retrofit2.Response] has errors, it creates [ApiResponse.Failure.Error].
 * If [retrofit2.Response] has occurred exceptions, it creates [ApiResponse.Failure.Exception].
 *
 * @return An [ApiResponse] model which holds information about the response.
 */
public inline fun <T> ApiResponse.Companion.responseOf(
  successCodeRange: IntRange = SandwichInitializer.successCodeRange,
  crossinline f: () -> Response<T>,
): ApiResponse<T> = apiResponseOf(successCodeRange, f)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponse Factory.
 *
 * @param successCodeRange A success code range for determining the response is successful or failure.
 * @param [f] Create [ApiResponse] from [retrofit2.Response] returning from the block.
 * If [retrofit2.Response] has no errors, it creates [ApiResponse.Success].
 * If [retrofit2.Response] has errors, it creates [ApiResponse.Failure.Error].
 * If [retrofit2.Response] has occurred exceptions, it creates [ApiResponse.Failure.Exception].
 *
 * @return An [ApiResponse] model which holds information about the response.
 */
@SuspensionFunction
public suspend inline fun <T> ApiResponse.Companion.suspendResponseOf(
  successCodeRange: IntRange = SandwichInitializer.successCodeRange,
  crossinline f: suspend () -> Response<T>,
): ApiResponse<T> = suspendApiResponseOf(successCodeRange, f)
