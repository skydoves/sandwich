# Ktorfit Integration

Sandwich offers seamless extensions of `ApiResponse` for [Ktorfit](https://github.com/Foso/Ktorfit) by utilizing Sandwich's converter factory. Basically, Ktorfit was built top of the [Ktor](https://github.com/ktorio/ktor), so you can use [Ktor extensions](ktor.md) as well.

To utilize these Ktorfit supports, simply add the following dependency:

[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/sandwich.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22sandwich%22)

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

For Kotlin Multiplatform, add the dependency below to your module's `build.gradle.kts` file:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.github.skydoves:sandwich-ktorfit:$version")
        }
    }
}
```

## ApiResponseConverterFactory

First, build your `Ktorfit` instance with the `ApiResponseConverterFactory` call adapter factory:

```kotlin
val ktorfit = Ktorfit.Builder()
    .baseUrl(BASE_URL)
    .converterFactories(ApiResponseConverterFactory.create())
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
val apiService = ktorfit.createMyApiService()
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
