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
import com.skydoves.sandwich.adapters.SuspensionFunction
import com.skydoves.sandwich.disposables.CompositeDisposable
import com.skydoves.sandwich.disposables.disposable
import com.skydoves.sandwich.executors.ArchTaskExecutor
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ResponseDataSource is an implementation of the [DataSource] interface.
 *
 * Asynchronously send requests and A response data holder from the REST API call.
 * Support observer for the every request responses, concat another [DataSource],
 * Retry fetching data when the request gets failure.
 */
public class ResponseDataSource<T> : DataSource<T> {

  // retrofit call from the retrofit service.
  public var call: Call<T>? = null

  // response callback for handling success and failure cases.
  public var callback: Callback<T>? = null

  private val dataLock = Any()
  private val empty = Any()

  // when postValue is called, we set the pending data and actual data swap happens on the main
  // thread.
  @Volatile
  internal var pending = empty

  // retain the fetched data on the memory storage temporarily.
  // this data can be changed internally and observable.
  @Volatile
  public var data: Any = empty
    private set

  // runnable for emit response data from disk thread.
  private val postValueRunnable = Runnable {
    synchronized(dataLock) {
      this.data = pending
      pending = empty
      emitResponseToObserver()
    }
  }

  // a disposable container that can hold onto multiple other disposables.
  private var compositeDisposable: CompositeDisposable? = null

  // a policy for retaining data on the internal storage or not.
  private var dataRetainPolicy = DataRetainPolicy.NO_RETAIN

  // backup property for retry count data.
  private var retry: Int = -1

  // retry request call if the request gets failure.
  private var retryCount: Int = -1
    set(value) {
      if (value >= 0) {
        field = value
        retry = value
      }
    }

  // timeout duration of the retry request.
  private var retryTimeInterval: Long = 0
    set(value) {
      if (value >= 0) {
        field = value
      }
    }

  // runnable for retry response data from disk thread.
  private val retryRunnable = Runnable {
    if (retryCount > 0) {
      synchronized(retryCount--) {
        enqueue()
      }
    }
  }

  // an observer for the new response data.
  private var responseObserver: ResponseObserver<T>? = null

  // a live data for observing response data instead of a ResponseObserver.
  private var liveData: MutableLiveData<T>? = null

  // a concat unit for executing after request success.
  private var concat: (() -> Unit)? = null

  // a strategy for determining to request continuously or stop when the first request got failed.
  public var concatStrategy: DataSource.ConcatStrategy = DataSource.ConcatStrategy.CONTINUOUS

  /** sets value on the worker thread and post the value to the main thread. */
  public fun postValue(value: ApiResponse<T>) {
    val postTask: Boolean
    synchronized(dataLock) {
      postTask = pending === empty
      pending = value
    }
    if (!postTask) {
      return
    }
    ArchTaskExecutor.instance.postToMainThread(postValueRunnable, 0)
  }

  /** sets [DataRetainPolicy] for limiting retaining data. */
  public fun dataRetainPolicy(dataRetainPolicy: DataRetainPolicy): ResponseDataSource<T> = apply {
    this.dataRetainPolicy = dataRetainPolicy
  }

  /** combine a call and callback instances for caching data. */
  public override fun combine(call: Call<T>, callback: Callback<T>?): ResponseDataSource<T> =
    apply {
      this.call = call
      this.callback = callback
    }

  /** combine a call and callback instances for caching data. */
  @JvmSynthetic
  public inline fun combine(
    call: Call<T>,
    crossinline onResult: (response: ApiResponse<T>) -> Unit
  ): ResponseDataSource<T> =
    combine(call, getCallbackFromOnResult(onResult))

  /** combine a call and callback instances for caching data on a [CoroutineScope]. */
  @JvmSynthetic
  @SuspensionFunction
  public inline fun suspendCombine(
    call: Call<T>,
    coroutineScope: CoroutineScope,
    crossinline onResult: suspend (response: ApiResponse<T>) -> Unit
  ): ResponseDataSource<T> =
    combine(call, getCallbackFromOnResultOnCoroutinesScope(coroutineScope, onResult))

  /** combine a call and callback instances for caching data on a [CoroutineContext]. */
  @JvmSynthetic
  @SuspensionFunction
  public inline fun suspendCombine(
    call: Call<T>,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline onResult: suspend (response: ApiResponse<T>) -> Unit
  ): ResponseDataSource<T> =
    combine(call, getCallbackFromOnResultWithContext(context, onResult))

  /** Retry requesting API call when the request gets failure. */
  public override fun retry(retryCount: Int, interval: Long): ResponseDataSource<T> = apply {
    this.retryCount = retryCount
    this.retryTimeInterval = interval
  }

