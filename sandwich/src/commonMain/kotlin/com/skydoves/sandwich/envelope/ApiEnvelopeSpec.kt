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

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A non-intrusive counterpart of [ApiEnvelope] that describes the envelope contract from the
 * outside, so the response model does not need to be modified.
 *
 * This is useful when the model is generated (protobuf, OpenAPI, KSP) or owned by another module.
 *
 * ```kotlin
 * data class BaseResponse<T>(val code: Int, val message: String, val data: T?)
 *
 * class BaseResponseSpec<T> : ApiEnvelopeSpec<BaseResponse<T>, T?, String> {
 *   override fun isEnvelopeSuccessful(envelope: BaseResponse<T>): Boolean = envelope.code == 0
 *   override fun envelopeBody(envelope: BaseResponse<T>): T? = envelope.data
 *   override fun envelopeError(envelope: BaseResponse<T>): String = envelope.message
 * }
 *
 * val response: ApiResponse<List<Poster>> =
 *   service.fetchPosters().unwrap(BaseResponseSpec())
 * ```
 *
 * @param ENV The envelope type.
 * @param T The type of the business payload.
 * @param E The type of the business error.
 */
public interface ApiEnvelopeSpec<in ENV, out T, out E> {

  /** Whether the [envelope] represents a successful business outcome. */
  public fun isEnvelopeSuccessful(envelope: ENV): Boolean

  /** Extracts the business payload from the [envelope]. */
  public fun envelopeBody(envelope: ENV): T

  /** Extracts the business error from the [envelope]. */
  public fun envelopeError(envelope: ENV): E
}
