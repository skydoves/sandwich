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
package com.skydoves.sandwich

import com.skydoves.sandwich.retry.RetryPolicy
import com.skydoves.sandwich.retry.runAndRetry
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.net.UnknownHostException

@RunWith(JUnit4::class)
internal class ApiResponseRetryTest {

  @Test
  fun `Should retry a call according to RetryPolicy`() = runTest {
    var currentValue = 1
    val maxAttempts = 3
    val retryPolicy = object : RetryPolicy {
      override fun shouldRetry(attempt: Int, message: String?): Boolean = attempt < maxAttempts

      override fun retryTimeout(attempt: Int, message: String?): Int = 0
    }

    runAndRetry(retryPolicy) { attempt, reason ->
      currentValue++
      ApiResponse.error<String>(UnknownHostException("UnknownHostException"))
    }

    assertThat(currentValue, `is`(maxAttempts + 1))
  }
}
