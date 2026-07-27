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
package com.skydoves.sandwich.ktor.serialization

import com.skydoves.sandwich.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Serializable
internal data class ErrorMessage(val code: Int, val message: String)

internal class SerializationExtensionsTest {

  private suspend fun errorResponse(body: String): HttpResponse {
    val engine = MockEngine {
      respond(
        content = body,
        status = HttpStatusCode.BadRequest,
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }
    return HttpClient(engine).get("https://skydoves.com/")
  }

  @Test
  internal fun deserializesTheErrorBodyOfAFailure(): TestResult = runTest {
    val response: ApiResponse<String> =
      ApiResponse.Failure.Error(errorResponse("""{"code":10001,"message":"wrong argument"}"""))

    val error: ErrorMessage? = response.deserializeErrorBody()

    assertEquals(ErrorMessage(10001, "wrong argument"), error)
  }

  @Test
  internal fun returnsNullForASuccessfulResponse(): TestResult = runTest {
    val response: ApiResponse<String> = ApiResponse.Success("poster")

    assertNull(response.deserializeErrorBody<String, ErrorMessage>())
  }

  @Test
  internal fun returnsNullWhenThePayloadIsNotAnHttpResponse(): TestResult = runTest {
    val response: ApiResponse<String> = ApiResponse.Failure.Error("plain text payload")

    assertNull(response.deserializeErrorBody<String, ErrorMessage>())
  }

  @Test
  internal fun returnsNullForAnEmptyBody(): TestResult = runTest {
    val response: ApiResponse<String> = ApiResponse.Failure.Error(errorResponse(""))

    assertNull(response.deserializeErrorBody<String, ErrorMessage>())
  }

  @Test
  internal fun onErrorDeserializeRunsForAnErrorCarryingABody(): TestResult = runTest {
    val response: ApiResponse<String> =
      ApiResponse.Failure.Error(errorResponse("""{"code":10000,"message":"limited"}"""))
    var received: ErrorMessage? = null

    val returned = response.onErrorDeserialize<String, ErrorMessage> { received = it }

    assertEquals(ErrorMessage(10000, "limited"), received)
    assertTrue(returned === response)
  }

  @Test
  internal fun onErrorDeserializeSkipsASuccess(): TestResult = runTest {
    val response: ApiResponse<String> = ApiResponse.Success("poster")
    var called = false

    response.onErrorDeserialize<String, ErrorMessage> { called = true }

    assertEquals(false, called)
  }
}
