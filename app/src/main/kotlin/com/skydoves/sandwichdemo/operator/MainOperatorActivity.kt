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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.skydoves.sandwichdemo.R
import com.skydoves.sandwichdemo.adapter.PosterAdapter
import com.skydoves.sandwichdemo.databinding.ActivityMainCoroutinesOperatorBinding

class MainOperatorActivity : AppCompatActivity() {

  private val viewModelFactory: MainOperatorViewModelFactory =
    MainOperatorViewModelFactory()
  private val viewModel: MainOperatorViewModel by lazy {
    viewModelFactory.create(MainOperatorViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    DataBindingUtil.setContentView<ActivityMainCoroutinesOperatorBinding>(
      this,
      R.layout.activity_main_coroutines_operator,
    ).apply {
      lifecycleOwner = this@MainOperatorActivity
      viewModel = this@MainOperatorActivity.viewModel
      adapter = PosterAdapter()
    }
  }
}
