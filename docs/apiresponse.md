# ApiResponse

`ApiResponse` serves as an interface designed to create consistent responses from API or I/O calls, such as network, database, or whatever. It offers convenient extensions to manage your payloads, encompassing both body data and exceptional scenarios. `ApiResponse` encompasses three distinct types: **Success**, **Failure.Error**, **Failure.Exception**, and **Failure.Cause**.

### ApiResponse.Success

This represents a successful response from API or I/O tasks. You can create an instance of [ApiResponse.Success] by giving the generic type and data.   

```kotlin
val apiResponse = Apiresponse.Success(data = myData)
val data = apiResponse.data
```

Depending on your model designs, you can also utilize `tag` property. The `tag` is an additional value that can be held to distinguish the origin of the [data] or to facilitate post-processing of successful data.

```kotlin
val apiResponse = Apiresponse.Success(data = myData, tag = myTag)
val tag = apiResponse.tag
```

### ApiResponse.Failure.Error

This denotes a failed API or I/O request, typically due to bad requests or internal server errors. You can additionally put an error payload that can contain detailed error information.

```kotlin
val apiResponse = ApiResponse.Failure.Error(payload = errorBody)
val payload = apiResponse.payload
```

### ApiResponse.Failure.Exception 

This signals a failed tasks captured by unexpected exceptions during API request creation or response processing on the client side, such as a network connection failure. You can obtain exception details from the `ApiResponse.Failure.Exception`.

```kotlin
val apiResponse = Apiresponse.Failure.Exception(exception = HttpTimeoutException())
val exception = apiResponse.exception
val message = apiResponse.message
```

### ApiResponse.Failure.Cause

This denotes a custom failure response that can be extended using custom classes and objects.

```kotlin
object LimitedRequest : ApiResponse.Failure.Cause() {
  override val payload: Any = "name is wrong"
}

object WrongArgument : ApiResponse.Failure.Cause() {
  override val payload: Any = "wrong argument"
}
```

This custom failure response is very useful when you want to explicitly define and handle error responses, especially when working with mappers.

```kotlin
val apiResponse = service.fetchMovieList()
apiResponse.onSuccess {
    // ..
}.flatMap {
  // if the ApiResponse is Failure.Error and contains error body, then maps it to a custom failure response.  
  if (this is ApiResponse.Failure.Error) {
    val errorBody = (payload as? Response)?.body?.string()
    if (errorBody != null) {
      val errorMessage: ErrorMessage = Json.decodeFromString(errorBody)
      when (errorMessage.code) {
        10000 -> LimitedRequest
        10001 -> WrongArgument
      }
    }
  }
  this
}
```

Then you can handle the errors based on your custom message in other layers:

```kotlin
val apiResponse = repository.fetchMovieList()
apiResponse.onCause {
  when (this) {
    LimitedRequest -> // update your UI
    WrongArgument -> // update your UI
  }
}
```

You might not want to use the `flatMap` extension for all API requests. If you aim to standardize custom error types across all API requests, you can explore the [ApiResponseMapper]().

### Creation of ApiResponse

Sandwich provides convenient ways to create an `ApiResponse` using functions such as `ApiResponse.of` or `apiResponseOf`, as shown below:

```kotlin
val apiResponse = ApiResponse.of { service.request() }
val apiResponse = apiResponseOf { service.request() }
```

If you need to run suspend functions inside the lambda, you can use `ApiResponse.suspendOf` or `suspendApiResponseOf` instead:

```kotlin
val apiResponse = ApiResponse.suspendOf { service.request() }
val apiResponse = suspendApiResponseOf { service.request() }
```

!!! note

    If you intend to utilize the global operator or global ApiResponse mapper in Sandwich, you should create an ApiResponse using the `ApiResponse.of` methods to ensure the application of these global functions.

## ApiResponse Extensions

You can effectively handling `ApiResponse` using the following extensions:

- **onSuccess**: Executes when the `ApiResponse` is of type `ApiResponse.Success`. Within this scope, you can directly access the body data.
- **onError**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Error`. Here, you can access the `messareOrNull` and `payload` here.
- **onException**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Exception`. You can access the `messareOrNull` and `exception` here.
- **onCause**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Cause`. You can access the `messareOrNull` and `payload` here.
- **onFailure**: Executes when the `ApiResponse` is either `ApiResponse.Failure.Error` or `ApiResponse.Failure.Exception`, or `ApiResponse.Failure.Cause`. You can access the `messareOrNull` here.

Each scope operates according to its corresponding `ApiResponse` type:

```kotlin
val response = disneyService.fetchDisneyPosterList()
response.onSuccess {
    // this scope will be executed if the request successful.
    // handle the success case
  }.onError {
    // this scope will be executed when the request failed with errors.
    // handle the error case
  }.onException {
   // this scope will be executed when the request failed with exceptions.
   // handle the exception case
  }.onCause {
    // this scope will be executed when the request failed with custom errors.
    // handle the custom exception cases
  }
```

If you don't want to specify each failure case, you can simplify it by using the `onFailure` extension:

```kotlin
val response = disneyService.fetchDisneyPosterList()
response.onSuccess {
    // this scope will be executed if the request successful.
    // handle the success case
  }.onFailure {
      
  }
```