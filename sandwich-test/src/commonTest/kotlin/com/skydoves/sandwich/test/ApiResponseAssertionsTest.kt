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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

internal class ApiResponseAssertionsTest {

  // region assertSuccess

  @Test
  internal fun assertSuccessReturnsNarrowedTypeOnSuccess() {
    val response: ApiResponse<String> = ApiResponse.fakeSuccess("hello")
    val success = response.assertSuccess()
    assertEquals("hello", success.data)
  }

  @Test
  internal fun assertSuccessThrowsOnError() {
    val response: ApiResponse<String> = ApiResponse.fakeError(payload = "fail")
    val error = assertFailsWith<AssertionError> {
      response.assertSuccess()
    }
    assertEquals(
      "Expected ApiResponse.Success but was Error(payload=fail)",
      error.message,
    )
  }

  @Test
  internal fun assertSuccessThrowsOnException() {
    val response: ApiResponse<String> = ApiResponse.fakeException("timeout")
    val error = assertFailsWith<AssertionError> {
      response.assertSuccess()
    }
    val message = error.message.orEmpty()
    assert(message.startsWith("Expected ApiResponse.Success but was Exception("))
    assert(message.contains("timeout"))
  }

  @Test
  internal fun assertSuccessWithBlockExecutesOnSuccess() {
    val response: ApiResponse<String> = ApiResponse.fakeSuccess("hello")
    var blockCalled = false
    response.assertSuccess {
      blockCalled = true
      assertEquals("hello", data)
    }
    assertEquals(true, blockCalled)
  }

  @Test
  internal fun assertSuccessWithBlockThrowsOnError() {
    val response: ApiResponse<String> = ApiResponse.fakeError()
    assertFailsWith<AssertionError> {
      response.assertSuccess { }
    }
  }

  @Test
  internal fun assertSuccessWithBlockThrowsOnException() {
    val response: ApiResponse<String> = ApiResponse.fakeException("timeout")
    assertFailsWith<AssertionError> {
      response.assertSuccess { }
    }
  }

  @Test
  internal fun assertSuccessChainingReturnsNarrowedType() {
    val response: ApiResponse<String> = ApiResponse.fakeSuccess("hello", tag = "cached")
    val data = response.assertSuccess { assertEquals("hello", data) }.data
    assertEquals("hello", data)
  }

  // endregion

  // region assertError

  @Test
  internal fun assertErrorReturnsNarrowedTypeOnError() {
    val response: ApiResponse<String> = ApiResponse.fakeError(payload = "Not found")
    val error = response.assertError()
    assertEquals("Not found", error.payload)
  }

  @Test
  internal fun assertErrorThrowsOnSuccess() {
    val response: ApiResponse<String> = ApiResponse.fakeSuccess("hello")
    val error = assertFailsWith<AssertionError> {
      response.assertError()
    }
    assertEquals(
      "Expected ApiResponse.Failure.Error but was Success(data=hello)",
      error.message,
    )
  }

  @Test
  internal fun assertErrorThrowsOnException() {
    val response: ApiResponse<String> = ApiResponse.fakeException("timeout")
    assertFailsWith<AssertionError> {
      response.assertError()
    }
  }

  @Test
  internal fun assertErrorWithBlockExecutesOnError() {
    val response: ApiResponse<String> = ApiResponse.fakeError(payload = "forbidden")
    var blockCalled = false
    response.assertError {
      blockCalled = true
      assertEquals("forbidden", payload)
    }
    assertEquals(true, blockCalled)
  }

  @Test
  internal fun assertErrorChainingReturnsPayload() {
    val response: ApiResponse<String> = ApiResponse.fakeError(payload = "err")
    val payload = response.assertError { assertEquals("err", payload) }.payload
    assertEquals("err", payload)
  }

  // endregion

  // region assertException

  @Test
  internal fun assertExceptionReturnsNarrowedTypeOnException() {
    val throwable = IllegalStateException("broken")
    val response: ApiResponse<String> = ApiResponse.fakeException(throwable)
    val exception = response.assertException()
    assertEquals(throwable, exception.throwable)
    assertEquals("broken", exception.message)
  }

  @Test
  internal fun assertExceptionThrowsOnSuccess() {
    val response: ApiResponse<String> = ApiResponse.fakeSuccess("hello")
    val error = assertFailsWith<AssertionError> {
      response.assertException()
    }
    assertEquals(
      "Expected ApiResponse.Failure.Exception but was Success(data=hello)",
      error.message,
    )
  }

  @Test
  internal fun assertExceptionThrowsOnError() {
    val response: ApiResponse<String> = ApiResponse.fakeError(payload = "err")
    val error = assertFailsWith<AssertionError> {
      response.assertException()
    }
    assertEquals(
      "Expected ApiResponse.Failure.Exception but was Error(payload=err)",
      error.message,
    )
  }

  @Test
  internal fun assertExceptionWithBlockReceivesCorrectThrowable() {
    val original = IllegalStateException("oops")
    val response: ApiResponse<String> = ApiResponse.fakeException(original)
    var blockCalled = false
    response.assertException {
      blockCalled = true
      assertEquals(original, throwable)
      assertEquals("oops", message)
    }
    assertEquals(true, blockCalled)
  }

  @Test
  internal fun assertExceptionChainingReturnsThrowable() {
    val original = RuntimeException("fail")
    val response: ApiResponse<String> = ApiResponse.fakeException(original)
    val throwable = response.assertExceptionMessage("fail").throwable
    assertEquals(original, throwable)
  }

