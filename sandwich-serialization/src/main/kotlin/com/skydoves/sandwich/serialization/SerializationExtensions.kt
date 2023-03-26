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

package com.skydoves.sandwich.serialization

import com.skydoves.sandwich.ApiResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.2.7
 *
 * Deserializes the Json string from error body of the [ApiResponse.Failure.Error] to the [E] custom type.
 * It returns `null` if the error body is empty.
 *
 * @param json [Json] instance that could be configured as needed.
 */
@JvmOverloads
public inline fun <T, reified E> ApiResponse<T>.deserializeErrorBody(json: Json = Json): E? {
  if (this is ApiResponse.Failure.Error<T>) {
    val errorBody = this.errorBody?.string() ?: return null
    return json.decodeFromString(errorBody)
  }
  return null
}

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.2.7
 *
 * A scope function that would be executed for handling error responses if the request failed.
 *
 * @param json [Json] instance that could be configured as needed.
 * @param onResult The receiver function that receiving [ApiResponse.Failure.Exception] if the request failed.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
public inline fun <T, reified E> ApiResponse<T>.onErrorDeserialize(
  json: Json = Json,
  crossinline onResult: ApiResponse.Failure.Error<T>.(E) -> Unit,
): ApiResponse<T> {
  val errorBody = this.deserializeErrorBody<T, E>(json = json)
  if (this is ApiResponse.Failure.Error && errorBody != null) {
    onResult(errorBody)
  }
  return this
}
