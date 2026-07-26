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

/**
 * @author skydoves (Jaewoong Eum)
 *
 * A contract for a response body that wraps the actual business payload, commonly known as
 * an envelope (or `BaseResponse`) pattern.
 *
 * Many backends answer with HTTP 200 regardless of the business outcome and encode the real
 * result inside the body:
 *
 * ```json
 * { "code": 0, "message": "ok", "data": { ... } }
 * ```
 *
 * Sandwich treats such a response as [ApiResponse.Success] because the transport layer did
 * succeed. Implementing [ApiEnvelope] lets Sandwich additionally understand the *business*
 * outcome, so a business failure can be demoted to [ApiResponse.Failure.Error].
 *
 * ```kotlin
 * data class BaseResponse<T>(
 *   val code: Int,
 *   val message: String,
 *   val data: T?,
 * ) : ApiEnvelope<T?, String> {
 *   override val isEnvelopeSuccessful: Boolean get() = code == 0
 *   override val envelopeBody: T? get() = data
 *   override val envelopeError: String get() = message
 * }
 * ```
 *
 * Implementing this interface enables two capabilities:
 *
 * 1. [unwrap] to flatten `ApiResponse<BaseResponse<T>>` into `ApiResponse<T>`.
 * 2. Automatic demotion of business failures when [ApiEnvelopeMapper] is registered on
 *    [com.skydoves.sandwich.SandwichInitializer.sandwichSuccessMappers].
 *
 * If you cannot modify the response model, use [ApiEnvelopeSpec] instead.
 *
 * @param T The type of the business payload.
 * @param E The type of the business error.
 */
public interface ApiEnvelope<out T, out E> {

  /** Whether the envelope represents a successful business outcome. */
  public val isEnvelopeSuccessful: Boolean

  /**
   * The business payload.
   *
   * This is only accessed when [isEnvelopeSuccessful] is `true`, so implementations may
   * throw or return a placeholder for the failed case.
   */
  public val envelopeBody: T

  /**
   * The business error.
   *
   * This is only accessed when [isEnvelopeSuccessful] is `false`, so implementations may
   * throw or return a placeholder for the successful case.
   */
  public val envelopeError: E
}
