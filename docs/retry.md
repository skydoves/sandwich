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