# ApiResponse With Ktor

Sandwich offers seamless extensions of `ApiResponse` for [Ktor](https://github.com/ktorio/ktor). You can obtain `ApiResponse` from the `HttpClient` by utilizing the `_ApiResponse` extensions:

To utilize these Ktor supports, simply add the following dependency:

<img src="https://user-images.githubusercontent.com/24237865/103460609-f18ee000-4d5a-11eb-81e2-17696e3a5804.png" width="774" height="224" alt="maven"/>

=== "Groovy"

    ```Groovy
    dependencies {
        implementation "com.github.skydoves:sandwich-ktor:$version"
    }
    ```

=== "KTS"

    ```kotlin
    dependencies {
        implementation("com.github.skydoves:sandwich-ktor:$version")
    }
    ```

## ApiResponse from the HttpClient

```kotlin
val client = HttpClient { .. }

val apiResponse = client.requestApiResponse<PokemonResponse>(REQUEST_URL) 
val apiResponse = client.getApiResponse<PokemonResponse>(REQUEST_URL) 
val apiResponse = client.postApiResponse<PokemonResponse>(REQUEST_URL) 
val apiResponse = client.deleteApiResponse<PokemonResponse>(REQUEST_URL) 
val apiResponse = client.patchApiResponse<PokemonResponse>(REQUEST_URL) 
val apiResponse = client.headApiResponse<PokemonResponse>(REQUEST_URL) 
```

You can also utilize the `HttpRequestBuilder`:

```kotlin
val response = client.getApiResponse<PokemonResponse>("https://pokeapi.co/api/v2/pokemon") {
      contentType(ContentType.Application.Json)
    }
response.onSuccess {
    ..
}.onError {
    ..
}.onException {
    ..
}
```

## ApiResponse Extensions for Ktor

The *sandwich-ktor* package provides valuable property extensions for `ApiResponse`.

### ApiResponse.Success

This indicates a successful network request. From the `ApiResponse.Success`, you can retrieve the response's body data as well as supplementary details such as `StatusCode`, `Headers`, and more.

```kotlin
val data: List<Poster> = response.data // or response.body()
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
val httpResponse: HttpResponse = response.httpResponse
```

### ApiResponse.Failure.Error

This denotes a failed network request, typically due to bad requests or internal server errors. You can access error messages and additional information like `StatusCode`, `Headers`, and more from the `ApiResponse.Failure.Error`.

```kotlin
val bodyChannel: String = response.bodyChannel()
val bodyString: String = response.bodyString()
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
```
