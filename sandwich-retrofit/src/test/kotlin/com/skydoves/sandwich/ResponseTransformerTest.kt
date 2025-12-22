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

import com.skydoves.sandwich.ErrorEnvelopeMapper.map
import com.skydoves.sandwich.retrofit.responseOf
import com.skydoves.sandwich.retrofit.statusCode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(JUnit4::class)
internal class ResponseTransformerTest : ApiAbstract<DisneyService>() {

  private lateinit var service: DisneyService

  @Before
  fun initService() {
    service = createService(DisneyService::class.java)
  }

  @Test
  fun getOrNullOnSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    val data = apiResponse.getOrElse("bar")

    assertThat(data, `is`("foo"))
  }

  @Test
  fun getOrNullOnErrorTest() {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }
    val data = apiResponse.getOrNull()

    assertNull(data)
  }

  @Test
  fun getOrNullOnExceptionTest() {
    val exception = IllegalArgumentException("foo")
    val apiResponse = ApiResponse.exception(exception)
    val data = apiResponse.getOrNull()

    assertNull(data)
  }

  @Test
  fun getOrElseOnSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    val data = apiResponse.getOrElse("bar")

    assertThat(data, `is`("foo"))
  }

  @Test
  fun getOrElseOnErrorTest() {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }
    val data = apiResponse.getOrElse("bar")

    assertThat(data, `is`("bar"))
  }

  @Test
  fun getOrElseOnExceptionTest() {
    val exception = IllegalArgumentException("foo")
    val apiResponse = ApiResponse.exception(exception)
    val data = apiResponse.getOrElse("bar")

    assertThat(data, `is`("bar"))
  }

  @Test
  fun getOrElseLambdaOnSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    val data = apiResponse.getOrElse { "bar" }

    assertThat(data, `is`("foo"))
  }

  @Test
  fun getOrElseLambdaOnErrorTest() {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }
    val data = apiResponse.getOrElse { "bar" }

    assertThat(data, `is`("bar"))
  }

  @Test
  fun getOrElseLambdaOnExceptionTest() {
    val exception = IllegalArgumentException("foo")
    val apiResponse = ApiResponse.exception(exception)
    val data = apiResponse.getOrElse { "bar" }

    assertThat(data, `is`("bar"))
  }

  @Test
  fun getOrThrowOnSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    val data = apiResponse.getOrThrow()

    assertThat(data, `is`("foo"))
  }

  @Test(expected = RuntimeException::class)
  fun getOrThrowOnErrorTest() {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }
    apiResponse.getOrThrow()
  }

  @Test(expected = IllegalArgumentException::class)
  fun getOrThrowOnExceptionTest() {
    val exception = IllegalArgumentException("foo")
    val apiResponse = ApiResponse.exception(exception)
    apiResponse.getOrThrow()
  }

  @Test
  fun onSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    var onResult = false

    apiResponse.onSuccess {
      onResult = true
    }

    assertThat(onResult, `is`(true))
  }

  @Test
  fun onSuccessInProcedureTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    var onResult = false

    apiResponse.onProcedure(
      onSuccess = {
        onResult = true
      },
      onException = {
        onResult = false
      },
      onError = {
        onResult = false
      },
    )

    assertThat(onResult, `is`(true))
  }

  @Test
  fun suspendOnSuccessTest() = runTest {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }

    flow {
      apiResponse.suspendOnSuccess {
        emit(true)
      }
    }.collect {
      assertThat(it, `is`(true))
    }
  }

  @Test
  fun suspendOnSuccessInProcedureTest() = runTest {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }

    flow {
      apiResponse.suspendOnProcedure(
        onSuccess = {
          emit(true)
        },
        onError = {
          emit(false)
        },
        onException = {
          emit(false)
        },
      )
    }.collect {
      assertThat(it, `is`(true))
    }
  }

  @Test
  fun onErrorTest() {
    var onResult = false
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

    apiResponse.onError {
      onResult = true
    }

    assertThat(onResult, `is`(true))
  }

  @Test
  fun onErrorInProcedureTest() {
    var onResult = false
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

    apiResponse.onProcedure(
      onSuccess = {
        onResult = false
      },
      onError = {
        onResult = true
      },
      onException = {
        onResult = false
      },
    )
    assertThat(onResult, `is`(true))
  }

  @Test
  fun onSuspendErrorTest() = runBlocking {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

    flow {
      apiResponse.suspendOnError {
        emit(true)
      }
    }.collect {
      assertThat(it, `is`(true))
    }
  }

  @Test
  fun onSuspendErrorInProcedureTest() = runBlocking {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

    flow {
      apiResponse.suspendOnProcedure(
        onSuccess = {
          emit(false)
        },
        onError = {
          emit(true)
        },
        onException = {
          emit(false)
        },
      )
    }.collect {
      assertThat(it, `is`(true))
    }
  }

  @Test
  fun onExceptionTest() {
    var onResult = false
    val apiResponse = ApiResponse.exception(Throwable())

    apiResponse.onException {
      onResult = true
    }

    assertThat(onResult, `is`(true))
  }

  @Test
  fun onExceptionInProcedureTest() {
    var onResult = false
    val apiResponse = ApiResponse.exception(Throwable())

    apiResponse.onProcedure(
      onSuccess = {
        onResult = false
      },
      onError = {
        onResult = false
      },
      onException = {
        onResult = true
      },
    )

    assertThat(onResult, `is`(true))
  }

  @Test
  fun suspendOnExceptionTest() = runTest {
    val apiResponse = ApiResponse.exception(Throwable())

    flow {
      apiResponse.suspendOnException {
        emit(true)
      }
    }.collect {
      assertThat(it, `is`(true))
    }
  }

  @Test
  fun suspendOnExceptionInProcedureTest() = runTest {
    val apiResponse = ApiResponse.exception(Throwable())

    flow {
      apiResponse.suspendOnProcedure(
        onSuccess = {
          emit(false)
        },
        onError = {
          emit(false)
        },
        onException = {
          emit(true)
        },
      )
    }.collect {
      assertThat(it, `is`(true))
    }
  }

  @Test
  fun mapSuccessTest() {
    var poster: Poster? = null
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.responseOf { response }

    val mappedResponse = apiResponse.mapSuccess { first() }
    mappedResponse.onSuccess {
      poster = data
    }
    assertThat(poster, `is`(response.body()?.first()))
  }

  @Test
  fun suspendMapSuccessTest() = runTest {
    var poster: Poster? = null
    val response =
      Response.success(flowOf(listOf(Poster.create(), Poster.create(), Poster.create())))
    val apiResponse = ApiResponse.responseOf { response }

    val mappedResponse = apiResponse.suspendMapSuccess { first().first() }
    mappedResponse.onSuccess {
      poster = data
    }
    assertThat(poster, `is`(response.body()?.first()?.first()))
  }

  @Test
  fun mapOnSuccessTest() {
    var poster: Poster? = null
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.responseOf { response }

    apiResponse.onSuccess {
      poster = map(SuccessPosterMapper)
    }

    assertThat(poster, `is`(response.body()?.first()))
  }

  @Test
  fun mapOnSuccessWithLambdaTest() {
    var poster: Poster? = null
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.responseOf { response }

    apiResponse.onSuccess {
      map(SuccessPosterMapper) {
        poster = this
      }
    }

    assertThat(poster, `is`(response.body()?.first()))
  }

  @Test
  fun mapOnSuccessWithExecutableLambdaTest() {
    var poster: Poster? = null
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.responseOf { response }

    apiResponse.onSuccess {
      map {
        poster = it.data.first()
      }
    }

    assertThat(poster, `is`(response.body()?.first()))
  }

  @Test
  fun mapOnSuccessWithParameterTest() {
    var poster: Poster? = null
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.responseOf { response }

    apiResponse.onSuccess(SuccessPosterMapper) {
      poster = this
    }

    assertThat(poster, `is`(response.body()?.first()))
  }

  @Test
  fun mapSuspendSuccessWithLambdaTest() = runTest {
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.responseOf { response }

    flow {
      apiResponse.suspendOnSuccess {
        suspendMap(SuccessPosterMapper) {
          emit(this)
        }
      }
    }.collect {
      assertThat(it, `is`(response.body()?.first()))
    }
  }

  @Test
  fun mapSuspendSuccessWitExecutableLambdaTest() = runTest {
    val poster = Poster.create()
    val response = Response.success(listOf(poster, Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.responseOf { response }

    flow {
      apiResponse.suspendOnSuccess {
        suspendMap {
          emit(it.data.first())
        }
      }
    }.collect {
      assertThat(it, `is`(poster))
    }
  }

  @Test
  fun mapSuspendSuccessWithParameterTest() = runTest {
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.responseOf { response }

    flow {
      apiResponse.suspendOnSuccess(SuccessPosterMapper) {
        emit(this)
      }
    }.collect {
      assertThat(it, `is`(response.body()?.first()))
    }
  }

  @Test
  fun mapOnErrorTest() {
    var onResult: String? = null
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

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
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

    apiResponse.onError {
      map(ErrorEnvelopeMapper) {
        onResult = code.toString()
      }
    }

    assertThat(onResult, `is`("404"))
  }

  @Test
  fun mapOnErrorWithExecutableLambdaTest() {
    var onResult: String? = null
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

    apiResponse.onError {
      map {
        onResult = it.statusCode.code.toString()
      }
    }

    assertThat(onResult, `is`("404"))
  }

  @Test
  fun mapOnErrorWithParameterTest() {
    var onResult: String? = null
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

    apiResponse.onError(ErrorEnvelopeMapper) {
      onResult = code.toString()
    }

    assertThat(onResult, `is`("404"))
  }

  @Test
  fun mapSuspendErrorWithLambdaTest() = runBlocking {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

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

  @Test
  fun mapSuspendErrorWithExecutableLambdaTest() = runBlocking {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

    flow {
      apiResponse.suspendOnError {
        suspendMap {
          emit(it.statusCode.code.toString())
        }
      }
    }.collect {
      assertThat(it, `is`("404"))
    }
  }

  @Test
  fun mapSuspendErrorWithParameterTest() = runBlocking {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }

    flow {
      apiResponse.suspendOnError(ErrorEnvelopeMapper) {
        emit(code.toString())
      }
    }.collect {
      assertThat(it, `is`("404"))
    }
  }

  @Test
  fun operatorTest() {
    var onSuccess = false
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.responseOf { response }
    apiResponse.operator(
      TestApiResponseOperator(
        onSuccess = { onSuccess = true },
        onError = {},
        onException = {},
      ),
    )

    assertThat(onSuccess, `is`(true))

    var onError = false
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiError = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }
    apiError.operator(
      TestApiResponseOperator(
        onSuccess = {},
        onError = { onError = true },
        onException = {},
      ),
    )

    assertThat(onError, `is`(true))

    var onException = false
    val apiException = ApiResponse.exception(Throwable())
    apiException.operator(
      TestApiResponseOperator(
        onSuccess = {},
        onError = {},
        onException = { onException = true },
      ),
    )

    assertThat(onException, `is`(true))
  }

  @Test
  fun suspendOperatorTest() = runBlocking {
    val response = Response.success(listOf(Poster.create(), Poster.create(), Poster.create()))
    val apiResponse = ApiResponse.responseOf { response }

    flow {
      apiResponse.suspendOperator(
        TestApiResponseSuspendOperator(
          onSuccess = { emit("100") },
          onError = {},
          onException = {},
        ),
      )
    }.collect {
      assertThat(it, `is`("100"))
    }

    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiError = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }
    flow {
      apiError.suspendOperator(
        TestApiResponseSuspendOperator(
          onSuccess = {},
          onError = { emit("404") },
          onException = {},
        ),
      )
    }.collect {
      assertThat(it, `is`("404"))
    }

    val apiException = ApiResponse.exception(Throwable())
    flow {
      apiException.suspendOperator(
        TestApiResponseSuspendOperator(
          onSuccess = {},
          onError = {},
          onException = { emit("201") },
        ),
      )
    }.collect {
      assertThat(it, `is`("201"))
    }
  }

  @Test
  fun toFlowTest() = runTest {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }

    apiResponse.toFlow().collect {
      assertThat(it, `is`("foo"))
    }
  }

  @Test
  fun toFlowWithTransformerTest() = runTest {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }

    apiResponse.toFlow {
      "hello, $this"
    }.collect {
      assertThat(it, `is`("hello, foo"))
    }
  }

  @Test
  fun toFlowWithSuspendTransformerTest() = runTest {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }

    apiResponse.toSuspendFlow {
      "hello, $this"
    }.collect {
      assertThat(it, `is`("hello, foo"))
    }
  }

  @Test
  fun transformSuccessResponseWithThen() = runTest {
    val response1 = Response.success("foo")
    val response2 = Response.success("bar")
    val apiResponse1 = ApiResponse.responseOf { response1 }
    val apiResponse2 = ApiResponse.responseOf { response2 }
    val apiResponse3 = apiResponse1 then { apiResponse2 }

    assertThat(apiResponse3.getOrThrow(), `is`("bar"))
  }

  // ========== Recovery Extension Tests ==========

  @Test
  fun recoverOnSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    val recovered = apiResponse.recover("bar")

    assertThat(recovered.getOrNull(), `is`("foo"))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun recoverOnFailureTest() {
    val apiResponse = ApiResponse.exception(Throwable("error")) as ApiResponse<String>
    val recovered = apiResponse.recover("bar")

    assertThat(recovered.getOrNull(), `is`("bar"))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun recoverLambdaOnFailureTest() {
    val apiResponse = ApiResponse.exception(Throwable("error")) as ApiResponse<String>
    val recovered = apiResponse.recover { "bar" }

    assertThat(recovered.getOrNull(), `is`("bar"))
  }

  @Test
  fun recoverWithOnSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    val recovered = apiResponse.recoverWith { ApiResponse.Success("bar") }

    assertThat(recovered.getOrNull(), `is`("foo"))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun recoverWithOnFailureTest() {
    val apiResponse = ApiResponse.exception(Throwable("error")) as ApiResponse<String>
    val recovered = apiResponse.recoverWith { ApiResponse.Success("bar") }

    assertThat(recovered.getOrNull(), `is`("bar"))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun suspendRecoverOnFailureTest() = runTest {
    val apiResponse = ApiResponse.exception(Throwable("error")) as ApiResponse<String>
    val recovered = apiResponse.suspendRecover { "bar" }

    assertThat(recovered.getOrNull(), `is`("bar"))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun suspendRecoverWithOnFailureTest() = runTest {
    val apiResponse = ApiResponse.exception(Throwable("error")) as ApiResponse<String>
    val recovered = apiResponse.suspendRecoverWith { ApiResponse.Success("bar") }

    assertThat(recovered.getOrNull(), `is`("bar"))
  }

  // ========== Validation Extension Tests ==========

  @Test
  fun validateOnSuccessPassTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    val validated = apiResponse.validate({ it.length > 2 }) { "Too short" }

    assertThat(validated.getOrNull(), `is`("foo"))
  }

  @Test
  fun validateOnSuccessFailTest() {
    val response = Response.success("fo")
    val apiResponse = ApiResponse.responseOf { response }
    val validated = apiResponse.validate({ it.length > 2 }) { "Too short" }

    assertThat(validated is ApiResponse.Failure.Error, `is`(true))
  }

  @Test
  fun validateOnFailureTest() {
    val apiResponse = ApiResponse.exception(Throwable("error"))
    val validated = apiResponse.validate({ true }) { "Error" }

    assertThat(validated is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun suspendValidateOnSuccessPassTest() = runTest {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    val validated = apiResponse.suspendValidate({ it.length > 2 })

    assertThat(validated.getOrNull(), `is`("foo"))
  }

  @Test
  fun suspendValidateOnSuccessFailTest() = runTest {
    val response = Response.success("fo")
    val apiResponse = ApiResponse.responseOf { response }
    val validated = apiResponse.suspendValidate({ it.length > 2 }, "Too short")

    assertThat(validated is ApiResponse.Failure.Error, `is`(true))
  }

  @Test
  fun requireNotNullOnSuccessWithValueTest() {
    val response = Response.success("foo" to "bar")
    val apiResponse = ApiResponse.responseOf { response }
    val required = apiResponse.requireNotNull({ it.first }) { "Value is null" }

    assertThat(required.getOrNull(), `is`("foo"))
  }

  @Test
  fun requireNotNullOnSuccessWithNullTest() {
    val response = Response.success("foo" to null as String?)
    val apiResponse = ApiResponse.responseOf { response }
    val required = apiResponse.requireNotNull({ it.second }) { "Value is null" }

    assertThat(required is ApiResponse.Failure.Error, `is`(true))
  }

  @Test
  fun requireNotNullOnFailureTest() {
    val apiResponse = ApiResponse.exception(Throwable("error"))
    val required = apiResponse.requireNotNull({ it }) { "Value is null" }

    assertThat(required is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun suspendRequireNotNullOnSuccessWithValueTest() = runTest {
    val response = Response.success("foo" to "bar")
    val apiResponse = ApiResponse.responseOf { response }
    val required = apiResponse.suspendRequireNotNull({ it.first })

    assertThat(required.getOrNull(), `is`("foo"))
  }

  // ========== Filter Extension Tests ==========

  @Test
  fun filterOnSuccessTest() {
    val response = Response.success(listOf(1, 2, 3, 4, 5))
    val apiResponse = ApiResponse.responseOf { response }
    val filtered = apiResponse.filter { it > 2 }

    assertThat(filtered.getOrNull(), `is`(listOf(3, 4, 5)))
  }

  @Test
  fun filterOnSuccessEmptyResultTest() {
    val response = Response.success(listOf(1, 2, 3))
    val apiResponse = ApiResponse.responseOf { response }
    val filtered = apiResponse.filter { it > 10 }

    assertThat(filtered.getOrNull(), `is`(emptyList()))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun filterOnFailureTest() {
    val apiResponse = ApiResponse.exception(Throwable("error")) as ApiResponse<List<Int>>
    val filtered = apiResponse.filter { it > 2 }

    assertThat(filtered is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun suspendFilterOnSuccessTest() = runTest {
    val response = Response.success(listOf(1, 2, 3, 4, 5))
    val apiResponse = ApiResponse.responseOf { response }
    val filtered = apiResponse.suspendFilter { it > 2 }

    assertThat(filtered.getOrNull(), `is`(listOf(3, 4, 5)))
  }

  @Test
  fun filterNotOnSuccessTest() {
    val response = Response.success(listOf(1, 2, 3, 4, 5))
    val apiResponse = ApiResponse.responseOf { response }
    val filtered = apiResponse.filterNot { it > 2 }

    assertThat(filtered.getOrNull(), `is`(listOf(1, 2)))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun filterNotOnFailureTest() {
    val apiResponse = ApiResponse.exception(Throwable("error")) as ApiResponse<List<Int>>
    val filtered = apiResponse.filterNot { it > 2 }

    assertThat(filtered is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun suspendFilterNotOnSuccessTest() = runTest {
    val response = Response.success(listOf(1, 2, 3, 4, 5))
    val apiResponse = ApiResponse.responseOf { response }
    val filtered = apiResponse.suspendFilterNot { it > 2 }

    assertThat(filtered.getOrNull(), `is`(listOf(1, 2)))
  }

  // ========== Zip/Combine Extension Tests ==========

  @Test
  fun zipTwoSuccessResponsesTest() {
    val response1 = Response.success("foo")
    val response2 = Response.success(123)
    val apiResponse1 = ApiResponse.responseOf { response1 }
    val apiResponse2 = ApiResponse.responseOf { response2 }

    val zipped = apiResponse1.zip(apiResponse2) { s, i -> "$s-$i" }

    assertThat(zipped.getOrNull(), `is`("foo-123"))
  }

  @Test
  fun zipTwoSuccessResponsesToPairTest() {
    val response1 = Response.success("foo")
    val response2 = Response.success(123)
    val apiResponse1 = ApiResponse.responseOf { response1 }
    val apiResponse2 = ApiResponse.responseOf { response2 }

    val zipped = apiResponse1.zip(apiResponse2)

    assertThat(zipped.getOrNull(), `is`(Pair("foo", 123)))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun zipFirstFailureTest() {
    val apiResponse1 = ApiResponse.exception(Throwable("error")) as ApiResponse<String>
    val response2 = Response.success(123)
    val apiResponse2 = ApiResponse.responseOf { response2 }

    val zipped = apiResponse1.zip(apiResponse2) { s, i -> "$s-$i" }

    assertThat(zipped is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun zipSecondFailureTest() {
    val response1 = Response.success("foo")
    val apiResponse1 = ApiResponse.responseOf { response1 }
    val apiResponse2 = ApiResponse.exception(Throwable("error")) as ApiResponse<Int>

    val zipped = apiResponse1.zip(apiResponse2) { s, i -> "$s-$i" }

    assertThat(zipped is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun suspendZipTwoSuccessResponsesTest() = runTest {
    val response1 = Response.success("foo")
    val response2 = Response.success(123)
    val apiResponse1 = ApiResponse.responseOf { response1 }
    val apiResponse2 = ApiResponse.responseOf { response2 }

    val zipped = apiResponse1.suspendZip(apiResponse2) { s, i -> "$s-$i" }

    assertThat(zipped.getOrNull(), `is`("foo-123"))
  }

  @Test
  fun zip3SuccessResponsesTest() {
    val response1 = Response.success("foo")
    val response2 = Response.success(123)
    val response3 = Response.success(true)
    val apiResponse1 = ApiResponse.responseOf { response1 }
    val apiResponse2 = ApiResponse.responseOf { response2 }
    val apiResponse3 = ApiResponse.responseOf { response3 }

    val zipped = apiResponse1.zip3(apiResponse2, apiResponse3) { s, i, b -> "$s-$i-$b" }

    assertThat(zipped.getOrNull(), `is`("foo-123-true"))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun zip3FirstFailureTest() {
    val apiResponse1 = ApiResponse.exception(Throwable("error")) as ApiResponse<String>
    val response2 = Response.success(123)
    val response3 = Response.success(true)
    val apiResponse2 = ApiResponse.responseOf { response2 }
    val apiResponse3 = ApiResponse.responseOf { response3 }

    val zipped = apiResponse1.zip3(apiResponse2, apiResponse3) { s, i, b -> "$s-$i-$b" }

    assertThat(zipped is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun zip3SecondFailureTest() {
    val response1 = Response.success("foo")
    val response3 = Response.success(true)
    val apiResponse1 = ApiResponse.responseOf { response1 }
    val apiResponse2 = ApiResponse.exception(Throwable("error")) as ApiResponse<Int>
    val apiResponse3 = ApiResponse.responseOf { response3 }

    val zipped = apiResponse1.zip3(apiResponse2, apiResponse3) { s, i, b -> "$s-$i-$b" }

    assertThat(zipped is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  @Suppress("UNCHECKED_CAST")
  fun zip3ThirdFailureTest() {
    val response1 = Response.success("foo")
    val response2 = Response.success(123)
    val apiResponse1 = ApiResponse.responseOf { response1 }
    val apiResponse2 = ApiResponse.responseOf { response2 }
    val apiResponse3 = ApiResponse.exception(Throwable("error")) as ApiResponse<Boolean>

    val zipped = apiResponse1.zip3(apiResponse2, apiResponse3) { s, i, b -> "$s-$i-$b" }

    assertThat(zipped is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun suspendZip3SuccessResponsesTest() = runTest {
    val response1 = Response.success("foo")
    val response2 = Response.success(123)
    val response3 = Response.success(true)
    val apiResponse1 = ApiResponse.responseOf { response1 }
    val apiResponse2 = ApiResponse.responseOf { response2 }
    val apiResponse3 = ApiResponse.responseOf { response3 }

    val zipped = apiResponse1.suspendZip3(apiResponse2, apiResponse3) { s, i, b -> "$s-$i-$b" }

    assertThat(zipped.getOrNull(), `is`("foo-123-true"))
  }

  // ========== Peek/Tap Extension Tests ==========

  @Test
  fun peekOnSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    var peeked = false

    val result = apiResponse.peek { peeked = true }

    assertThat(peeked, `is`(true))
    assertThat(result.getOrNull(), `is`("foo"))
  }

  @Test
  fun peekOnFailureTest() {
    val apiResponse = ApiResponse.exception(Throwable("error"))
    var peeked = false

    val result = apiResponse.peek { peeked = true }

    assertThat(peeked, `is`(true))
    assertThat(result is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun suspendPeekTest() = runTest {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    var peeked = false

    val result = apiResponse.suspendPeek { peeked = true }

    assertThat(peeked, `is`(true))
    assertThat(result.getOrNull(), `is`("foo"))
  }

  @Test
  fun peekSuccessOnSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    var peekedValue: String? = null

    val result = apiResponse.peekSuccess { peekedValue = it }

    assertThat(peekedValue, `is`("foo"))
    assertThat(result.getOrNull(), `is`("foo"))
  }

  @Test
  fun peekSuccessOnFailureTest() {
    val apiResponse = ApiResponse.exception(Throwable("error"))
    var peeked = false

    val result = apiResponse.peekSuccess { peeked = true }

    assertThat(peeked, `is`(false))
    assertThat(result is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun suspendPeekSuccessTest() = runTest {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    var peekedValue: String? = null

    val result = apiResponse.suspendPeekSuccess { peekedValue = it }

    assertThat(peekedValue, `is`("foo"))
    assertThat(result.getOrNull(), `is`("foo"))
  }

  @Test
  fun peekFailureOnSuccessTest() {
    val response = Response.success("foo")
    val apiResponse = ApiResponse.responseOf { response }
    var peeked = false

    val result = apiResponse.peekFailure { peeked = true }

    assertThat(peeked, `is`(false))
    assertThat(result.getOrNull(), `is`("foo"))
  }

  @Test
  fun peekFailureOnFailureTest() {
    val apiResponse = ApiResponse.exception(Throwable("error"))
    var peeked = false

    val result = apiResponse.peekFailure { peeked = true }

    assertThat(peeked, `is`(true))
    assertThat(result is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun suspendPeekFailureTest() = runTest {
    val apiResponse = ApiResponse.exception(Throwable("error"))
    var peeked = false

    val result = apiResponse.suspendPeekFailure { peeked = true }

    assertThat(peeked, `is`(true))
    assertThat(result is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun peekErrorOnErrorTest() {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }
    var peeked = false

    val result = apiResponse.peekError { peeked = true }

    assertThat(peeked, `is`(true))
    assertThat(result is ApiResponse.Failure.Error, `is`(true))
  }

  @Test
  fun peekErrorOnExceptionTest() {
    val apiResponse = ApiResponse.exception(Throwable("error"))
    var peeked = false

    val result = apiResponse.peekError { peeked = true }

    assertThat(peeked, `is`(false))
    assertThat(result is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun suspendPeekErrorTest() = runTest {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }
    var peeked = false

    val result = apiResponse.suspendPeekError { peeked = true }

    assertThat(peeked, `is`(true))
    assertThat(result is ApiResponse.Failure.Error, `is`(true))
  }

  @Test
  fun peekExceptionOnExceptionTest() {
    val apiResponse = ApiResponse.exception(Throwable("error"))
    var peeked = false

    val result = apiResponse.peekException { peeked = true }

    assertThat(peeked, `is`(true))
    assertThat(result is ApiResponse.Failure.Exception, `is`(true))
  }

  @Test
  fun peekExceptionOnErrorTest() {
    val retrofit: Retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

    val service = retrofit.create(DisneyService::class.java)
    mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("foo"))

    val responseBody = requireNotNull(service.fetchDisneyPosterList().execute().errorBody())
    val apiResponse = ApiResponse.responseOf { Response.error<Poster>(404, responseBody) }
    var peeked = false

    val result = apiResponse.peekException { peeked = true }

    assertThat(peeked, `is`(false))
    assertThat(result is ApiResponse.Failure.Error, `is`(true))
  }

  @Test
  fun suspendPeekExceptionTest() = runTest {
    val apiResponse = ApiResponse.exception(Throwable("error"))
    var peeked = false

    val result = apiResponse.suspendPeekException { peeked = true }

    assertThat(peeked, `is`(true))
    assertThat(result is ApiResponse.Failure.Exception, `is`(true))
  }
}
