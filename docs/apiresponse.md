# ApiResponse

`ApiResponse` serves as an interface designed to create consistent responses from [Retrofit](https://github.com/square/retrofit) calls. It offers convenient extensions to manage network payloads, encompassing both body data and exceptional scenarios. `ApiResponse` encompasses three distinct types: **Success**, **Failure.Error**, and **Failure.Exception**.

### ApiResponse.Success
This indicates a successful network request. From the `ApiResponse.Success`, you can retrieve the response's body data as well as supplementary details such as `StatusCode`, `Headers`, and more.

```kotlin
val data: List<Poster>? = response.data
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
```

### ApiResponse.Failure.Error
This denotes a failed network request, typically due to bad requests or internal server errors. You can access error messages and additional information like `StatusCode`, `Headers`, and more from the `ApiResponse.Failure.Error`.

```kotlin
val message: String = response.message()
val errorBody: ResponseBody? = response.errorBody
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
```

### ApiResponse.Failure.Exception 
This signals a failed network request triggered by unexpected exceptions during request creation or response processing on the client side, such as a network connection failure. You can obtain exception details from the `ApiResponse.Failure.Exception`.


## Getting ApiResponse From Retrofit Call

To obtain an instance of `ApiResponse`, you can acquire it from the Retrofit's [Call](https://square.github.io/retrofit/2.x/retrofit/retrofit2/Call.html) using the `request` scope extension. Here is an example that demonstrates how to obtain an `ApiResponse` from a `Call` instance:

```kotlin
interface DisneyService {
  @GET("/")
  fun fetchDisneyPosterList(): Call<List<Poster>>
}

val disneyService = retrofit.create(DisneyService::class.java)
// fetches a model list from the network and getting [ApiResponse] asynchronously.
disneyService.fetchDisneyPosterList().request { response ->
    when (response) {
        // handles the success case when the API request gets a successful response.
        is ApiResponse.Success -> {
          posterDao.insertPosterList(response.data)
          stateFlow.value = response.data
        }
        // handles error cases when the API request gets an error response.
        // e.g., internal server error.
        is ApiResponse.Failure.Error -> {
          // stub error case
          Timber.d(message())

          // handles error cases depending on the status code.
          when (statusCode) {
            StatusCode.InternalServerError -> toastLiveData.postValue("InternalServerError")
            StatusCode.BadGateway -> toastLiveData.postValue("BadGateway")
            else -> toastLiveData.postValue("$statusCode(${statusCode.code}): ${message()}")
          }
        }
        // handles exceptional cases when the API request gets an exception response.
        // e.g., network connection error, timeout.
        is ApiResponse.Failure.Exception -> {
          // stub exception case
        }
    }
}
```

## ApiResponse Extensions
You can effectively manage `ApiResponse` using the following extensions:

- **onSuccess**: Executes when the `ApiResponse` is of type `ApiResponse.Success`. Within this scope, you can directly access the body data.
- **onError**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Error`. Here, you can access the `message()` and `errorBody`.
- **onException**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Exception`. You can access the `message()` here.
- **onFailure**: Executes when the `ApiResponse` is either `ApiResponse.Failure.Error` or `ApiResponse.Failure.Exception`. You can access the `message()` here.

Each scope operates according to its corresponding `ApiResponse` type:

```kotlin
disneyService.fetchDisneyPosterList().request { response ->
    response.onSuccess {
     // this scope will be executed if the request successful.
     // handle the success case
    }.onError {
      // this scope will be executed when the request failed with errors.
      // handle the error case
    }.onException {
     // this scope will be executed when the request failed with exceptions.
     // handle the exception case
    }
}
```