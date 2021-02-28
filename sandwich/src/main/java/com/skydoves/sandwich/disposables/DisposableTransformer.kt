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

@file:Suppress("unused")
@file:JvmName("DisposableTransformer")
@file:JvmMultifileClass

package com.skydoves.sandwich.disposables

import com.skydoves.sandwich.request
import retrofit2.Call

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Returns an instance of [Disposable] from a [Call].
 */
fun <T> Call<T>.disposable(): Disposable {
  val call = this
  return object : Disposable {
    override fun dispose() {
      if (call.isExecuted && !isDisposed()) {
        call.cancel()
      }
    }

    override fun isDisposed() = call.isCanceled
  }
}

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Joins onto [CompositeDisposable] as a disposable. must be called before [request].
 */
fun <T> Call<T>.joinDisposable(compositeDisposable: CompositeDisposable) = apply {
  compositeDisposable.add(disposable())
}
