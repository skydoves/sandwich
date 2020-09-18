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

package com.skydoves.sandwich.executors

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RestrictTo
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class DefaultTaskExecutor : TaskExecutor() {

  private val mLock = Any()

  private val mDiskIO = Executors.newFixedThreadPool(
    4,
    object : ThreadFactory {
      private val THREAD_NAME_STEM = "arch_disk_io_%d"

      private val mThreadId = AtomicInteger(0)

      override fun newThread(r: Runnable): Thread {
        val t = Thread(r)
        t.name = String.format(THREAD_NAME_STEM, mThreadId.getAndIncrement())
        return t
      }
    }
  )

  @Volatile
  private var mMainHandler: Handler? = null

  override fun executeOnDiskIO(runnable: Runnable) {
    mDiskIO.execute(runnable)
  }

  override fun postToMainThread(runnable: Runnable, duration: Long) {
    if (mMainHandler == null) {
      synchronized(mLock) {
        if (mMainHandler == null) {
          mMainHandler = createAsync(Looper.getMainLooper())
        }
      }
    }

    mMainHandler?.postDelayed(runnable, duration)
  }

  override val isMainThread: Boolean
    get() = Looper.getMainLooper().thread === Thread.currentThread()

  private fun createAsync(looper: Looper): Handler {
    if (Build.VERSION.SDK_INT >= 28) {
      return Handler.createAsync(looper)
    }
    if (Build.VERSION.SDK_INT >= 16) {
      try {
        return Handler::class.java.getDeclaredConstructor(
          Looper::class.java,
          Handler.Callback::class.java,
          Boolean::class.javaPrimitiveType
        )
          .newInstance(looper, null, true)
      } catch (ignored: IllegalAccessException) {
      } catch (ignored: InstantiationException) {
      } catch (ignored: NoSuchMethodException) {
      } catch (e: InvocationTargetException) {
        return Handler(looper)
      }
    }
    return Handler(looper)
  }
}
