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

import com.skydoves.sandwich.coroutines.ApiResponseCallAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
internal class ResponseRetryTest : ApiAbstract<DisneyService>() {

  private lateinit var service: DisneyService

  @Before
  fun initService() {
    service = createService(DisneyService::class.java)
  }

  @Test
  fun `Retry Test`() = runBlocking {
    var retryTick = 0
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
      .build()

    val service = retrofit.create(DisneyCoroutinesService::class.java)
    val response = retry(
      retry = 2,
      timeMillis = 0,
    ) {
      retryTick++
      mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))
      service.fetchDisneyPosterList()
    }

    assertThat(retryTick, `is`(3))

    assertThat(response, instanceOf(ApiResponse.Failure.Error::class.java))
    response as ApiResponse.Failure.Error<List<Poster>>
    assertThat(response.statusCode, `is`(StatusCode.NotFound))
  }
}
