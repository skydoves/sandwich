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
internal class ApiResponseExceptionFactoryTest {

  @get:Rule
  val coroutinesRule = MainCoroutinesRule()

  @Before
  fun setup() {
    SandwichInitializer.sandwichOperators.clear()
    SandwichInitializer.sandwichFailureMappers.clear()
  }

  @After
  fun tearDown() {
    SandwichInitializer.sandwichOperators.clear()
    SandwichInitializer.sandwichFailureMappers.clear()
  }

  // region exception() - non-suspend factory

  @Test
  fun `exception should return Failure Exception with correct throwable`() {
    val throwable = RuntimeException("test error")
    val result = ApiResponse.exception(throwable)

    assertThat(result, `is`(ApiResponse.Failure.Exception(throwable)))
    assertThat(result.throwable, `is`(throwable))
    assertThat(result.message, `is`("test error"))
  }

  @Test
  fun `exception should trigger non-suspend operator onException`() {
    var onExceptionCalled = false

    SandwichInitializer.sandwichOperators += TestApiResponseOperator<Any>(
      onSuccess = {},
      onError = {},
      onException = { onExceptionCalled = true },
    )

    ApiResponse.exception(RuntimeException("test"))

    assertThat(onExceptionCalled, `is`(true))
  }

  @Test
  fun `exception should apply non-suspend failure mapper`() {
    var mapperCalled = false

    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      mapperCalled = true
      failure
    }

    ApiResponse.exception(RuntimeException("test"))

