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

import com.skydoves.sandwich.retrofit.retryAfterMillis
import com.skydoves.sandwich.retry.RetryPolicies
import com.skydoves.sandwich.retry.runAndRetry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
internal class RetryPoliciesTest {

  @Test
  fun `none never retries`() {
    val policy = RetryPolicies.none()

    assertThat(policy.shouldRetry(attempt = 1, message = null), `is`(false))
  }

  @Test
  fun `fixedDelay retries until maxAttempts and keeps the delay constant`() {
    val policy = RetryPolicies.fixedDelay(maxAttempts = 3, delayMillis = 500)

    assertThat(policy.shouldRetry(1, null), `is`(true))
    assertThat(policy.shouldRetry(2, null), `is`(true))
    assertThat(policy.shouldRetry(3, null), `is`(false))
    assertThat(policy.retryTimeout(1, null), `is`(500))
    assertThat(policy.retryTimeout(2, null), `is`(500))
  }

  @Test
  fun `linear grows the delay by a constant step and respects the cap`() {
    val policy = RetryPolicies.linear(maxAttempts = 5, delayMillis = 1_000, maxDelayMillis = 2_500)

    assertThat(policy.retryTimeout(1, null), `is`(1_000))
    assertThat(policy.retryTimeout(2, null), `is`(2_000))
    assertThat(policy.retryTimeout(3, null), `is`(2_500))
  }

  @Test
  fun `exponentialBackoff doubles the delay and respects the cap`() {
    val policy = RetryPolicies.exponentialBackoff(
      maxAttempts = 6,
      initialDelayMillis = 100,
      factor = 2.0,
      maxDelayMillis = 500,
      jitter = 0.0,
    )

    assertThat(policy.retryTimeout(1, null), `is`(100))
    assertThat(policy.retryTimeout(2, null), `is`(200))
    assertThat(policy.retryTimeout(3, null), `is`(400))
    assertThat(policy.retryTimeout(4, null), `is`(500))
  }

  @Test
  fun `exponentialBackoff jitter stays within the expected window`() {
    val policy = RetryPolicies.exponentialBackoff(
      maxAttempts = 3,
      initialDelayMillis = 1_000,
      factor = 2.0,
      jitter = 0.5,
    )

    repeat(200) {
      val timeout = policy.retryTimeout(1, null)
      assertTrue("timeout $timeout outside [500, 1000]", timeout in 500..1_000)
    }
  }

  @Test
  fun `an invalid configuration is rejected`() {
    assertThrows(IllegalArgumentException::class.java) { RetryPolicies.fixedDelay(maxAttempts = 0) }
    assertThrows(IllegalArgumentException::class.java) {
      RetryPolicies.exponentialBackoff(jitter = 1.5)
    }
    assertThrows(IllegalArgumentException::class.java) {
      RetryPolicies.exponentialBackoff(factor = 0.5)
    }
  }

  @Test
  fun `runAndRetry stops after maxAttempts`() = runTest {
    var calls = 0

    val response = runAndRetry(RetryPolicies.fixedDelay(maxAttempts = 3, delayMillis = 0)) { _, _ ->
      calls++
      ApiResponse.Failure.Error("failed")
    }

    assertThat(calls, `is`(3))
    assertThat(response is ApiResponse.Failure.Error, `is`(true))
  }

  @Test
  fun `runAndRetry stops as soon as the task succeeds`() = runTest {
    var calls = 0

    val response = runAndRetry(RetryPolicies.fixedDelay(maxAttempts = 5, delayMillis = 0)) { a, _ ->
      calls++
      if (a < 2) ApiResponse.Failure.Error("failed") else ApiResponse.Success("poster")
    }

    assertThat(calls, `is`(2))
    assertThat(response.getOrNull(), `is`("poster"))
  }

  @Test
  fun `runAndRetry does not retry a failure the predicate rejects`() = runTest {
    var calls = 0

    val response = runAndRetry(
      retryPolicy = RetryPolicies.fixedDelay(maxAttempts = 5, delayMillis = 0),
      retryOn = { failure -> failure is ApiResponse.Failure.Exception },
    ) { _, _ ->
      calls++
      ApiResponse.Failure.Error("client error, do not retry")
    }

    assertThat(calls, `is`(1))
    assertThat(response is ApiResponse.Failure.Error, `is`(true))
  }

  @Test
  fun `runAndRetry retries a failure the predicate accepts`() = runTest {
    var calls = 0

    runAndRetry(
      retryPolicy = RetryPolicies.fixedDelay(maxAttempts = 3, delayMillis = 0),
      retryOn = { failure -> failure is ApiResponse.Failure.Exception },
    ) { _, _ ->
      calls++
      ApiResponse.Failure.Exception(RuntimeException("timeout"))
    }

    assertThat(calls, `is`(3))
  }

  @Test
  fun `retryAfterMillis reads the delta-seconds form of the header`() {
    val response = errorResponseWithRetryAfter("120")

    assertThat(response.retryAfterMillis, `is`(120_000L))
  }

  @Test
  fun `retryAfterMillis returns null for an absent or non numeric header`() {
    assertThat(errorResponseWithRetryAfter(null).retryAfterMillis, `is`(nullValue()))
    assertThat(
      errorResponseWithRetryAfter("Wed, 21 Oct 2015 07:28:00 GMT").retryAfterMillis,
      `is`(nullValue()),
    )
    assertThat(errorResponseWithRetryAfter("-5").retryAfterMillis, `is`(nullValue()))
    assertThat(ApiResponse.Failure.Error("not a response").retryAfterMillis, `is`(nullValue()))
  }

  private fun nullValue() = org.hamcrest.core.IsNull.nullValue(Long::class.java)

  private fun errorResponseWithRetryAfter(value: String?): ApiResponse.Failure.Error {
    val builder = okhttp3.Response.Builder()
      .code(503)
      .message("Service Unavailable")
      .protocol(Protocol.HTTP_1_1)
      .request(Request.Builder().url("https://skydoves.com/").build())
    if (value != null) {
      builder.header("Retry-After", value)
    }
    val raw = builder.build()
    val body = "{}".toResponseBody("application/json".toMediaType())
    return ApiResponse.Failure.Error(Response.error<String>(body, raw))
  }
}
