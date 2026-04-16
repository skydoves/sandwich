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
import com.skydoves.sandwich.StatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

internal class FakeApiResponseTest {

  @Test
  internal fun fakeSuccessCreatesSuccessWithData() {
    val response = ApiResponse.fakeSuccess(data = "hello")
    assertIs<ApiResponse.Success<String>>(response)
    assertEquals("hello", response.data)
    assertNull(response.tag)
  }

  @Test
  internal fun fakeSuccessCreatesSuccessWithTag() {
    val response = ApiResponse.fakeSuccess(data = 42, tag = "cached")
    assertIs<ApiResponse.Success<Int>>(response)
    assertEquals(42, response.data)
    assertEquals("cached", response.tag)
  }

  @Test
  internal fun fakeSuccessCreatesSuccessWithNullData() {
    val response = ApiResponse.fakeSuccess<String?>(data = null)
    assertIs<ApiResponse.Success<String?>>(response)
    assertNull(response.data)
  }

  @Test
  internal fun fakeSuccessCreatesSuccessWithComplexData() {
    val data = listOf("a", "b", "c")
    val response = ApiResponse.fakeSuccess(data = data)
    assertIs<ApiResponse.Success<List<String>>>(response)
    assertEquals(listOf("a", "b", "c"), response.data)
  }

  @Test
  internal fun fakeErrorCreatesErrorWithPayload() {
    val response = ApiResponse.fakeError(payload = "Not found")
    assertIs<ApiResponse.Failure.Error>(response)
    assertEquals("Not found", response.payload)
  }

  @Test
  internal fun fakeErrorCreatesErrorWithNullPayload() {
    val response = ApiResponse.fakeError()
    assertIs<ApiResponse.Failure.Error>(response)
    assertNull(response.payload)
  }

  @Test
  internal fun fakeErrorCreatesErrorWithStatusCodeAsPayload() {
    val response = ApiResponse.fakeError(payload = StatusCode.NotFound)
    assertIs<ApiResponse.Failure.Error>(response)
    assertEquals(StatusCode.NotFound, response.payload)
  }

  @Test
  internal fun fakeExceptionCreatesExceptionFromThrowable() {
    val throwable = IllegalStateException("something went wrong")
    val response = ApiResponse.fakeException(throwable = throwable)
    assertIs<ApiResponse.Failure.Exception>(response)
    assertEquals(throwable, response.throwable)
    assertEquals("something went wrong", response.message)
  }

  @Test
  internal fun fakeExceptionCreatesExceptionFromMessage() {
    val response = ApiResponse.fakeException(message = "timeout")
    assertIs<ApiResponse.Failure.Exception>(response)
    assertIs<RuntimeException>(response.throwable)
    assertEquals("timeout", response.message)
  }
}
