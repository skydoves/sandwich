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
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.StatusCode
import com.skydoves.sandwich.map
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.sandwichdemo.mapper.ErrorEnvelopeMapper
import com.skydoves.sandwichdemo.model.Poster
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel constructor(mainRepository: MainRepository) : ViewModel() {

  private val _posterListFlow = MutableStateFlow<List<Poster>?>(emptyList())
  val posterListFlow: StateFlow<List<Poster>?> = _posterListFlow

  val toastLiveData = MutableLiveData<String>()

  init {
    Timber.d("initialized MainViewModel.")

    viewModelScope.launch {
      mainRepository.fetchPosters()
        // handles the success scenario when the API request succeeds.
        .suspendOnSuccess {
          _posterListFlow.emit(data)
        }
        // handles the error scenario when the API request fail.
        // e.g., internal server error.
        .onError {
          // handles error cases depending on the status code.
          val message = when (statusCode) {
            StatusCode.InternalServerError -> "InternalServerError"
            StatusCode.BadGateway -> "BadGateway"
            else -> "$statusCode(${statusCode.code}): ${message()}"
          }
          toastLiveData.postValue(message)

          // map the ApiResponse.Failure.Error to our custom error model using the mapper.
          map(ErrorEnvelopeMapper) {
            Timber.d("[Code: $code]: $message")
          }
        }
        // handles the error scenario when an unexpected exception happens.
        // e.g., network connection error, timeout.
        .onException {
          toastLiveData.postValue(message)
        }
    }
  }
}
