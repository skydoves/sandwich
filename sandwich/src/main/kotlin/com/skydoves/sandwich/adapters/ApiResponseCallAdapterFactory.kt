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

@file:Suppress("unused")

package com.skydoves.sandwich.adapters

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.adapters.internal.ApiResponseCallAdapter
import com.skydoves.sandwich.adapters.internal.ApiResponseDeferredCallAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author skydoves (Jaewoong Eum)
 *
 * CoroutinesResponseCallAdapterFactory is an coroutines call adapter factory for creating [ApiResponse].
 *
 * Adding this class to [Retrofit] allows you to return on [ApiResponse] from service method.
 *
 * ```
 * @GET("DisneyPosters.json")
 * suspend fun fetchDisneyPosterList(): ApiResponse<List<Poster>>
 * ```
 */
public class ApiResponseCallAdapterFactory private constructor(
  private val coroutineScope: CoroutineScope,
) : CallAdapter.Factory() {

  override fun get(
    returnType: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit,
  ): CallAdapter<*, *>? {
    when (getRawType(returnType)) {
      Call::class.java -> {
        val callType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawType = getRawType(callType)
        if (rawType != ApiResponse::class.java) {
          return null
        }

        val resultType = getParameterUpperBound(0, callType as ParameterizedType)
        return ApiResponseCallAdapter(resultType, coroutineScope)
      }
      Deferred::class.java -> {
        val callType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawType = getRawType(callType)
        if (rawType != ApiResponse::class.java) {
          return null
        }

        val resultType = getParameterUpperBound(0, callType as ParameterizedType)
        return ApiResponseDeferredCallAdapter<Any>(resultType, coroutineScope)
      }
      else -> return null
    }
  }

  public companion object {
    @JvmStatic
    public fun create(
      coroutineScope: CoroutineScope = SandwichInitializer.sandwichScope,
    ): ApiResponseCallAdapterFactory = ApiResponseCallAdapterFactory(coroutineScope)
  }
}
