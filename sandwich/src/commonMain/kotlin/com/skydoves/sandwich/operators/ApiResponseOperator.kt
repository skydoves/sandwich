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
package com.skydoves.sandwich.operators

import com.skydoves.sandwich.ApiResponse

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponseOperator operates on an [ApiResponse] and return an [ApiResponse].
 * This allows you to handle success and error response instead of the [com.skydoves.sandwich.onSuccess],
 * [com.skydoves.sandwich.onError], [com.skydoves.sandwich.onException] transformers.
 * This operator can be applied globally as a singleton instance, or on each [ApiResponse] one by one.
 */
public abstract class ApiResponseOperator<T> : SandwichOperator {

  /**
   * Operates the [ApiResponse.Success] for handling successful responses if the request succeeds.
   *
   * @param apiResponse The successful response.
   */
  public abstract fun onSuccess(apiResponse: ApiResponse.Success<T>)

  /**
   * Operates the [ApiResponse.Failure.Error] for handling error responses if the request failed.
   *
   * @param apiResponse The failed response.
   */
  public abstract fun onError(apiResponse: ApiResponse.Failure.Error<T>)

  /**
   * Operates the [ApiResponse.Failure.Exception] for handling exception responses if the request get an exception.
   *
   * @param apiResponse The exception response.
   */
  public abstract fun onException(apiResponse: ApiResponse.Failure.Exception<T>)

  /**
   * Operates the [ApiResponse.Failure.Cause] for handling custom failure responses.
   *
   * @param apiResponse The cause response.
   */
  public abstract fun onCause(apiResponse: ApiResponse.Failure.Cause)
}
