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

package com.skydoves.sandwich

/**
 * @author skydoves (Jaewoong Eum)
 *
 * This is a list of Hypertext Transfer Protocol (HTTP) response status codes.
 */
public enum class StatusCode(public val code: Int) {
  Unknown(0),

  Continue(100),
  SwitchingProtocols(101),
  Processing(102),
  EarlyHints(103),

  OK(200),
  Created(201),
  Accepted(202),
  NonAuthoritative(203),
  NoContent(204),
  ResetContent(205),
  PartialContent(206),
  MultiStatus(207),
  AlreadyReported(208),
  IMUsed(209),

  MultipleChoices(300),
  MovePermanently(301),
  Found(302),
  SeeOther(303),
  NotModified(304),
  UseProxy(305),
  SwitchProxy(306),
  TemporaryRedirect(307),
  PermanentRedirect(308),

  BadRequest(400),
  Unauthorized(401),
  PaymentRequired(402),
  Forbidden(403),
  NotFound(404),
  MethodNotAllowed(405),
  NotAcceptable(406),
  ProxyAuthenticationRequired(407),
  RequestTimeout(408),
  Conflict(409),
  Gone(410),
  LengthRequired(411),
  PreconditionFailed(412),
  PayloadTooLarge(413),
  URITooLong(414),
  UnsupportedMediaType(415),
  RangeNotSatisfiable(416),
  ExpectationFailed(417),
  IMATeapot(418),
  MisdirectedRequest(421),
  UnProcessableEntity(422),
  Locked(423),
  FailedDependency(424),
  TooEarly(425),
  UpgradeRequired(426),
  PreconditionRequired(428),
  TooManyRequests(429),
  RequestHeaderFieldsTooLarge(431),
  UnavailableForLegalReasons(451),

  InternalServerError(500),
  NotImplemented(501),
  BadGateway(502),
  ServiceUnavailable(503),
  GatewayTimeout(504),
  HTTPVersionNotSupported(505),
  NotExtended(510),
  NetworkAuthenticationRequired(511),
}
