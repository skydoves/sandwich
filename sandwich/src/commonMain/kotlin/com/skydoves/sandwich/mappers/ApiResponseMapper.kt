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
 * A mapper interface for mapping [ApiResponse] response as another type of [ApiResponse] .
 */
public fun interface ApiResponseMapper<T, V> {

  /**
   * maps the [T] type of [ApiResponse] to the [V] type of [ApiResponse] using the mapper.
   *
   * @param apiResponse The [ApiResponse] error response from the network request.
   * @return Another type of the [ApiResponse].
   */
  public fun map(apiResponse: ApiResponse<T>): ApiResponse<V>
}
