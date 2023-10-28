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
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpStatement
import io.ktor.http.HttpMethod
import io.ktor.http.Url

/**
 * Executes an [HttpClient]'s request with the parameters specified using [builder].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.requestApiResponse(
  builder: HttpRequestBuilder = HttpRequestBuilder(),
): ApiResponse<T> {
  val response = HttpStatement(builder, this).execute()
  return apiResponseOf { response }
}

/**
 * Executes an [HttpClient]'s request with the parameters specified in [block].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.requestApiResponse(
  block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> {
  val response = request(HttpRequestBuilder().apply(block))
  return apiResponseOf { response }
}

/**
 * Executes an [HttpClient]'s request with the [urlString] and the parameters configured in [block].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.requestApiResponse(
  urlString: String,
  block: HttpRequestBuilder.() -> Unit = {},
): ApiResponse<T> {
  val response = request {
    url(urlString)
    block()
  }
  return apiResponseOf { response }
}

/**
 * Executes an [HttpClient]'s request with the [url] and the parameters configured in [block].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.requestApiResponse(
  url: Url,
  block: HttpRequestBuilder.() -> Unit = {},
): ApiResponse<T> {
  val response = request {
    url(url)
    block()
  }
  return apiResponseOf { response }
}

/**
 * Executes an [HttpClient]'s GET request with the parameters configured in [builder].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.getApiResponse(
  builder: HttpRequestBuilder,
): ApiResponse<T> {
  builder.method = HttpMethod.Get
  val response = request(builder)
  return apiResponseOf { response }
}

/**
 * Executes an [HttpClient]'s POST request with the parameters configured in [builder].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.postApiResponse(
  builder: HttpRequestBuilder,
): ApiResponse<T> {
  builder.method = HttpMethod.Post
  val response = request(builder)
  return apiResponseOf { response }
}

/**
 * Executes a [HttpClient] PUT request with the parameters configured in [builder].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.putApiResponse(
  builder: HttpRequestBuilder,
): ApiResponse<T> {
  builder.method = HttpMethod.Put
  val response = request(builder)
  return apiResponseOf { response }
}

/**
 * Executes a [HttpClient] DELETE request with the parameters configured in [builder].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.deleteApiResponse(
  builder: HttpRequestBuilder,
): ApiResponse<T> {
  builder.method = HttpMethod.Delete
  val response = request(builder)
  return apiResponseOf { response }
}

/**
 * Executes a [HttpClient] OPTIONS request with the parameters configured in [builder].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.optionsApiResponse(
  builder: HttpRequestBuilder,
): ApiResponse<T> {
  builder.method = HttpMethod.Options
  val response = request(builder)
  return apiResponseOf { response }
}

/**
 * Executes a [HttpClient] PATCH request with the parameters configured in [builder].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.patchApiResponse(
  builder: HttpRequestBuilder,
): ApiResponse<T> {
  builder.method = HttpMethod.Patch
  val response = request(builder)
  return apiResponseOf { response }
}

/**
 * Executes a [HttpClient] HEAD request with the parameters configured in [builder].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.headApiResponse(
  builder: HttpRequestBuilder,
): ApiResponse<T> {
  builder.method = HttpMethod.Head
  val response = request(builder)
  return apiResponseOf { response }
}

/**
 * Executes an [HttpClient]'s GET request with the parameters configured in [block].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.getApiResponse(
  block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> = getApiResponse(HttpRequestBuilder().apply(block))

/**
 * Executes an [HttpClient]'s POST request with the parameters configured in [block].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.postApiResponse(
  block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> = postApiResponse(HttpRequestBuilder().apply(block))

/**
 * Executes an [HttpClient]'s PUT request with the parameters configured in [block].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.putApiResponse(
  block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> = putApiResponse(HttpRequestBuilder().apply(block))

/**
 * Executes an [HttpClient]'s DELETE request with the parameters configured in [block].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.deleteApiResponse(
  block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> = deleteApiResponse(HttpRequestBuilder().apply(block))

/**
 * Executes an [HttpClient]'s OPTIONS request with the parameters configured in [block].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.optionsApiResponse(
  block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> = optionsApiResponse(HttpRequestBuilder().apply(block))

/**
 * Executes an [HttpClient]'s PATCH request with the parameters configured in [block].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.patchApiResponse(
  block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> = patchApiResponse(HttpRequestBuilder().apply(block))

/**
 * Executes an [HttpClient]'s HEAD request with the parameters configured in [block].
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.headApiResponse(
  block: HttpRequestBuilder.() -> Unit,
): ApiResponse<T> = headApiResponse(HttpRequestBuilder().apply(block))

/**
 * Executes an [HttpClient]'s GET request with the specified [url] and
 * an optional [block] receiving an [HttpRequestBuilder] for configuring the request.
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.getApiResponse(
  urlString: String,
  block: HttpRequestBuilder.() -> Unit = {},
): ApiResponse<T> = getApiResponse {
  url(urlString)
  block()
}

/**
 * Executes an [HttpClient]'s POST request with the specified [url] and
 * an optional [block] receiving an [HttpRequestBuilder] for configuring the request.
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.postApiResponse(
  urlString: String,
  block: HttpRequestBuilder.() -> Unit = {},
): ApiResponse<T> = postApiResponse {
  url(urlString)
  block()
}

/**
 * Executes an [HttpClient]'s PUT request with the specified [url] and
 * an optional [block] receiving an [HttpRequestBuilder] for configuring the request.
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.putApiResponse(
  urlString: String,
  block: HttpRequestBuilder.() -> Unit = {},
): ApiResponse<T> = putApiResponse {
  url(urlString)
  block()
}

/**
 * Executes an [HttpClient]'s DELETE request with the specified [url] and
 * an optional [block] receiving an [HttpRequestBuilder] for configuring the request.
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.deleteApiResponse(
  urlString: String,
  block: HttpRequestBuilder.() -> Unit = {},
): ApiResponse<T> = deleteApiResponse {
  url(urlString)
  block()
}

/**
 * Executes an [HttpClient]'s OPTIONS request with the specified [url] and
 * an optional [block] receiving an [HttpRequestBuilder] for configuring the request.
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.optionsApiResponse(
  urlString: String,
  block: HttpRequestBuilder.() -> Unit = {},
): ApiResponse<T> = optionsApiResponse {
  url(urlString)
  block()
}

/**
 * Executes an [HttpClient]'s PATCH request with the specified [url] and
 * an optional [block] receiving an [HttpRequestBuilder] for configuring the request.
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.patchApiResponse(
  urlString: String,
  block: HttpRequestBuilder.() -> Unit = {},
): ApiResponse<T> = patchApiResponse {
  url(urlString)
  block()
}

/**
 * Executes an [HttpClient]'s HEAD request with the specified [url] and
 * an optional [block] receiving an [HttpRequestBuilder] for configuring the request.
 *
 * @return [ApiResponse]
 */
public suspend inline fun <reified T> HttpClient.headApiResponse(
  urlString: String,
  block: HttpRequestBuilder.() -> Unit = {},
): ApiResponse<T> = headApiResponse {
  url(urlString)
  block()
}
