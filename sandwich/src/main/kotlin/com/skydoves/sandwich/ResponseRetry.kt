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

import kotlinx.coroutines.delay

/**
 * Retry the given function [execute] specified number of times with delays
 * if the [execute] returns [ApiResponse.Failure].
 *
 * @param retry Retry times if the given function [execute] returns [ApiResponse.Failure].
 * @param timeMillis Milli seconds delay before retrying the given function [execute].
 * @param execute An executable lambda which returns [ApiResponse].
 */
public suspend inline fun <T : Any> retry(
  retry: Int = 1,
  timeMillis: Long = 1000,
  execute: () -> ApiResponse<T>,
): ApiResponse<T> {
  repeat(times = retry) {
    when (val response = execute.invoke()) {
      is ApiResponse.Success<T> -> return response
      else -> delay(timeMillis)
    }
  }
  return execute.invoke()
}
