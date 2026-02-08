# ApiResponse

`ApiResponse` serves as an interface designed to create consistent responses from API or I/O calls, such as network, database, or whatever. It offers convenient extensions to manage your payloads, encompassing both body data and exceptional scenarios. `ApiResponse` encompasses three distinct types: **Success**, **Failure.Error**, and **Failure.Exception**.

### ApiResponse.Success

This represents a successful response from API or I/O tasks. You can create an instance of [ApiResponse.Success] by giving the generic type and data.   

```kotlin
val apiResponse = ApiResponse.Success(data = myData)
val data = apiResponse.data
```

Depending on your model designs, you can also utilize `tag` property. The `tag` is an additional value that can be held to distinguish the origin of the [data] or to facilitate post-processing of successful data.

```kotlin
val apiResponse = ApiResponse.Success(data = myData, tag = myTag)
val tag = apiResponse.tag
```

### ApiResponse.Failure.Exception 

This signals a failed tasks captured by unexpected exceptions during API request creation or response processing on the client side, such as a network connection failure. You can obtain exception details from the `ApiResponse.Failure.Exception`.

```kotlin
val apiResponse = ApiResponse.Failure.Exception(exception = HttpTimeoutException())
val exception = apiResponse.exception
val message = apiResponse.message
```

### ApiResponse.Failure.Error

This denotes a failed API or I/O request, typically due to bad requests or internal server errors. You can additionally put an error payload that can contain detailed error information.

```kotlin
val apiResponse = ApiResponse.Failure.Error(payload = errorBody)
val payload = apiResponse.payload
```

You can also define custom error responses that extend `ApiResponse.Failure.Error` or `ApiResponse.Failure.Exception`, as demonstrated in the example below:

```kotlin
data object LimitedRequest : ApiResponse.Failure.Error(
  payload = "your request is limited",
)

data object WrongArgument : ApiResponse.Failure.Error(
  payload = "wrong argument",
)

data object HttpException : ApiResponse.Failure.Exception(
  throwable = RuntimeException("http exception")
)
```

The custom error response is very useful when you want to explicitly define and handle error responses, especially when working with map extensions.

```kotlin
val apiResponse = service.fetchMovieList()
apiResponse.onSuccess {
    // ..
}.flatMap {
  // if the ApiResponse is Failure.Error and contains error body, then maps it to a custom error response.  
  if (this is ApiResponse.Failure.Error) {
    val errorBody = (payload as? Response)?.body?.string()
    if (errorBody != null) {
      val errorMessage: ErrorMessage = Json.decodeFromString(errorBody)
      when (errorMessage.code) {
        10000 -> LimitedRequest
        10001 -> WrongArgument
      }
    }
  }
  this
}
```

Then you can handle the errors based on your custom message in other layers:

```kotlin
val apiResponse = repository.fetchMovieList()
apiResponse.onError {
  when (this) {
    LimitedRequest -> // update your UI
    WrongArgument -> // update your UI
  }
}
```

