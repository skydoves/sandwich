# Map

Sandwich provides versatile mapping extensions for `ApiResponse`.

### mapSuccess and suspendMapSuccess

If the `ApiResponse` is of type `ApiResponse.Success`, this function maps a `T` type to a `V` type within the `ApiResponse`.

The provided example below illustrates the utilization of the `mapSuccess` function to map the `ApiResponse<UserAuthResponse>` type to `ApiResponse<LoginInfo>`.

```kotlin
class LoginRepositoryImpl {
    override fun requestToken(
        authProvider: String,
        authIdentifier: String,
        email: String,
    ): Flow<ApiResponse<LoginInfo>> = flow {
        val result = authService.requestToken(
            UserRequest(
                authProvider = authProvider,
                authIdentifier = authIdentifier,
                email = email,
            ),
        ).mapSuccess { LoginInfo(user = user, token = token) }
        emit(result)
    }.flowOn(ioDispatcher)
}
```

### mapFailure and suspendMapFailure

In case the `ApiResponse` is of type `ApiResponse.Failure`, this operation maps a `T` type from the `ApiResponse` to a `V` type.

It's useful when you need to manipulate the error response deliberately.

```kotlin
val apiResponse2 = apiResponse1.mapFailure { responseBody ->
      "error body: ${responseBody?.string()}".toResponseBody()
    }
```

## Mapper

Mappers are especially useful when you need to transform the `ApiResponse.Success` or `ApiResponse.Failure.Error` into your custom model within the extension scopes of `ApiResponse`.

### ApiSuccessModelMapper

You can map the `ApiResponse.Success` model to your custom model using the `SuccessPosterMapper<T, R>` and the `map` extension as demonstrated below:

```kotlin
object SuccessPosterMapper : ApiSuccessModelMapper<List<Poster>, Poster?> {

  override fun map(apiErrorResponse: ApiResponse.Success<List<Poster>>): Poster? {
    return apiErrorResponse.data.first()
  }
}

// Maps the success response data.
val poster: Poster? = map(SuccessPosterMapper)
```

You can also use the `map` extension with a lambda expression as shown below:

```kotlin
// Maps the success response data using a lambda.
map(SuccessPosterMapper) { poster ->
  emit(poster) // You can use the `this` keyword instead of "poster".
}
```

If you want to receive transformed body data within the scope, you can utilize the mapper as a parameter with the `onSuccess` or `suspendOnSuccess` extensions, as illustrated below:

```kotlin
apiResponse.suspendOnSuccess(SuccessPosterMapper) {
    val poster = this
}
```

### ApiErrorModelMapper

You can utilize mappers to convert the `ApiResponse.Failure.Error` model to your custom error model using the `ApiErrorModelMapper<T>` and the `map` extension, as demonstrated in the examples below:

```kotlin
// Define your custom error model.
data class ErrorEnvelope(
  val code: Int,
  val message: String
)

// Create a mapper for error responses.
// Within the `map` function, construct an instance of your custom model using the information from `ApiResponse.Failure.Error`.
object ErrorEnvelopeMapper : ApiErrorModelMapper<ErrorEnvelope> {

  override fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): ErrorEnvelope {
    return ErrorEnvelope(apiErrorResponse.statusCode.code, apiErrorResponse.message())
  }
}

// Apply the mapper to an error response.
response.onError {
  // Use the mapper to convert ApiResponse.Failure.Error to your custom error model.
  map(ErrorEnvelopeMapper) {
     val code = this.code
     val message = this.message
  }
}
```

If you intend to obtain transformed data within the scope, you can use the mapper as a parameter with the `onError` or `suspendOnError` extensions, as shown in the examples below:

```kotlin
apiResponse.suspendOnError(ErrorEnvelopeMapper) {
    val message = this.message
}
```