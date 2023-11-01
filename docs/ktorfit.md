# ApiResponse With Ktor

Sandwich offers seamless extensions of `ApiResponse` for [Ktorfit](https://github.com/Foso/Ktorfit) by utilizing Sandwich's converter factory. Basically, Ktorfit was built top of the [Ktor](https://github.com/ktorio/ktor), so you can use [Ktor extensions](ktor.md) as well.

To utilize these Ktorfit supports, simply add the following dependency:

<img src="https://user-images.githubusercontent.com/24237865/103460609-f18ee000-4d5a-11eb-81e2-17696e3a5804.png" width="774" height="224" alt="maven"/>

=== "Groovy"

    ```Groovy
    dependencies {
        implementation "com.github.skydoves:sandwich-ktorfit:$version"
    }
    ```

=== "KTS"

    ```kotlin
    dependencies {
        implementation("com.github.skydoves:sandwich-ktorfit:$version")
    }
    ```

## ApiResponseConverterFactory

First, build your `Ktorfit` instance with the `ApiResponseConverterFactory` call adapter factory:

```kotlin
val retrofit = Ktorfit.Builder()
    .baseUrl(BASE_URL)
    .addCallAdapterFactory(ApiResponseConverterFactory.create())
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
val apiService = ktorfit.create<MyApiService>()
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

## ApiResponse from the HttpClient

Basically, Ktorfit is built on top of Ktor, so you can leverage all `ApiResponse` extensions as described in the [Ktor documentation](ktor.md).