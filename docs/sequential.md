# Sequential

Sandwich provides sequential solutions for scenarios where you require sequential execution of network requests.

## then and suspendThen

If you have a scenario where you need to execute tasks A, B, and C in a dependent sequence, for example, where task B depends on the completion of task A, and task C depends on the completion of task B, you can effectively utilize the `then` or `suspendThen` extensions, as demonstrated in the example below:

```kotlin
service.getUserToken(id) suspendThen { tokenResponse ->
    service.getUserDetails(tokenResponse.token) 
} suspendThen { userResponse ->
    service.queryPosters(userResponse.user.name)
}.mapSuccess { posterResponse ->
  posterResponse.posters
}.onSuccess {
    posterStateFlow.value = data
}.onFailure {
    Log.e("sequential", message())
}
```