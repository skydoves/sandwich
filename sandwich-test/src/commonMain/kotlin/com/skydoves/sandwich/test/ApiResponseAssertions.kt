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
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Asserts that this [ApiResponse] is [ApiResponse.Success] and returns the narrowed type.
 *
 * @throws AssertionError if the response is not [ApiResponse.Success].
 */
public fun <T> ApiResponse<T>.assertSuccess(): ApiResponse.Success<T> {
  if (this is ApiResponse.Success<T>) return this
  throw AssertionError(
    "Expected ApiResponse.Success but was ${describeResponse()}",
  )
}

/**
 * Asserts that this [ApiResponse] is [ApiResponse.Success] and executes [block]
 * with the success response as the receiver. Returns the narrowed type for chaining.
 *
 * @param block A lambda to execute custom assertions on the [ApiResponse.Success].
 * @throws AssertionError if the response is not [ApiResponse.Success].
 */
public inline fun <T> ApiResponse<T>.assertSuccess(
  crossinline block: ApiResponse.Success<T>.() -> Unit,
): ApiResponse.Success<T> {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  val success = assertSuccess()
  success.block()
  return success
}

/**
 * Asserts that this [ApiResponse] is [ApiResponse.Failure.Error] and returns the narrowed type.
 *
 * @throws AssertionError if the response is not [ApiResponse.Failure.Error].
 */
public fun <T> ApiResponse<T>.assertError(): ApiResponse.Failure.Error {
  if (this is ApiResponse.Failure.Error) return this
  throw AssertionError(
    "Expected ApiResponse.Failure.Error but was ${describeResponse()}",
  )
}

/**
 * Asserts that this [ApiResponse] is [ApiResponse.Failure.Error] and executes [block]
 * with the error response as the receiver. Returns the narrowed type for chaining.
 *
 * @param block A lambda to execute custom assertions on the [ApiResponse.Failure.Error].
 * @throws AssertionError if the response is not [ApiResponse.Failure.Error].
 */
public inline fun <T> ApiResponse<T>.assertError(
  crossinline block: ApiResponse.Failure.Error.() -> Unit,
): ApiResponse.Failure.Error {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  val error = assertError()
  error.block()
  return error
}

/**
 * Asserts that this [ApiResponse] is [ApiResponse.Failure.Exception] and returns the narrowed type.
 *
 * @throws AssertionError if the response is not [ApiResponse.Failure.Exception].
 */
public fun <T> ApiResponse<T>.assertException(): ApiResponse.Failure.Exception {
  if (this is ApiResponse.Failure.Exception) return this
  throw AssertionError(
    "Expected ApiResponse.Failure.Exception but was ${describeResponse()}",
  )
}

/**
 * Asserts that this [ApiResponse] is [ApiResponse.Failure.Exception] and executes [block]
 * with the exception response as the receiver. Returns the narrowed type for chaining.
 *
 * @param block A lambda to execute custom assertions on the [ApiResponse.Failure.Exception].
 * @throws AssertionError if the response is not [ApiResponse.Failure.Exception].
 */
public inline fun <T> ApiResponse<T>.assertException(
  crossinline block: ApiResponse.Failure.Exception.() -> Unit,
): ApiResponse.Failure.Exception {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  val exception = assertException()
  exception.block()
  return exception
}

/**
 * Asserts that this [ApiResponse] is [ApiResponse.Failure] (either Error or Exception)
 * and returns the narrowed type.
 *
 * @throws AssertionError if the response is not [ApiResponse.Failure].
 */
public fun <T> ApiResponse<T>.assertFailure(): ApiResponse.Failure<T> {
  if (this is ApiResponse.Failure<T>) return this
  throw AssertionError(
    "Expected ApiResponse.Failure but was ${describeResponse()}",
  )
}

/**
 * Asserts that this [ApiResponse] is [ApiResponse.Failure] and executes [block]
 * with the failure response as the receiver. Returns the narrowed type for chaining.
 *
 * @param block A lambda to execute custom assertions on the [ApiResponse.Failure].
 * @throws AssertionError if the response is not [ApiResponse.Failure].
 */
public inline fun <T> ApiResponse<T>.assertFailure(
  crossinline block: ApiResponse.Failure<T>.() -> Unit,
): ApiResponse.Failure<T> {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  val failure = assertFailure()
  failure.block()
  return failure
}

/**
 * Asserts that this [ApiResponse] is [ApiResponse.Success] and that
 * its [data][ApiResponse.Success.data] equals the [expected] value.
 *
 * @param expected The expected success data.
 * @throws AssertionError if the response is not a success or if data does not match.
 */
public fun <T> ApiResponse<T>.assertSuccessData(expected: T): ApiResponse.Success<T> {
  val success = assertSuccess()
  if (success.data != expected) {
    throw AssertionError(
      "Expected success data <$expected> but was <${success.data}>",
    )
  }
  return success
}

/**
 * Asserts that this [ApiResponse] is [ApiResponse.Failure.Exception] and that
 * its [message][ApiResponse.Failure.Exception.message] equals the [expected] string.
 *
 * @param expected The expected exception message.
 * @throws AssertionError if the response is not an exception or if the message does not match.
 */
public fun <T> ApiResponse<T>.assertExceptionMessage(
  expected: String?,
): ApiResponse.Failure.Exception {
  val exception = assertException()
  if (exception.message != expected) {
    throw AssertionError(
      "Expected exception message <$expected> but was <${exception.message}>",
    )
  }
  return exception
}

/**
 * Returns a human-readable description of this [ApiResponse] for assertion error messages.
 */
internal fun <T> ApiResponse<T>.describeResponse(): String = when (this) {
  is ApiResponse.Success -> "Success(data=$data)"
  is ApiResponse.Failure.Error -> "Error(payload=$payload)"
  is ApiResponse.Failure.Exception -> "Exception(message=$message, throwable=$throwable)"
}
