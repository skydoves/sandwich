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
package com.skydoves.sandwich.ktorfit

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.ApiResponse.Companion.maps
import com.skydoves.sandwich.ApiResponse.Companion.operate
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlin.jvm.JvmStatic

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponseConverterFactory is Ktorfit converter factory for creating [ApiResponse].
 *
 * Adding this class to [Ktorfit] allows you to return on [ApiResponse] from service method.
 *
 * ```
 * @GET("DisneyPosters.json")
 * suspend fun fetchDisneyPosterList(): ApiResponse<List<Poster>>
 * ```
 */
public class ApiResponseConverterFactory internal constructor() : Converter.Factory {

  override fun suspendResponseConverter(
    typeData: TypeData,
    ktorfit: Ktorfit,
  ): Converter.SuspendResponseConverter<HttpResponse, ApiResponse<Any>>? {
    if (typeData.typeInfo.type == ApiResponse::class) {
      return object : Converter.SuspendResponseConverter<HttpResponse, ApiResponse<Any>> {
        override suspend fun convert(response: HttpResponse): ApiResponse<Any> {
          val apiResponse: ApiResponse<Any> = try {
            ApiResponse.Success(response.body(typeData.typeArgs.first().typeInfo))
          } catch (e: Throwable) {
            ApiResponse.exception(e)
          }
          return apiResponse.operate().maps()
        }
      }
    }

    return null
  }

  public companion object {
    @JvmStatic
    public fun create(): ApiResponseConverterFactory {
      return ApiResponseConverterFactory()
    }
  }
}
