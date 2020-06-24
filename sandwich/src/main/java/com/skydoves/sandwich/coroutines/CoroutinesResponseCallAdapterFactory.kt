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

import com.skydoves.sandwich.ApiResponse
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

class CoroutinesResponseCallAdapterFactory : CallAdapter.Factory() {

  override fun get(
    returnType: Type,
    annotations: Array<Annotation>,
    retrofit: Retrofit
  ) = when (getRawType(returnType)) {
    Call::class.java -> {
      val callType = getParameterUpperBound(0, returnType as ParameterizedType)
      when (getRawType(callType)) {
        ApiResponse::class.java -> {
          val resultType = getParameterUpperBound(0, callType as ParameterizedType)
          CoroutinesResponseCallAdapter(resultType)
        }
        else -> null
      }
    }
    else -> null
  }
}
