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
package com.skydoves.sandwich.retrofit.exceptions

import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.exceptions.SandwichException
import com.skydoves.sandwich.exceptions.SandwichExceptionClassifier
import com.skydoves.sandwich.exceptions.SandwichHttpException
import com.skydoves.sandwich.exceptions.SandwichNetworkException
import com.skydoves.sandwich.exceptions.SandwichSerializationException
import com.skydoves.sandwich.exceptions.SandwichTimeoutException
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A [SandwichExceptionClassifier] that understands the throwables reported by OkHttp and Retrofit.
 *
 * Register it once during startup:
 *
 * ```kotlin
 * SandwichInitializer.sandwichExceptionClassifiers += RetrofitExceptionClassifier
 * ```
 */
public object RetrofitExceptionClassifier : SandwichExceptionClassifier {

  override fun classify(throwable: Throwable): SandwichException? = when (throwable) {
    // OkHttp reports the overall call timeout as a plain InterruptedIOException with this message,
    // and connect and read timeouts as SocketTimeoutException.
    is SocketTimeoutException -> SandwichTimeoutException(throwable.message, throwable)
    is InterruptedIOException -> if (throwable.message == "timeout") {
      SandwichTimeoutException(throwable.message, throwable)
    } else {
      SandwichNetworkException(throwable.message, throwable)
    }
    // A truncated body surfaces as an EOFException from the converter rather than from the socket.
    is EOFException -> SandwichSerializationException(throwable.message, throwable)
    is HttpException -> SandwichHttpException(throwable.code(), throwable.message(), throwable)
    is IOException -> SandwichNetworkException(throwable.message, throwable)
    else -> null
  }
}
