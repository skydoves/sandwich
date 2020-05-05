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

import retrofit2.Call
import retrofit2.Callback

/** An abstract interface design for data sources. */
interface DataSource<T> {

  /** combine a call and a callback to the DataSource. */
  fun combine(call: Call<T>, callback: Callback<T>): DataSource<T>

  /** retry fetching data few times with time interval when the request gets failure. */
  fun retry(retryCount: Int, interval: Long): DataSource<T>

  /** observes a [ApiResponse] value from the API call request. */
  fun observeResponse(observer: ResponseObserver<T>): DataSource<T>

  /**
   * concat an another [DataSource] and request API call to the received data source
   * if the receiver previous call getting successful.
   */
  fun <R> concat(dataSource: DataSource<R>): DataSource<R>

  /** request API call and response to the callback. */
  fun request(): DataSource<T>

  /** invalidate cached data and retry counts, request again API call. */
  fun invalidate()
}
