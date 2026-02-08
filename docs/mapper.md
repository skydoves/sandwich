# Mapper

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

## Model Mapper

Mappers are especially useful when you need to transform the `ApiResponse.Success` or `ApiResponse.Failure.Error` into your custom model within the extension scopes of `ApiResponse`.

### ApiSuccessModelMapper

You can map the `ApiResponse.Success` model to your custom model using the `SuccessPosterMapper<T, R>` and the `map` extension as demonstrated below:

```kotlin
object SuccessPosterMapper : ApiSuccessModelMapper<List<Poster>, Poster?> {

  override fun map(apiSuccessResponse: ApiResponse.Success<List<Poster>>): Poster? {
    return apiSuccessResponse.data.first()
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

## Global Failure Mapper

You can map `ApiResponse.Failure` responses into your custom error response by using `flatMap` as described [ApiResponse.Failure.Error documentation](https://skydoves.github.io/sandwich/apiresponse/#apiresponsefailureerror).

Alternatively, Sandwich provides a robust solution for mapping responses, enabling you to transform all `ApiResponse.Failure` responses into your preferred `ApiResponse.Failure.Error` or `ApiResponse.Failure.Exception` types. This can be applied globally without the need for using `flatMap` extensions across all network and I/O requests or when creating `ApiResponse` instances.

You can implement this globally by using `SandwichInitializer.sandwichFailureMappers`. The example below shows how to map all your `ApiResponse.Failure.Error` to your custom `ApiResponse.Failure.Exception`.

```kotlin
data object UnKnownError : ApiResponse.Failure.Exception(
  throwable = RuntimeException("unknwon error")
)

data object LimitedRequest : ApiResponse.Failure.Exception(
  throwable = RuntimeException("your request is limited")
)

data object WrongArgument : ApiResponse.Failure.Exception(
  throwable = RuntimeException("wrong argument")
)

data object HttpException : ApiResponse.Failure.Exception(
  throwable = RuntimeException("http exception"),
)

SandwichInitializer.sandwichFailureMappers += listOf(
  object : ApiResponseFailureMapper {
    override fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
      if (apiResponse is ApiResponse.Failure.Error) {
        val errorBody = (apiResponse.payload as? okhttp3.Response)?.body?.string()
        if (errorBody != null) {
          val errorMessage: ErrorMessage = Json.decodeFromString(errorBody)
          when (errorMessage.code) {
            10000 -> LimitedRequest
            10001 -> WrongArgument
            10002 -> HttpException
            else -> UnKnownError
          }
        }
      }
      return apiResponse
    }
  },
)
```

Given the example above, which maps all `ApiResponse.Failure.Error` to your custom `ApiResponse.Failure.Exception` according to your preferences, you'll only need to focus on handling exceptional cases when dealing with your `ApiResponse`.

```kotlin
val apiResponse = service.fetchMovieList()
apiResponse.onSuccess {
  // ..
}.onException {
  when (this) {
    LimitedRequest -> // ..
    WrongArgument -> // ..
    HttpException -> // ..
    UnKnownError -> // ..
  }
}
```

### Suspend Global Failure Mapper (Ktor/Ktorfit)

If you're using **Ktor** or **Ktorfit**, parsing the error response body requires suspend functions (e.g., `bodyAsText()`). In this case, use `ApiResponseFailureSuspendMapper` instead of `ApiResponseFailureMapper`. The suspend mapper is properly awaited in suspend contexts, ensuring the mapped response is correctly returned to callers.

```kotlin
SandwichInitializer.sandwichFailureMappers += listOf(
  object : ApiResponseFailureSuspendMapper {
    override suspend fun map(apiResponse: ApiResponse.Failure<*>): ApiResponse.Failure<*> {
      if (apiResponse is ApiResponse.Failure.Error) {
        val errorBody = (apiResponse.payload as? HttpResponse)?.bodyAsText()
        if (errorBody != null) {
          val errorMessage: ErrorMessage = Json.decodeFromString(errorBody)
          return when (errorMessage.code) {
            10000 -> LimitedRequest
            10001 -> WrongArgument
            10002 -> HttpException
            else -> UnKnownError
          }
        }
      }
      return apiResponse
    }
  },
)
```

!!! note

    `ApiResponseFailureSuspendMapper` is designed for suspend contexts (Ktor, Ktorfit, and `suspendOf`). If you're using Retrofit with synchronous response handling, use `ApiResponseFailureMapper` instead.