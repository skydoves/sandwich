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

package com.skydoves.sandwich.coroutines

import com.skydoves.sandwich.DataSource
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author skydoves (Jaewoong Eum)
 *
 * CoroutinesDataSourceCallAdapterFactory is an coroutines call adapter factory for creating [DataSource].
 *
 * Adding this class to [Retrofit] allows you to return on [DataSource] from service method.
 *
 * ```
 * @GET("DisneyPosters.json")
 * suspend fun fetchDisneyPosterList(): DataSource<List<Poster>>
 * ```
 */
public class CoroutinesDataSourceCallAdapterFactory private constructor() : CallAdapter.Factory() {

  override fun get(
    returnType: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ): CallAdapter<*, *>? = when (getRawType(returnType)) {
    Call::class.java -> {
      val callType = getParameterUpperBound(0, returnType as ParameterizedType)
      when (getRawType(callType)) {
        DataSource::class.java -> {
          val resultType = getParameterUpperBound(0, callType as ParameterizedType)
          CoroutinesDataSourceCallAdapter(resultType)
        }
        else -> null
      }
    }
    else -> null
  }

  public companion object {
    @JvmStatic
    public fun create(): CoroutinesDataSourceCallAdapterFactory =
      CoroutinesDataSourceCallAdapterFactory()
  }
}
