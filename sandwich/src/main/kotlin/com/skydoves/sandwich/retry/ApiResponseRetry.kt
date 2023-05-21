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

package com.skydoves.sandwich.retry

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.adapters.internal.SuspensionFunction
import com.skydoves.sandwich.messageOrNull
import kotlinx.coroutines.delay

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Run the [task] and retry if the result of [task] is failure following the [retryPolicy].
 *
 * @param retryPolicy A policy that determines whether retry the [task] or not.
 * @param task A task that you should run and retry. The default 'attempt' parameter starts from 1,
 * and the 'reason' parameter represents the error message if the [task] is failed. If the [task]
 * succeeds, it will be null.
 */
@SuspensionFunction
public suspend fun <T : Any> runAndRetry(
  retryPolicy: RetryPolicy,
  task: suspend (attempt: Int, reason: String?) -> ApiResponse<T>,
): ApiResponse<T> {
  var attempt = 1
  var reason: String? = null
  var apiResponse: ApiResponse<T>
  while (true) {
    apiResponse = task(attempt, reason)
    when (apiResponse) {
      is ApiResponse.Success -> break
      is ApiResponse.Failure -> {
        reason = apiResponse.messageOrNull
        val shouldRetry = retryPolicy.shouldRetry(attempt, reason)
        val timeout = retryPolicy.retryTimeout(attempt, reason)

        if (shouldRetry) {
          delay(timeout.toLong())
          attempt += 1
        } else {
          break
        }
      }
    }
  }

  return apiResponse
}
