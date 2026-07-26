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
package com.skydoves.sandwich.envelope

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.mappers.ApiResponseSuccessMapper

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A global [ApiResponseSuccessMapper] that demotes a transport-level success into an
 * [ApiResponse.Failure.Error] when the body is an [ApiEnvelope] reporting a business failure.
 *
 * Register it once so that every response carrying an envelope is classified consistently,
 * including responses observed by global operators:
 *
 * ```kotlin
 * SandwichInitializer.sandwichSuccessMappers += ApiEnvelopeMapper
 * ```
 *
 * This mapper deliberately does **not** flatten a successful envelope, because doing so would
 * make the runtime payload disagree with the declared type. Use [unwrap] at the call site to
 * flatten `ApiResponse<BaseResponse<T>>` into `ApiResponse<T>`.
 */
public object ApiEnvelopeMapper : ApiResponseSuccessMapper {

  override fun map(apiResponse: ApiResponse.Success<*>): ApiResponse<*> {
    val data = apiResponse.data
    return if (data is ApiEnvelope<*, *> && !data.isEnvelopeSuccessful) {
      ApiResponse.Failure.Error(payload = data.envelopeError)
    } else {
      apiResponse
    }
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Registers [ApiEnvelopeMapper] on [SandwichInitializer.sandwichSuccessMappers] if it has not
 * been registered yet.
 */
public fun SandwichInitializer.enableEnvelopeSupport() {
  if (!sandwichSuccessMappers.contains(ApiEnvelopeMapper)) {
    sandwichSuccessMappers += ApiEnvelopeMapper
  }
}
