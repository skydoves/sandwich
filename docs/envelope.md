# Envelope

Many backends answer with HTTP 200 regardless of the business outcome, and encode the real result
inside the body:

```json
{ "code": 0, "message": "ok", "data": { ... } }
```

Sandwich classifies such a response as `ApiResponse.Success`, because the transport layer did
succeed. The **envelope** support lets Sandwich additionally understand the *business* outcome, so
a business failure is classified as `ApiResponse.Failure.Error` and the payload can be flattened.

## ApiEnvelope

Implement `ApiEnvelope<T, E>` on the response model, where `T` is the business payload and `E` is
the business error:

```kotlin
data class BaseResponse<T>(
  val code: Int,
  val message: String,
  val data: T?,
) : ApiEnvelope<T?, String> {
  override val isEnvelopeSuccessful: Boolean get() = code == 0
  override val envelopeBody: T? get() = data
  override val envelopeError: String get() = message
}
```

`envelopeBody` is only read when `isEnvelopeSuccessful` is `true`, and `envelopeError` only when it
is `false`, so the unused side may throw or return a placeholder.

## Automatic classification

`ApiEnvelopeMapper` is registered on `SandwichInitializer.sandwichSuccessMappers` **by default**, so
a business failure is demoted to `ApiResponse.Failure.Error` without any setup:

```kotlin
val response = service.fetchPosters() // ApiResponse<BaseResponse<List<Poster>>>

response.onSuccess {
  // only runs when code == 0
}.onError {
  // runs when code != 0, and `payload` is the envelope's message
}
```

This also means global operators and global failure mappers observe the business failure, so
cross-cutting concerns such as logging or token refresh see a consistent picture.

A response body that does not implement `ApiEnvelope` passes through untouched, so the default
registration has no effect on models that do not opt in. To opt out entirely:

```kotlin
SandwichInitializer.sandwichSuccessMappers -= ApiEnvelopeMapper
```

## unwrap

Automatic classification does not change the *type* of the response, because the runtime payload
must keep agreeing with the declared type. Use `unwrap` to flatten
`ApiResponse<BaseResponse<T>>` into `ApiResponse<T>`, typically at the repository boundary:

```kotlin
// service declares the wire type
interface PosterService {
  @GET("posters")
  suspend fun fetchPosters(): ApiResponse<BaseResponse<List<Poster>>>
}

// repository exposes the business type
class PosterRepository(private val service: PosterService) {
  suspend fun posters(): ApiResponse<List<Poster>?> = service.fetchPosters().unwrap()
}
```

`unwrap` handles all three cases:

| Input | Output |
|---|---|
| `Success` with a business-successful envelope | `Success` holding `envelopeBody` |
| `Success` with a business-failed envelope | `Failure.Error` holding `envelopeError` |
| `Failure.Error` / `Failure.Exception` | returned as is |

The `tag` of a successful response is preserved, so Retrofit and Ktor extensions such as
`statusCode` and `headers` keep working after unwrapping.

## ApiEnvelopeSpec

When the response model cannot be modified — a generated DTO, or a model owned by another module —
describe the envelope contract from the outside with `ApiEnvelopeSpec`:

```kotlin
data class LegacyResponse(val status: String, val payload: String, val reason: String)

object LegacyResponseSpec : ApiEnvelopeSpec<LegacyResponse, String, String> {
  override fun isEnvelopeSuccessful(envelope: LegacyResponse) = envelope.status == "OK"
  override fun envelopeBody(envelope: LegacyResponse) = envelope.payload
  override fun envelopeError(envelope: LegacyResponse) = envelope.reason
}

val response: ApiResponse<String> = service.fetchLegacy().unwrap(LegacyResponseSpec)
```

Note that a spec is only consulted by an explicit `unwrap(spec)` call. Automatic classification
relies on the `ApiEnvelope` interface, because it has to recognize the envelope at runtime.

## Non-JSON error bodies

`ApiEnvelope` is not limited to a `code`/`message`/`data` shape. Any rule that inspects the parsed
body works, which covers servers that answer HTTP 200 with an error page:

```kotlin
data class SpreadSheetResponse(val raw: String) : ApiEnvelope<String, String> {
  override val isEnvelopeSuccessful: Boolean get() = !raw.startsWith("<!DOCTYPE html")
  override val envelopeBody: String get() = raw
  override val envelopeError: String get() = "the server returned an HTML error page"
}
```

## Custom success mappers

`ApiEnvelopeMapper` is a regular `ApiResponseSuccessMapper`, and you can register your own to run
any global rule that may demote a success:

```kotlin
SandwichInitializer.sandwichSuccessMappers += ApiResponseSuccessMapper { response ->
  val data = response.data
  if (data is List<*> && data.isEmpty()) {
    ApiResponse.Failure.Error(payload = "empty result")
  } else {
    response
  }
}
```

Success mappers run **before** failure mappers, so a demoted response still flows through
[global failure mappers](mapper.md#global-failure-mapper).

For a suspending rule, implement `ApiResponseSuccessSuspendMapper` instead. It is only applied on
suspending creation paths such as `ApiResponse.suspendOf`, because a suspending mapper cannot be
awaited from a blocking one.
