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

package com.skydoves.sandwich

import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * ApiResponse is an interface for building specific response cases from the retrofit response.
 */
sealed class ApiResponse<out T> {

  /**
   * API Success response class from retrofit.
   *
   * [data] is optional. (There are responses without data)
   */
  data class Success<T>(val response: Response<T>) : ApiResponse<T>() {
    val statusCode: StatusCode = getStatusCodeFromResponse(response)
    val headers: Headers = response.headers()
    val raw: okhttp3.Response = response.raw()
    val data: T? = response.body()
    override fun toString() = "[ApiResponse.Success](data=$data)"
  }

  /**
   * API Failure response class.
   *
   * ## API format error case.
   * API communication conventions do not match or applications need to handle errors.
   * e.g. internal server error.
   *
   * ## API Exception error case.
   * Gets called when an unexpected exception occurs while creating the request or processing the response in client.
   * e.g. network connection error.
   */
  sealed class Failure<T> {
    data class Error<T>(val response: Response<T>) : ApiResponse<T>() {
      val statusCode: StatusCode = getStatusCodeFromResponse(response)
      val headers: Headers = response.headers()
      val raw: okhttp3.Response = response.raw()
      val errorBody: ResponseBody? = response.errorBody()
      override fun toString(): String = "[ApiResponse.Failure.Error-$statusCode](errorResponse=$response)"
    }

    data class Exception<T>(val exception: Throwable) : ApiResponse<T>() {
      val message: String? = exception.localizedMessage
      override fun toString(): String = "[ApiResponse.Failure.Exception](message=$message)"
    }
  }

  companion object {
    /**
     * ApiResponse error Factory.
     *
     * [Failure] factory function. Only receives [Throwable] as an argument.
     */
    fun <T> error(ex: Throwable) = Failure.Exception<T>(ex)

    /**
     * ApiResponse Factory.
     *
     * [f] Create [ApiResponse] from [retrofit2.Response] returning from the block.
     * If [retrofit2.Response] has no errors, it creates [ApiResponse.Success].
     * If [retrofit2.Response] has errors, it creates [ApiResponse.Failure.Error].
     * If [retrofit2.Response] has occurred exceptions, it creates [ApiResponse.Failure.Exception].
     */
    fun <T> of(
      successCodeRange: IntRange = SandwichInitializer.successCodeRange,
      f: () -> Response<T>
    ): ApiResponse<T> = try {
      val response = f()
      if (response.raw().code in successCodeRange) {
        Success(response)
      } else {
        Failure.Error(response)
      }
    } catch (ex: Exception) {
      Failure.Exception(ex)
    }

    /** returns a status code from the [Response]. */
    fun <T> getStatusCodeFromResponse(response: Response<T>): StatusCode {
      return StatusCode.values().find { it.code == response.code() }
        ?: StatusCode.Unknown
    }
  }
}
