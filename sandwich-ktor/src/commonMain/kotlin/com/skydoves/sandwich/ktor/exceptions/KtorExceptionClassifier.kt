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
package com.skydoves.sandwich.ktor.exceptions

import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.exceptions.SandwichException
import com.skydoves.sandwich.exceptions.SandwichExceptionClassifier
import com.skydoves.sandwich.exceptions.SandwichHttpException
import com.skydoves.sandwich.exceptions.SandwichNetworkException
import com.skydoves.sandwich.exceptions.SandwichTimeoutException
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A [SandwichExceptionClassifier] that understands the throwables reported by the Ktor client.
 *
 * Register it once during startup:
 *
 * ```kotlin
 * SandwichInitializer.sandwichExceptionClassifiers += KtorExceptionClassifier
 * ```
 */
public object KtorExceptionClassifier : SandwichExceptionClassifier {

  override fun classify(throwable: Throwable): SandwichException? = when (throwable) {
    is HttpRequestTimeoutException,
    is ConnectTimeoutException,
    is SocketTimeoutException,
    -> SandwichTimeoutException(throwable.message, throwable)

    is ResponseException -> SandwichHttpException(
      statusCode = throwable.response.status.value,
      message = throwable.message,
      cause = throwable,
    )

    is IOException -> SandwichNetworkException(throwable.message, throwable)

    else -> null
  }
}
