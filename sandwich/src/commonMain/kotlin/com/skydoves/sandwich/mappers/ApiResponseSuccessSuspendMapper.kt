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
 * A suspending mapper interface for mapping an [ApiResponse.Success] into another [ApiResponse].
 *
 * This mapper is only applied on suspending creation paths such as
 * [com.skydoves.sandwich.ApiResponse.Companion.suspendOf], because a suspending mapper cannot be
 * awaited from a blocking creation path. Use [ApiResponseSuccessMapper] if the mapping has to run
 * for every creation path.
 */
public interface ApiResponseSuccessSuspendMapper : SandwichSuccessMapper {

  /**
   * Maps an [ApiResponse.Success].
   *
   * @param apiResponse The [ApiResponse.Success] response from the network request.
   * @return The same or a demoted [ApiResponse].
   */
  public suspend fun map(apiResponse: ApiResponse.Success<*>): ApiResponse<*>
}
