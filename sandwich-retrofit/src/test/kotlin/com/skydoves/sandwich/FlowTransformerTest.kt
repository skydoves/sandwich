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

import com.skydoves.sandwich.flow.apiResponseFlow
import com.skydoves.sandwich.flow.filterSuccessData
import com.skydoves.sandwich.flow.foldToFlow
import com.skydoves.sandwich.flow.mapSuccess
import com.skydoves.sandwich.flow.mapToDataOrNull
import com.skydoves.sandwich.flow.onError
import com.skydoves.sandwich.flow.onException
import com.skydoves.sandwich.flow.onFailure
import com.skydoves.sandwich.flow.onSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
internal class FlowTransformerTest {

  @Test
  fun `apiResponseFlow emits the produced response`() = runTest {
    val response = apiResponseFlow { ApiResponse.Success("poster") }.first()

    assertThat(response.getOrNull(), `is`("poster"))
  }

  @Test
  fun `apiResponseFlow captures a throwing block as an exception response`() = runTest {
    val response = apiResponseFlow<String> { error("boom") }.first()

    assertThat(response is ApiResponse.Failure.Exception, `is`(true))
    assertThat((response as ApiResponse.Failure.Exception).message, `is`("boom"))
  }

  @Test
  fun `apiResponseFlow rethrows a CancellationException`() = runTest {
    assertThrows(CancellationException::class.java) {
      kotlinx.coroutines.runBlocking {
        apiResponseFlow<String> { throw CancellationException("cancelled") }.first()
      }
    }
  }

  @Test
  fun `onSuccess runs only for successful emissions`() = runTest {
    val seen = mutableListOf<String>()

    flowOf<ApiResponse<String>>(
      ApiResponse.Success("a"),
      ApiResponse.Failure.Error("e"),
      ApiResponse.Success("b"),
    ).onSuccess { seen += data }.toList()

    assertThat(seen, `is`(listOf("a", "b")))
  }

  @Test
  fun `onError and onException each run only for their own type`() = runTest {
    val errors = mutableListOf<String>()
    val exceptions = mutableListOf<String>()

    flowOf<ApiResponse<String>>(
      ApiResponse.Success("a"),
      ApiResponse.Failure.Error("bad request"),
      ApiResponse.Failure.Exception(RuntimeException("timeout")),
    ).onError { errors += payload.toString() }
      .onException { exceptions += message.orEmpty() }
      .toList()

    assertThat(errors, `is`(listOf("bad request")))
    assertThat(exceptions, `is`(listOf("timeout")))
  }

  @Test
  fun `onFailure runs for both failure types`() = runTest {
    var count = 0

    flowOf<ApiResponse<String>>(
      ApiResponse.Success("a"),
      ApiResponse.Failure.Error("e"),
      ApiResponse.Failure.Exception(RuntimeException("x")),
    ).onFailure { count++ }.toList()

    assertThat(count, `is`(2))
  }

  @Test
  fun `mapSuccess transforms data and passes failures through`() = runTest {
    val results = flowOf<ApiResponse<String>>(
      ApiResponse.Success("poster"),
      ApiResponse.Failure.Error("e"),
    ).mapSuccess { length }.toList()

    assertThat(results[0].getOrNull(), `is`(6))
    assertThat(results[1] is ApiResponse.Failure.Error, `is`(true))
  }

  @Test
  fun `mapToDataOrNull emits null for failures`() = runTest {
    val results = flowOf<ApiResponse<String>>(
      ApiResponse.Success("a"),
      ApiResponse.Failure.Error("e"),
    ).mapToDataOrNull().toList()

    assertThat(results, `is`(listOf("a", null)))
  }

  @Test
  fun `filterSuccessData drops failures`() = runTest {
    val results = flowOf<ApiResponse<String>>(
      ApiResponse.Success("a"),
      ApiResponse.Failure.Error("e"),
      ApiResponse.Success("b"),
    ).filterSuccessData().toList()

    assertThat(results, `is`(listOf("a", "b")))
  }

  @Test
  fun `foldToFlow collapses both branches into one type`() = runTest {
    val results = flowOf<ApiResponse<String>>(
      ApiResponse.Success("poster"),
      ApiResponse.Failure.Error("bad request"),
    ).foldToFlow(
      onSuccess = { "ok:$it" },
      onFailure = { "err:$it" },
    ).toList()

    assertThat(results, `is`(listOf("ok:poster", "err:bad request")))
  }

  @Test
  fun `the operators keep the original emissions intact for chaining`() = runTest {
    val results = flowOf<ApiResponse<String>>(ApiResponse.Success("a"))
      .onSuccess { }
      .onFailure { }
      .toList()

    assertThat(results.size, `is`(1))
    assertThat(results[0].getOrNull(), `is`("a"))
  }
}
