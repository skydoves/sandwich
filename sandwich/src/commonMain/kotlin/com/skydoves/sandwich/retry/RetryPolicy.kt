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

/**
 * @author skydoves (Jaewoong Eum)
 *
 * The retry policy is being used to determine if and when the request should be retried if a temporary error occurred.
 */
public interface RetryPolicy {
  /**
   * Determines whether the request should be retried.
   *
   * @param attempt Current retry attempt.
   * @param message The error message returned by the previous attempt.
   *
   * @return true if the request should be retried, false otherwise.
   */
  public fun shouldRetry(attempt: Int, message: String?): Boolean

  /**
   * Provides a timeout used to delay the next request.
   *
   * @param attempt Current retry attempt.
   * @param message The error message returned by the previous attempt.
   *
   * @return The timeout in milliseconds before making a retry.
   */
  public fun retryTimeout(attempt: Int, message: String?): Int
}
