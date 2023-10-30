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

package com.skydoves.sandwichdemo

import android.app.Application
import androidx.multidex.BuildConfig
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.mappers.ApiResponseFailureMapper
import com.skydoves.sandwichdemo.causes.LimitedRequest
import com.skydoves.sandwichdemo.causes.WrongArgument
import com.skydoves.sandwichdemo.operator.GlobalResponseOperator
import retrofit2.Response
import timber.log.Timber

class SandwichDemoApp : Application() {

  override fun onCreate() {
    super.onCreate()

    sandwichApp = this

    SandwichInitializer.sandwichOperators += GlobalResponseOperator<Any>(this)
    SandwichInitializer.sandwichFailureMappers += listOf(
      object : ApiResponseFailureMapper {
        override fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
          return if (apiResponse is ApiResponse.Failure.Error<*> &&
            apiResponse.payload is Response<*>
          ) {
            val response = apiResponse.payload as Response<*>
            if (response.errorBody().toString().contains("limited")) {
              LimitedRequest
            } else {
              apiResponse
            }
          } else {
            WrongArgument
          }
        }
      },
    )

    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }

  companion object {
    @JvmStatic
    lateinit var sandwichApp: Application
      private set
  }
}
