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
import com.skydoves.sandwich.ApiResponseMergePolicy
import com.skydoves.sandwich.SuspensionFunction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
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
      onResult(ApiResponse.of { response })
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
      onResult(ApiResponse.error(throwable))
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
        onResult(ApiResponse.of { response })
      }
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
      coroutineScope.launch {
        onResult(ApiResponse.error(throwable))
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
        onResult(ApiResponse.of { response })
      }
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
      scope.launch {
        onResult(ApiResponse.error(throwable))
      }
    }
  }
}

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
public fun <T, V> ApiResponse<T>.mapSuccess(transformer: T.() -> V): ApiResponse<V> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success<T>) {
    return ApiResponse.of { Response.success(transformer(data)) }
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
public suspend fun <T, V> ApiResponse<T>.suspendMapSuccess(
  transformer: suspend T.() -> V,
): ApiResponse<V> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Success<T>) {
    val invoke = transformer.invoke(data)
    return ApiResponse.of { Response.success(invoke) }
  }
  return this as ApiResponse<V>
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps a [T] type of the [ApiResponse] to a [V] type of the [ApiResponse] if the [ApiResponse] is [ApiResponse.Failure].
 *
 * @param transformer A transformer that receives [T] and returns [V].
 *
 * @return A [V] type of the [ApiResponse].
 */
@Suppress("UNCHECKED_CAST")
public fun <T, V> ApiResponse<T>.mapFailure(
  contentType: MediaType = "text/plain".toMediaType(),
  transformer: (ResponseBody?) -> V,
): ApiResponse<V> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Error<T>) {
    return ApiResponse.of {
      Response.error(
        statusCode.code,
        transformer(errorBody).toString().toResponseBody(
          contentType = contentType,
        ),
      )
    }
  } else if (this is ApiResponse.Failure.Exception<T>) {
    return ApiResponse.error(exception)
  }
  return this as ApiResponse<V>
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Maps a [T] type of the [ApiResponse] to a [V] type of the [ApiResponse] if the [ApiResponse] is [ApiResponse.Failure].
 *
 * @param transformer A suspend transformer that receives [T] and returns [V].
 *
 * @return A [V] type of the [ApiResponse].
 */
@JvmSynthetic
@SuspensionFunction
@Suppress("UNCHECKED_CAST")
public suspend fun <T, V> ApiResponse<T>.suspendMapFailure(
  contentType: MediaType = "text/plain".toMediaType(),
  transformer: suspend (ResponseBody?) -> V,
): ApiResponse<V> {
  contract { callsInPlace(transformer, InvocationKind.AT_MOST_ONCE) }
  if (this is ApiResponse.Failure.Error<T>) {
    val invoke = transformer.invoke(errorBody)
    return ApiResponse.of {
      Response.error(
        statusCode.code,
        invoke.toString().toResponseBody(
          contentType = contentType,
        ),
      )
    }
  } else if (this is ApiResponse.Failure.Exception<T>) {
    return ApiResponse.error(exception)
  }
  return this as ApiResponse<V>
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

  var apiResponse: ApiResponse<List<T>> =
    ApiResponse.of { Response.success(mutableListOf(), Headers.headersOf()) }

  val data: MutableList<T> = mutableListOf()

  for (response in apiResponses) {
    if (response is ApiResponse.Success) {
      data.addAll(response.data)
      apiResponse = ApiResponse.of { Response.success(data, response.headers) }
    } else if (mergePolicy === ApiResponseMergePolicy.PREFERRED_FAILURE) {
      return response
    }
  }

  return apiResponse
}
