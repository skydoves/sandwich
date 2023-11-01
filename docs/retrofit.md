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