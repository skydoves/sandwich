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
package com.skydoves.sandwich.exceptions

import com.skydoves.sandwich.ApiResponse

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A platform independent classification of the throwable held by an
 * [ApiResponse.Failure.Exception].
 *
 * The transport libraries each report their own exception types for the same situation, so
 * inspecting the raw throwable means writing platform specific code in shared modules. Integrations
 * translate their exceptions into these types, so a shared module can branch on the cause of the
 * failure instead:
 *
 * ```kotlin
 * response.onException {
 *   when (throwable.asSandwichException()) {
 *     is SandwichTimeoutException -> retryLater()
 *     is SandwichNetworkException -> showOfflineBanner()
 *     else -> report(throwable)
 *   }
 * }
 * ```
 *
 * @param message A description of the failure.
 * @param cause The original throwable reported by the transport.
 */
public open class SandwichException(message: String? = null, cause: Throwable? = null) :
  Throwable(message, cause)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * The request could not reach the server. e.g., no connectivity, DNS failure, connection reset.
 */
public open class SandwichNetworkException(message: String? = null, cause: Throwable? = null) :
  SandwichException(message, cause)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * The request reached the server but did not complete in time. e.g., connect, socket or overall
 * request timeout.
 */
public open class SandwichTimeoutException(message: String? = null, cause: Throwable? = null) :
  SandwichException(message, cause)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * The response arrived but could not be converted into the expected type. e.g., malformed JSON,
 * a missing required field, an unknown enum value.
 */
public open class SandwichSerializationException(
  message: String? = null,
  cause: Throwable? = null,
) : SandwichException(message, cause)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * The transport reported an HTTP level failure as a throwable rather than as a response body.
 *
 * @property statusCode The HTTP status code reported by the transport, or `null` if unknown.
 */
public open class SandwichHttpException(
  public val statusCode: Int? = null,
  message: String? = null,
  cause: Throwable? = null,
) : SandwichException(message, cause)
