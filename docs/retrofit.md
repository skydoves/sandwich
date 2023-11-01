# Retrofit Integration

Sandwich provides seamless ways to integrate the `ApiResponse<*>` type into your Retrofit services with [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html).

To utilize these Retrofit supports, simply add the following dependency:

<img src="https://user-images.githubusercontent.com/24237865/103460609-f18ee000-4d5a-11eb-81e2-17696e3a5804.png" width="774" height="224" alt="maven"/>

=== "Groovy"

    ```Groovy
    dependencies {
        implementation "com.github.skydoves:sandwich-retrofit:$version"
    }
    ```

=== "KTS"

    ```kotlin
    dependencies {
        implementation("com.github.skydoves:sandwich-retrofit:$version")
    }
    ```

## ApiResponseCallAdapterFactory

First, build your `Retrofit` instance with the `ApiResponseCallAdapterFactory` call adapter factory:

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

## ApiResponse Extensions for Retrofit

The *sandwich-retrofit* package provides valuable property extensions for `ApiResponse`.

### ApiResponse.Success

This indicates a successful network request. From the `ApiResponse.Success`, you can retrieve the response's body data as well as supplementary details such as `StatusCode`, `Headers`, and more.

```kotlin
val data: List<Poster> = response.data // or response.body 
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
val raw: okhttp3.Response = response.raw
```

### ApiResponse.Failure.Error

This denotes a failed network request, typically due to bad requests or internal server errors. You can access error messages and additional information like `StatusCode`, `Headers`, and more from the `ApiResponse.Failure.Error`.

```kotlin
val message: String = response.message()
val errorBody: ResponseBody? = response.errorBody
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
val raw: okhttp3.Response = response.raw
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

# Serialization for Retrofit

Sandwich facilitates the deserialization of your Retrofit response's error body into your customized error class, utilizing [Kotlin's Serialization](https://kotlinlang.org/docs/serialization.html).

!!! note

    To learn more about configuring the plugin and its dependency, refer to [Kotlin's Serialization documentation](https://kotlinlang.org/docs/serialization.html).

[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/sandwich.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22sandwich%22)

Add the dependency below to your **module**'s `build.gradle` file:

=== "Groovy"

    ```Groovy
    dependencies {
        implementation "com.github.skydoves:sandwich-retrofit-serialization:$version"
    }
    ```

=== "KTS"

    ```kotlin
    dependencies {
        implementation("com.github.skydoves:sandwich-retrofit-serialization:$version")
    }
    ```

## Error Body Deserialization

To deserialize your error body, utilize the `deserializeErrorBody` extension along with your custom error class. Begin by defining your custom error class adhering to the formats of your RESTful API, as shown below:

```kotlin
@Serializable
data class ErrorMessage(
    val code: Int,
    val message: String
)
```

Subsequently, retrieve the error class result from the `ApiResponse` instance using the `deserializeErrorBody` extension, as demonstrated in the example below:

```kotlin
val apiResponse = pokemonService.fetchPokemonList()
val errorModel: ErrorMessage? = apiResponse.deserializeErrorBody<String, ErrorMessage>()
```

Alternatively, you can directly obtain the deserialized error response through the `onErrorDeserialize` extension, as depicted here:

```kotlin
val apiResponse = mainRepository.fetchPosters()
apiResponse.onErrorDeserialize<List<Poster>, ErrorMessage> { errorMessage ->
  // Handle the error message
}
```
