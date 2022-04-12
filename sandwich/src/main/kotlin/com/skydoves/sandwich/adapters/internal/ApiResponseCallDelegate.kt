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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponseCallDelegate is a delegate [Call] proxy for handling and transforming normal generic type [T]
 * as [ApiResponse] that wrapping [T] data from the network responses.
 */
internal class ApiResponseCallDelegate<T>(proxy: Call<T>) : CallDelegate<T, ApiResponse<T>>(proxy) {

  override fun enqueueImpl(callback: Callback<ApiResponse<T>>) = proxy.enqueue(
    object : Callback<T> {
      override fun onResponse(call: Call<T>, response: Response<T>) {
        val apiResponse = ApiResponse.of { response }
        callback.onResponse(this@ApiResponseCallDelegate, Response.success(apiResponse))
      }

      override fun onFailure(call: Call<T>, t: Throwable) {
        callback.onResponse(this@ApiResponseCallDelegate, Response.success(ApiResponse.error(t)))
      }
    }
  )

  override fun cloneImpl() = ApiResponseCallDelegate(proxy.clone())
}
