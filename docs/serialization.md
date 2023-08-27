# Serialization

This library facilitates the deserialization of your Retrofit response's error body into your customized error class, utilizing [Kotlin's Serialization](https://kotlinlang.org/docs/serialization.html).

!!! note

    To learn more about configuring the plugin and its dependency, refer to [Kotlin's Serialization documentation](https://kotlinlang.org/docs/serialization.html).

[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/sandwich.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22sandwich%22)

Add the dependency below to your **module**'s `build.gradle` file:

=== "Groovy"

    ```Groovy
    dependencies {
        implementation "com.github.skydoves:sandwich-serialization:$version"
    }
    ```

=== "KTS"

    ```kotlin
    dependencies {
        implementation("com.github.skydoves:sandwich-serialization:$version")
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
val errorModel: ErrorMessage? = apiResponse.deserializeErrorBody()
```

Alternatively, you can directly obtain the deserialized error response through the `onErrorDeserialize` extension, as depicted here:

```kotlin
val apiResponse = mainRepository.fetchPosters()
apiResponse.onErrorDeserialize<List<Poster>, ErrorMessage> { errorMessage ->
  // Handle the error message
}
```