You might not want to use the `flatMap` extension for all API requests. If you aim to standardize custom error types across all API requests, you can explore the [Global Failure Mapper](https://skydoves.github.io/sandwich/mapper/#global-failure-mapper).

### Creation of ApiResponse

Sandwich provides convenient ways to create an `ApiResponse` using functions such as `ApiResponse.of` or `apiResponseOf`, as shown below:

```kotlin
val apiResponse = ApiResponse.of { service.request() }
val apiResponse = apiResponseOf { service.request() }
```

If you need to run suspend functions inside the lambda, you can use `ApiResponse.suspendOf` or `suspendApiResponseOf` instead:

```kotlin
val apiResponse = ApiResponse.suspendOf { service.request() }
val apiResponse = suspendApiResponseOf { service.request() }
```

!!! note

    If you intend to utilize the global operator or global ApiResponse mapper in Sandwich, you should create an `ApiResponse` using the `ApiResponse.of` or `ApiResponse.suspendOf` method to ensure the application of these global functions. If you're using `ApiResponseFailureSuspendMapper` or `ApiResponseSuspendOperator` (common with Ktor/Ktorfit), use `ApiResponse.suspendOf` to ensure suspend mappers and operators are properly awaited.

## ApiResponse Extensions

You can effectively handle `ApiResponse` using the following extensions:

- **onSuccess**: Executes when the `ApiResponse` is of type `ApiResponse.Success`. Within this scope, you can directly access the body data.
- **onError**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Error`. You can access `messageOrNull` and `payload` here.
- **onException**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Exception`. You can access `messageOrNull` and `exception` here.
- **onFailure**: Executes when the `ApiResponse` is either `ApiResponse.Failure.Error` or `ApiResponse.Failure.Exception`. You can access `messageOrNull` here.

Each scope operates according to its corresponding `ApiResponse` type:

```kotlin
val response = disneyService.fetchDisneyPosterList()
response.onSuccess {
    // this scope will be executed if the request successful.
    // handle the success case
  }.onError {
    // this scope will be executed when the request failed with errors.
    // handle the error case
  }.onException {
   // this scope will be executed when the request failed with exceptions.
   // handle the exception case
  }
```

If you don't want to specify each failure case, you can simplify it by using the `onFailure` extension:

```kotlin
val response = disneyService.fetchDisneyPosterList()
response.onSuccess {
    // this scope will be executed if the request successful.
    // handle the success case
  }.onFailure {
      
  }
```

## ApiResponse Extensions With Coroutines

With the `ApiResponse` type, you can leverage [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) extensions to handle responses seamlessly within coroutine scopes. These extensions provide a convenient way to process different response types. Here's how you can use them:

- **suspendOnSuccess**: This extension runs if the `ApiResponse` is of type `ApiResponse.Success`. You can access the body data directly within this scope.

- **suspendOnError**: This extension is executed if the `ApiResponse` is of type `ApiResponse.Failure.Error`. You can access the error message and the error body in this scope.

- **suspendOnException**: If the `ApiResponse` is of type `ApiResponse.Failure.Exception`, this extension is triggered. You can access the exception message in this scope.

- **suspendOnFailure**: This extension is executed if the `ApiResponse` is either `ApiResponse.Failure.Error` or `ApiResponse.Failure.Exception`. You can access the error message in this scope.

Each extension scope operates based on the corresponding `ApiResponse` type. By utilizing these extensions, you can handle responses effectively within different coroutine contexts.

```kotlin
flow {
  val response = disneyService.fetchDisneyPosterList()
  response.suspendOnSuccess {
    posterDao.insertPosterList(data) // insertPosterList(data) is a suspend function.
    emit(data)
  }.suspendOnError {
    // handles error cases
  }.suspendOnException {
    // handles exceptional cases
  }
}.flowOn(Dispatchers.IO)
```

## Flow

Sandwich offers some useful extensions to transform your `ApiResponse` into a [Flow](https://kotlinlang.org/docs/flow.html) by using the `toFlow` extension:

```kotlin
val flow = disneyService.fetchDisneyPosterList()
  .onError {
    // handles error cases when the API request gets an error response.
  }.onException {
    // handles exceptional cases when the API request gets an exception response.
  }.toFlow() // returns a coroutines flow
  .flowOn(Dispatchers.IO)
```

If you want to transform the original data and work with a `Flow` containing the transformed data, you can do so as shown in the examples below:

```kotlin
val response = pokedexClient.fetchPokemonList(page = page)
response.toFlow { pokemons ->
  pokemons.forEach { pokemon -> pokemon.page = page }
  pokemonDao.insertPokemonList(pokemons)
  pokemonDao.getAllPokemonList(page)
}.flowOn(Dispatchers.IO)
```

## Recovery

Sandwich provides recovery extensions to transform a failed `ApiResponse` back into a successful one with fallback data.

### recover

Returns an `ApiResponse.Success` with the fallback value if the response is a failure, otherwise returns the original success:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .recover(emptyList()) // Returns empty list if the request fails

// With a lambda for lazy evaluation
val response = disneyService.fetchDisneyPosterList()
  .recover { cachedPosters }
```

### recoverWith

Recovers the failure by executing an alternative `ApiResponse`:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .recoverWith { failure ->
    // Try an alternative data source on failure
    localDatabase.fetchCachedPosters()
  }
```

For coroutines, use `suspendRecover` and `suspendRecoverWith`:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .suspendRecoverWith { failure ->
    backupService.fetchPosters() // suspend function
  }
```

## Validation

Validation extensions allow you to validate success data and convert it to a failure if the validation fails.

### validate

Validates the success data with a predicate. If the predicate returns false, the response is converted to `ApiResponse.Failure.Error`:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .validate(
    predicate = { it.isNotEmpty() },
    errorMessage = { "Poster list cannot be empty" }
  )
```

### requireNotNull

Requires a non-null value from the success data. If the selected value is null, the response is converted to `ApiResponse.Failure.Error`:

```kotlin
val response = userService.fetchUser()
  .requireNotNull(
    selector = { it.profileImage },
    errorMessage = { "Profile image is required" }
  )
```

For coroutines, use `suspendValidate` and `suspendRequireNotNull`:

```kotlin
val response = userService.fetchUser()
  .suspendValidate { user ->
    userValidator.isValid(user) // suspend function
  }
```

## Filter

Filter extensions allow you to filter items in list data within an `ApiResponse`.

### filter

Filters the items in the success data list, keeping only items that match the predicate:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .filter { poster -> poster.isActive }
```

### filterNot

Filters the items in the success data list, excluding items that match the predicate:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .filterNot { poster -> poster.isDeprecated }
```

For coroutines, use `suspendFilter` and `suspendFilterNot`:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .suspendFilter { poster ->
    posterValidator.isValid(poster) // suspend function
  }
```

## Zip / Combine

Zip extensions allow you to combine multiple `ApiResponse` instances into a single response.

### zip

Combines two `ApiResponse` instances. If both are successful, the transform function is applied. If either is a failure, the first failure is returned:

```kotlin
val usersResponse = userService.fetchUsers()
val postersResponse = disneyService.fetchPosters()

val combined = usersResponse.zip(postersResponse) { users, posters ->
  HomeData(users = users, posters = posters)
}

// Or combine into a Pair
val paired = usersResponse.zip(postersResponse) // Returns ApiResponse<Pair<Users, Posters>>
```

### zip3

Combines three `ApiResponse` instances:

```kotlin
val response1 = service.fetchUsers()
val response2 = service.fetchPosters()
val response3 = service.fetchSettings()

val combined = response1.zip3(response2, response3) { users, posters, settings ->
  AppData(users = users, posters = posters, settings = settings)
}
```

For coroutines, use `suspendZip` and `suspendZip3`:

```kotlin
val combined = response1.suspendZip(response2) { data1, data2 ->
  processData(data1, data2) // suspend function
}
```

## Peek / Tap

Peek extensions allow you to observe the `ApiResponse` without modifying it. This is useful for logging, analytics, or side effects.

### peek

Performs an action on the `ApiResponse` regardless of its type:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .peek { response ->
    logger.log("Response received: $response")
  }
```

### peekSuccess

Performs an action only if the response is successful:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .peekSuccess { posters ->
    analytics.trackPostersLoaded(posters.size)
  }
```

### peekFailure

Performs an action only if the response is a failure (either error or exception):

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .peekFailure { failure ->
    logger.error("Request failed: ${failure.message()}")
  }
```

### peekError

Performs an action only if the response is `ApiResponse.Failure.Error`:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .peekError { error ->
    errorTracker.trackApiError(error.statusCode)
  }
```

### peekException

Performs an action only if the response is `ApiResponse.Failure.Exception`:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .peekException { exception ->
    crashReporter.recordException(exception.throwable)
  }
```

For coroutines, use `suspendPeek`, `suspendPeekSuccess`, `suspendPeekFailure`, `suspendPeekError`, and `suspendPeekException`:

```kotlin
val response = disneyService.fetchDisneyPosterList()
  .suspendPeekSuccess { posters ->
    cache.savePosters(posters) // suspend function
  }
```