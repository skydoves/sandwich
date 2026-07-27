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
@file:JvmName("FlowTransformer")
@file:JvmMultifileClass

package com.skydoves.sandwich.flow

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.SuspensionFunction
import com.skydoves.sandwich.getOrNull
import com.skydoves.sandwich.message
import com.skydoves.sandwich.suspendMapSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.cancellation.CancellationException
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Creates a [Flow] that emits a single [ApiResponse] produced by [block].
 *
 * A repository usually wraps a request in `flow { emit(service.fetch()) }` and has to remember to
 * capture exceptions itself. This builder does that capture, so a throwing [block] is emitted as
 * [ApiResponse.Failure.Exception] rather than cancelling the flow:
 *
 * ```kotlin
 * fun posters(): Flow<ApiResponse<List<Poster>>> = apiResponseFlow {
 *   service.fetchPosters()
 * }.flowOn(Dispatchers.IO)
 * ```
 *
 * A [CancellationException] is rethrown so coroutine cancellation keeps working.
 *
 * @param block A suspending block that produces the [ApiResponse] to emit.
 *
 * @return A [Flow] emitting exactly one [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public fun <T> apiResponseFlow(block: suspend () -> ApiResponse<T>): Flow<ApiResponse<T>> = flow {
  val response = try {
    block()
  } catch (e: CancellationException) {
    throw e
  } catch (e: Exception) {
    ApiResponse.Failure.Exception(e)
  }
  emit(response)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Invokes [action] for every emission that is an [ApiResponse.Success], leaving the flow unchanged.
 *
 * @param action A suspending action receiving the successful response.
 *
 * @return The original [Flow].
 */
@JvmSynthetic
@SuspensionFunction
public fun <T> Flow<ApiResponse<T>>.onSuccess(
  action: suspend ApiResponse.Success<T>.() -> Unit,
): Flow<ApiResponse<T>> = onEach { response ->
  if (response is ApiResponse.Success) action(response)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Invokes [action] for every emission that is an [ApiResponse.Failure.Error], leaving the flow
 * unchanged.
 *
 * @param action A suspending action receiving the error response.
 *
 * @return The original [Flow].
 */
@JvmSynthetic
@SuspensionFunction
public fun <T> Flow<ApiResponse<T>>.onError(
  action: suspend ApiResponse.Failure.Error.() -> Unit,
): Flow<ApiResponse<T>> = onEach { response ->
  if (response is ApiResponse.Failure.Error) action(response)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Invokes [action] for every emission that is an [ApiResponse.Failure.Exception], leaving the flow
 * unchanged.
 *
 * @param action A suspending action receiving the exception response.
 *
 * @return The original [Flow].
 */
@JvmSynthetic
@SuspensionFunction
public fun <T> Flow<ApiResponse<T>>.onException(
  action: suspend ApiResponse.Failure.Exception.() -> Unit,
): Flow<ApiResponse<T>> = onEach { response ->
  if (response is ApiResponse.Failure.Exception) action(response)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Invokes [action] for every emission that is an [ApiResponse.Failure], leaving the flow unchanged.
 *
 * @param action A suspending action receiving the failed response.
 *
 * @return The original [Flow].
 */
@JvmSynthetic
@SuspensionFunction
public fun <T> Flow<ApiResponse<T>>.onFailure(
  action: suspend ApiResponse.Failure<T>.() -> Unit,
): Flow<ApiResponse<T>> = onEach { response ->
  if (response is ApiResponse.Failure) action(response)
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps the data of every successful emission with [transformer], passing failures through.
 *
 * @param transformer A suspending transformer that receives [T] and returns [V].
 *
 * @return A [Flow] of the transformed [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
public inline fun <reified T, reified V> Flow<ApiResponse<T>>.mapSuccess(
  crossinline transformer: suspend T.() -> V,
): Flow<ApiResponse<V>> = map { response -> response.suspendMapSuccess { transformer() } }

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps every emission to its success data, or to `null` for a failure.
 *
 * @return A [Flow] of the nullable success data.
 */
@JvmSynthetic
public fun <T> Flow<ApiResponse<T>>.mapToDataOrNull(): Flow<T?> = map { it.getOrNull() }

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Keeps only successful emissions and unwraps their data, dropping failures.
 *
 * Use this when a failure is already handled elsewhere, for example by a global operator, and the
 * collector only cares about data.
 *
 * @return A [Flow] of the success data.
 */
@JvmSynthetic
public fun <T : Any> Flow<ApiResponse<T>>.filterSuccessData(): Flow<T> =
  mapNotNull { it.getOrNull() }

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps every emission to the result of [onSuccess] or [onFailure], collapsing the [ApiResponse]
 * into a single type such as a UI state.
 *
 * @param onSuccess A suspending mapper receiving the success data.
 * @param onFailure A suspending mapper receiving the failure message.
 *
 * @return A [Flow] of the folded value.
 */
@JvmSynthetic
@SuspensionFunction
public fun <T, R> Flow<ApiResponse<T>>.foldToFlow(
  onSuccess: suspend (value: T) -> R,
  onFailure: suspend (message: String) -> R,
): Flow<R> = map { response ->
  if (response is ApiResponse.Success) {
    onSuccess(response.data)
  } else {
    onFailure((response as ApiResponse.Failure).message())
  }
}
