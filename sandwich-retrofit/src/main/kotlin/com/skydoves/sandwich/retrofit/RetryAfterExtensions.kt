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

package com.skydoves.sandwich.retrofit

import com.skydoves.sandwich.ApiResponse
import retrofit2.Response

/**
 * @author skydoves (Jaewoong Eum)
 *
 * The delay requested by the server through the `Retry-After` response header, in milliseconds.
 *
 * A server answering 429 or 503 often states how long to wait before trying again. Honouring it
 * beats any client side backoff:
 *
 * ```kotlin
 * runAndRetry(RetryPolicies.exponentialBackoff()) { attempt, _ ->
 *   val response = repository.fetchPosters()
 *   response.onError { retryAfterMillis?.let { delay(it) } }
 *   response
 * }
 * ```
 *
 * Only the delta-seconds form of the header is understood. The HTTP-date form and a missing or
 * malformed header all return `null`.
 */
public val ApiResponse.Failure.Error.retryAfterMillis: Long?
  get() {
    val response = payload as? Response<*> ?: return null
    val header = response.headers()["Retry-After"] ?: return null
    return header.trim().toLongOrNull()?.takeIf { it >= 0 }?.times(1_000)
  }
