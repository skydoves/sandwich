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

import com.skydoves.sandwich.exceptions.SandwichException
import com.skydoves.sandwich.exceptions.SandwichHttpException
import com.skydoves.sandwich.exceptions.SandwichNetworkException
import com.skydoves.sandwich.exceptions.SandwichTimeoutException
import com.skydoves.sandwich.exceptions.asSandwichException
import com.skydoves.sandwich.exceptions.isNetworkFailure
import com.skydoves.sandwich.exceptions.isSerializationFailure
import com.skydoves.sandwich.exceptions.isTimeout
import com.skydoves.sandwich.exceptions.sandwichException
import com.skydoves.sandwich.retrofit.exceptions.RetrofitExceptionClassifier
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.HttpException
import retrofit2.Response
import java.io.EOFException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@RunWith(JUnit4::class)
internal class SandwichExceptionTest {

  @Before
  fun setUp() {
    SandwichInitializer.sandwichExceptionClassifiers += RetrofitExceptionClassifier
  }

  @After
  fun tearDown() {
    SandwichInitializer.sandwichExceptionClassifiers = mutableListOf()
  }

  @Test
  fun `a socket timeout is classified as a timeout`() {
    val response = ApiResponse.Failure.Exception(SocketTimeoutException("read timed out"))

    assertThat(response.sandwichException is SandwichTimeoutException, `is`(true))
    assertThat(response.isTimeout, `is`(true))
    assertThat(response.isNetworkFailure, `is`(false))
  }

  @Test
  fun `an OkHttp call timeout is classified as a timeout`() {
    val response = ApiResponse.Failure.Exception(InterruptedIOException("timeout"))

    assertThat(response.isTimeout, `is`(true))
  }

  @Test
  fun `an unknown host is classified as a network failure`() {
    val response = ApiResponse.Failure.Exception(UnknownHostException("no dns"))

    assertThat(response.sandwichException is SandwichNetworkException, `is`(true))
    assertThat(response.isNetworkFailure, `is`(true))
    assertThat(response.isTimeout, `is`(false))
  }

  @Test
  fun `a truncated body is classified as a serialization failure`() {
    val response = ApiResponse.Failure.Exception(EOFException("end of stream"))

    assertThat(response.isSerializationFailure, `is`(true))
  }

  @Test
  fun `an HttpException carries its status code`() {
    val errorBody = "{}".toResponseBody("application/json".toMediaType())
    val response = ApiResponse.Failure.Exception(
      HttpException(Response.error<String>(503, errorBody)),
    )

    val classified = response.sandwichException
    assertThat(classified is SandwichHttpException, `is`(true))
    assertThat((classified as SandwichHttpException).statusCode, `is`(503))
  }

  @Test
  fun `an unrecognized throwable falls back to a plain SandwichException`() {
    val cause = IllegalStateException("boom")

    val classified = cause.asSandwichException()

    assertThat(classified::class, `is`(SandwichException::class))
    assertThat(classified.cause, `is`(cause))
    assertThat(classified.message, `is`("boom"))
  }

  @Test
  fun `a SandwichException is returned as is`() {
    val original = SandwichTimeoutException("already classified")

    assertThat(original.asSandwichException(), `is`(original))
  }

  @Test
  fun `classification without a registered classifier never crashes`() {
    SandwichInitializer.sandwichExceptionClassifiers = mutableListOf()

    val classified = SocketTimeoutException("read timed out").asSandwichException()

    assertThat(classified::class, `is`(SandwichException::class))
  }
}
