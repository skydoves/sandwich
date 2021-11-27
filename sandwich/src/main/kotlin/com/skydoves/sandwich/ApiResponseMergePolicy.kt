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

/**
 * @author skydoves (Jaewoong Eum)
 *
 * [ApiResponseMergePolicy] is a policy for merging response data depend on the success or not.
 */
public enum class ApiResponseMergePolicy {
  /**
   * Regardless of the order, ignores failure responses in the responses.
   * if there are three responses (success, success, failure) or (success, failure, success),
   * the response will be the [ApiResponse.Success] that has a merged list of the data.
   */
  IGNORE_FAILURE,

  /**
   * Regardless of the order, prefers failure responses in the responses.
   * if there are three responses (success, success, failure) or (success, failure, success),
   * the response will be the [ApiResponse.Failure.Error] or [ApiResponse.Failure.Exception].
   */
  PREFERRED_FAILURE
}
