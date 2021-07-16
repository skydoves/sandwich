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

import com.skydoves.sandwich.disposables.CompositeDisposable
import retrofit2.Call
import retrofit2.Callback

/**
 * @author skydoves (Jaewoong Eum)
 *
 * An abstract interface design for data sources.
 */
public interface DataSource<T> {

  /** combine a call and a callback to the DataSource. */
  public fun combine(call: Call<T>, callback: Callback<T>?): DataSource<T>

  /** retry fetching data few times with time interval when the request gets failure. */
  public fun retry(retryCount: Int, interval: Long): DataSource<T>

  /** observes a [ApiResponse] value from the API call request. */
  public fun observeResponse(observer: ResponseObserver<T>): DataSource<T>

  /**
   * concat an another [DataSource] and request API calls sequentially.
   * we can determine request continuously the concat [DataSource] or stop when failed using [ConcatStrategy].
   */
  public fun <R> concat(dataSource: DataSource<R>): DataSource<R>

  /** request API call and response to the callback. */
  public fun request(): DataSource<T>

  /** joins onto [CompositeDisposable] as a disposable. */
  public fun joinDisposable(disposable: CompositeDisposable): DataSource<T>

  /** invalidate cached data and retry counts, request again API call. */
  public fun invalidate()

  /** A concat strategy for determining to request continuously or stop when the first request got failed. */
  public enum class ConcatStrategy {
    // request next call continuously.
    CONTINUOUS,

    // break requesting chain when the previous request got failed.
    BREAK
  }
}
