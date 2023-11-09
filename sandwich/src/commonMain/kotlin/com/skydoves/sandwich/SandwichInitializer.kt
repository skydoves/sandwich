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

import com.skydoves.sandwich.mappers.SandwichFailureMapper
import com.skydoves.sandwich.operators.SandwichOperator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okio.Timeout
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic
import kotlin.native.concurrent.ThreadLocal

/**
 * @author skydoves (Jaewoong Eum)
 *
 * SandwichInitializer is a rules and strategies initializer of the network response.
 */
@ThreadLocal
public object SandwichInitializer {

  /**
   * @author skydoves (Jaewoong Eum)
   *
   * determines the success code range of network responses.
   *
   * if a network request is successful and the response code is in the [successCodeRange],
   * its response will be a [ApiResponse.Success].
   *
   * if a network request is successful but out of the [successCodeRange] or failure,
   * the response will be a [ApiResponse.Failure.Error].
   * */
  @JvmStatic
  public var successCodeRange: IntRange = 200..299

  /**
   * @author skydoves (Jaewoong Eum)
   *
   * A list of global operators that is executed by [ApiResponse]s globally on each response.
   *
   * [com.skydoves.sandwich.operators.ApiResponseOperator] allows you to handle success and error response instead of
   * the [ApiResponse.onSuccess], [ApiResponse.onError], [ApiResponse.onException] transformers.
   * [com.skydoves.sandwich.operators.ApiResponseSuspendOperator] can be used for suspension scope.
   *
   * By setting [sandwichOperators], you don't need to set operator for every [ApiResponse] by using [ApiResponse.of] function.
   */
  @JvmStatic
  public var sandwichOperators: MutableList<SandwichOperator> = mutableListOf()

  /**
   * @author skydoves (Jaewoong Eum)
   *
   * A list of global failure mappers that is executed by [ApiResponse]s globally on each response.
   *
   * [com.skydoves.sandwich.mappers.ApiResponseFailureMapper] allows you to map the failure responses
   * to transform the payload, or changes to custom types.
   * [com.skydoves.sandwich.mappers.ApiResponseFailureSuspendMapper] can be used for suspension scope.
   *
   * By setting [sandwichFailureMappers], Sandwich will automatically maps all [ApiResponse] by using [ApiResponse.of] function.
   */
  @JvmStatic
  public var sandwichFailureMappers: MutableList<SandwichFailureMapper> = mutableListOf()

  /**
   * @author skydoves (Jaewoong Eum)
   *
   * A [CoroutineScope] for executing and operating the overall Retrofit network requests.
   */
  @JvmSynthetic
  public var sandwichScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  /**
   * @author skydoves (Jaewoong Eum)
   *
   * A global [Timeout] for operating the [com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory].
   *
   * Returns a timeout that spans the entire call: resolving DNS, connecting, writing the request
   * body, server processing, and reading the response body. If the call requires redirects or
   * retries all must complete within one timeout period.
   */
  @JvmStatic
  public var sandwichTimeout: Timeout? = null
}
