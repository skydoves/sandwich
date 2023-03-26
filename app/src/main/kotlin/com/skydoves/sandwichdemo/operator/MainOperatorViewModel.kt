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

package com.skydoves.sandwichdemo.operator

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.skydoves.sandwich.suspendOperator
import com.skydoves.sandwichdemo.SandwichDemoApp
import com.skydoves.sandwichdemo.model.Poster
import com.skydoves.sandwichdemo.network.DisneyService
import timber.log.Timber

class MainOperatorViewModel constructor(
  disneyService: DisneyService,
) : AndroidViewModel(SandwichDemoApp.sandwichApp) {

  val posterListLiveData: LiveData<List<Poster>>

  init {
    Timber.d("initialized MainViewModel.")

    posterListLiveData = liveData(viewModelScope.coroutineContext) {
      disneyService.fetchDisneyPosters().suspendOperator(
        CommonResponseOperator(
          success = { success ->
            emit(success.data)
            Timber.d("$success.data")
          },
          application = getApplication(),
        ),
      )
    }
  }
}
