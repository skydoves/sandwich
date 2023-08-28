# Retrieving

Sandwich provides effortless methods to directly extract the encapsulated body data from the `ApiResponse`. You can take advantage of the following functionalities:

## getOrNull
Returns the encapsulated data if this instance represents `ApiResponse.Success` or returns null if this is failed.

```kotlin
val data: List<Poster>? = disneyService.fetchDisneyPosterList().getOrNull()
```

## getOrElse
Returns the encapsulated data if this instance represents `ApiResponse.Success` or returns a default value if this is failed.

```kotlin
val data: List<Poster> = disneyService.fetchDisneyPosterList().getOrElse(emptyList())
```

## getOrThrow
Returns the encapsulated data if this instance represents `ApiResponse.Success` or throws the encapsulated `Throwable` exception if this is failed.

```kotlin
try {
  val data: List<Poster> = disneyService.fetchDisneyPosterList().getOrThrow()
} catch (e: Exception) {
  e.printStackTrace()
}
```