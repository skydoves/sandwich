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
@file:Suppress("unused", "RedundantVisibilityModifier")
@file:JvmName("EnvelopeTransformer")
@file:JvmMultifileClass

package com.skydoves.sandwich.envelope

import com.skydoves.sandwich.ApiResponse
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Flattens an `ApiResponse<ENV>` that holds an [ApiEnvelope] into an `ApiResponse<T>` that holds
 * the business payload.
 *
 * - If the response is [ApiResponse.Success] and the envelope reports a successful business
 *   outcome, an [ApiResponse.Success] with [ApiEnvelope.envelopeBody] is returned.
 * - If the response is [ApiResponse.Success] but the envelope reports a business failure, it is
 *   demoted to an [ApiResponse.Failure.Error] carrying [ApiEnvelope.envelopeError] as the payload.
 * - If the response is already an [ApiResponse.Failure], it is returned as is.
 *
 * ```kotlin
 * // service declares the wire type
 * suspend fun fetchPosters(): ApiResponse<BaseResponse<List<Poster>>>
 *
 * // repository exposes the business type
 * suspend fun posters(): ApiResponse<List<Poster>?> = service.fetchPosters().unwrap()
 * ```
 *
 * @return An [ApiResponse] holding the unwrapped business payload.
 */
@JvmSynthetic
@Suppress("UNCHECKED_CAST")
public fun <T> ApiResponse<ApiEnvelope<T, *>>.unwrap(): ApiResponse<T> = when (this) {
  is ApiResponse.Success -> data.toApiResponse(tag)
  is ApiResponse.Failure -> this as ApiResponse<T>
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Flattens an `ApiResponse<ENV>` into an `ApiResponse<T>` using an external [ApiEnvelopeSpec],
 * so the response model does not need to implement [ApiEnvelope].
 *
 * @param spec A specification that describes how to interpret the envelope.
 *
 * @return An [ApiResponse] holding the unwrapped business payload.
 */
@JvmSynthetic
@Suppress("UNCHECKED_CAST")
public fun <ENV, T> ApiResponse<ENV>.unwrap(spec: ApiEnvelopeSpec<ENV, T, *>): ApiResponse<T> =
  when (this) {
    is ApiResponse.Success -> if (spec.isEnvelopeSuccessful(data)) {
      ApiResponse.Success(data = spec.envelopeBody(data), tag = tag)
    } else {
      ApiResponse.Failure.Error(payload = spec.envelopeError(data))
    }
    is ApiResponse.Failure -> this as ApiResponse<T>
  }

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Converts this [ApiEnvelope] into an [ApiResponse] depending on its business outcome.
 *
 * @param tag An additional value that will be held by the resulting [ApiResponse.Success].
 *
 * @return [ApiResponse.Success] holding [ApiEnvelope.envelopeBody] if the envelope is successful,
 * otherwise [ApiResponse.Failure.Error] holding [ApiEnvelope.envelopeError].
 */
@JvmSynthetic
public fun <T> ApiEnvelope<T, *>.toApiResponse(tag: Any? = null): ApiResponse<T> =
  if (isEnvelopeSuccessful) {
    ApiResponse.Success(data = envelopeBody, tag = tag)
  } else {
    ApiResponse.Failure.Error(payload = envelopeError)
  }
