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
@file:Suppress("unused", "RedundantVisibilityModifier")
@file:JvmName("SandwichExceptions")
@file:JvmMultifileClass

package com.skydoves.sandwich.exceptions

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.SandwichInitializer
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Translates a transport specific throwable into a [SandwichException].
 *
 * Integrations ship a classifier that understands their own exception types. Register it once so
 * that shared code can branch on the cause of a failure without knowing which transport produced
 * it:
 *
 * ```kotlin
 * SandwichInitializer.sandwichExceptionClassifiers += KtorExceptionClassifier
 * ```
 */
public fun interface SandwichExceptionClassifier {

  /**
   * Classifies the [throwable].
   *
   * @param throwable The throwable reported by the transport.
   * @return A [SandwichException] describing the failure, or `null` if this classifier does not
   * recognize the throwable.
   */
  public fun classify(throwable: Throwable): SandwichException?
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Classifies this throwable using the registered
 * [SandwichInitializer.sandwichExceptionClassifiers].
 *
 * A throwable that is already a [SandwichException] is returned as is. A throwable that no
 * classifier recognizes is wrapped in a plain [SandwichException], so the result is never `null`
 * and `when` branches stay exhaustive.
 *
 * @return A [SandwichException] describing this throwable.
 */
public fun Throwable.asSandwichException(): SandwichException {
  if (this is SandwichException) return this
  SandwichInitializer.sandwichExceptionClassifiers.forEach { classifier ->
    classifier.classify(this)?.let { return it }
  }
  return SandwichException(message = message, cause = this)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Classifies the throwable held by this failure. See [asSandwichException].
 */
public val ApiResponse.Failure.Exception.sandwichException: SandwichException
  get() = throwable.asSandwichException()

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns true if this failure was caused by a timeout.
 */
public val ApiResponse.Failure.Exception.isTimeout: Boolean
  get() = sandwichException is SandwichTimeoutException

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns true if this failure was caused by the request not reaching the server.
 */
public val ApiResponse.Failure.Exception.isNetworkFailure: Boolean
  get() = sandwichException is SandwichNetworkException

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns true if this failure was caused by the response not converting into the expected type.
 */
public val ApiResponse.Failure.Exception.isSerializationFailure: Boolean
  get() = sandwichException is SandwichSerializationException
