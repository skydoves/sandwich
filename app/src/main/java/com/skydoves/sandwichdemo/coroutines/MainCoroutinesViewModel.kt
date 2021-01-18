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
import com.skydoves.sandwich.suspendOnProcedure
import com.skydoves.sandwichdemo.mapper.ErrorEnvelopeMapper
import com.skydoves.sandwichdemo.model.Poster
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
            .suspendOnProcedure(
              // handles the success case when the API request gets a successful response.
              onSuccess = {
                Timber.d("$data")

                data?.let { emit(it) }
              },
              // handles error cases when the API request gets an error response.
              // e.g., internal server error.
              onError = {
                Timber.d(message())

                // handles error cases depending on the status code.
                when (statusCode) {
                  StatusCode.InternalServerError -> toastLiveData.postValue("InternalServerError")
                  StatusCode.BadGateway -> toastLiveData.postValue("BadGateway")
                  else -> toastLiveData.postValue("$statusCode(${statusCode.code}): ${message()}")
                }

                // map the ApiResponse.Failure.Error to our custom error model using the mapper.
                map(ErrorEnvelopeMapper) {
                  Timber.d("[Code: $code]: $message")
                }
              },
              // handles exceptional cases when the API request gets an exception response.
              // e.g., network connection error.
              onException = {
                Timber.d(message())
                toastLiveData.postValue(message())
              }
            )
        }.flowOn(Dispatchers.IO).asLiveData()
      )
    }
  }
}
