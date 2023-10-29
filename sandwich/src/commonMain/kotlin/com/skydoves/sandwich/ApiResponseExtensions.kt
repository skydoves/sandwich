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

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponse Factory.
 *
 * Create an [ApiResponse] from the given executable [f].
 *
 * If the [f] doesn't throw any exceptions, it creates [ApiResponse.Success].
 * If the [f] throws an exception, it creates [ApiResponse.Failure.Exception].
 */
public inline fun <T> apiResponseBy(tag: Any? = null, crossinline f: () -> T): ApiResponse<T> =
  ApiResponse.by(tag = tag, f = f)

/**
 * @author skydoves (Jaewoong Eum)
 *
 * ApiResponse Factory.
 *
 * Create an [ApiResponse] from the given executable [f].
 *
 * If the [f] doesn't throw any exceptions, it creates [ApiResponse.Success].
 * If the [f] throws an exception, it creates [ApiResponse.Failure.Exception].
 */
@SuspensionFunction
public suspend inline fun <T> apiResponseBySuspend(
  tag: Any? = null,
  crossinline f: suspend () -> T,
): ApiResponse<T> = ApiResponse.bySuspend(tag = tag, f = f)

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.3.1
 *
 *  Returns true if this instance represents an [ApiResponse.Success].
 */
public inline val ApiResponse<Any>.isSuccess: Boolean
  get() = this is ApiResponse.Success

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.3.1
 *
 *  Returns true if this instance represents an [ApiResponse.Failure].
 */
public inline val ApiResponse<Any>.isFailure: Boolean
  get() = this is ApiResponse.Failure

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.3.1
 *
 *  Returns true if this instance represents an [ApiResponse.Failure.Error].
 */
public inline val ApiResponse<Any>.isError: Boolean
  get() = this is ApiResponse.Failure.Error

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.3.1
 *
 *  Returns true if this instance represents an [ApiResponse.Failure.Exception].
 */
public inline val ApiResponse<Any>.isException: Boolean
  get() = this is ApiResponse.Failure.Exception

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.3.2
 *
 *  Returns The error message or null depending on the type of [ApiResponse].
 */
public inline val ApiResponse<Any>.messageOrNull: String?
  get() = when (this) {
    is ApiResponse.Failure.Error -> payload.toString()
    is ApiResponse.Failure.Exception -> message
    else -> null
  }
