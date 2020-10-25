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

package com.skydoves.sandwich.disposables

/** A disposable container that can hold onto multiple other disposables. */
class CompositeDisposable {

  @Volatile
  var disposed: Boolean = false
    private set

  private var disposables: MutableSet<Disposable>? = hashSetOf()

  /** adds a new [Disposable] to this [CompositeDisposable] if not yet disposed. */
  fun add(disposable: Disposable) {
    if (disposable.isDisposed()) {
      return
    }

    if (!disposed) {
      synchronized(this) {
        if (!disposed) {
          disposables?.add(disposable)
          return
        }
      }
    }
  }

  /** removes a [Disposable] from this [CompositeDisposable] and dispose the target. */
  fun remove(disposable: Disposable) {
    if (!disposed) {
      synchronized(this) {
        if (disposed || disposables?.remove(disposable) == false) {
          return
        }
      }
      disposable.dispose()
    }
  }

  /** disposes all disposables that are currently part of this [CompositeDisposable]. */
  fun clear() {
    if (!disposed) {
      var mutableCollection: MutableCollection<Disposable>?
      synchronized(this) {
        if (disposed) {
          return
        }
        mutableCollection = disposables
        disposables = null
        disposed = true
      }
      mutableCollection?.forEach(Disposable::dispose)
    }
  }
}
