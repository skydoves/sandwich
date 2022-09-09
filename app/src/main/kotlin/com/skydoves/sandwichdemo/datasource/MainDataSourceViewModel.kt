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

package com.skydoves.sandwichdemo.datasource

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.datasource.disposables.CompositeDisposable
import com.skydoves.sandwich.datasource.toResponseDataSource
import com.skydoves.sandwich.map
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwichdemo.mapper.ErrorEnvelopeMapper
import com.skydoves.sandwichdemo.model.Poster
import com.skydoves.sandwichdemo.network.DisneyDataSourceService
import kotlinx.coroutines.launch
import timber.log.Timber

class MainDataSourceViewModel constructor(disneyService: DisneyDataSourceService) : ViewModel() {

  val posterListLiveData: MutableLiveData<List<Poster>> = MutableLiveData()
  val toastLiveData = MutableLiveData<String>()
  private val disposable = CompositeDisposable()

  init {
    Timber.d("initialized MainViewModel.")

    viewModelScope.launch {
      disneyService.fetchDisneyPosterList().toResponseDataSource()
        // retry fetching data 3 times with 5000L interval when the request gets failure.
        .retry(3, 5000L)
        // a retain policy for retaining data on the internal storage.
        .dataRetainPolicy(com.skydoves.sandwich.datasource.DataRetainPolicy.RETAIN)
        // joins onto CompositeDisposable as a disposable and dispose onCleared().
        .joinDisposable(disposable)
        // request API network call asynchronously.
        // if the request is successful, the data source will hold the success data.
        // in the next request after success, returns the temporarily cached API response.
        // if you want to fetch a new response data, use NO_RETAIN policy or invalidate().
        .request {
          // handle the case when the API request gets a success response.
          onSuccess {
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
            // e.g. network connection error, timeout.
            .onException {
              Timber.d(message())
              toastLiveData.postValue(message())
            }
        }
    }
  }

  override fun onCleared() {
    super.onCleared()
    if (!disposable.disposed) {
      disposable.clear()
    }
  }
}
