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
package com.skydoves.sandwich.test

import com.skydoves.sandwich.ApiResponse

/**
 * Creates a fake [ApiResponse.Success] with the given [data] and optional [tag].
 *
 * Unlike [ApiResponse.of], this factory bypasses [com.skydoves.sandwich.SandwichInitializer]
 * global operators and failure mappers, producing a deterministic response for testing.
 *
 * @param data The success response data.
 * @param tag An optional tag to distinguish the origin of the data.
 * @return A new [ApiResponse.Success] instance.
 */
public fun <T> ApiResponse.Companion.fakeSuccess(
  data: T,
  tag: Any? = null,
): ApiResponse.Success<T> = ApiResponse.Success(data = data, tag = tag)

/**
 * Creates a fake [ApiResponse.Failure.Error] with the given [payload].
 *
 * Unlike production error responses, this factory bypasses [com.skydoves.sandwich.SandwichInitializer]
 * global operators and failure mappers, producing a deterministic response for testing.
 *
 * @param payload An error payload that can contain detailed error information.
 * @return A new [ApiResponse.Failure.Error] instance.
 */
public fun ApiResponse.Companion.fakeError(payload: Any? = null): ApiResponse.Failure.Error =
  ApiResponse.Failure.Error(payload = payload)

/**
 * Creates a fake [ApiResponse.Failure.Exception] from a [Throwable].
 *
 * Unlike [ApiResponse.exception], this factory bypasses [com.skydoves.sandwich.SandwichInitializer]
 * global operators and failure mappers, producing a deterministic response for testing.
 *
 * @param throwable The throwable exception.
 * @return A new [ApiResponse.Failure.Exception] instance.
 */
public fun ApiResponse.Companion.fakeException(
  throwable: Throwable,
): ApiResponse.Failure.Exception = ApiResponse.Failure.Exception(throwable = throwable)

/**
 * Creates a fake [ApiResponse.Failure.Exception] from a [message] string.
 *
 * This is a convenience overload that wraps the message in a [RuntimeException].
 * Unlike [ApiResponse.exception], this factory bypasses [com.skydoves.sandwich.SandwichInitializer]
 * global operators and failure mappers, producing a deterministic response for testing.
 *
 * @param message The error message.
 * @return A new [ApiResponse.Failure.Exception] instance wrapping a [RuntimeException].
 */
public fun ApiResponse.Companion.fakeException(message: String): ApiResponse.Failure.Exception =
  ApiResponse.Failure.Exception(throwable = RuntimeException(message))
