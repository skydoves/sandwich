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

@file:Suppress("unused")

package com.skydoves.sandwich

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skydoves.sandwich.coroutines.SuspensionFunction
import retrofit2.Call
import retrofit2.Callback

/**
 * Asynchronously send the request and notify [ApiResponse] of its response or if an error
 * occurred talking to the server, creating the request, or processing the response.
 */
inline fun <T> Call<T>.request(
  crossinline onResult: (response: ApiResponse<T>) -> Unit
) {
  enqueue(getCallbackFromOnResult(onResult))
}

/**
 * combine a [DataSource] to the call for processing response data more handy.
 */
inline fun <T> Call<T>.combineDataSource(
  dataSource: DataSource<T>,
  crossinline onResult: (response: ApiResponse<T>) -> Unit
): DataSource<T> {
  dataSource.combine(this, getCallbackFromOnResult(onResult))
  return dataSource
}

/** get a response callback from onResult unit. */
inline fun <T> getCallbackFromOnResult(
  crossinline onResult: (response: ApiResponse<T>) -> Unit
): Callback<T> {
  return object : Callback<T> {
    override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
      onResult(ApiResponse.of { response })
    }

    override fun onFailure(call: Call<T>, throwable: Throwable) {
      onResult(ApiResponse.error(throwable))
    }
  }
}

/**
 * A scope function for handling success response [ApiResponse.Success] a unit
 * block of code within the context of the response.
 */
fun <T> ApiResponse<T>.onSuccess(onResult: ApiResponse.Success<T>.() -> Unit): ApiResponse<T> {
  if (this is ApiResponse.Success) {
    onResult(this)
  }
  return this
}

/**
 * A suspend scope function for handling success response [ApiResponse.Success] a unit
 * block of code within the context of the response.
 */
@SuspensionFunction
suspend fun <T> ApiResponse<T>.suspendOnSuccess(
  onResult: suspend ApiResponse.Success<T>.() -> Unit
): ApiResponse<T> {
  if (this is ApiResponse.Success) {
    onResult(this)
  }
  return this
}

/**
 * A scope function for handling failure response [ApiResponse.Failure] a unit
 * block of code within the context of the response.
 */
fun <T> ApiResponse<T>.onFailure(onResult: ApiResponse.Failure<*>.() -> Unit): ApiResponse<T> {
  if (this is ApiResponse.Failure<*>) {
    onResult(this)
  }
  return this
}

/**
 * A suspend scope function for handling failure response [ApiResponse.Failure] a unit
 * block of code within the context of the response.
 */
@SuspensionFunction
suspend fun <T> ApiResponse<T>.suspendOnFailure(
  onResult: suspend ApiResponse.Failure<*>.() -> Unit
): ApiResponse<T> {
  if (this is ApiResponse.Failure<*>) {
    onResult(this)
  }
  return this
}

/**
 * A scope function for handling error response [ApiResponse.Failure.Error] a unit
 * block of code within the context of the response.
 */
fun <T> ApiResponse<T>.onError(onResult: ApiResponse.Failure.Error<T>.() -> Unit): ApiResponse<T> {
  if (this is ApiResponse.Failure.Error) {
    onResult(this)
  }
  return this
}

/**
 * A suspend scope function for handling error response [ApiResponse.Failure.Error] a unit
 * block of code within the context of the response.
 */
@SuspensionFunction
suspend fun <T> ApiResponse<T>.suspendOnError(
  onResult: suspend ApiResponse.Failure.Error<T>.() -> Unit
): ApiResponse<T> {
  if (this is ApiResponse.Failure.Error) {
    onResult(this)
  }
  return this
}

/**
 * A scope function for handling exception response [ApiResponse.Failure.Exception] a unit
 * block of code within the context of the response.
 */
fun <T> ApiResponse<T>.onException(onResult: ApiResponse.Failure.Exception<T>.() -> Unit): ApiResponse<T> {
  if (this is ApiResponse.Failure.Exception) {
    onResult(this)
  }
  return this
}

/**
 * A suspend scope function for handling exception response [ApiResponse.Failure.Exception] a unit
 * block of code within the context of the response.
 */
@SuspensionFunction
suspend fun <T> ApiResponse<T>.suspendOnException(
  onResult: suspend ApiResponse.Failure.Exception<T>.() -> Unit
): ApiResponse<T> {
  if (this is ApiResponse.Failure.Exception) {
    onResult(this)
  }
  return this
}

/** Returns a [LiveData] contains data if the response is a success.*/
fun <T> ApiResponse<T>.toLiveData(): LiveData<T> {
  val liveData = MutableLiveData<T>()
  if (this is ApiResponse.Success) {
    liveData.postValue(data)
  }
  return liveData
}

/** Map [ApiResponse.Failure.Error] to a customized error response model. */
fun <T, V> ApiResponse.Failure.Error<T>.map(converter: ApiErrorModelMapper<V>): V {
  return converter.map(this)
}

/** Map [ApiResponse.Failure.Error] to a customized error response model. */
fun <T, V> ApiResponse.Failure.Error<T>.map(
  converter: ApiErrorModelMapper<V>,
  onResult: V.() -> Unit
) {
  onResult(converter.map(this))
}

/** A message from the [ApiResponse.Failure.Error]. */
fun <T> ApiResponse.Failure.Error<T>.message(): String = toString()

/** A message from the [ApiResponse.Failure.Exception]. */
fun <T> ApiResponse.Failure.Exception<T>.message(): String = toString()
