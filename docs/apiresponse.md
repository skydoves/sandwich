# ApiResponse

`ApiResponse` serves as an interface designed to create consistent responses from API or I/O calls, such as network, database, or whatever. It offers convenient extensions to manage your payloads, encompassing both body data and exceptional scenarios. `ApiResponse` encompasses three distinct types: **Success**, **Failure.Error**, and **Failure.Exception**.

### ApiResponse.Success

This represents a successful response from API or I/O tasks. You can create an instance of [ApiResponse.Success] by giving the generic type and data.   

```kotlin
val apiResponse = Apiresponse.Success(data = myData)
val data = apiResponse.data
```

Depending on your model designs, you can also utilize `tag` property. The `tag` is an additional value that can be held to distinguish the origin of the [data] or to facilitate post-processing of successful data.

```kotlin
val apiResponse = Apiresponse.Success(data = myData, tag = myTag)
val tag = apiResponse.tag
```

### ApiResponse.Failure.Exception 

This signals a failed tasks captured by unexpected exceptions during API request creation or response processing on the client side, such as a network connection failure. You can obtain exception details from the `ApiResponse.Failure.Exception`.

```kotlin
val apiResponse = Apiresponse.Failure.Exception(exception = HttpTimeoutException())
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

You might not want to use the `flatMap` extension for all API requests. If you aim to standardize custom error types across all API requests, you can explore the [ApiResponseMapper](mapper.md).

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

    If you intend to utilize the global operator or global ApiResponse mapper in Sandwich, you should create an `ApiResponse` using the `ApiResponse.of` method to ensure the application of these global functions.

## ApiResponse Extensions

You can effectively handling `ApiResponse` using the following extensions:

- **onSuccess**: Executes when the `ApiResponse` is of type `ApiResponse.Success`. Within this scope, you can directly access the body data.
- **onError**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Error`. Here, you can access the `messareOrNull` and `payload` here.
- **onException**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Exception`. You can access the `messareOrNull` and `exception` here.
- **onFailure**: Executes when the `ApiResponse` is either `ApiResponse.Failure.Error` or `ApiResponse.Failure.Exception`. You can access the `messareOrNull` here.

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