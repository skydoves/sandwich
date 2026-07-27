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
package com.skydoves.sandwich.test

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.SandwichInitializer
import com.skydoves.sandwich.operators.ApiResponseOperator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Probes whether a globally registered operator is still visible from another thread.
 *
 * [SandwichInitializer] is annotated with `@ThreadLocal`, which gives every Kotlin/Native thread
 * its own copy of the object state. An operator registered during app startup on the main thread
 * would then be invisible to a request completing on a background thread.
 */
internal class ThreadLocalGlobalStateTest {

  private class CountingOperator<T> : ApiResponseOperator<T>() {
    override fun onSuccess(apiResponse: ApiResponse.Success<T>) {
      observed += 1
    }

    override fun onError(apiResponse: ApiResponse.Failure.Error) = Unit
    override fun onException(apiResponse: ApiResponse.Failure.Exception) = Unit

    companion object {
      var observed: Int = 0
    }
  }

  @AfterTest
  internal fun tearDown() {
    SandwichInitializer.sandwichOperators = mutableListOf()
    CountingOperator.observed = 0
  }

  @Test
  internal fun anOperatorRegisteredOnThisThreadIsVisibleOnThisThread(): TestResult = runTest {
    SandwichInitializer.sandwichOperators += CountingOperator<Any>()

    ApiResponse.of { "poster" }

    assertEquals(1, CountingOperator.observed)
  }

  @Test
  internal fun anOperatorRegisteredOnThisThreadIsVisibleOnAnotherThread(): TestResult = runTest {
    SandwichInitializer.sandwichOperators += CountingOperator<Any>()

    withContext(Dispatchers.Default) {
      ApiResponse.of { "poster" }
    }

    assertEquals(
      1,
      CountingOperator.observed,
      "the global operator did not run on the background thread",
    )
  }
}
