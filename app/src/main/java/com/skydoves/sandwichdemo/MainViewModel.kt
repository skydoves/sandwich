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

package com.skydoves.sandwichdemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skydoves.sandwich.ResponseDataSource
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.map
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwichdemo.model.Poster
import com.skydoves.sandwichdemo.network.DisneyService
import com.skydoves.sandwichdemo.network.ErrorEnvelopeMapper
import timber.log.Timber

class MainViewModel constructor(
  private val disneyService: DisneyService
) : ViewModel() {

  // request API call Asynchronously and holding successful response data.
  private val dataSource = ResponseDataSource<List<Poster>>()

  val posterListLiveData = MutableLiveData<List<Poster>>()
  val toastLiveData = MutableLiveData<String>()

  init {
    Timber.d("initialized MainViewModel.")
  }

  /** fetch poster list data from the network. */
  fun fetchDisneyPosters() {
    dataSource
      // retry fetching data 3 times with 5000L interval when the request gets failure.
      .retry(3, 5000L)
      // combine network service to the data source.
      .combine(disneyService.fetchDisneyPosterList()) { response ->
        // handle the case when the API request gets a success response.
        response.onSuccess {
          Timber.d("$data")
          posterListLiveData.postValue(data)
        }
          // handle the case when the API request gets a error response.
          // e.g. internal server error.
          .onError {
            Timber.d(message())

            // handling error based on status code.
            when (statusCode) {
              StatusCode.InternalServerError -> toastLiveData.postValue("InternalServerError")
              StatusCode.BadGateway -> toastLiveData.postValue("BadGateway")
              else -> toastLiveData.postValue("$statusCode(${statusCode.code}): ${message()}")
            }

            // map the ApiResponse.Failure.Error to a customized error model using the mapper.
            map(ErrorEnvelopeMapper) {
              Timber.d(this.toString())
            }
          }
          // handle the case when the API request gets a exception response.
          // e.g. network connection error.
          .onException {
            Timber.d(message())
            toastLiveData.postValue(message())
          }
      }
      // observe every API request responses.
      .observeResponse {
        Timber.d("observeResponse: $it")
      }
      // request API network call asynchronously.
      // if the request is successful, the data source will hold the success data.
      // in the next request after success, returns the cached API response.
      // if you want to fetch a new response data, use invalidate().
      .request()
  }
}
