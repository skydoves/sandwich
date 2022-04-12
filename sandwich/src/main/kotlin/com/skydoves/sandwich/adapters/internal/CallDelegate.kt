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

package com.skydoves.sandwich.adapters.internal

import com.skydoves.sandwich.SandwichInitializer
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author skydoves (Jaewoong Eum)
 *
 * CallDelegate is a delegate [Call] proxy for handling and transforming one to another generic types
 * between the two different types of [Call] requests.
 */
internal abstract class CallDelegate<TIn, TOut>(
  protected val proxy: Call<TIn>
) : Call<TOut> {
  override fun execute(): Response<TOut> = throw NotImplementedError()
  final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
  final override fun clone(): Call<TOut> = cloneImpl()

  override fun cancel() = proxy.cancel()
  override fun request(): Request = proxy.request()
  override fun isExecuted() = proxy.isExecuted
  override fun isCanceled() = proxy.isCanceled
  override fun timeout(): Timeout = SandwichInitializer.sandwichTimeout ?: proxy.timeout()

  abstract fun enqueueImpl(callback: Callback<TOut>)
  abstract fun cloneImpl(): Call<TOut>
}
