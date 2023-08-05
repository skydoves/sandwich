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

import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsInstanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

@RunWith(JUnit4::class)
internal class ApiResponseSuspendTest : ApiAbstract<DisneyCoroutinesService>() {

  @get:Rule
  val coroutinesRule = MainCoroutinesRule()

  private lateinit var service: DisneyCoroutinesService

  @Before
  fun initService() {
    val testScope = TestScope(coroutinesRule.testDispatcher)
    val retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create())
      .addCallAdapterFactory(ApiResponseCallAdapterFactory.create(testScope))
      .build()
    service = retrofit.create()
  }

  @Test
  fun `fetch items as an ApiResponse with suspend function`() = runTest {
    enqueueResponse("/DisneyPosters.json")

    val apiResponse = service.fetchDisneyPosters()
    assertThat(apiResponse, IsInstanceOf.instanceOf(ApiResponse.Success::class.java))

    val first = (apiResponse as ApiResponse.Success).data.firstOrNull()
    assertThat(first?.id, CoreMatchers.`is`(0L))
    assertThat(first?.name, CoreMatchers.`is`("Frozen II"))
    assertThat(first?.release, CoreMatchers.`is`("2019"))
  }

  @Test
  fun `fetch items as a deferred ApiResponse with await`() = runTest {
    enqueueResponse("/DisneyPosters.json")

    val apiResponse = service.fetchDisneyPostersAsync().await()
    assertThat(apiResponse, IsInstanceOf.instanceOf(ApiResponse.Success::class.java))

    val first = (apiResponse as ApiResponse.Success).data.firstOrNull()
    assertThat(first?.id, CoreMatchers.`is`(0L))
    assertThat(first?.name, CoreMatchers.`is`("Frozen II"))
    assertThat(first?.release, CoreMatchers.`is`("2019"))
  }
}
