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

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response
import java.io.IOException

@RunWith(JUnit4::class)
internal class SandwichInitializerTest {

  @Before
  fun initSuccessCodeRange() {
    SandwichInitializer.successCodeRange = 201..400
  }

  @After
  fun resetSuccessCodeRange() {
    SandwichInitializer.successCodeRange = 200..299
  }

  @Test
  @Throws(IOException::class)
  fun success() {
    val successResponse = Response.success("foo")
    val apiResponse =
      ApiResponse.of { successResponse }
    assertThat(
      apiResponse,
      CoreMatchers.instanceOf(ApiResponse.Failure.Error::class.java),
    )

    val errorResponse = apiResponse as ApiResponse.Failure.Error
    assertThat(
      errorResponse.message(),
      CoreMatchers.`is`(
        "[ApiResponse.Failure.Error-${errorResponse.statusCode}](" +
          "errorResponse=${errorResponse.response})",
      ),
    )
  }

  @Test
  fun globalOperatorTest() {
    var onSuccess = false
    var onError = false
    var onException = false

    SandwichInitializer.sandwichOperators += TestApiResponseOperator<Any>(
      onSuccess = { onSuccess = true },
      onError = { onError = true },
      onException = { onException = true },
    )

    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    ApiResponse.of(200..299) { response }
    assertThat(onSuccess, `is`(true))

    val successResponse = Response.success("foo")
    ApiResponse.of { successResponse }
    assertThat(onError, `is`(true))

    ApiResponse.error<Poster>(Throwable())
    assertThat(onException, `is`(true))
  }
}
