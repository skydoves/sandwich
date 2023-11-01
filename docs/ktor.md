# ApiResponse With Ktor

Sandwich offers seamless extensions of `ApiResponse` for [Ktor](https://github.com/ktorio/ktor). You can obtain `ApiResponse` from the `HttpClient` by utilizing the `_ApiResponse` extensions:

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