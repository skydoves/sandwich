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

import com.skydoves.sandwich.exceptions.NoContentException
import com.skydoves.sandwich.operators.ApiResponseOperator
import com.skydoves.sandwich.operators.ApiResponseSuspendOperator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.Headers
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponse is an interface for constructing standard responses from the retrofit call.
 */
public sealed class ApiResponse<out T> {

  /**
   * @author skydoves (Jaewoong Eum)
   *
   * API Success response class from OkHttp request call.
   * The [data] is a nullable generic type. (A response without data)
   *
   * @param response A response from OkHttp request call.
   *
   * @property statusCode [StatusCode] is Hypertext Transfer Protocol (HTTP) response status codes.
   * @property headers The header fields of a single HTTP message.
   * @property raw The raw response from the HTTP client.
   * @property data The de-serialized response body of a successful data.
   */
  public data class Success<T>(val response: Response<T>) : ApiResponse<T>() {
    val statusCode: StatusCode = getStatusCodeFromResponse(response)
    val headers: Headers = response.headers()
    val raw: okhttp3.Response = response.raw()
    val data: T by lazy { response.body() ?: throw NoContentException(statusCode.code) }
    override fun toString(): String = "[ApiResponse.Success](data=$data)"
  }

  /**
   * @author skydoves (Jaewoong Eum)
   *
   * API Failure response class from OkHttp request call.
   * There are two subtypes: [ApiResponse.Failure.Error] and [ApiResponse.Failure.Exception].
   */
  public sealed class Failure<T> : ApiResponse<T>() {
    /**
     * API response error case.
     * API communication conventions do not match or applications need to handle errors.
     * e.g., internal server error.
     *
     * @param response A response from OkHttp request call.
     *
     * @property statusCode [StatusCode] is Hypertext Transfer Protocol (HTTP) response status codes.
     * @property headers The header fields of a single HTTP message.
     * @property raw The raw response from the HTTP client.
     * @property errorBody The [ResponseBody] can be consumed only once.
     */
    public data class Error<T>(val response: Response<T>) : ApiResponse.Failure<T>() {
      val statusCode: StatusCode = getStatusCodeFromResponse(response)
      val headers: Headers = response.headers()
      val raw: okhttp3.Response = response.raw()
      val errorBody: ResponseBody? = response.errorBody()
      override fun toString(): String =
        "[ApiResponse.Failure.Error-$statusCode](errorResponse=$response)"
    }

    /**
     * @author skydoves (Jaewoong Eum)
     *
     * API request Exception case.
     * An unexpected exception occurs while creating requests or processing an response in the client side.
     * e.g., network connection error, timeout.
     *
     * @param exception An throwable exception.
     *
     * @property message The localized message from the exception.
     */
    public data class Exception<T>(val exception: Throwable) : ApiResponse.Failure<T>() {
      val message: String? = exception.localizedMessage
      override fun toString(): String = "[ApiResponse.Failure.Exception](message=$message)"
    }
  }

  public companion object {
    /**
     * @author skydoves (Jaewoong Eum)
     *
     * [Failure] factory function. Only receives [Throwable] as an argument.
     *
     * @param ex A throwable.
     *
     * @return A [ApiResponse.Failure.Exception] based on the throwable.
     */
    public fun <T> error(ex: Throwable): Failure.Exception<T> =
      Failure.Exception<T>(ex).apply { operate() }

    /**
     * @author skydoves (Jaewoong Eum)
     *
     * ApiResponse Factory.
     *
     * @param successCodeRange A success code range for determining the response is successful or failure.
     * @param [f] Create [ApiResponse] from [retrofit2.Response] returning from the block.
     * If [retrofit2.Response] has no errors, it creates [ApiResponse.Success].
     * If [retrofit2.Response] has errors, it creates [ApiResponse.Failure.Error].
     * If [retrofit2.Response] has occurred exceptions, it creates [ApiResponse.Failure.Exception].
     *
     * @return An [ApiResponse] model which holds information about the response.
     */
    @JvmSynthetic
    public inline fun <T> of(
      successCodeRange: IntRange = SandwichInitializer.successCodeRange,
      crossinline f: () -> Response<T>
    ): ApiResponse<T> = try {
      val response = f()
      if (response.raw().code in successCodeRange) {
        Success(response)
      } else {
        Failure.Error(response)
      }
    } catch (ex: Exception) {
      Failure.Exception(ex)
    }.operate()

    /**
     * @author skydoves (Jaewoong Eum)
     *
     * Operates if there is a global [com.skydoves.sandwich.operators.SandwichOperator]
     * which operates on [ApiResponse]s globally on each response and returns the target [ApiResponse].
     *
     * @return [ApiResponse] A target [ApiResponse].
     */
    @PublishedApi
    @Suppress("UNCHECKED_CAST")
    internal fun <T> ApiResponse<T>.operate(): ApiResponse<T> = apply {
      val globalOperator = SandwichInitializer.sandwichOperator ?: return@apply
      if (globalOperator is ApiResponseOperator<*>) {
        operator(globalOperator as ApiResponseOperator<T>)
      } else if (globalOperator is ApiResponseSuspendOperator<*>) {
        val context = SandwichInitializer.sandwichOperatorContext
        val supervisorJob = SupervisorJob(context[Job])
        val scope = CoroutineScope(context + supervisorJob)
        scope.launch {
          suspendOperator(globalOperator as ApiResponseSuspendOperator<T>)
        }
      }
    }

    /**
     * @author skydoves (Jaewoong Eum)
     *
     * Returns a status code from the [Response].
     *
     * @param response A network callback response.
     *
     * @return A [StatusCode] from the network callback response.
     */
    public fun <T> getStatusCodeFromResponse(response: Response<T>): StatusCode {
      return StatusCode.values().find { it.code == response.code() }
        ?: StatusCode.Unknown
    }
  }
}
