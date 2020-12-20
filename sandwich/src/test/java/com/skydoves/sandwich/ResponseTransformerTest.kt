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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.verify
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class ResponseTransformerTest : ApiAbstract<DisneyService>() {

  @get:Rule
  var instantExecutorRule = InstantTaskExecutorRule()

  private lateinit var service: DisneyService

  @Before
  fun initService() {
    service = createService(DisneyService::class.java)
  }

  @Test
  fun onSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.of { response }
    var onResult = false

    apiResponse.onSuccess {
      onResult = true
    }

    assertThat(onResult, `is`(true))
  }

  @Test
  fun suspendOnSuccessTest() = runBlocking {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.of { response }

    flow {
      apiResponse.suspendOnSuccess {
        emit(true)
      }
    }.collect {
      assertThat(it, `is`(true))
    }
  }

  @Test
  fun onErrorTest() {
    var onResult = false
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.of { Response.error<Poster>(404, responseBody) }

    apiResponse.onError {
      onResult = true
    }

    assertThat(onResult, `is`(true))
  }

  @Test
  fun onSuspendErrorTest() = runBlocking {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.of { Response.error<Poster>(404, responseBody) }

    flow {
      apiResponse.suspendOnError {
        emit(true)
      }
    }.collect {
      assertThat(it, `is`(true))
    }
  }

  @Test
  fun onExceptionTest() {
    var onResult = false
    val apiResponse = ApiResponse.error<Poster>(Throwable())

    apiResponse.onException {
      onResult = true
    }

    assertThat(onResult, `is`(true))
  }

  @Test
  fun suspendOnExceptionTest() = runBlocking {
    val apiResponse = ApiResponse.error<Poster>(Throwable())

    flow {
      apiResponse.suspendOnException {
        emit(true)
      }
    }.collect {
      assertThat(it, `is`(true))
    }
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun toLiveDataTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.of { response }
    val observer = mock<Observer<String>>()

    apiResponse.toLiveData().observeForever(observer)

    verify(observer).onChanged("foo")
  }

  @Test
  fun mapOnSuccessTest() {
    var poster: Poster? = null
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.of { response }

    apiResponse.onSuccess {
      poster = map(SuccessPosterMapper)
    }

    assertThat(poster, `is`(Poster.create()))
  }

  @Test
  fun mapOnSuccessWithLambdaTest() {
    var poster: Poster? = null
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.of { response }

    apiResponse.onSuccess {
      map(SuccessPosterMapper) {
        poster = this
      }
    }

    assertThat(poster, `is`(Poster.create()))
  }

  @Test
  fun mapSuspendSuccessWithLambdaTest() = runBlocking {
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.of { response }

    flow {
      apiResponse.suspendOnSuccess {
        suspendMap(SuccessPosterMapper) {
          emit(this)
        }
      }
    }.collect {
      assertThat(it, `is`(Poster.create()))
    }
  }

  @Test
  fun mapOnErrorTest() {
    var onResult: String? = null
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.of { Response.error<Poster>(404, responseBody) }

    apiResponse.onError {
      val errorEnvelope = map(ErrorEnvelopeMapper)
      onResult = errorEnvelope.code.toString()
    }

    assertThat(onResult, `is`("404"))
  }

  @Test
  fun mapOnErrorWithLambdaTest() {
    var onResult: String? = null
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.of { Response.error<Poster>(404, responseBody) }

    apiResponse.onError {
      map(ErrorEnvelopeMapper) {
        onResult = code.toString()
      }
    }

    assertThat(onResult, `is`("404"))
  }

  @Test
  fun mapSuspendErrorWithLambdaTest() = runBlocking {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.of { Response.error<Poster>(404, responseBody) }

    flow {
      apiResponse.suspendOnError {
        suspendMap(ErrorEnvelopeMapper) {
          emit(code.toString())
        }
      }
    }.collect {
      assertThat(it, `is`("404"))
    }
  }
}
