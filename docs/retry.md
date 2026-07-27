# Retry

Sandwich offers seamless ways to run and retry tasks. To execute and retry network or I/O requests, you can employ the `RetryPolicy` interface along with the `runAndRetry` extension, as demonstrated in the code below:

```kotlin
val retryPolicy = object : RetryPolicy {
  override fun shouldRetry(attempt: Int, message: String?): Boolean = attempt <= 3

  override fun retryTimeout(attempt: Int, message: String?): Int = 3000
}

val apiResponse = runAndRetry(retryPolicy) { attempt, reason ->
  mainRepository.fetchPosters()
}.onSuccess {
  // Handle a success case
}.onFailure {
  // Handle failure cases
}
```

This setup allows you to define a retry policy that determines whether a retry attempt should occur and specifies the retry timeout. The `runAndRetry` extension then encapsulates the execution logic, applying the defined policy, and providing the response in a clean and structured manner.
## Ready made policies

`RetryPolicies` covers the usual strategies, so a custom `RetryPolicy` is only needed for something
unusual:

```kotlin
RetryPolicies.none()
RetryPolicies.fixedDelay(maxAttempts = 3, delayMillis = 1_000)
RetryPolicies.linear(maxAttempts = 3, delayMillis = 1_000, maxDelayMillis = 10_000)
RetryPolicies.exponentialBackoff(
  maxAttempts = 4,
  initialDelayMillis = 1_000,
  factor = 2.0,
  maxDelayMillis = 30_000,
  jitter = 0.5,
)
```

Retrying a struggling server on a fixed schedule makes every client come back at the same moment,
so `exponentialBackoff` randomizes part of each delay. `jitter` is the fraction that is randomized,
where `0.0` disables it.

## Retrying only some failures

`RetryPolicy` only sees the failure message, which is rarely enough to decide whether a retry is
worthwhile. Retrying a 400 just wastes a round trip. The `retryOn` parameter receives the failure
itself:

```kotlin
val apiResponse = runAndRetry(
  retryPolicy = RetryPolicies.exponentialBackoff(maxAttempts = 4),
  retryOn = { failure -> failure is ApiResponse.Failure.Exception && failure.isTimeout },
) { attempt, reason ->
  mainRepository.fetchPosters()
}
```

## Retry-After

A server answering 429 or 503 often states how long to wait through the `Retry-After` header.
Honouring it beats any client side backoff, so both integrations expose it:

```kotlin
response.onError {
  val waitMillis = retryAfterMillis  // null when the header is absent or is an HTTP-date
}
```
