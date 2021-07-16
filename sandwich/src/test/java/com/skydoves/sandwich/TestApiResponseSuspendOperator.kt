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

import com.skydoves.sandwich.operators.ApiResponseSuspendOperator

internal class TestApiResponseSuspendOperator<T> constructor(
  private val onSuccess: suspend () -> Unit,
  private val onError: suspend () -> Unit,
  private val onException: suspend () -> Unit
) : ApiResponseSuspendOperator<T>() {

  override suspend fun onSuccess(apiResponse: ApiResponse.Success<T>) = onSuccess()

  override suspend fun onError(apiResponse: ApiResponse.Failure.Error<T>) = onError()

  override suspend fun onException(apiResponse: ApiResponse.Failure.Exception<T>) = onException()
}
