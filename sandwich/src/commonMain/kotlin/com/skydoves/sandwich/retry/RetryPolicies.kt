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
@file:Suppress("unused", "RedundantVisibilityModifier")

package com.skydoves.sandwich.retry

import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

/**
 * @author skydoves (Jaewoong Eum)
 *
 * Ready made [RetryPolicy] implementations.
 *
 * ```kotlin
 * val response = runAndRetry(RetryPolicies.exponentialBackoff(maxAttempts = 4)) { _, _ ->
 *   repository.fetchPosters()
 * }
 * ```
 */
public object RetryPolicies {

  /** A policy that never retries. */
  @JvmStatic
  public fun none(): RetryPolicy = FixedDelayRetryPolicy(maxAttempts = 1, delayMillis = 0)

  /**
   * A policy that retries up to [maxAttempts] times, waiting [delayMillis] between attempts.
   *
   * @param maxAttempts The total number of attempts, including the first one. Must be at least 1.
   * @param delayMillis The delay before every retry.
   */
  @JvmStatic
  @JvmOverloads
  public fun fixedDelay(maxAttempts: Int = 3, delayMillis: Int = 1_000): RetryPolicy =
    FixedDelayRetryPolicy(maxAttempts, delayMillis)

  /**
   * A policy whose delay grows by [delayMillis] on every attempt. e.g., 1s, 2s, 3s.
   *
   * @param maxAttempts The total number of attempts, including the first one. Must be at least 1.
   * @param delayMillis The delay added for each successive retry.
   * @param maxDelayMillis An upper bound applied to the computed delay.
   */
  @JvmStatic
  @JvmOverloads
  public fun linear(
    maxAttempts: Int = 3,
    delayMillis: Int = 1_000,
    maxDelayMillis: Int = Int.MAX_VALUE,
  ): RetryPolicy = LinearRetryPolicy(maxAttempts, delayMillis, maxDelayMillis)

  /**
   * A policy whose delay grows by [factor] on every attempt. e.g., 1s, 2s, 4s, 8s.
   *
   * Retrying a struggling server on a fixed schedule makes every client come back at the same
   * moment, so [jitter] spreads the retries out. It is the fraction of the computed delay that is
   * randomized, where `0.0` disables randomization and `1.0` picks uniformly between zero and the
   * full delay.
   *
   * @param maxAttempts The total number of attempts, including the first one. Must be at least 1.
   * @param initialDelayMillis The delay before the first retry.
   * @param factor The multiplier applied on every successive retry. Must be at least 1.
   * @param maxDelayMillis An upper bound applied to the computed delay.
   * @param jitter The fraction of the delay to randomize, between `0.0` and `1.0`.
   */
  @JvmStatic
  @JvmOverloads
  public fun exponentialBackoff(
    maxAttempts: Int = 3,
    initialDelayMillis: Int = 1_000,
    factor: Double = 2.0,
    maxDelayMillis: Int = 30_000,
    jitter: Double = 0.5,
  ): RetryPolicy = ExponentialBackoffRetryPolicy(
    maxAttempts = maxAttempts,
    initialDelayMillis = initialDelayMillis,
    factor = factor,
    maxDelayMillis = maxDelayMillis,
    jitter = jitter,
  )
}

private fun requireAttempts(maxAttempts: Int) {
  require(maxAttempts >= 1) { "maxAttempts must be at least 1 but was $maxAttempts" }
}

private class FixedDelayRetryPolicy(private val maxAttempts: Int, private val delayMillis: Int) :
  RetryPolicy {
  init {
    requireAttempts(maxAttempts)
  }

  override fun shouldRetry(attempt: Int, message: String?): Boolean = attempt < maxAttempts

  override fun retryTimeout(attempt: Int, message: String?): Int = delayMillis
}

private class LinearRetryPolicy(
  private val maxAttempts: Int,
  private val delayMillis: Int,
  private val maxDelayMillis: Int,
) : RetryPolicy {
  init {
    requireAttempts(maxAttempts)
  }

  override fun shouldRetry(attempt: Int, message: String?): Boolean = attempt < maxAttempts

  override fun retryTimeout(attempt: Int, message: String?): Int =
    min(delayMillis.toLong() * attempt, maxDelayMillis.toLong()).toInt()
}

private class ExponentialBackoffRetryPolicy(
  private val maxAttempts: Int,
  private val initialDelayMillis: Int,
  private val factor: Double,
  private val maxDelayMillis: Int,
  private val jitter: Double,
) : RetryPolicy {
  init {
    requireAttempts(maxAttempts)
    require(factor >= 1.0) { "factor must be at least 1.0 but was $factor" }
    require(jitter in 0.0..1.0) { "jitter must be between 0.0 and 1.0 but was $jitter" }
  }

  override fun shouldRetry(attempt: Int, message: String?): Boolean = attempt < maxAttempts

  override fun retryTimeout(attempt: Int, message: String?): Int {
    val exponential = initialDelayMillis * factor.pow(attempt - 1)
    val capped = min(exponential, maxDelayMillis.toDouble())
    if (jitter == 0.0) return capped.toInt()
    val floor = capped * (1.0 - jitter)
    return (floor + Random.nextDouble() * (capped - floor)).toInt()
  }
}
