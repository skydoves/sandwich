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

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DataSourceTest : ApiAbstract<DisneyService>() {

  private lateinit var service: DisneyService

  @Before
  fun initService() {
    service = createService(DisneyService::class.java)
  }

  @Test
  fun combine() {
    val onResult: (response: ApiResponse<List<Poster>>) -> Unit = {}
    val dataSource = ResponseDataSource<List<Poster>>()
    val call = service.fetchDisneyPosterList()
    val callback = getCallbackFromOnResult(onResult)
    dataSource.combine(call, callback)
    assertThat(dataSource.call, `is`(call))
    assertThat(dataSource.callback, `is`(callback))
  }

  @Test
  fun concat() {
    var requests = 0

    val dataSource: ResponseDataSource<List<Poster>> = mock()
    whenever(dataSource.request()).thenAnswer {
      requests++
      assertThat(requests, `is`(1))
      dataSource
    }

    val dataSource2: ResponseDataSource<List<Poster>> = mock()
    whenever(dataSource2.request()).thenAnswer {
      requests++
      assertThat(requests, `is`(2))
      dataSource
    }

    whenever(dataSource.concat(dataSource2)).thenAnswer {
      dataSource2.request()
    }

    val dataSource3: ResponseDataSource<List<Poster>> = mock()
    whenever(dataSource3.request()).thenAnswer {
      requests++
      assertThat(requests, `is`(3))
      dataSource
    }

    whenever(dataSource2.concat(dataSource3)).thenAnswer {
      dataSource3.request()
    }

    dataSource.request()
      .concat(dataSource2)
      .concat(dataSource3)
  }
}
