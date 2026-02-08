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

import com.skydoves.sandwich.mappers.ApiErrorModelMapper
import com.skydoves.sandwich.mappers.ApiResponseMapper
import com.skydoves.sandwich.mappers.ApiSuccessModelMapper
import com.skydoves.sandwich.operators.ApiResponseOperator
import com.skydoves.sandwich.operators.ApiResponseSuspendOperator
import kotlinx.coroutines.CancellationException
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
public fun <T> ApiResponse<T>.getOrNull(): T? = when (this) {
  is ApiResponse.Success -> data
  is ApiResponse.Failure -> null
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns the encapsulated data if this instance represents [ApiResponse.Success] or
 * returns the [defaultValue] if it is [ApiResponse.Failure.Error] or [ApiResponse.Failure.Exception].
 *
 * @return The encapsulated data or [defaultValue].
 */
public fun <T> ApiResponse<T>.getOrElse(defaultValue: T): T = when (this) {
  is ApiResponse.Success -> data
  is ApiResponse.Failure -> defaultValue
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns the encapsulated data if this instance represents [ApiResponse.Success] or
 * invokes the lambda [defaultValue] that returns [T] if it is [ApiResponse.Failure.Error] or [ApiResponse.Failure.Exception].
 *
 * @return The encapsulated data or [defaultValue].
 */
public inline fun <T> ApiResponse<T>.getOrElse(defaultValue: () -> T): T = when (this) {
  is ApiResponse.Success -> data
  is ApiResponse.Failure -> defaultValue()
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns the encapsulated data if this instance represents [ApiResponse.Success] or
 * throws the encapsulated Throwable exception if it is [ApiResponse.Failure.Error] or [ApiResponse.Failure.Exception].
 *
 * @throws RuntimeException if it is [ApiResponse.Failure.Error] or
 * the encapsulated Throwable exception if it is [ApiResponse.Failure.Exception.throwable]
 *
 * @return The encapsulated data.
 */
public fun <T> ApiResponse<T>.getOrThrow(): T {
  when (this) {
    is ApiResponse.Success -> return data
    is ApiResponse.Failure.Error -> throw RuntimeException(message())
    is ApiResponse.Failure.Exception -> throw throwable
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
 * Note: If the failure is an [ApiResponse.Failure.Exception] caused by a [CancellationException],
 * the [CancellationException] will be re-thrown to ensure proper coroutine cancellation handling.
 *
 * @param onResult The receiver function that receiving [ApiResponse.Failure] if the request failed or get an exception.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
@Throws(CancellationException::class)
public suspend inline fun <T> ApiResponse<T>.suspendOnFailure(
  crossinline onResult: suspend ApiResponse.Failure<T>.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure<T>) {
    if (this is ApiResponse.Failure.Exception && throwable is CancellationException) {
      throw throwable
    }
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
  crossinline onResult: ApiResponse.Failure.Error.() -> Unit,
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
  crossinline onResult: suspend ApiResponse.Failure.Error.() -> Unit,
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
  crossinline onResult: ApiResponse.Failure.Exception.() -> Unit,
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
 * Note: If the wrapped [ApiResponse.Failure.Exception.throwable] is a [CancellationException],
 * it will be re-thrown to preserve coroutine cancellation semantics.
 *
 * @param onResult The receiver function that receiving [ApiResponse.Failure.Exception] if the request get an exception.
 *
 * @return The original [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
@Throws(CancellationException::class)
public suspend inline fun <T> ApiResponse<T>.suspendOnException(
  crossinline onResult: suspend ApiResponse.Failure.Exception.() -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Exception) {
    if (throwable is CancellationException) {
      throw throwable
    }
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
  crossinline onError: ApiResponse.Failure.Error.() -> Unit,
  crossinline onException: ApiResponse.Failure.Exception.() -> Unit,
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
 *  @return The original [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendOnProcedure(
  crossinline onSuccess: suspend ApiResponse.Success<T>.() -> Unit,
  crossinline onError: suspend ApiResponse.Failure.Error.() -> Unit,
  crossinline onException: suspend ApiResponse.Failure.Exception.() -> Unit,
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
 * Maps a [T] type of the [ApiResponse] to a [V] type of the [ApiResponse].
 *
 * @param transformer A transformer that receives [T] and returns [V].
 *
 * @return A [V] type of the [ApiResponse].
 */
public inline fun <reified T, reified V> ApiResponse<T>.flatMap(
  crossinline transformer: ApiResponse<T>.() -> ApiResponse<V>,
): ApiResponse<V> = transformer.invoke(this)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps a [T] type of the [ApiResponse] to a [V] type of the [ApiResponse].
 *
 * @param transformer A transformer that receives [T] and returns [V].
 *
 * @return A [V] type of the [ApiResponse].
 */
public suspend inline fun <reified T, reified V> ApiResponse<T>.suspendFlatMap(
  crossinline transformer: suspend ApiResponse<T>.() -> ApiResponse<V>,
): ApiResponse<V> = transformer.invoke(this)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps a [T] type of the [ApiResponse] to a [V] type of the [ApiResponse].
 *
 * @param mapper A transformer that receives [T] and returns [V].
 *
 * @return A [V] type of the [ApiResponse].
 */
public inline fun <reified T, reified V> ApiResponse<T>.flatmap(
  mapper: ApiResponseMapper<T, V>,
): ApiResponse<V> = mapper.map(this)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps a [T] type of the [ApiResponse] to a [V] type of the [ApiResponse] if the [ApiResponse] is [ApiResponse.Success].
 *
 * @param transformer A transformer that receives [T] and returns [V].
 *
 * @return A [V] type of the [ApiResponse].
 */
@Suppress("UNCHECKED_CAST")
public inline fun <reified T, reified V> ApiResponse<T>.mapSuccess(
  crossinline transformer: T.() -> V,
): ApiResponse<V> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success<T>) {
    return ApiResponse.of(tag = tag) { transformer(data) }
  }
  return this as ApiResponse<V>
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps a [T] type of the [ApiResponse] to a [V] type of the [ApiResponse] if the [ApiResponse] is [ApiResponse.Success].
 *
 * @param transformer A suspend transformer that receives [T] and returns [V].
 *
 * @return A [V] type of the [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
@Suppress("UNCHECKED_CAST")
public suspend inline fun <reified T, reified V> ApiResponse<T>.suspendMapSuccess(
  crossinline transformer: suspend T.() -> V,
): ApiResponse<V> {
  if (this is ApiResponse.Success<T>) {
    val invoke = transformer(data)
    return ApiResponse.of(tag = tag) { invoke }
  }
  return this as ApiResponse<V>
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [Any] type of the [ApiResponse.Failure.Error.payload] to another Any type.
 *
 * @param transformer A transformer that receives [Any] and returns [Any].
 *
 * @return A [T] type of the [ApiResponse].
 */
public fun <T> ApiResponse<T>.mapFailure(transformer: Any?.() -> Any?): ApiResponse<T> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Error) {
    return ApiResponse.Failure.Error(payload = transformer.invoke(payload))
  } else if (this is ApiResponse.Failure.Exception) {
    return ApiResponse.exception(ex = (transformer.invoke(throwable) as? Throwable) ?: throwable)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [Any] type of the [ApiResponse.Failure.Error.payload] to another Any type.
 *
 * @param transformer A transformer that receives [Any] and returns [Any].
 *
 * @return A [T] type of the [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public suspend fun <T> ApiResponse<T>.suspendMapFailure(
  transformer: suspend Any?.() -> Any?,
): ApiResponse<T> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Error) {
    return ApiResponse.Failure.Error(payload = transformer.invoke(payload))
  } else if (this is ApiResponse.Failure.Exception) {
    return ApiResponse.exception(ex = (transformer.invoke(throwable) as? Throwable) ?: throwable)
  }
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
public fun <T, V> ApiResponse.Success<T>.map(mapper: ApiSuccessModelMapper<T, V>): V =
  mapper.map(this)

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
 * @param mapper A mapper interface for mapping [ApiResponse.Failure.Error] response as a custom [T] instance model.
 *
 * @return A mapped custom [T] error response model.
 */
public fun <T> ApiResponse.Failure.Error.map(mapper: ApiErrorModelMapper<T>): T = mapper.map(this)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Failure.Error] to a customized error response model.
 *
 * @param mapper An executable lambda for mapping [ApiResponse.Failure.Error] response as a custom [T] instance model.
 *
 * @return A mapped custom [T] error response model.
 */
public fun <T> ApiResponse.Failure.Error.map(mapper: (ApiResponse.Failure.Error) -> T): T {
  contract { callsInPlace(mapper, InvocationKind.AT_MOST_ONCE) }
  return mapper(this)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Failure.Error] to a customized error response model with a receiver scope lambda.
 *
 * @param mapper A mapper interface for mapping [ApiResponse.Failure.Error] response as a custom [T] instance model.
 * @param onResult A receiver scope lambda of the mapped custom [T] error response model.
 *
 * @return A mapped custom [T] error response model.
 */
@JvmSynthetic
public inline fun <T> ApiResponse.Failure.Error.map(
  mapper: ApiErrorModelMapper<T>,
  crossinline onResult: T.() -> Unit,
) {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  onResult(mapper.map(this))
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Failure.Error] to a customized error response model with a suspension receiver scope lambda.
 *
 * @param mapper A mapper interface for mapping [ApiResponse.Failure.Error] response as a custom [T] instance model.
 * @param onResult A suspension receiver scope lambda of the mapped custom [T] error response model.
 *
 * @return A mapped custom [T] error response model.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse.Failure.Error.suspendMap(
  mapper: ApiErrorModelMapper<T>,
  crossinline onResult: suspend T.() -> Unit,
) {
  contract { callsInPlace(onResult, InvocationKind.AT_MOST_ONCE) }
  onResult(mapper.map(this))
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps [ApiResponse.Failure.Error] to a customized error response model with a suspension receiver scope lambda.
 *
 * @param mapper A mapper interface for mapping [ApiResponse.Failure.Error] response as a custom [T] instance model.
 *
 * @return A mapped custom [T] error response model.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse.Failure.Error.suspendMap(
  crossinline mapper: suspend (ApiResponse.Failure.Error) -> T,
): T = mapper(this)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns the tag value if this instance represents [ApiResponse.Success].
 *
 * @return The encapsulated data.
 */
public fun <T> ApiResponse<T>.tagOrNull(): Any? = if (this is ApiResponse.Success) {
  tag
} else {
  null
}

/**
 * Returns an error message from the [ApiResponse.Failure] that consists of the localized message.
 *
 * @return An error message from the [ApiResponse.Failure].
 */
public fun <T> ApiResponse.Failure<T>.message(): String = when (this) {
  is ApiResponse.Failure.Error -> message()
  is ApiResponse.Failure.Exception -> message()
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns an error message from the [ApiResponse.Failure.Error] that consists of the status and error response.
 *
 * @return An error message from the [ApiResponse.Failure.Error].
 */
public fun ApiResponse.Failure.Error.message(): String = payload?.toString() ?: toString()

/**
 * Returns an error message from the [ApiResponse.Failure.Exception] that consists of the localized message.
 *
 * @return An error message from the [ApiResponse.Failure.Exception].
 */
public fun ApiResponse.Failure.Exception.message(): String = message ?: toString()

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
 * Merges multiple [ApiResponse]s as one [ApiResponse] depending on the policy, [ApiResponseMergePolicy].
 * The default policy is [ApiResponseMergePolicy.IGNORE_FAILURE].
 *
 * @param responses Responses for merging as one [ApiResponse].
 * @param mergePolicy A policy for merging response data depend on the success or not.
 *
 * @return [ApiResponse] that depends on the [ApiResponseMergePolicy].
 */
@JvmSynthetic
public fun <T> ApiResponse<List<T>>.merge(
  vararg responses: ApiResponse<List<T>>,
  mergePolicy: ApiResponseMergePolicy = ApiResponseMergePolicy.IGNORE_FAILURE,
): ApiResponse<List<T>> {
  val apiResponses = responses.toMutableList()
  apiResponses.add(0, this)

  var apiResponse: ApiResponse<List<T>> = ApiResponse.of(tag = tagOrNull()) { mutableListOf() }

  val data: MutableList<T> = mutableListOf()

  for (response in apiResponses) {
    if (response is ApiResponse.Success) {
      data.addAll(response.data)
      apiResponse = ApiResponse.Success(data = data, tag = response.tag)
    } else if (mergePolicy === ApiResponseMergePolicy.PREFERRED_FAILURE) {
      return response
    }
  }

  return apiResponse
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns a [Flow] which emits successful data if the response is a [ApiResponse.Success] and the data is not null.
 *
 * @return A coroutines [Flow] which emits successful data.
 */
@JvmSynthetic
public fun <T> ApiResponse<T>.toFlow(): Flow<T> = if (this is ApiResponse.Success) {
  flowOf(data)
} else {
  emptyFlow()
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
public inline fun <T, R> ApiResponse<T>.toFlow(crossinline transformer: T.() -> R): Flow<R> {
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

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [ApiResponse.Success]
 * or the result of [onFailure] function for the encapsulated [ApiResponse.Failure.Error] or [ApiResponse.Failure.Exception].
 *
 * @param onSuccess A scope function that would be executed for handling successful responses if the request succeeds.
 * @param onError A scope function that would be executed for handling error responses if the request failed.
 *
 * @return A coroutines [Flow] which emits successful data.
 */
@JvmSynthetic
public inline fun <T, R> ApiResponse<T>.fold(
  onSuccess: (value: T) -> R,
  onFailure: (message: String) -> R,
): R {
  contract {
    callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
  }
  return if (this is ApiResponse.Success) {
    onSuccess.invoke(data)
  } else {
    val failure = this as ApiResponse.Failure
    onFailure.invoke(failure.message())
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [ApiResponse.Success]
 * or the result of [onFailure] function for the encapsulated [ApiResponse.Failure.Error] or [ApiResponse.Failure.Exception].
 *
 * @param onSuccess A scope function that would be executed for handling successful responses if the request succeeds.
 * @param onError A scope function that would be executed for handling error responses if the request failed.
 *
 * @return A coroutines [Flow] which emits successful data.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, R> ApiResponse<T>.suspendFold(
  onSuccess: suspend (value: T) -> R,
  onFailure: suspend (message: String) -> R,
): R {
  contract {
    callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
  }
  return if (this is ApiResponse.Success) {
    onSuccess.invoke(data)
  } else {
    val failure = this as ApiResponse.Failure
    onFailure.invoke(failure.message())
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Recovers the [ApiResponse.Failure] to [ApiResponse.Success] with a given [fallback] value.
 *
 * @param fallback A fallback value that will be used to recover the failure.
 *
 * @return An [ApiResponse.Success] with the fallback value if this is a failure, otherwise the original success.
 */
@JvmSynthetic
public fun <T> ApiResponse<T>.recover(fallback: T): ApiResponse<T> {
  if (this is ApiResponse.Failure) {
    return ApiResponse.Success(data = fallback)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Recovers the [ApiResponse.Failure] to [ApiResponse.Success] with a lazily evaluated fallback value.
 *
 * @param fallback A lambda that provides the fallback value when the response is a failure.
 *
 * @return An [ApiResponse.Success] with the fallback value if this is a failure, otherwise the original success.
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.recover(crossinline fallback: () -> T): ApiResponse<T> {
  contract { callsInPlace(fallback, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure) {
    return ApiResponse.Success(data = fallback())
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Recovers the [ApiResponse.Failure] by executing an alternative [ApiResponse] from the given [fallback] lambda.
 *
 * @param fallback A lambda that receives the [ApiResponse.Failure] and returns an alternative [ApiResponse].
 *
 * @return The alternative [ApiResponse] if this is a failure, otherwise the original success.
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.recoverWith(
  crossinline fallback: (ApiResponse.Failure<T>) -> ApiResponse<T>,
): ApiResponse<T> {
  contract { callsInPlace(fallback, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure) {
    return fallback(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Recovers the [ApiResponse.Failure] to [ApiResponse.Success] with a suspend fallback lambda.
 *
 * @param fallback A suspend lambda that provides the fallback value when the response is a failure.
 *
 * @return An [ApiResponse.Success] with the fallback value if this is a failure, otherwise the original success.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendRecover(
  crossinline fallback: suspend () -> T,
): ApiResponse<T> {
  contract { callsInPlace(fallback, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure) {
    return ApiResponse.Success(data = fallback())
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Recovers the [ApiResponse.Failure] by executing an alternative suspend [ApiResponse] from the given [fallback] lambda.
 *
 * @param fallback A suspend lambda that receives the [ApiResponse.Failure] and returns an alternative [ApiResponse].
 *
 * @return The alternative [ApiResponse] if this is a failure, otherwise the original success.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendRecoverWith(
  crossinline fallback: suspend (ApiResponse.Failure<T>) -> ApiResponse<T>,
): ApiResponse<T> {
  contract { callsInPlace(fallback, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure) {
    return fallback(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Validates the success data with the given [predicate].
 * If the predicate returns false, the response is converted to [ApiResponse.Failure.Error].
 *
 * @param predicate A predicate function that validates the success data.
 * @param errorMessage A lambda that provides the error message when validation fails.
 *
 * @return The original [ApiResponse.Success] if validation passes, otherwise [ApiResponse.Failure.Error].
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.validate(
  crossinline predicate: (T) -> Boolean,
  crossinline errorMessage: () -> String = { "Validation failed" },
): ApiResponse<T> {
  contract {
    callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    callsInPlace(errorMessage, InvocationKind.AT_MOST_ONCE)
  }
  if (this is ApiResponse.Success && !predicate(data)) {
    return ApiResponse.Failure.Error(payload = errorMessage())
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Validates the success data with the given suspend [predicate].
 * If the predicate returns false, the response is converted to [ApiResponse.Failure.Error].
 *
 * @param predicate A suspend predicate function that validates the success data.
 * @param errorMessage The error message when validation fails.
 *
 * @return The original [ApiResponse.Success] if validation passes, otherwise [ApiResponse.Failure.Error].
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendValidate(
  crossinline predicate: suspend (T) -> Boolean,
  errorMessage: String = "Validation failed",
): ApiResponse<T> {
  contract { callsInPlace(predicate, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success && !predicate(data)) {
    return ApiResponse.Failure.Error(payload = errorMessage)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Requires a non-null value from the success data using the given [selector].
 * If the selected value is null, the response is converted to [ApiResponse.Failure.Error].
 *
 * @param selector A selector function that extracts a nullable value from the success data.
 * @param errorMessage A lambda that provides the error message when the selected value is null.
 *
 * @return An [ApiResponse] with the non-null selected value, or [ApiResponse.Failure.Error] if null.
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
public inline fun <T, R> ApiResponse<T>.requireNotNull(
  crossinline selector: (T) -> R?,
  crossinline errorMessage: () -> String = { "Required value was null" },
): ApiResponse<R> {
  contract {
    callsInPlace(selector, InvocationKind.AT_MOST_ONCE)
    callsInPlace(errorMessage, InvocationKind.AT_MOST_ONCE)
  }
  if (this is ApiResponse.Success) {
    val selected = selector(data)
    return if (selected != null) {
      ApiResponse.Success(data = selected, tag = tag)
    } else {
      ApiResponse.Failure.Error(payload = errorMessage())
    }
  }
  return this as ApiResponse<R>
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Requires a non-null value from the success data using the given suspend [selector].
 * If the selected value is null, the response is converted to [ApiResponse.Failure.Error].
 *
 * @param selector A suspend selector function that extracts a nullable value from the success data.
 * @param errorMessage The error message when the selected value is null.
 *
 * @return An [ApiResponse] with the non-null selected value, or [ApiResponse.Failure.Error] if null.
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, R> ApiResponse<T>.suspendRequireNotNull(
  crossinline selector: suspend (T) -> R?,
  errorMessage: String = "Required value was null",
): ApiResponse<R> {
  contract { callsInPlace(selector, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success) {
    val selected = selector(data)
    return if (selected != null) {
      ApiResponse.Success(data = selected, tag = tag)
    } else {
      ApiResponse.Failure.Error(payload = errorMessage)
    }
  }
  return this as ApiResponse<R>
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Filters the items in the success data list with the given [predicate].
 * This is useful when the success data is a [List] and you want to filter its elements.
 *
 * @param predicate A predicate function that filters each item in the list.
 *
 * @return An [ApiResponse.Success] with the filtered list, or the original failure.
 */
@JvmSynthetic
public inline fun <T> ApiResponse<List<T>>.filter(
  crossinline predicate: (T) -> Boolean,
): ApiResponse<List<T>> {
  contract { callsInPlace(predicate, InvocationKind.UNKNOWN) }
  if (this is ApiResponse.Success) {
    return ApiResponse.Success(data = data.filter(predicate), tag = tag)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Filters the items in the success data list with the given suspend [predicate].
 * This is useful when the success data is a [List] and you want to filter its elements.
 *
 * @param predicate A suspend predicate function that filters each item in the list.
 *
 * @return An [ApiResponse.Success] with the filtered list, or the original failure.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<List<T>>.suspendFilter(
  crossinline predicate: suspend (T) -> Boolean,
): ApiResponse<List<T>> {
  if (this is ApiResponse.Success) {
    val filtered = data.filter { predicate(it) }
    return ApiResponse.Success(data = filtered, tag = tag)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Filters the success data list to exclude items that match the given [predicate].
 * This is useful when the success data is a [List] and you want to exclude certain elements.
 *
 * @param predicate A predicate function that determines which items to exclude.
 *
 * @return An [ApiResponse.Success] with the filtered list excluding matching items, or the original failure.
 */
@JvmSynthetic
public inline fun <T> ApiResponse<List<T>>.filterNot(
  crossinline predicate: (T) -> Boolean,
): ApiResponse<List<T>> {
  contract { callsInPlace(predicate, InvocationKind.UNKNOWN) }
  if (this is ApiResponse.Success) {
    return ApiResponse.Success(data = data.filterNot(predicate), tag = tag)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Filters the success data list to exclude items that match the given suspend [predicate].
 * This is useful when the success data is a [List] and you want to exclude certain elements.
 *
 * @param predicate A suspend predicate function that determines which items to exclude.
 *
 * @return An [ApiResponse.Success] with the filtered list excluding matching items, or the original failure.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<List<T>>.suspendFilterNot(
  crossinline predicate: suspend (T) -> Boolean,
): ApiResponse<List<T>> {
  if (this is ApiResponse.Success) {
    val filtered = data.filterNot { predicate(it) }
    return ApiResponse.Success(data = filtered, tag = tag)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Combines two [ApiResponse]s into a single [ApiResponse] using the given [transform] function.
 * If both responses are successful, the transform function is applied to combine the data.
 * If either response is a failure, the first failure is returned.
 *
 * @param other The other [ApiResponse] to combine with.
 * @param transform A function that combines the data from both successful responses.
 *
 * @return A combined [ApiResponse] with the transformed data, or the first failure.
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
public inline fun <T, U, R> ApiResponse<T>.zip(
  other: ApiResponse<U>,
  crossinline transform: (T, U) -> R,
): ApiResponse<R> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return when {
    this is ApiResponse.Success && other is ApiResponse.Success -> {
      ApiResponse.Success(data = transform(this.data, other.data), tag = this.tag)
    }
    this is ApiResponse.Failure -> this as ApiResponse<R>
    else -> other as ApiResponse<R>
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Combines two [ApiResponse]s into a single [ApiResponse] using the given suspend [transform] function.
 * If both responses are successful, the transform function is applied to combine the data.
 * If either response is a failure, the first failure is returned.
 *
 * @param other The other [ApiResponse] to combine with.
 * @param transform A suspend function that combines the data from both successful responses.
 *
 * @return A combined [ApiResponse] with the transformed data, or the first failure.
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, U, R> ApiResponse<T>.suspendZip(
  other: ApiResponse<U>,
  crossinline transform: suspend (T, U) -> R,
): ApiResponse<R> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return when {
    this is ApiResponse.Success && other is ApiResponse.Success -> {
      ApiResponse.Success(data = transform(this.data, other.data), tag = this.tag)
    }
    this is ApiResponse.Failure -> this as ApiResponse<R>
    else -> other as ApiResponse<R>
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Combines three [ApiResponse]s into a single [ApiResponse] using the given [transform] function.
 * If all responses are successful, the transform function is applied to combine the data.
 * If any response is a failure, the first failure is returned.
 *
 * @param second The second [ApiResponse] to combine with.
 * @param third The third [ApiResponse] to combine with.
 * @param transform A function that combines the data from all successful responses.
 *
 * @return A combined [ApiResponse] with the transformed data, or the first failure.
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
public inline fun <T, U, V, R> ApiResponse<T>.zip3(
  second: ApiResponse<U>,
  third: ApiResponse<V>,
  crossinline transform: (T, U, V) -> R,
): ApiResponse<R> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return when {
    this is ApiResponse.Success &&
      second is ApiResponse.Success &&
      third is ApiResponse.Success -> {
      ApiResponse.Success(
        data = transform(this.data, second.data, third.data),
        tag = this.tag,
      )
    }
    this is ApiResponse.Failure -> this as ApiResponse<R>
    second is ApiResponse.Failure -> second as ApiResponse<R>
    else -> third as ApiResponse<R>
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Combines three [ApiResponse]s into a single [ApiResponse] using the given suspend [transform]
 * function.
 * If all responses are successful, the transform function is applied to combine the data.
 * If any response is a failure, the first failure is returned.
 *
 * @param second The second [ApiResponse] to combine with.
 * @param third The third [ApiResponse] to combine with.
 * @param transform A suspend function that combines the data from all successful responses.
 *
 * @return A combined [ApiResponse] with the transformed data, or the first failure.
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T, U, V, R> ApiResponse<T>.suspendZip3(
  second: ApiResponse<U>,
  third: ApiResponse<V>,
  crossinline transform: suspend (T, U, V) -> R,
): ApiResponse<R> {
  contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
  return when {
    this is ApiResponse.Success &&
      second is ApiResponse.Success &&
      third is ApiResponse.Success -> {
      ApiResponse.Success(
        data = transform(this.data, second.data, third.data),
        tag = this.tag,
      )
    }
    this is ApiResponse.Failure -> this as ApiResponse<R>
    second is ApiResponse.Failure -> second as ApiResponse<R>
    else -> third as ApiResponse<R>
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Combines two [ApiResponse]s into a [Pair] of their data.
 * If both responses are successful, returns an [ApiResponse.Success] containing the paired data.
 * If either response is a failure, the first failure is returned.
 *
 * @param other The other [ApiResponse] to combine with.
 *
 * @return An [ApiResponse] containing a [Pair] of data, or the first failure.
 */
@Suppress("UNCHECKED_CAST")
@JvmSynthetic
public fun <T, U> ApiResponse<T>.zip(other: ApiResponse<U>): ApiResponse<Pair<T, U>> = when {
  this is ApiResponse.Success && other is ApiResponse.Success -> {
    ApiResponse.Success(data = Pair(this.data, other.data), tag = this.tag)
  }
  this is ApiResponse.Failure -> this as ApiResponse<Pair<T, U>>
  else -> other as ApiResponse<Pair<T, U>>
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Performs the given [action] on the [ApiResponse] without modifying it.
 * This is useful for logging or debugging purposes.
 *
 * @param action An action to perform on the [ApiResponse].
 *
 * @return The original [ApiResponse] unchanged.
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.peek(
  crossinline action: (ApiResponse<T>) -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
  action(this)
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Performs the given suspend [action] on the [ApiResponse] without modifying it.
 * This is useful for logging or debugging purposes.
 *
 * @param action A suspend action to perform on the [ApiResponse].
 *
 * @return The original [ApiResponse] unchanged.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendPeek(
  crossinline action: suspend (ApiResponse<T>) -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
  action(this)
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Performs the given [action] on the success data if this is an [ApiResponse.Success].
 * This is useful for logging or side effects without modifying the response.
 *
 * @param action An action to perform on the success data.
 *
 * @return The original [ApiResponse] unchanged.
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.peekSuccess(crossinline action: (T) -> Unit): ApiResponse<T> {
  contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success) {
    action(data)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Performs the given suspend [action] on the success data if this is an [ApiResponse.Success].
 * This is useful for logging or side effects without modifying the response.
 *
 * @param action A suspend action to perform on the success data.
 *
 * @return The original [ApiResponse] unchanged.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendPeekSuccess(
  crossinline action: suspend (T) -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success) {
    action(data)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Performs the given [action] on the failure if this is an [ApiResponse.Failure].
 * This is useful for logging or side effects without modifying the response.
 *
 * @param action An action to perform on the failure.
 *
 * @return The original [ApiResponse] unchanged.
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.peekFailure(
  crossinline action: (ApiResponse.Failure<T>) -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure) {
    action(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Performs the given suspend [action] on the failure if this is an [ApiResponse.Failure].
 * This is useful for logging or side effects without modifying the response.
 *
 * @param action A suspend action to perform on the failure.
 *
 * @return The original [ApiResponse] unchanged.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendPeekFailure(
  crossinline action: suspend (ApiResponse.Failure<T>) -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure) {
    action(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Performs the given [action] on the error if this is an [ApiResponse.Failure.Error].
 * This is useful for logging or side effects without modifying the response.
 *
 * @param action An action to perform on the error.
 *
 * @return The original [ApiResponse] unchanged.
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.peekError(
  crossinline action: (ApiResponse.Failure.Error) -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Error) {
    action(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Performs the given suspend [action] on the error if this is an [ApiResponse.Failure.Error].
 * This is useful for logging or side effects without modifying the response.
 *
 * @param action A suspend action to perform on the error.
 *
 * @return The original [ApiResponse] unchanged.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendPeekError(
  crossinline action: suspend (ApiResponse.Failure.Error) -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Error) {
    action(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Performs the given [action] on the exception if this is an [ApiResponse.Failure.Exception].
 * This is useful for logging or side effects without modifying the response.
 *
 * @param action An action to perform on the exception.
 *
 * @return The original [ApiResponse] unchanged.
 */
@JvmSynthetic
public inline fun <T> ApiResponse<T>.peekException(
  crossinline action: (ApiResponse.Failure.Exception) -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Exception) {
    action(this)
  }
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Performs the given suspend [action] on the exception if this is an [ApiResponse.Failure.Exception].
 * This is useful for logging or side effects without modifying the response.
 *
 * @param action A suspend action to perform on the exception.
 *
 * @return The original [ApiResponse] unchanged.
 */
@JvmSynthetic
@SuspensionFunction
public suspend inline fun <T> ApiResponse<T>.suspendPeekException(
  crossinline action: suspend (ApiResponse.Failure.Exception) -> Unit,
): ApiResponse<T> {
  contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Exception) {
    action(this)
  }
  return this
}
