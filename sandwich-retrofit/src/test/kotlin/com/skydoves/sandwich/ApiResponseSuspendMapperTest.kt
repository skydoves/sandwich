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

import com.skydoves.sandwich.ApiResponse.Companion.suspendMaps
import com.skydoves.sandwich.mappers.ApiResponseFailureMapper
import com.skydoves.sandwich.mappers.ApiResponseFailureSuspendMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class ApiResponseSuspendMapperTest {

  @get:Rule
  val coroutinesRule = MainCoroutinesRule()

  @Before
  fun setup() {
    SandwichInitializer.sandwichFailureMappers.clear()
  }

  @After
  fun tearDown() {
    SandwichInitializer.sandwichFailureMappers.clear()
  }

  @Test
  fun `suspendMaps should properly await and return mapped failure response`() = runTest {
    var mapperCalled = false
    val expectedPayload = "mapped error"

    // Register a suspend failure mapper
    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        mapperCalled = true
        delay(100) // Simulate async work like parsing error body
        return ApiResponse.Failure.Error(expectedPayload)
      }
    }

    // Create a failure response
    val response: ApiResponse<String> = ApiResponse.Failure.Error("original error")
    val mapped = response.suspendMaps()

    // Verify mapper was called and response was properly mapped
    assertThat(mapperCalled, `is`(true))
    assertThat((mapped as ApiResponse.Failure.Error).payload, `is`(expectedPayload))
  }

  @Test
  fun `suspendMaps should work with synchronous mapper as well`() = runTest {
    var mapperCalled = false
    val expectedPayload = "sync mapped error"

    // Register a synchronous failure mapper
    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      mapperCalled = true
      ApiResponse.Failure.Error(expectedPayload)
    }

    // Create a failure response
    val response: ApiResponse<String> = ApiResponse.Failure.Error("original error")
    val mapped = response.suspendMaps()

    // Verify mapper was called and response was properly mapped
    assertThat(mapperCalled, `is`(true))
    assertThat((mapped as ApiResponse.Failure.Error).payload, `is`(expectedPayload))
  }

  @Test
  fun `suspendMaps should not call mapper for success responses`() = runTest {
    var mapperCalled = false

    // Register a suspend failure mapper
    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        mapperCalled = true
        return apiResponse
      }
    }

    // Create a success response
    val response: ApiResponse<String> = ApiResponse.Success("data")
    val mapped = response.suspendMaps()

    // Verify mapper was NOT called for success
    assertThat(mapperCalled, `is`(false))
    assertThat((mapped as ApiResponse.Success).data, `is`("data"))
  }

  @Test
  fun `suspendMaps should chain multiple mappers in order`() = runTest {
    val callOrder = mutableListOf<Int>()

    // Register multiple mappers
    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        callOrder.add(1)
        delay(50)
        return ApiResponse.Failure.Error(
          "first: ${(apiResponse as ApiResponse.Failure.Error).payload}",
        )
      }
    }

    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        callOrder.add(2)
        delay(50)
        return ApiResponse.Failure.Error(
          "second: ${(apiResponse as ApiResponse.Failure.Error).payload}",
        )
      }
    }

    // Create a failure response
    val response: ApiResponse<String> = ApiResponse.Failure.Error("original")
    val mapped = response.suspendMaps()

    // Verify mappers were called in order
    assertThat(callOrder, `is`(listOf(1, 2)))
    assertThat(
      (mapped as ApiResponse.Failure.Error).payload,
      `is`("second: first: original"),
    )
  }

  @Test
  fun `suspendMaps should handle exception failures`() = runTest {
    var mapperCalled = false
    val expectedException = RuntimeException("mapped exception")

    // Register a suspend failure mapper
    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        mapperCalled = true
        return ApiResponse.Failure.Exception(expectedException)
      }
    }

    // Create an exception failure response
    val response: ApiResponse<String> = ApiResponse.Failure.Exception(
      IllegalStateException("original"),
    )
    val mapped = response.suspendMaps()

    // Verify mapper was called and response was properly mapped
    assertThat(mapperCalled, `is`(true))
    assertThat((mapped as ApiResponse.Failure.Exception).throwable, `is`(expectedException))
  }
}
