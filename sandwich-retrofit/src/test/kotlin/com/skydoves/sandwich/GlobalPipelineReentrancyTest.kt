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

import com.skydoves.sandwich.envelope.ApiEnvelopeMapper
import com.skydoves.sandwich.mappers.ApiResponseFailureMapper
import com.skydoves.sandwich.operators.ApiResponseOperator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Regression tests for finding A16: transformers used to re-enter the global operator and mapper
 * pipeline, because they created their result through [ApiResponse.of] and [ApiResponse.exception].
 *
 * A global operator must observe a response exactly once per request, no matter how long the
 * transformation chain that follows is.
 */
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
internal class GlobalPipelineReentrancyTest {

  private class CountingOperator<T> : ApiResponseOperator<T>() {
    var successCount: Int = 0
    var errorCount: Int = 0
    var exceptionCount: Int = 0

    override fun onSuccess(apiResponse: ApiResponse.Success<T>) {
      successCount++
    }

    override fun onError(apiResponse: ApiResponse.Failure.Error) {
      errorCount++
    }

    override fun onException(apiResponse: ApiResponse.Failure.Exception) {
      exceptionCount++
    }
  }

  @After
  fun tearDown() {
    SandwichInitializer.sandwichOperators = mutableListOf()
    SandwichInitializer.sandwichSuccessMappers = mutableListOf(ApiEnvelopeMapper)
    SandwichInitializer.sandwichFailureMappers = mutableListOf()
  }

  @Test
  fun `a global operator observes a success exactly once across a mapSuccess chain`() {
    val operator = CountingOperator<Any>()
    SandwichInitializer.sandwichOperators += operator

    ApiResponse.of { "poster" }
      .mapSuccess { length }
      .mapSuccess { this * 2 }
      .mapSuccess { toString() }

    assertThat(operator.successCount, `is`(1))
  }

  @Test
  fun `a global operator observes a success exactly once across a suspendMapSuccess chain`() =
    runTest {
      val operator = CountingOperator<Any>()
      SandwichInitializer.sandwichOperators += operator

      ApiResponse.suspendOf { "poster" }
        .suspendMapSuccess { length }
        .suspendMapSuccess { this * 2 }

      assertThat(operator.successCount, `is`(1))
    }

  @Test
  fun `a global operator observes an exception exactly once across a mapFailure chain`() {
    val operator = CountingOperator<Any>()
    SandwichInitializer.sandwichOperators += operator

    ApiResponse.exception(RuntimeException("boom"))
      .mapFailure { RuntimeException("wrapped") }
      .mapFailure { RuntimeException("wrapped twice") }

    assertThat(operator.exceptionCount, `is`(1))
  }

  @Test
  fun `merge does not report its intermediate accumulator to a global operator`() {
    val operator = CountingOperator<Any>()
    SandwichInitializer.sandwichOperators += operator

    val first = ApiResponse.Success(listOf("a"))
    val second = ApiResponse.Success(listOf("b"))

    val merged = first.merge(second)

    assertThat(merged.getOrNull(), `is`(listOf("a", "b")))
    assertThat(operator.successCount, `is`(0))
  }

  @Test
  fun `a global failure mapper is applied exactly once across a mapFailure chain`() {
    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      ApiResponse.Failure.Exception(
        RuntimeException("mapped:" + (failure as ApiResponse.Failure.Exception).message),
      )
    }

    val response = ApiResponse.exception(RuntimeException("boom"))
      .mapFailure { this }

    val message = (response as ApiResponse.Failure.Exception).message
    assertThat(message, `is`("mapped:boom"))
  }

  @Test
  fun `mapSuccess still converts a throwing transformer into a failure`() {
    val response = ApiResponse.Success("poster")
      .mapSuccess<String, Int> { error("transformer failed") }

    assertThat(response is ApiResponse.Failure.Exception, `is`(true))
    assertThat((response as ApiResponse.Failure.Exception).message, `is`("transformer failed"))
  }

  @Test
  fun `mapSuccess preserves the tag`() {
    val response = ApiResponse.Success("poster", tag = "myTag").mapSuccess { length }

    assertThat(response.tagOrNull(), `is`("myTag"))
  }
}
