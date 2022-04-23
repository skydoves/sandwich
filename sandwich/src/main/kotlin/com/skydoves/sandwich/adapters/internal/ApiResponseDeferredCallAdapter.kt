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

package com.skydoves.sandwich.adapters.internal

import com.skydoves.sandwich.ApiResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponseCallAdapter is an call adapter for creating [ApiResponse] by executing Retrofit's service methods.
 *
 * Request API network call asynchronously and returns [Deferred] of [ApiResponse].
 */
internal class ApiResponseDeferredCallAdapter<T> constructor(
  private val resultType: Type
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

    call.enqueue(object : Callback<T> {
      override fun onResponse(call: Call<T>, response: Response<T>) {
        val apiResponse = ApiResponse.of { response }
        deferred.complete(apiResponse)
      }

      override fun onFailure(call: Call<T>, throwable: Throwable) {
        val apiResponse = ApiResponse.error<T>(throwable)
        deferred.complete(apiResponse)
      }
    })

    return deferred
  }
}
