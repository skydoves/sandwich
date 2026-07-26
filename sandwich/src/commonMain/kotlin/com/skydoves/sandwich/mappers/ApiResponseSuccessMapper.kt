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
package com.skydoves.sandwich.mappers

import com.skydoves.sandwich.ApiResponse

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A mapper interface for mapping an [ApiResponse.Success] into another [ApiResponse].
 *
 * Unlike [ApiResponseFailureMapper], the returned type is a plain [ApiResponse], so an
 * implementation may **demote** a transport-level success into an [ApiResponse.Failure.Error].
 * This is the hook that powers the envelope (`BaseResponse`) pattern where the server answers
 * HTTP 200 while the body encodes a business failure.
 *
 * The returned [ApiResponse] must keep the same runtime payload type, because the static type
 * of the response cannot change. To also flatten the payload type, use
 * [com.skydoves.sandwich.envelope.unwrap] at the call site.
 */
public fun interface ApiResponseSuccessMapper : SandwichSuccessMapper {

  /**
   * Maps an [ApiResponse.Success].
   *
   * @param apiResponse The [ApiResponse.Success] response from the network request.
   * @return The same or a demoted [ApiResponse].
   */
  public fun map(apiResponse: ApiResponse.Success<*>): ApiResponse<*>
}
