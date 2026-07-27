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
import com.skydoves.sandwich.SuspensionFunction
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
): ApiResponse<T> = runAndRetry(retryPolicy, { true }, task)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Run the [task] and retry if the result of [task] is a failure that [retryOn] accepts, following
 * the [retryPolicy].
 *
 * [RetryPolicy] only sees the failure message, which is not enough to decide whether retrying is
 * worthwhile. [retryOn] receives the failure itself, so the decision can be based on the status
 * code or on the classified cause:
 *
 * ```kotlin
 * runAndRetry(
 *   retryPolicy = RetryPolicies.exponentialBackoff(maxAttempts = 4),
 *   retryOn = { failure ->
 *     failure is ApiResponse.Failure.Exception && failure.isTimeout
 *   },
 * ) { _, _ -> repository.fetchPosters() }
 * ```
 *
 * @param retryPolicy A policy that determines whether to retry the [task] and how long to wait.
 * @param retryOn A predicate that decides whether a given failure is worth retrying at all.
 * @param task A task that you should run and retry. The default 'attempt' parameter starts from 1,
 * and the 'reason' parameter represents the error message if the [task] is failed. If the [task]
 * succeeds, it will be null.
 */
@SuspensionFunction
public suspend fun <T : Any> runAndRetry(
  retryPolicy: RetryPolicy,
  retryOn: (failure: ApiResponse.Failure<T>) -> Boolean,
  task: suspend (attempt: Int, reason: String?) -> ApiResponse<T>,
): ApiResponse<T> {
  var attempt = 1
  var reason: String? = null
  var apiResponse: ApiResponse<T>
  while (true) {
    apiResponse = task(attempt, reason)
    when (val response = apiResponse) {
      is ApiResponse.Success -> break
      is ApiResponse.Failure -> {
        reason = response.messageOrNull
        if (!retryOn(response)) break

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