  // endregion

  // region assertFailure

  @Test
  internal fun assertFailureReturnsOnError() {
    val response: ApiResponse<String> = ApiResponse.fakeError(payload = "fail")
    val failure = response.assertFailure()
    assertIs<ApiResponse.Failure.Error>(failure)
  }

  @Test
  internal fun assertFailureReturnsOnException() {
    val response: ApiResponse<String> = ApiResponse.fakeException("fail")
    val failure = response.assertFailure()
    assertIs<ApiResponse.Failure.Exception>(failure)
  }

  @Test
  internal fun assertFailureThrowsOnSuccess() {
    val response: ApiResponse<String> = ApiResponse.fakeSuccess("hello")
    val error = assertFailsWith<AssertionError> {
      response.assertFailure()
    }
    assertEquals(
      "Expected ApiResponse.Failure but was Success(data=hello)",
      error.message,
    )
  }

  @Test
  internal fun assertFailureWithBlockExecutesOnError() {
    val response: ApiResponse<String> = ApiResponse.fakeError(payload = "err")
    var blockCalled = false
    response.assertFailure {
      blockCalled = true
      assertIs<ApiResponse.Failure.Error>(this)
    }
    assertEquals(true, blockCalled)
  }

  @Test
  internal fun assertFailureWithBlockExecutesOnException() {
    val response: ApiResponse<String> = ApiResponse.fakeException("fail")
    var receivedMessage: String? = null
    response.assertFailure {
      assertIs<ApiResponse.Failure.Exception>(this)
      receivedMessage = (this as ApiResponse.Failure.Exception).message
    }
    assertEquals("fail", receivedMessage)
  }

  // endregion

  // region assertSuccessData

  @Test
  internal fun assertSuccessDataPassesWhenDataMatches() {
    val response: ApiResponse<String> = ApiResponse.fakeSuccess("hello")
    response.assertSuccessData("hello")
  }

  @Test
  internal fun assertSuccessDataThrowsWhenDataMismatches() {
    val response: ApiResponse<String> = ApiResponse.fakeSuccess("hello")
    val error = assertFailsWith<AssertionError> {
      response.assertSuccessData("world")
    }
    assertEquals(
      "Expected success data <world> but was <hello>",
      error.message,
    )
  }

  @Test
  internal fun assertSuccessDataThrowsOnNonSuccess() {
    val response: ApiResponse<String> = ApiResponse.fakeError()
    assertFailsWith<AssertionError> {
      response.assertSuccessData("hello")
    }
  }

  @Test
  internal fun assertSuccessDataPassesWithNullData() {
    val response: ApiResponse<String?> = ApiResponse.fakeSuccess(data = null)
    response.assertSuccessData(null)
  }

  @Test
  internal fun assertSuccessDataThrowsWhenExpectedNullButActualNonNull() {
    val response: ApiResponse<String?> = ApiResponse.fakeSuccess("hello")
    val error = assertFailsWith<AssertionError> {
      response.assertSuccessData(null)
    }
    assertEquals(
      "Expected success data <null> but was <hello>",
      error.message,
    )
  }

  @Test
  internal fun assertSuccessDataThrowsWhenExpectedNonNullButActualNull() {
    val response: ApiResponse<String?> = ApiResponse.fakeSuccess(data = null)
    val error = assertFailsWith<AssertionError> {
      response.assertSuccessData("expected")
    }
    assertEquals(
      "Expected success data <expected> but was <null>",
      error.message,
    )
  }

  // endregion

  // region assertExceptionMessage

  @Test
  internal fun assertExceptionMessagePassesWhenMessageMatches() {
    val response: ApiResponse<String> = ApiResponse.fakeException("timeout")
    response.assertExceptionMessage("timeout")
  }

  @Test
  internal fun assertExceptionMessageThrowsWhenMessageMismatches() {
    val response: ApiResponse<String> = ApiResponse.fakeException("timeout")
    val error = assertFailsWith<AssertionError> {
      response.assertExceptionMessage("other error")
    }
    assertEquals(
      "Expected exception message <other error> but was <timeout>",
      error.message,
    )
  }

  @Test
  internal fun assertExceptionMessageThrowsOnNonException() {
    val response: ApiResponse<String> = ApiResponse.fakeSuccess("hello")
    assertFailsWith<AssertionError> {
      response.assertExceptionMessage("timeout")
    }
  }

  @Test
  internal fun assertExceptionMessagePassesWithNullMessage() {
    val response: ApiResponse<String> =
      ApiResponse.fakeException(throwable = object : RuntimeException(null as String?) {})
    response.assertExceptionMessage(null)
  }

  @Test
  internal fun assertExceptionMessageThrowsWhenActualMessageIsNull() {
    val response: ApiResponse<String> =
      ApiResponse.fakeException(throwable = object : RuntimeException(null as String?) {})
    val error = assertFailsWith<AssertionError> {
      response.assertExceptionMessage("expected")
    }
    assertEquals(
      "Expected exception message <expected> but was <null>",
      error.message,
    )
  }

  // endregion

  // region error message for null payload

  @Test
  internal fun assertSuccessErrorMessageDescribesNullPayloadError() {
    val response: ApiResponse<String> = ApiResponse.fakeError()
    val error = assertFailsWith<AssertionError> {
      response.assertSuccess()
    }
    assertEquals(
      "Expected ApiResponse.Success but was Error(payload=null)",
      error.message,
    )
  }

  // endregion
}
