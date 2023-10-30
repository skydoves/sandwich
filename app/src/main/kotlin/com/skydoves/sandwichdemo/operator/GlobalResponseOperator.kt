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
package com.skydoves.sandwichdemo.operator

import android.app.Application
import android.widget.Toast
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.map
import com.skydoves.sandwich.message
import com.skydoves.sandwich.operators.ApiResponseSuspendOperator
import com.skydoves.sandwich.retrofit.statusCode
import com.skydoves.sandwichdemo.mapper.ErrorEnvelopeMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * A global response operator for handling [ApiResponse]s regardless of its type.
 */
class GlobalResponseOperator<T>(
  private val application: Application,
) : ApiResponseSuspendOperator<T>() {

  // handle the case when the API request gets a success response.
  override suspend fun onSuccess(apiResponse: ApiResponse.Success<T>) {
    Timber.d("$apiResponse")
  }

  // handle the case when the API request gets a error response.
  // e.g., internal server error.
  override suspend fun onError(apiResponse: ApiResponse.Failure.Error<T>) {
    withContext(Dispatchers.Main.immediate) {
      apiResponse.run {
        Timber.d(message())

        // handling error based on status code.
        when (statusCode) {
          StatusCode.InternalServerError -> toast("InternalServerError")
          StatusCode.BadGateway -> toast("BadGateway")
          else -> toast("$statusCode(${statusCode.code}): ${message()}")
        }

        // map the ApiResponse.Failure.Error to a customized error model using the mapper.
        map(ErrorEnvelopeMapper) {
          Timber.d("[Code: $code]: $message")
        }
      }
    }
  }

  // handle the case when the API request gets a exception response.
  // e.g., network connection error, timeout.
  override suspend fun onException(apiResponse: ApiResponse.Failure.Exception<T>) {
    withContext(Dispatchers.Main.immediate) {
      apiResponse.run {
        Timber.d(message())
        toast(message())
      }
    }
  }

  override suspend fun onCause(apiResponse: ApiResponse.Failure.Cause) {
  }

  private fun toast(message: String) {
    Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
  }
}
