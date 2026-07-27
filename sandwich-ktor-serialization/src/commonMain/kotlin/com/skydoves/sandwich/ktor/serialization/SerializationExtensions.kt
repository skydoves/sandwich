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
@file:Suppress("unused")

package com.skydoves.sandwich.ktor.serialization

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.SuspensionFunction
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Deserializes the error body of an [ApiResponse.Failure.Error] into the [E] custom type.
 *
 * A Ktor error payload is the raw [HttpResponse], so reading it means calling `bodyAsText` and
 * decoding by hand at every call site. This does both:
 *
 * ```kotlin
 * val response = service.addToCart(productId)
 * val error: ErrorMessage? = response.deserializeErrorBody()
 * ```
 *
 * Returns `null` when the response is not an error, when the payload is not an [HttpResponse], or
 * when the body is empty. A body that is present but does not match [E] throws, so a malformed
 * contract is not silently swallowed.
 *
 * @param json A [Json] instance that can be configured as needed.
 */
@SuspensionFunction
public suspend inline fun <T, reified E> ApiResponse<T>.deserializeErrorBody(
  json: Json = Json,
): E? {
  if (this !is ApiResponse.Failure.Error) return null
  val response = payload as? HttpResponse ?: return null
  val body = response.bodyAsText()
  if (body.isEmpty()) return null
  return json.decodeFromString<E>(body)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A scope function executed with the deserialized error body when this response is an
 * [ApiResponse.Failure.Error] carrying one.
 *
 * ```kotlin
 * service.addToCart(productId)
 *   .onErrorDeserialize<CartResponse, ErrorMessage> { error ->
 *     showMessage(error.message)
 *   }
 * ```
 *
 * @param json A [Json] instance that can be configured as needed.
 * @param onResult A receiver function invoked with the deserialized error body.
 *
 * @return The original [ApiResponse].
 */
@SuspensionFunction
public suspend inline fun <T, reified E> ApiResponse<T>.onErrorDeserialize(
  json: Json = Json,
  crossinline onResult: suspend ApiResponse.Failure.Error.(E) -> Unit,
): ApiResponse<T> {
  val errorBody = deserializeErrorBody<T, E>(json = json)
  if (this is ApiResponse.Failure.Error && errorBody != null) {
    onResult(errorBody)
  }
  return this
}
