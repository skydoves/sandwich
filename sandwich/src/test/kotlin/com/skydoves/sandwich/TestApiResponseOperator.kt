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

package com.skydoves.sandwich

import com.skydoves.sandwich.operators.ApiResponseOperator

internal class TestApiResponseOperator<T> constructor(
  private val onSuccess: () -> Unit,
  private val onError: () -> Unit,
  private val onException: () -> Unit,
) : ApiResponseOperator<T>() {

  override fun onSuccess(apiResponse: ApiResponse.Success<T>) = onSuccess()

  override fun onError(apiResponse: ApiResponse.Failure.Error<T>) = onError()

  override fun onException(apiResponse: ApiResponse.Failure.Exception<T>) = onException()
}
