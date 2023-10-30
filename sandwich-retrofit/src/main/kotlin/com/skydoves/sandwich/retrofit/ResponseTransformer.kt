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
package com.skydoves.sandwich.retrofit

import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Requests asynchronously and executes the lambda that receives [ApiResponse] as a result.
 *
 * @param onResult An lambda that receives [ApiResponse] as a result.
 *
 * @return The original [Call].
 */
@JvmSynthetic
public inline fun <T> Call<T>.request(
  crossinline onResult: (response: ApiResponse<T>) -> Unit,
): Call<T> {
  contract {
    callsInPlace(onResult, InvocationKind.AT_MOST_ONCE)
  }
  enqueue(getCallbackFromOnResult(onResult))
  return this
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns a response callback from an onResult lambda.
 *
 * @param onResult A lambda that would be executed when the request finished.
 *
 * @return A [Callback] will be executed.
 */
@PublishedApi
@JvmSynthetic
internal inline fun <T> getCallbackFromOnResult(
  crossinline onResult: (response: ApiResponse<T>) -> Unit,
): Callback<T> {
  return object : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
      onResult(ApiResponse.responseOf { response })
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
      onResult(ApiResponse.exception(throwable))
    }
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns a response callback from an onResult lambda.
 *
 * @param onResult A lambda that would be executed when the request finished.
 *
 * @return A [Callback] will be executed.
 */
@PublishedApi
@JvmSynthetic
internal inline fun <T> getCallbackFromOnResultOnCoroutinesScope(
  coroutineScope: CoroutineScope,
  crossinline onResult: suspend (response: ApiResponse<T>) -> Unit,
): Callback<T> {
  return object : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
      coroutineScope.launch {
        onResult(ApiResponse.responseOf { response })
      }
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
      coroutineScope.launch {
        onResult(ApiResponse.exception(throwable))
      }
    }
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns a response callback from an onResult lambda.
 *
 * @param onResult A lambda that would be executed when the request finished.
 *
 * @return A [Callback] will be executed.
 */
@PublishedApi
@JvmSynthetic
internal inline fun <T> getCallbackFromOnResultWithContext(
  context: CoroutineContext = EmptyCoroutineContext,
  crossinline onResult: suspend (response: ApiResponse<T>) -> Unit,
): Callback<T> {
  return object : Callback<T> {
    val supervisorJob = SupervisorJob(context[Job])
    val scope = CoroutineScope(context + supervisorJob)
    override fun onResponse(call: Call<T>, response: Response<T>) {
      scope.launch {
        onResult(ApiResponse.responseOf { response })
      }
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
      scope.launch {
        onResult(ApiResponse.exception(throwable))
      }
    }
  }
}
