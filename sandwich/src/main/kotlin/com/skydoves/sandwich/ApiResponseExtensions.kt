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
 * @since 1.2.8
 *
 *  Returns true if this instance represents an [ApiResponse.Success].
 */
public inline val ApiResponse<Any>.isSuccess: Boolean
  get() = this is ApiResponse.Success

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.2.8
 *
 *  Returns true if this instance represents an [ApiResponse.Failure].
 */
public inline val ApiResponse<Any>.isFailure: Boolean
  get() = this is ApiResponse.Failure

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.2.8
 *
 *  Returns true if this instance represents an [ApiResponse.Failure.Error].
 */
public inline val ApiResponse<Any>.isError: Boolean
  get() = this is ApiResponse.Failure.Error

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.2.8
 *
 *  Returns true if this instance represents an [ApiResponse.Failure.Exception].
 */
public inline val ApiResponse<Any>.isException: Boolean
  get() = this is ApiResponse.Failure.Exception

/**
 * @author skydoves (Jaewoong Eum)
 * @since 1.2.8
 *
 *  Returns the message of this [ApiResponse].
 */
public inline val ApiResponse<Any>.message: String
  get() = if (this is ApiResponse.Success) {
    this.data.toString()
  } else {
    (this as ApiResponse.Failure).message()
  }
