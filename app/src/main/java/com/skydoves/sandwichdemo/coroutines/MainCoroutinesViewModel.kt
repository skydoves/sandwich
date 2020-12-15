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

package com.skydoves.sandwichdemo.coroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.map
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.sandwichdemo.model.Poster
import com.skydoves.sandwichdemo.network.ErrorEnvelopeMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

class MainCoroutinesViewModel constructor(disneyService: DisneyCoroutinesService) : ViewModel() {

  val posterListLiveData: LiveData<List<Poster>>
  val toastLiveData = MutableLiveData<String>()

  init {
    Timber.d("initialized MainViewModel.")

    posterListLiveData = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
      emitSource(
        flow {
          disneyService.fetchDisneyPosterList()
            // handle the case when the API request gets a success response.
            .suspendOnSuccess {
              Timber.d("$data")

              data?.let { emit(it) }
            }
            // handle the case when the API request gets a error response.
            // e.g., internal server error.
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
                Timber.d("[Code: $code]: $message")
              }
            }
            // handle the case when the API request gets a exception response.
            // e.g., network connection error.
            .onException {
              Timber.d(message())
              toastLiveData.postValue(message())
            }
        }.flowOn(Dispatchers.IO).asLiveData()
      )
    }
  }
}
