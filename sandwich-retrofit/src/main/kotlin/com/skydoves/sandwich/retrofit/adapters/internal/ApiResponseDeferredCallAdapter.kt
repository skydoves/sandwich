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
package com.skydoves.sandwich.retrofit.adapters.internal

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.retrofit.of
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.awaitResponse
import java.lang.reflect.Type

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponseCallAdapter is an call adapter for creating [ApiResponse] by executing Retrofit's service methods.
 *
 * Request API network call asynchronously and returns [Deferred] of [ApiResponse].
 */
internal class ApiResponseDeferredCallAdapter<T>(
  private val resultType: Type,
  private val coroutineScope: CoroutineScope,
) : CallAdapter<T, Deferred<ApiResponse<T>>> {

  override fun responseType(): Type {
    return resultType
  }

  @Suppress("DeferredIsResult")
  override fun adapt(call: Call<T>): Deferred<ApiResponse<T>> {
    val deferred = CompletableDeferred<ApiResponse<T>>().apply {
      invokeOnCompletion {
        if (isCancelled && !call.isCanceled) {
          call.cancel()
        }
      }
    }

    coroutineScope.launch {
      try {
        val response = call.awaitResponse()
        val apiResponse = ApiResponse.of { response }
        deferred.complete(apiResponse)
      } catch (e: Exception) {
        val apiResponse = ApiResponse.error<T>(e)
        deferred.complete(apiResponse)
      }
    }

    return deferred
  }
}
