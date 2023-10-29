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
import com.skydoves.sandwich.getOrThrow
import com.skydoves.sandwich.isSuccess
import com.skydoves.sandwich.ktor.bodyString
import com.skydoves.sandwich.ktor.getApiResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.messageOrNull
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwich.retrofit.statusCode
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnSuccess
import com.skydoves.sandwichdemo.model.PokemonResponse
import com.skydoves.sandwichdemo.model.Poster
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber

class MainViewModel(mainRepository: MainRepository) : ViewModel() {

  val toastLiveData = MutableLiveData<String>()

  private val client = HttpClient(OkHttp) {
    defaultRequest {
      contentType(ContentType.Application.Json)
      accept(ContentType.Application.Json)
    }

    install(ContentNegotiation) {
      json(
        Json {
          prettyPrint = true
          isLenient = true
          ignoreUnknownKeys = true
        },
      )
    }
  }

  // Use Case 1 - update the fetched posters as a property
  val posterList: StateFlow<List<Poster>> = mainRepository.fetchPostersFlow().map {
    if (it.isSuccess) {
      it.getOrThrow()
    } else {
      toastLiveData.postValue(it.messageOrNull)
      emptyList()
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())
  // End (Use Case 1)

  // Use Case 2 - update the fetched posters manually
  private val _posterList2Flow = MutableStateFlow<List<Poster>?>(emptyList())
  val posterList2Flow: StateFlow<List<Poster>?> = _posterList2Flow

  init {
    Timber.plant(Timber.DebugTree())
    Timber.d("initialized MainViewModel.")

    viewModelScope.launch {
      val response = client.getApiResponse<PokemonResponse>("https://pokeapi.co/api/v2/pokemon")
      response.onSuccess {
        Timber.d("success: $data")
      }.suspendOnError {
        Timber.d("error: ${bodyString()}")
      }.onException {
        Timber.d("exception: $messageOrNull")
      }

      mainRepository.fetchPosters()
        // handles the success scenario when the API request succeeds.
        .suspendOnSuccess {
          _posterList2Flow.emit(data)
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
        }
        // handles the error scenario when an unexpected exception happens.
        // e.g., network connection error, timeout.
        .onException {
          toastLiveData.postValue(message)
        }
    }
  }
  // End (Use Case 2)
}