  /**
   * request API network call asynchronously.
   * if the request is successful, this data source will hold the success response model.
   * in the next request after success, returns the cached API response.
   * if you need to fetch a new response data or refresh, use invalidate().
   */
  @Suppress("UNCHECKED_CAST")
  public override fun request(): ResponseDataSource<T> = apply {
    val call = this.call ?: return this
    if (data === empty || dataRetainPolicy == DataRetainPolicy.NO_RETAIN) {
      enqueue()
    } else {
      when (val data = data as ApiResponse<T>) {
        is ApiResponse.Success<T> -> {
          callback?.onResponse(call, data.response)
          emitResponseToObserver()
        }
        else -> enqueue()
      }
    }
  }

  /** extension method for requesting and observing response at once. */
  @JvmSynthetic
  public inline fun request(crossinline action: (ApiResponse<T>).() -> Unit): ResponseDataSource<T> =
    apply {
      if (call != null && callback == null) {
        combine(requireNotNull(call), action)
      }
      request()
    }

  /** extension method for requesting and observing response at once on a [CoroutineScope]. */
  @JvmSynthetic
  @SuspensionFunction
  public inline fun suspendRequest(
    coroutineScope: CoroutineScope,
    crossinline action: suspend (ApiResponse<T>).() -> Unit
  ): ResponseDataSource<T> = apply {
    if (call != null && callback == null) {
      suspendCombine(requireNotNull(call), coroutineScope, action)
    }
    request()
  }

  /** extension method for requesting and observing response at once with a [CoroutineContext]. */
  @JvmSynthetic
  @SuspensionFunction
  public inline fun suspendRequest(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline action: suspend (ApiResponse<T>).() -> Unit
  ): ResponseDataSource<T> = apply {
    if (call != null && callback == null) {
      suspendCombine(requireNotNull(call), context, action)
    }
    request()
  }

  /** joins onto [CompositeDisposable] as a disposable. must be called before [request]. */
  public override fun joinDisposable(disposable: CompositeDisposable): ResponseDataSource<T> =
    apply {
      this.compositeDisposable = disposable
    }

  /** invalidate a cached data and re-fetching the API request. */
  override fun invalidate() {
    this.data = empty
    this.retryCount = retry
    enqueue()
  }

  /**
   * if the response is successful, it returns a [LiveData] which contains response data.
   * if the response is failure or exception, it returns an empty [LiveData].
   * this live data can be observable from the network requests.
   */
  @Suppress("UNCHECKED_CAST")
  public fun asLiveData(): LiveData<T> {
    return MutableLiveData<T>().apply {
      liveData = this
      if (data != empty) {
        val data = data as ApiResponse<T>
        if (data is ApiResponse.Success<T>) {
          postValue(data.response.body())
        }
      }
    }
  }

  /** enqueue a callback to call and cache the [ApiResponse] data. */
  private fun enqueue() {
    val call = call?.clone() ?: return
    if (!call.isExecuted && compositeDisposable?.disposed == false) {
      val callback = object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
          callback?.onResponse(call, response)
          postValue(ApiResponse.of { response })
        }

        override fun onFailure(call: Call<T>, throwable: Throwable) {
          callback?.onFailure(call, throwable)
          postValue(ApiResponse.error(throwable))
          ArchTaskExecutor.instance.postToMainThread(retryRunnable, retryTimeInterval)
          call.cancel()
        }
      }
      compositeDisposable?.add(call.disposable())
      call.enqueue(callback)
    }
  }

  /** emit response data to an observer when the request is successful. */
  @Suppress("UNCHECKED_CAST")
  private fun emitResponseToObserver() {
    if (data != empty && (data as ApiResponse<T>) is ApiResponse.Success<T>) {
      this.responseObserver?.observe(data as ApiResponse<T>)
      this.liveData?.postValue((data as ApiResponse.Success<T>).data)
      this.concat?.invoke()
    } else if (concatStrategy == DataSource.ConcatStrategy.CONTINUOUS) {
      this.concat?.invoke()
    }
  }

  /**
   * concat an another [DataSource] and request API call sequentially
   * if the API call getting successful.
   */
  override fun <R> concat(dataSource: DataSource<R>): DataSource<R> {
    this.concat = { dataSource.request() }
    return dataSource
  }

  /** observes a [ApiResponse] value from the API call request. */
  public override fun observeResponse(observer: ResponseObserver<T>): ResponseDataSource<T> =
    apply {
      this.responseObserver = observer
    }

  /** observes a [ApiResponse] value from the API call request. */
  @JvmSynthetic
  public inline fun observeResponse(crossinline action: (ApiResponse<T>) -> Unit): ResponseDataSource<T> =
    observeResponse(ResponseObserver { response -> action(response) })
}
