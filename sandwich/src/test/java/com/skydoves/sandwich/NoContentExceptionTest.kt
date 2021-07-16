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

import com.skydoves.sandwich.exceptions.NoContentException
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
internal class NoContentExceptionTest {

  @Test
  fun `throw NoContentException when body is null without accessing data property`() {
    val response = Response.success<String?>(204, null)
    val apiResponse = ApiResponse.of { response }
    assertThat(apiResponse, instanceOf(ApiResponse.Success::class.java))

    val success = apiResponse as ApiResponse.Success<String>
    assertThat(success.statusCode.code, `is`(204))
    assertThat(success.raw, `is`(response.raw()))
    assertThat(success.headers, `is`(response.headers()))
  }

  @Test(expected = NoContentException::class)
  fun `throw NoContentException when body is null with accessing data property`() {
    val response = Response.success<String?>(204, null)
    val apiResponse = ApiResponse.of { response }
    assertThat(apiResponse, instanceOf(ApiResponse.Success::class.java))

    val success = apiResponse as ApiResponse.Success<String>
    assertThat(success.statusCode.code, `is`(204))
    assertThat(success.raw, `is`(response.raw()))
    assertThat(success.headers, `is`(response.headers()))
    assertThat(success.data, `is`("foo"))
  }
}