    assertThat(mapperCalled, `is`(true))
  }

  @Test
  fun `exception should not trigger operator onSuccess or onError`() {
    var onSuccessCalled = false
    var onErrorCalled = false

    SandwichInitializer.sandwichOperators += TestApiResponseOperator<Any>(
      onSuccess = { onSuccessCalled = true },
      onError = { onErrorCalled = true },
      onException = {},
    )

    ApiResponse.exception(RuntimeException("test"))

    assertThat(onSuccessCalled, `is`(false))
    assertThat(onErrorCalled, `is`(false))
  }

  @Test
  fun `exception should work without any operators or mappers`() {
    val throwable = IllegalStateException("no operators")
    val result = ApiResponse.exception(throwable)

    assertThat(result.throwable, `is`(throwable))
  }

  @Test
  fun `exception should trigger multiple non-suspend operators in order`() {
    val callOrder = mutableListOf<Int>()

    SandwichInitializer.sandwichOperators += TestApiResponseOperator<Any>(
      onSuccess = {},
      onError = {},
      onException = { callOrder.add(1) },
    )

    SandwichInitializer.sandwichOperators += TestApiResponseOperator<Any>(
      onSuccess = {},
      onError = {},
      onException = { callOrder.add(2) },
    )

    ApiResponse.exception(RuntimeException("test"))

    assertThat(callOrder, `is`(listOf(1, 2)))
  }

  @Test
  fun `exception should chain multiple non-suspend failure mappers in order`() {
    val callOrder = mutableListOf<Int>()

    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      callOrder.add(1)
      failure
    }

    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      callOrder.add(2)
      failure
    }

    ApiResponse.exception(RuntimeException("test"))

    assertThat(callOrder, `is`(listOf(1, 2)))
  }

  // endregion

  // region suspendException() - suspend factory

  @Test
  fun `suspendException should return Failure Exception with correct throwable`() = runTest {
    val throwable = RuntimeException("test error")
    val result = ApiResponse.suspendException(throwable)

    assertThat(result, `is`(ApiResponse.Failure.Exception(throwable)))
    assertThat(result.throwable, `is`(throwable))
    assertThat(result.message, `is`("test error"))
  }

  @Test
  fun `suspendException should trigger non-suspend operator onException`() = runTest {
    var onExceptionCalled = false

    SandwichInitializer.sandwichOperators += TestApiResponseOperator<Any>(
      onSuccess = {},
      onError = {},
      onException = { onExceptionCalled = true },
    )

    ApiResponse.suspendException(RuntimeException("test"))

    assertThat(onExceptionCalled, `is`(true))
  }

  @Test
  fun `suspendException should properly await suspend operator onException`() = runTest {
    var onExceptionCalled = false

    SandwichInitializer.sandwichOperators += TestApiResponseSuspendOperator<Any>(
      onSuccess = {},
      onError = {},
      onException = {
        delay(100)
        onExceptionCalled = true
      },
    )

    ApiResponse.suspendException(RuntimeException("test"))

    assertThat(onExceptionCalled, `is`(true))
  }

  @Test
  fun `suspendException should apply non-suspend failure mapper`() = runTest {
    var mapperCalled = false

    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      mapperCalled = true
      failure
    }

    ApiResponse.suspendException(RuntimeException("test"))

    assertThat(mapperCalled, `is`(true))
  }

  @Test
  fun `suspendException should properly await suspend failure mapper`() = runTest {
    var mapperCalled = false

    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        delay(100)
        mapperCalled = true
        return apiResponse
      }
    }

    ApiResponse.suspendException(RuntimeException("test"))

    assertThat(mapperCalled, `is`(true))
  }

  @Test
  fun `suspendException should not trigger operator onSuccess or onError`() = runTest {
    var onSuccessCalled = false
    var onErrorCalled = false

    SandwichInitializer.sandwichOperators += TestApiResponseSuspendOperator<Any>(
      onSuccess = { onSuccessCalled = true },
      onError = { onErrorCalled = true },
      onException = {},
    )

    ApiResponse.suspendException(RuntimeException("test"))

    assertThat(onSuccessCalled, `is`(false))
    assertThat(onErrorCalled, `is`(false))
  }

  @Test
  fun `suspendException should work without any operators or mappers`() = runTest {
    val throwable = IllegalStateException("no operators")
    val result = ApiResponse.suspendException(throwable)

    assertThat(result.throwable, `is`(throwable))
  }

  @Test
  fun `suspendException should trigger both suspend and non-suspend operators`() = runTest {
    var nonSuspendCalled = false
    var suspendCalled = false

    SandwichInitializer.sandwichOperators += TestApiResponseOperator<Any>(
      onSuccess = {},
      onError = {},
      onException = { nonSuspendCalled = true },
    )

    SandwichInitializer.sandwichOperators += TestApiResponseSuspendOperator<Any>(
      onSuccess = {},
      onError = {},
      onException = {
        delay(100)
        suspendCalled = true
      },
    )

    ApiResponse.suspendException(RuntimeException("test"))

    assertThat(nonSuspendCalled, `is`(true))
    assertThat(suspendCalled, `is`(true))
  }

  @Test
  fun `suspendException should chain both suspend and non-suspend mappers in order`() = runTest {
    val callOrder = mutableListOf<Int>()

    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      callOrder.add(1)
      failure
    }

    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        callOrder.add(2)
        delay(50)
        return apiResponse
      }
    }

    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      callOrder.add(3)
      failure
    }

    ApiResponse.suspendException(RuntimeException("test"))

    assertThat(callOrder, `is`(listOf(1, 2, 3)))
  }

  @Test
  fun `suspendException should return mapped result from suspend mapper`() = runTest {
    val originalThrowable = RuntimeException("original")
    val mappedThrowable = IllegalStateException("mapped")

    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        delay(100)
        return ApiResponse.Failure.Exception(mappedThrowable)
      }
    }

    val result = ApiResponse.suspendException(originalThrowable)

    assertThat(result.throwable, `is`(mappedThrowable))
  }

  @Test
  fun `suspendException should chain multiple suspend mappers and return final result`() = runTest {
    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        delay(50)
        val original = (apiResponse as ApiResponse.Failure.Exception).throwable
        return ApiResponse.Failure.Exception(
          RuntimeException("first: ${original.message}"),
        )
      }
    }

    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        delay(50)
        val previous = (apiResponse as ApiResponse.Failure.Exception).throwable
        return ApiResponse.Failure.Exception(
          RuntimeException("second: ${previous.message}"),
        )
      }
    }

    val result = ApiResponse.suspendException(RuntimeException("original"))

    assertThat(result.throwable.message, `is`("second: first: original"))
  }

  // endregion

  // region of() and suspendOf() - factory functions

  @Test
  fun `of should trigger operator onException when block throws`() {
    var onExceptionCalled = false

    SandwichInitializer.sandwichOperators += TestApiResponseOperator<Any>(
      onSuccess = {},
      onError = {},
      onException = { onExceptionCalled = true },
    )

    ApiResponse.of<String> { throw RuntimeException("test") }

    assertThat(onExceptionCalled, `is`(true))
  }

  @Test
  fun `of should trigger operator onSuccess when block succeeds`() {
    var onSuccessCalled = false

    SandwichInitializer.sandwichOperators += TestApiResponseOperator<Any>(
      onSuccess = { onSuccessCalled = true },
      onError = {},
      onException = {},
    )

    ApiResponse.of { "success" }

    assertThat(onSuccessCalled, `is`(true))
  }

  @Test
  fun `of should apply non-suspend mapper on exception`() {
    var mapperCalled = false

    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      mapperCalled = true
      failure
    }

    ApiResponse.of<String> { throw RuntimeException("test") }

    assertThat(mapperCalled, `is`(true))
  }

  @Test
  fun `of should not apply mapper on success`() {
    var mapperCalled = false

    SandwichInitializer.sandwichFailureMappers += ApiResponseFailureMapper { failure ->
      mapperCalled = true
      failure
    }

    val result = ApiResponse.of { "data" }

    assertThat(mapperCalled, `is`(false))
    assertThat((result as ApiResponse.Success).data, `is`("data"))
  }

  @Test
  fun `suspendOf should trigger suspend operator onException when block throws`() = runTest {
    var onExceptionCalled = false

    SandwichInitializer.sandwichOperators += TestApiResponseSuspendOperator<Any>(
      onSuccess = {},
      onError = {},
      onException = {
        delay(100)
        onExceptionCalled = true
      },
    )

    ApiResponse.suspendOf<String> { throw RuntimeException("test") }

    assertThat(onExceptionCalled, `is`(true))
  }

  @Test
  fun `suspendOf should trigger suspend operator onSuccess when block succeeds`() = runTest {
    var onSuccessCalled = false

    SandwichInitializer.sandwichOperators += TestApiResponseSuspendOperator<Any>(
      onSuccess = {
        delay(100)
        onSuccessCalled = true
      },
      onError = {},
      onException = {},
    )

    ApiResponse.suspendOf { "success" }

    assertThat(onSuccessCalled, `is`(true))
  }

  @Test
  fun `suspendOf should properly await suspend mapper on exception`() = runTest {
    var mapperCalled = false
    val mappedThrowable = IllegalStateException("mapped")

    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        delay(100)
        mapperCalled = true
        return ApiResponse.Failure.Exception(mappedThrowable)
      }
    }

    val result = ApiResponse.suspendOf<String> { throw RuntimeException("original") }

    assertThat(mapperCalled, `is`(true))
    assertThat(
      (result as ApiResponse.Failure.Exception).throwable,
      `is`(mappedThrowable),
    )
  }

  @Test
  fun `suspendOf should not apply mapper on success`() = runTest {
    var mapperCalled = false

    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        mapperCalled = true
        return apiResponse
      }
    }

    val result = ApiResponse.suspendOf { "data" }

    assertThat(mapperCalled, `is`(false))
    assertThat((result as ApiResponse.Success).data, `is`("data"))
  }

  @Test
  fun `suspendOf should not double-execute operators when exception is thrown`() = runTest {
    var operatorCallCount = 0

    SandwichInitializer.sandwichOperators += TestApiResponseSuspendOperator<Any>(
      onSuccess = {},
      onError = {},
      onException = { operatorCallCount++ },
    )

    ApiResponse.suspendOf<String> { throw RuntimeException("test") }

    assertThat(operatorCallCount, `is`(1))
  }

  @Test
  fun `suspendOf should not double-execute mappers when exception is thrown`() = runTest {
    var mapperCallCount = 0

    SandwichInitializer.sandwichFailureMappers += object : ApiResponseFailureSuspendMapper {
      override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
        mapperCallCount++
        return apiResponse
      }
    }

    ApiResponse.suspendOf<String> { throw RuntimeException("test") }

    assertThat(mapperCallCount, `is`(1))
  }

  // endregion
}
