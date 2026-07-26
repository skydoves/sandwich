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

import com.skydoves.sandwich.envelope.ApiEnvelope
import com.skydoves.sandwich.envelope.ApiEnvelopeMapper
import com.skydoves.sandwich.envelope.ApiEnvelopeSpec
import com.skydoves.sandwich.envelope.unwrap
import com.skydoves.sandwich.mappers.ApiResponseFailureMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Verifies the envelope (`BaseResponse`) design spike against the scenarios reported in
 * issues #30, #38, #85, and #138.
 */
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
internal class ApiEnvelopeTest {

  /** `{ "code": 0, "msg": "success", "data": { ... } }` — issues #30, #38, #138. */
  private data class BaseResponse<T>(val code: Int, val msg: String, val data: T?) :
    ApiEnvelope<T?, String> {
    override val isEnvelopeSuccessful: Boolean get() = code == 0
    override val envelopeBody: T? get() = data
    override val envelopeError: String get() = msg
  }

  /** A model that cannot be modified, described from the outside — the non-intrusive path. */
  private data class LegacyResponse(val status: String, val payload: String, val reason: String)

  private object LegacyResponseSpec : ApiEnvelopeSpec<LegacyResponse, String, String> {
    override fun isEnvelopeSuccessful(envelope: LegacyResponse) = envelope.status == "OK"
    override fun envelopeBody(envelope: LegacyResponse) = envelope.payload
    override fun envelopeError(envelope: LegacyResponse) = envelope.reason
  }

  /** Issue #85 — Google Apps Script answers HTTP 200 with an HTML error page. */
  private data class SpreadSheetResponse(val raw: String) : ApiEnvelope<String, String> {
    override val isEnvelopeSuccessful: Boolean get() = !raw.startsWith("<!DOCTYPE html")
    override val envelopeBody: String get() = raw
    override val envelopeError: String get() = "Apps Script returned an HTML error page"
  }

  @After
  fun tearDown() {
    // NOTE: global state leaks between tests today — this is finding C3
    // (sandwich-test needs a reset rule).
    SandwichInitializer.sandwichSuccessMappers = mutableListOf()
    SandwichInitializer.sandwichFailureMappers = mutableListOf()
  }

  @Test
  fun `unwrap flattens a business-successful envelope into the payload`() {
    val response: ApiResponse<BaseResponse<String>> =
      ApiResponse.Success(BaseResponse(code = 0, msg = "success", data = "poster"))

    val unwrapped: ApiResponse<String?> = response.unwrap()

    assertThat(unwrapped is ApiResponse.Success, `is`(true))
    assertThat(unwrapped.getOrNull(), `is`("poster"))
  }

  @Test
  fun `unwrap demotes a business-failed envelope to Failure Error`() {
    val response: ApiResponse<BaseResponse<String>> =
      ApiResponse.Success(BaseResponse(code = -1, msg = "fail", data = null))

    val unwrapped: ApiResponse<String?> = response.unwrap()

    assertThat(unwrapped is ApiResponse.Failure.Error, `is`(true))
    assertThat((unwrapped as ApiResponse.Failure.Error).payload, `is`("fail"))
  }

  @Test
  fun `unwrap preserves the tag of a successful envelope`() {
    val response: ApiResponse<BaseResponse<String>> =
      ApiResponse.Success(BaseResponse(0, "success", "poster"), tag = "myTag")

    assertThat(response.unwrap().tagOrNull(), `is`("myTag"))
  }

  @Test
  fun `unwrap passes an existing transport failure through untouched`() {
    val throwable = RuntimeException("no network")
    val response: ApiResponse<BaseResponse<String>> = ApiResponse.Failure.Exception(throwable)

    val unwrapped: ApiResponse<String?> = response.unwrap()

    assertThat(unwrapped is ApiResponse.Failure.Exception, `is`(true))
    assertThat((unwrapped as ApiResponse.Failure.Exception).throwable, `is`(throwable))
  }

  @Test
  fun `unwrap with a spec works without modifying the response model`() {
    val ok: ApiResponse<LegacyResponse> = ApiResponse.Success(LegacyResponse("OK", "payload", ""))
    val failed: ApiResponse<LegacyResponse> =
      ApiResponse.Success(LegacyResponse("NG", "", "quota exceeded"))

    assertThat(ok.unwrap(LegacyResponseSpec).getOrNull(), `is`("payload"))
    assertThat(failed.unwrap(LegacyResponseSpec) is ApiResponse.Failure.Error, `is`(true))
    assertThat(failed.unwrap(LegacyResponseSpec).messageOrNull, `is`("quota exceeded"))
  }

  @Test
  fun `global envelope mapper demotes a business failure without an explicit unwrap`() {
    SandwichInitializer.sandwichSuccessMappers += ApiEnvelopeMapper

    // The transport succeeded with HTTP 200, but the body encodes a business failure.
    val response = ApiResponse.of { BaseResponse(code = -1, msg = "fail", data = null) }

    assertThat(response is ApiResponse.Failure.Error, `is`(true))
    assertThat((response as ApiResponse.Failure.Error).payload, `is`("fail"))
  }

  @Test
  fun `global envelope mapper leaves a business success as Success`() {
    SandwichInitializer.sandwichSuccessMappers += ApiEnvelopeMapper

    val response = ApiResponse.of { BaseResponse(code = 0, msg = "success", data = "poster") }

    assertThat(response is ApiResponse.Success, `is`(true))
    assertThat(response.getOrNull()?.data, `is`("poster"))
  }

  @Test
  fun `a demoted envelope failure still flows through global failure mappers`() {
    SandwichInitializer.sandwichSuccessMappers += ApiEnvelopeMapper
    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      val payload = (failure as ApiResponse.Failure.Error).payload
      ApiResponse.Failure.Error(payload = "mapped: $payload")
    }

    val response = ApiResponse.of { BaseResponse(code = 10000, msg = "limited", data = null) }

    assertThat(response is ApiResponse.Failure.Error, `is`(true))
    assertThat((response as ApiResponse.Failure.Error).payload, `is`("mapped: limited"))
  }

  @Test
  fun `global envelope mapper applies on the suspending creation path`() = runTest {
    SandwichInitializer.sandwichSuccessMappers += ApiEnvelopeMapper

    val response = ApiResponse.suspendOf { BaseResponse(code = -1, msg = "fail", data = null) }

    assertThat(response is ApiResponse.Failure.Error, `is`(true))
    assertThat((response as ApiResponse.Failure.Error).payload, `is`("fail"))
  }

  @Test
  fun `no registered success mapper leaves the pipeline untouched`() {
    val response = ApiResponse.of { BaseResponse(code = -1, msg = "fail", data = null) }

    assertThat(response is ApiResponse.Success, `is`(true))
    assertThat(response.getOrNull()?.code, `is`(-1))
  }

  @Test
  fun `an envelope can classify a 200 response that carries a non-json error body`() {
    val html: ApiResponse<SpreadSheetResponse> =
      ApiResponse.Success(SpreadSheetResponse("<!DOCTYPE html><html>500</html>"))

    val unwrapped = html.unwrap()

    assertThat(unwrapped is ApiResponse.Failure.Error, `is`(true))
    assertTrue((unwrapped as ApiResponse.Failure.Error).message().contains("HTML error page"))
  }
}
