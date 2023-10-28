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
@file:JvmName("ResponseTransformer")
@file:JvmMultifileClass

package com.skydoves.sandwich

import com.skydoves.sandwich.operators.ApiResponseOperator
import com.skydoves.sandwich.operators.ApiResponseSuspendOperator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns the encapsulated data if this instance represents [ApiResponse.Success] or
 * returns null if it is [ApiResponse.Failure.Error] or [ApiResponse.Failure.Exception].
 *
 * @return The encapsulated data or null.
 */
public fun <T> ApiResponse<T>.getOrNull(): T? {
  return when (this) {
    is ApiResponse.Success -> data
    is ApiResponse.Failure.Error -> null
    is ApiResponse.Failure.Exception -> null
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns the encapsulated data if this instance represents [ApiResponse.Success] or
 * returns the [defaultValue] if it is [ApiResponse.Failure.Error] or [ApiResponse.Failure.Exception].
 *
 * @return The encapsulated data or [defaultValue].
 */
public fun <T> ApiResponse<T>.getOrElse(defaultValue: T): T {
  return when (this) {
    is ApiResponse.Success -> data
    is ApiResponse.Failure.Error -> defaultValue
    is ApiResponse.Failure.Exception -> defaultValue
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns the encapsulated data if this instance represents [ApiResponse.Success] or
 * invokes the lambda [defaultValue] that returns [T] if it is [ApiResponse.Failure.Error] or [ApiResponse.Failure.Exception].
 *
 * @return The encapsulated data or [defaultValue].
 */
public inline fun <T> ApiResponse<T>.getOrElse(defaultValue: () -> T): T {
  return when (this) {
    is ApiResponse.Success -> data
    is ApiResponse.Failure.Error -> defaultValue()
    is ApiResponse.Failure.Exception -> defaultValue()
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns the encapsulated data if this instance represents [ApiResponse.Success] or
 * throws the encapsulated Throwable exception if it is [ApiResponse.Failure.Error] or [ApiResponse.Failure.Exception].
 *
 * @throws RuntimeException if it is [ApiResponse.Failure.Error] or
 * the encapsulated Throwable exception if it is [ApiResponse.Failure.Exception.exception]
 *
 * @return The encapsulated data.
 */
public fun <T> ApiResponse<T>.getOrThrow(): T {
  when (this) {
    is ApiResponse.Success -> return data
    is ApiResponse.Failure.Error -> throw RuntimeException(message())
    is ApiResponse.Failure.Exception -> throw exception
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A scope function that would be executed for handling successful responses if the request succeeds.
 *
 * @param onResult The receiver function that receiving [ApiResponse.Success] if the request succeeds.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.onSuccess(
  crossinline onResult: ApiResponse.Success<T>.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success) {
    onResult(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A scope function that would be executed for handling successful responses if the request succeeds with a [ApiSuccessModelMapper].
 *
 * @param mapper The [ApiSuccessModelMapper] for mapping [ApiResponse.Success] response as a custom [V] instance model.
 * @param onResult The receiver function that receiving [ApiResponse.Success] if the request succeeds.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
public inline fun <T, V> ApiResponse<T>.onSuccess(
  mapper: ApiSuccessModelMapper<T, V>,
  crossinline onResult: V.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success) {
    onResult(map(mapper))
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A suspension scope function that would be executed for handling successful responses if the request succeeds.
 *
 * @param onResult The receiver function that receiving [ApiResponse.Success] if the request succeeds.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendOnSuccess(
  crossinline onResult: suspend ApiResponse.Success<T>.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success) {
    onResult(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A suspension scope function that would be executed for handling successful responses if the request succeeds with a [ApiSuccessModelMapper].
 *
 * @param mapper The [ApiSuccessModelMapper] for mapping [ApiResponse.Success] response as a custom [V] instance model.
 * @param onResult The receiver function that receiving [ApiResponse.Success] if the request succeeds.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, V> ApiResponse<T>.suspendOnSuccess(
  mapper: ApiSuccessModelMapper<T, V>,
  crossinline onResult: suspend V.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success) {
    onResult(map(mapper))
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A function that would be executed for handling error responses if the request failed or get an exception.
 *
 * @param onResult The receiver function that receiving [ApiResponse.Failure] if the request failed or get an exception.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.onFailure(
  crossinline onResult: ApiResponse.Failure<T>.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure<T>) {
    onResult(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A suspension function that would be executed for handling error responses if the request failed or get an exception.
 *
 * @param onResult The receiver function that receiving [ApiResponse.Failure] if the request failed or get an exception.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendOnFailure(
  crossinline onResult: suspend ApiResponse.Failure<T>.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure<T>) {
    onResult(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A scope function that would be executed for handling error responses if the request failed.
 *
 * @param onResult The receiver function that receiving [ApiResponse.Failure.Exception] if the request failed.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.onError(
  crossinline onResult: ApiResponse.Failure.Error<T>.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Error) {
    onResult(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A scope function that would be executed for handling error responses if the request failed with a [ApiErrorModelMapper].
 * This function receives a [ApiErrorModelMapper] and returns the mapped result into the scope.
 *
 * @param mapper The [ApiErrorModelMapper] for mapping [ApiResponse.Failure.Error] response as a custom [V] instance model.
 * @param onResult The receiver function that receiving [ApiResponse.Failure.Exception] if the request failed.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
public inline fun <T, V> ApiResponse<T>.onError(
  mapper: ApiErrorModelMapper<V>,
  crossinline onResult: V.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Error) {
    onResult(map(mapper))
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A suspension scope function that would be executed for handling error responses if the request failed.
 *
 * @param onResult The receiver function that receiving [ApiResponse.Failure.Exception] if the request failed.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendOnError(
  crossinline onResult: suspend ApiResponse.Failure.Error<T>.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Error) {
    onResult(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A suspension scope function that would be executed for handling error responses if the request failed with a [ApiErrorModelMapper].
 * This function receives a [ApiErrorModelMapper] and returns the mapped result into the scope.
 *
 * @param mapper The [ApiErrorModelMapper] for mapping [ApiResponse.Failure.Error] response as a custom [V] instance model.
 * @param onResult The receiver function that receiving [ApiResponse.Failure.Exception] if the request failed.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, V> ApiResponse<T>.suspendOnError(
  mapper: ApiErrorModelMapper<V>,
  crossinline onResult: suspend V.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Error) {
    onResult(map(mapper))
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A scope function that would be executed for handling exception responses if the request get an exception.
 *
 * @param onResult The receiver function that receiving [ApiResponse.Failure.Exception] if the request get an exception.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.onException(
  crossinline onResult: ApiResponse.Failure.Exception<T>.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Exception) {
    onResult(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A suspension scope function that would be executed for handling exception responses if the request get an exception.
 *
 * @param onResult The receiver function that receiving [ApiResponse.Failure.Exception] if the request get an exception.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendOnException(
  crossinline onResult: suspend ApiResponse.Failure.Exception<T>.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Exception) {
    onResult(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A scope function that will be executed for handling successful, error, exception responses.
 *  This function receives and handles [ApiResponse.onSuccess], [ApiResponse.onError],
 *  and [ApiResponse.onException] in one scope.
 *
 * @param onSuccess A scope function that would be executed for handling successful responses if the request succeeds.
 * @param onError A scope function that would be executed for handling error responses if the request failed.
 * @param onException A scope function that would be executed for handling exception responses if the request get an exception.
 *
 *  @return The original [ApiResponse].
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.onProcedure(
  crossinline onSuccess: ApiResponse.Success<T>.() -> Unit,
  crossinline onError: ApiResponse.Failure.Error<T>.() -> Unit,
  crossinline onException: ApiResponse.Failure.Exception<T>.() -> Unit,
): ApiResponse<T> {
  contract {
    callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    callsInPlace(onError, InvocationKind.AT_MOST_ONCE)
    callsInPlace(onException, InvocationKind.AT_MOST_ONCE)
  }
  this.onSuccess(onSuccess)
  this.onError(onError)
  this.onException(onException)
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A suspension scope function that will be executed for handling successful, error, exception responses.
 *  This function receives and handles [ApiResponse.onSuccess], [ApiResponse.onError],
 *  and [ApiResponse.onException] in one scope.
 *
 * @param onSuccess A suspension scope function that would be executed for handling successful responses if the request succeeds.
 * @param onError A suspension scope function that would be executed for handling error responses if the request failed.
 * @param onException A suspension scope function that would be executed for handling exception responses if the request get an exception.
 *
 *  @return The original [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendOnProcedure(
  crossinline onSuccess: suspend ApiResponse.Success<T>.() -> Unit,
  crossinline onError: suspend ApiResponse.Failure.Error<T>.() -> Unit,
  crossinline onException: suspend ApiResponse.Failure.Exception<T>.() -> Unit,
): ApiResponse<T> {
  contract {
    callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    callsInPlace(onError, InvocationKind.AT_MOST_ONCE)
    callsInPlace(onException, InvocationKind.AT_MOST_ONCE)
  }
  this.suspendOnSuccess(onSuccess)
  this.suspendOnError(onError)
  this.suspendOnException(onException)
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Success] to a customized success response model.
 *
 * @param mapper A mapper interface for mapping [ApiResponse.Success] response as a custom [V] instance model.
 *
 * @return A mapped custom [V] error response model.
 */
public fun <T, V> ApiResponse.Success<T>.map(mapper: ApiSuccessModelMapper<T, V>): V {
  return mapper.map(this)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Success] to a customized success response model.
 *
 * @param mapper An executable lambda for mapping [ApiResponse.Success] response as a custom [V] instance model.
 *
 * @return A mapped custom [V] error response model.
 */
public fun <T, V> ApiResponse.Success<T>.map(mapper: (ApiResponse.Success<T>) -> V): V {
  contract { callsInPlace(mapper, InvocationKind.AT_MOST_ONCE) }
  return mapper(this)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Success] to a customized error response model with a receiver scope lambda.
 *
 * @param mapper A mapper interface for mapping [ApiResponse.Success] response as a custom [V] instance model.
 * @param onResult A receiver scope lambda of the mapped custom [V] success response model.
 *
 * @return A mapped custom [V] success response model.
 */
@JvmSynthetic
public inline fun <T, V> ApiResponse.Success<T>.map(
  mapper: ApiSuccessModelMapper<T, V>,
  crossinline onResult: V.() -> Unit,
) {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  onResult(mapper.map(this))
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Success] to a customized error response model with a suspension receiver scope lambda.
 *
 * @param mapper A mapper interface for mapping [ApiResponse.Success] response as a custom [V] instance model.
 * @param onResult A suspension receiver scope lambda of the mapped custom [V] success response model.
 *
 * @return A mapped custom [V] success response model.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, V> ApiResponse.Success<T>.suspendMap(
  mapper: ApiSuccessModelMapper<T, V>,
  crossinline onResult: suspend V.() -> Unit,
) {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  onResult(mapper.map(this))
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Success] to a customized error response model with a suspension receiver scope lambda.
 *
 * @param mapper An executable lambda for mapping [ApiResponse.Success] response as a custom [V] instance model.
 *
 * @return A mapped custom [V] success response model.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, V> ApiResponse.Success<T>.suspendMap(
  crossinline mapper: suspend (ApiResponse.Success<T>) -> V,
): V {
  contract { callsInPlace(mapper, InvocationKind.AT_MOST_ONCE) }
  return mapper(this)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Failure.Error] to a customized error response model.
 *
 * @param mapper A mapper interface for mapping [ApiResponse.Failure.Error] response as a custom [V] instance model.
 *
 * @return A mapped custom [V] error response model.
 */
public fun <T, V> ApiResponse.Failure.Error<T>.map(mapper: ApiErrorModelMapper<V>): V {
  return mapper.map(this)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Failure.Error] to a customized error response model.
 *
 * @param mapper An executable lambda for mapping [ApiResponse.Failure.Error] response as a custom [V] instance model.
 *
 * @return A mapped custom [V] error response model.
 */
public fun <T, V> ApiResponse.Failure.Error<T>.map(mapper: (ApiResponse.Failure.Error<T>) -> V): V {
  contract { callsInPlace(mapper, InvocationKind.AT_MOST_ONCE) }
  return mapper(this)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Failure.Error] to a customized error response model with a receiver scope lambda.
 *
 * @param mapper A mapper interface for mapping [ApiResponse.Failure.Error] response as a custom [V] instance model.
 * @param onResult A receiver scope lambda of the mapped custom [V] error response model.
 *
 * @return A mapped custom [V] error response model.
 */
@JvmSynthetic
public inline fun <T, V> ApiResponse.Failure.Error<T>.map(
  mapper: ApiErrorModelMapper<V>,
  crossinline onResult: V.() -> Unit,
) {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  onResult(mapper.map(this))
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Failure.Error] to a customized error response model with a suspension receiver scope lambda.
 *
 * @param mapper A mapper interface for mapping [ApiResponse.Failure.Error] response as a custom [V] instance model.
 * @param onResult A suspension receiver scope lambda of the mapped custom [V] error response model.
 *
 * @return A mapped custom [V] error response model.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, V> ApiResponse.Failure.Error<T>.suspendMap(
  mapper: ApiErrorModelMapper<V>,
  crossinline onResult: suspend V.() -> Unit,
) {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  onResult(mapper.map(this))
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Failure.Error] to a customized error response model with a suspension receiver scope lambda.
 *
 * @param mapper A mapper interface for mapping [ApiResponse.Failure.Error] response as a custom [V] instance model.
 *
 * @return A mapped custom [V] error response model.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, V> ApiResponse.Failure.Error<T>.suspendMap(
  crossinline mapper: suspend (ApiResponse.Failure.Error<T>) -> V,
): V {
  return mapper(this)
}

/**
 * Returns an error message from the [ApiResponse.Failure] that consists of the localized message.
 *
 * @return An error message from the [ApiResponse.Failure].
 */
public fun <T> ApiResponse.Failure<T>.message(): String {
  return when (this) {
    is ApiResponse.Failure.Error -> message()
    is ApiResponse.Failure.Exception -> message()
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns an error message from the [ApiResponse.Failure.Error] that consists of the status and error response.
 *
 * @return An error message from the [ApiResponse.Failure.Error].
 */
public fun <T> ApiResponse.Failure.Error<T>.message(): String = toString()

/**
 * Returns an error message from the [ApiResponse.Failure.Exception] that consists of the localized message.
 *
 * @return An error message from the [ApiResponse.Failure.Exception].
 */
public fun <T> ApiResponse.Failure.Exception<T>.message(): String = toString()

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Operates on an [ApiResponse] and return an [ApiResponse].
 * This allows you to handle success and error response instead of the [ApiResponse.onSuccess],
 * [ApiResponse.onError], [ApiResponse.onException] transformers.
 */
@JvmSynthetic
public fun <T, V : ApiResponseOperator<T>> ApiResponse<T>.operator(
  apiResponseOperator: V,
): ApiResponse<T> = apply {
  when (this) {
    is ApiResponse.Success -> apiResponseOperator.onSuccess(this)
    is ApiResponse.Failure.Error -> apiResponseOperator.onError(this)
    is ApiResponse.Failure.Exception -> apiResponseOperator.onException(this)
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Operates on an [ApiResponse] and return an [ApiResponse] which should be handled in the suspension scope.
 * This allows you to handle success and error response instead of the [ApiResponse.suspendOnSuccess],
 * [ApiResponse.suspendOnError], [ApiResponse.suspendOnException] transformers.
 */
@JvmSynthetic
@SuspensionFunction
public suspend fun <T, V : ApiResponseSuspendOperator<T>> ApiResponse<T>.suspendOperator(
  apiResponseOperator: V,
): ApiResponse<T> = apply {
  when (this) {
    is ApiResponse.Success -> apiResponseOperator.onSuccess(this)
    is ApiResponse.Failure.Error -> apiResponseOperator.onError(this)
    is ApiResponse.Failure.Exception -> apiResponseOperator.onException(this)
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns a [Flow] which emits successful data if the response is a [ApiResponse.Success] and the data is not null.
 *
 * @return A coroutines [Flow] which emits successful data.
 */
@JvmSynthetic
public fun <T> ApiResponse<T>.toFlow(): Flow<T> {
  return if (this is ApiResponse.Success) {
    flowOf(data)
  } else {
    emptyFlow()
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns a [Flow] which contains transformed data using successful data
 * if the response is a [ApiResponse.Success] and the data is not null.
 *
 * @param transformer A transformer lambda receives successful data and returns anything.
 *
 * @return A coroutines [Flow] which emits successful data.
 */
@JvmSynthetic
public inline fun <T, R> ApiResponse<T>.toFlow(
  crossinline transformer: T.() -> R,
): Flow<R> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  return if (this is ApiResponse.Success) {
    flowOf(data.transformer())
  } else {
    emptyFlow()
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns a [Flow] which contains transformed data using successful data
 * if the response is a [ApiResponse.Success] and the data is not null.
 *
 * @param transformer A suspension transformer lambda receives successful data and returns anything.
 *
 * @return A coroutines [Flow] which emits successful data.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, R> ApiResponse<T>.toSuspendFlow(
  crossinline transformer: suspend T.() -> R,
): Flow<R> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  return if (this is ApiResponse.Success) {
    flowOf(data.transformer())
  } else {
    emptyFlow()
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Composition the [ApiResponse] with a given [ApiResponse] from the [transformer].
 *
 * If the give [ApiResponse] success, execute [transformer] for executing the next request.
 * If the give [ApiResponse] failed, it returns the first [ApiResponse.Failure] response.
 *
 * @param transformer A transformer lambda receives successful data and returns anything.
 *
 * @return A mapped custom [V] success response or failed response depending on the given [ApiResponse].
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
public inline infix fun <T : Any, V : Any> ApiResponse<T>.then(
  transformer: (T) -> ApiResponse<V>,
): ApiResponse<V> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success<T>) {
    return transformer(this.data)
  }
  return this as ApiResponse<V>
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Composition the [ApiResponse] with a given [ApiResponse] from the [transformer].
 *
 * If the give [ApiResponse] success, execute [transformer] for executing the next request.
 * If the give [ApiResponse] failed, it returns the first [ApiResponse.Failure] response.
 *
 * @param transformer A transformer lambda receives successful data and returns anything.
 *
 * @return A mapped custom [V] success response or failed response depending on the given [ApiResponse].
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
public suspend inline infix fun <T : Any, V : Any> ApiResponse<T>.suspendThen(
  crossinline transformer: suspend (T) -> ApiResponse<V>,
): ApiResponse<V> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success<T>) {
    return transformer(this.data)
  }
  return this as ApiResponse<V>
}
