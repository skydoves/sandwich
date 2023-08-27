# ApiResponse With Coroutines

You can seamlessly integrate the `ApiResponse<*>` type into your Retrofit services by using the `suspend` keyword. Here's how to set it up:

First, build your `Retrofit` instance using the `ApiResponseCallAdapterFactory` call adapter factory:

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
    .build()
```

Next, define your service interface with the `suspend` keyword and `ApiResponse<*>` as the response type:

```kotlin
interface MyApiService {

  @GET("DisneyPosters.json")
  suspend fun fetchData(): ApiResponse<List<Poster>>
}
```

Lastly, execute the defined service to receive the `ApiResponse`:

```kotlin
val apiService = retrofit.create(MyApiService::class.java)
val response: ApiResponse<List<Poster>> = apiService.fetchData()
response.onSuccess {
    // handles the success case when the API request gets a successful response.
    mutableStateFlow.value = data
  }.onError {
    // handles error cases when the API request gets an error response.
  }.onException {
    // handles exceptional cases when the API request gets an exception response.
}
```

By following these steps, you can easily utilize the `ApiResponse` type in your Retrofit services. If you're interested in injecting your own coroutine scope or performing unit tests with a test coroutine scope, you can refer to the [Injecting a custom CoroutineScope and Unit Tests](https://github.com/skydoves/sandwich#injecting-a-custom-coroutinescope-and-unit-tests) section for more details.

> **Note**: If you're interested in injecting your own coroutine scope and unit testing with a test coroutine scope, check out the [Injecting a custom CoroutineScope and Unit Tests](https://github.com/skydoves/sandwich#injecting-a-custom-coroutinescope-and-unit-tests).

## ApiResponse Extensions With Coroutines

With the `ApiResponse` type, you can leverage coroutines extensions to handle responses seamlessly within coroutine scopes. These extensions provide a convenient way to process different response types. Here's how you can use them:

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

## Injecting a Custom CoroutineScope and Unit Tests

You can inject your own custom `CoroutineScope` into Sandwich's internal execution by setting it on the `ApiResponseCallAdapterFactory`:

```kotlin
.addCallAdapterFactory(ApiResponseCallAdapterFactory.create(
  coroutineScope = `Your Coroutine Scope`
))
```

To apply your custom coroutine scope globally for the `ApiResponseCallAdapterFactory`, set your scope on `SandwichInitializer`:

```kotlin
SandwichInitializer.sandwichScope = `Your Coroutine Scope`
```

Additionally, you can inject a test coroutine scope into the `ApiResponseCallAdapterFactory` for your unit test cases:

```kotlin
val testScope = TestScope(coroutinesRule.testDispatcher)
val retrofit = Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(MoshiConverterFactory.create())
      .addCallAdapterFactory(ApiResponseCallAdapterFactory.create(testScope))
      .build()
```

By following these guidelines, you can effectively manage coroutine scopes within Sandwich, making it suitable for a variety of use cases and ensuring robustness in unit testing scenarios.