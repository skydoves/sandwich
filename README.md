
<h1 align="center">Sandwich</h1></br>

<p align="center"> 
🥪 A lightweight and standardized Android network response interface for handling successful data and error responses.
</p>
</br>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=16"><img alt="API" src="https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/skydoves/Sandwich/actions"><img alt="Build Status" src="https://github.com/skydoves/Sandwich/workflows/Android%20CI/badge.svg"/></a>
  <a href="https://skydoves.github.io/libraries/sandwich/javadoc/sandwich/com.skydoves.sandwich/index.html"><img alt="Javadoc" src="https://skydoves.github.io/badges/javadoc-sandwich.svg"/></a>
  <a href="https://proandroiddev.com/handling-network-http-response-of-success-data-and-failure-for-your-android-project-using-sandwich-36db824bd82d"><img alt="Medium" src="https://skydoves.github.io/badges/Story-Medium.svg"/></a>
  <a href="https://github.com/skydoves"><img alt="Profile" src="https://skydoves.github.io/badges/skydoves.svg"/></a>
</p>

<p align="center">
<img src="https://user-images.githubusercontent.com/24237865/104847167-deb30880-5921-11eb-8313-e634058b370b.png" width="783" height="406"/>
</p>

## Why Sandwich?
Sandwich was invented for constructing the standardized response interface from the network response. We can handle successful data, error response, and an exceptional case intuitively using useful extensions of the interface. So we don't need to design and implement wrapper classes like `Resource` or `Result`, and it helps to reduce our work time and makes focus on only business codes. Sandwich supports [handling error responses globally](https://github.com/skydoves/sandwich#global-operator), [Mapper](https://github.com/skydoves/sandwich#mapper), [Operator](https://github.com/skydoves/sandwich#operator), and great compatibilities like [toLiveData](https://github.com/skydoves/sandwich#tolivadata) or [toFlow](https://github.com/skydoves/sandwich#toflow). Also, we can implement great harmony with [coroutines](https://github.com/skydoves/sandwich#apiresponse-for-coroutines) and [flow](https://github.com/skydoves/sandwich#suspendonsuccess-suspendonerror-suspendonexception) in our projects using this library.

## Download
[![Download](https://api.bintray.com/packages/devmagician/maven/sandwich/images/download.svg)](https://bintray.com/devmagician/maven/sandwich/_latestVersion)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/sandwich.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22sandwich%22)
[![Jitpack](https://jitpack.io/v/skydoves/Sandwich.svg)](https://jitpack.io/#skydoves/Sandwich)

🥪 Sandwich has been downloaded in more than __50k__ Android projects all over the world! <br>

<img src="https://user-images.githubusercontent.com/24237865/103460609-f18ee000-4d5a-11eb-81e2-17696e3a5804.png" width="774" height="224"/>

### Gradle
Add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "com.github.skydoves:sandwich:1.0.8"
}
```

## Usecase
You can reference the good use cases of this library in the below repositories.
- [Pokedex](https://github.com/skydoves/pokedex) - 🗡️ Android Pokedex using Hilt, Motion, Coroutines, Flow, Jetpack (Room, ViewModel, LiveData) based on MVVM architecture.
- [DisneyMotions](https://github.com/skydoves/DisneyMotions) - 🦁 A Disney app using transformation motions based on MVVM (ViewModel, Coroutines, LiveData, Room, Repository, Koin) architecture.
- [MarvelHeroes](https://github.com/skydoves/marvelheroes) - ❤️ A sample Marvel heroes application based on MVVM (ViewModel, Coroutines, LiveData, Room, Repository, Koin)  architecture.
- [TheMovies2](https://github.com/skydoves/TheMovies2) - 🎬 A demo project using The Movie DB based on Kotlin MVVM architecture and material design & animations.

## Table of contents
- [ApiResponse](https://github.com/skydoves/sandwich#apiresponse)
- [onSuccess, onError, onException](https://github.com/skydoves/sandwich#apiresponse-extensions)
- [ApiResponse for coroutines](https://github.com/skydoves/sandwich#apiresponse-for-coroutines)
- [suspendOnSuccess. suspendOnError, suspendOnException](https://github.com/skydoves/sandwich#suspendonsuccess-suspendonerror-suspendonexception)
- [Mapper](https://github.com/skydoves/sandwich#mapper)
- [Operator](https://github.com/skydoves/sandwich#operator), [Operator for coroutines](https://github.com/skydoves/sandwich#operator-with-coroutines), [Global Operator](https://github.com/skydoves/sandwich#global-operator)
- [Merge](https://github.com/skydoves/sandwich#merge)
- [toLiveData](https://github.com/skydoves/sandwich#tolivadata). [toFlow](https://github.com/skydoves/sandwich#toflow)
- [ResponseDataSource](https://github.com/skydoves/sandwich#responsedatasource)

## Usage
### ApiResponse
`ApiResponse` is an interface for constructing standard responses from the response of the retrofit call. It provides useful extensions for handling successful data and error responses. We can get `ApiResponse` using the scope extension `request` from the `Call`. The below example is the basic of getting an `ApiResponse` from an instance of the `Call`.

```kotlin
interface DisneyService {
  @GET("/")
  fun fetchDisneyPosterList(): Call<List<Poster>>
}

val disneyService = retrofit.create(DisneyService::class.java)
// fetches a model list from the network and getting [ApiResponse] asynchronously.
disneyService.fetchDisneyPosterList().request { response ->
      when (response) {
        is ApiResponse.Success -> {
          // stub success case
          livedata.post(response.data)
        }
        // handles the case when the API request gets an error response.
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
        // handles the case when the API request gets an exception response.
        // e.g., network connection error.
        is ApiResponse.Failure.Exception -> {
          // stub exception case
        }
      }
    }
```
#### ApiResponse.Success
A standard Success response interface from Retrofit network responses.<br>
We can get the successful body data of the response, `StatusCode`, `Headers` and etc from the `ApiResponse.Success`.

```kotlin
val data: List<Poster>? = response.data
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
```

#### ApiResponse.Failure.Error
A standard failure response interface from Retrofit network responses.<br>
API communication conventions do not match or applications need to handle errors.
e.g., internal server error.

```kotlin
val errorBody: ResponseBody? = response.errorBody
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
```

#### ApiResponse.Failure.Exception 
An unexpected exception occurs while creating requests or processing an response in the client side. e.g., Network connection error.

### ApiResponse Extensions
We can handle response cases conveniently using extensions.

#### onSuccess, onError, onException
We can use these scope functions to the `ApiResponse`, we handle the response cases without using the if-else/when clause. <br>
Each scope will be executed or not depending on the type of the `ApiResponse`. (success, error, exception)
```kotlin
disneyService.fetchDisneyPosterList().request { response ->
    response.onSuccess {
     // this scope will be only executed if the request would successful.
     // handle the success case
    }.onError {
      // this scope will be only executed when the request would get errors.
      // handle the error case
    }.onException {
     // this scope will be only executed when the request would get exceptions.
     // handle the exception case
    }
  }
```

### ApiResponse for coroutines
We can use the `suspend` keyword in our Retrofit services and gets `ApiResponse<*>` as a response type.<br>
Build your Retrofit using the `CoroutinesResponseCallAdapterFactory` call adapter factory.
```kotlin
.addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
```
We should make normal service functions as suspension functions using the `suspend` keyword. And we can get the `ApiResponse<*>` as a response type. So we can get the `ApiResponse` from the Retrofit service call, and handle them right away using extensions.
```kotlin
interface DisneyCoroutinesService {

  @GET("DisneyPosters.json")
  suspend fun fetchDisneyPosterList(): ApiResponse<List<Poster>>
}
```
We can use like the below.
```kotlin
class MainCoroutinesViewModel constructor(disneyService: DisneyCoroutinesService) : ViewModel() {

  val posterListLiveData: MutableLiveData<List<Poster>>

  init {
     val response = disneyService.fetchDisneyPosterList()
     response.onSuccess {
       // handle the success case
       posterListLiveData.post(data)
      }.onError {
       // handle the error case
      }.onException {
       // handle the exception case
      }
    }
  }
}
```
#### suspendOnSuccess, suspendOnError, suspendOnException
We can use suspension extensions for invoking suspension related functions inside scopes. These extensions are not functionally different from the `onSuccess`, `onError`, and `onException` extensions. <br>
Generally, we can use this way on the [repository pattern](https://github.com/skydoves/Pokedex/blob/main/app/src/main/java/com/skydoves/pokedex/repository/MainRepository.kt).
```kotlin
flow {
  val response = disneyService.fetchDisneyPosterList()
  response.suspendOnSuccess {
    emit(data)
  }.suspendOnError {
    // stub error case
  }.suspendOnFailure {
    // stub exception case
  }
}.flowOn(Dispatchers.IO)
```

### Mapper
Mapper is useful when we want to transform the `ApiResponse.Success` or `ApiResponse.Failure.Error` to our custom model in our `ApiResponse` extension scopes.

#### ApiSuccessModelMapper
We can map the `ApiResponse.Success` model to our custom model using the `SuccessPosterMapper<T, R>` and `map` extension.
```kotlin
object SuccessPosterMapper : ApiSuccessModelMapper<List<Poster>, Poster?> {

  override fun map(apiErrorResponse: ApiResponse.Success<List<Poster>>): Poster? {
    return apiErrorResponse.data?.first()
  }
}

// Maps the success response data.
val poster: Poster? = map(SuccessPosterMapper)
```
We can use the `map` extension with a lambda.
```kotlin
// Maps the success response data using a lambda.
map(SuccessPosterMapper) { poster ->
  emit(poster) // we can use the `this` keyword instead of the poster.
}
```
If we want to get the transformed data from the start in the lambda, we can pass the mapper as a parameter for the `suspendOnSuccess`.
```kotlin
.suspendOnSuccess(SuccessPosterMapper) {
    val poster = this
}
```

#### ApiErrorModelMapper
We can map the `ApiResponse.Failure.Error` model to our custom error model using the `ApiErrorModelMapper<T>` and `map` extension.

```kotlin
// Create your custom error model.
data class ErrorEnvelope(
  val code: Int,
  val message: String
)

// An error response mapper.
// Create an instance of your custom model using the `ApiResponse.Failure.Error` in the `map`.
object ErrorEnvelopeMapper : ApiErrorModelMapper<ErrorEnvelope> {

  override fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): ErrorEnvelope {
    return ErrorEnvelope(apiErrorResponse.statusCode.code, apiErrorResponse.message())
  }
}

// Maps the error response.
response.onError {
  // Maps the ApiResponse.Failure.Error to a custom error model using the mapper.
  map(ErrorEnvelopeMapper) {
     val code = this.code
     val message = this.message
  }
}
```

### Operator
We can delegate the `onSuccess`, `onError`, `onException` using the `operator` extension and `ApiResponseOperator`. Operator is very useful if we want to handle `ApiResponse`s standardly or reduce the role of the `ViewModel` and `Repository`. Here is an example of standardized error and exception handing.

#### ViewModel
We can delegate and operate the `CommonResponseOperator` using the `operate` extension.
```kotlin
disneyService.fetchDisneyPosterList().operator(
      CommonResponseOperator(
        success = {
          data?.let { emit(it) }
          Timber.d("$success.data")
        },
        application = getApplication()
      )
    )
```

#### CommonResponseOperator
The `CommonResponseOperator` extends `ApiResponseOperator` with the `onSuccess`, `onError`, `onException` override methods. They will be executed depending on the type of the `ApiResponse`.
```kotlin
/** A common response operator for handling [ApiResponse]s regardless of its type. */
class CommonResponseOperator<T> constructor(
  private val success: suspend (ApiResponse.Success<T>) -> Unit,
  private val application: Application
) : ApiResponseOperator<T>() {

  // handle the case when the API request gets a success response.
  override fun onSuccess(apiResponse: ApiResponse.Success<T>) = success(apiResponse)

  // handle the case when the API request gets a error response.
  // e.g., internal server error.
  override fun onError(apiResponse: ApiResponse.Failure.Error<T>) {
    apiResponse.run {
      Timber.d(message())
      
      // map the ApiResponse.Failure.Error to a customized error model using the mapper.
      map(ErrorEnvelopeMapper) {
        Timber.d("[Code: $code]: $message")
      }
    }
  }

  // handle the case when the API request gets a exception response.
  // e.g., network connection error.
  override fun onException(apiResponse: ApiResponse.Failure.Exception<T>) {
    apiResponse.run {
      Timber.d(message())
      toast(message())
    }
  }
}
```

### Operator for coroutines
If we want to operate and delegate a suspension lambda to the operator, we can use the `suspendOperator` extension and `ApiResponseSuspendOperator` class.

#### ViewModel
We can use suspension functions like `emit` in the `success` lambda.
```kotlin
flow {
  disneyService.fetchDisneyPosterList().suspendOperator(
      CommonResponseOperator(
        success = {
          data?.let { emit(it) }
          Timber.d("$success.data")
        },
        application = getApplication()
      )
    )
}.flowOn(Dispatchers.IO)
```

#### CommonResponseOperator
The `CommonResponseOperator` extends `ApiResponseSuspendOperator` with suspend override methods.
```kotlin
class CommonResponseOperator<T> constructor(
  private val success: suspend (ApiResponse.Success<T>) -> Unit,
  private val application: Application
) : ApiResponseSuspendOperator<T>() {

  // handle the case when the API request gets a success response.
  override suspend fun onSuccess(apiResponse: ApiResponse.Success<T>) = success(apiResponse)

  // ... //
```

### Global operator
We can operate an operator globally all `ApiResponse`s in our application using the `SandwichInitializer`. So we don't need to create every instance of the Operators or use dependency injection for handling common operations. Here is an example of handling a global operator about the `ApiResponse.Failure.Error` and `ApiResponse.Failure.Exception`. In this example, We will handle `ApiResponse.Success` manually.

#### Application class
We can initialize the global operator on the `SandwichInitializer.sandwichOperator`. It is recommended to initialize it in the Application class.
```kotlin
class SandwichDemoApp : Application() {

  override fun onCreate() {
    super.onCreate()
    
    // We will handle only the error and exception cases, 
    // so we don't need to mind the generic type of the operator.
    SandwichInitializer.sandwichOperator = GlobalResponseOperator<Any>(this)

    // ... //
```

#### GlobalResponseOperator
The `GlobalResponseOperator` can extend any operator (`ApiResponseSuspendOperator` or `ApiResponseOperator`)
```kotlin
class GlobalResponseOperator<T> constructor(
  private val application: Application
) : ApiResponseSuspendOperator<T>() {

  // The body is empty, because we will handle the success case manually.
  override suspend fun onSuccess(apiResponse: ApiResponse.Success<T>) { }

  // handle the case when the API request gets a error response.
  // e.g., internal server error.
  override suspend fun onError(apiResponse: ApiResponse.Failure.Error<T>) {
    withContext(Dispatchers.Main) {
      apiResponse.run {
        Timber.d(message())

        // handling error based on status code.
        when (statusCode) {
          StatusCode.InternalServerError -> toast("InternalServerError")
          StatusCode.BadGateway -> toast("BadGateway")
          else -> toast("$statusCode(${statusCode.code}): ${message()}")
        }

        // map the ApiResponse.Failure.Error to a customized error model using the mapper.
        map(ErrorEnvelopeMapper) {
          Timber.d("[Code: $code]: $message")
        }
      }
    }
  }

  // handle the case when the API request gets a exception response.
  // e.g., network connection error.
  override suspend fun onException(apiResponse: ApiResponse.Failure.Exception<T>) {
    withContext(Dispatchers.Main) {
      apiResponse.run {
        Timber.d(message())
        toast(message())
      }
    }
  }

  private fun toast(message: String) {
    Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
  }
}
```

#### ViewModel
We don't need to use the `operator` expression. The global operator will be operated automatically, so we should handle only the  `ApiResponse.Success`.
```kotlin
flow {
  disneyService.fetchDisneyPosterList().
    suspendOnSuccess {
      data?.let { emit(it) }
    }
}.flowOn(Dispatchers.IO).asLiveData()
```

### Merge
We can merge multiple `ApiResponse`s as one `ApiResponse` depending on the policy.<br>
The below example is merging three `ApiResponse` as one if every three `ApiResponse`s are successful.

```kotlin
disneyService.fetchDisneyPosterList(page = 0).merge(
   disneyService.fetchDisneyPosterList(page = 1),
   disneyService.fetchDisneyPosterList(page = 2),
   mergePolicy = ApiResponseMergePolicy.PREFERRED_FAILURE
).onSuccess { 
  // handle response data..
}.onError { 
  // handle error..
}
```

#### ApiResponseMergePolicy
`ApiResponseMergePolicy` is a policy for merging response data depend on the success or not.
- IGNORE_FAILURE: Regardless of the merging order, ignores failure responses in the responses.
- PREFERRED_FAILURE (default): Regardless of the merging order, prefers failure responses in the responses.

### toLivaData
We can get a `LivaData` which contains successful data if the response is a `ApiResponse.Success`. If our goal is only getting a LiveData that holds successful data, we can emit the `onSuccess` extension.
```kotlin
posterListLiveData = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
  emitSource(
    disneyService.fetchDisneyPosterList()
     .onError {
      // handle the error case
     }.onException {
      // handle the exception case
     }.toLiveData()) // returns an observable LiveData
}
```
If we want to transform the original data and get a `LiveData` which contains transformed data using successful data if the response is a `ApiResponse.Success`.
```kotlin
posterListLiveData = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
  emitSource(
   disneyService.fetchDisneyPosterList()
    .onError {
      // handle the error case
    }.onException {
      // handle the exception case
    }.toLiveData {
      this.onEach { poster -> poster.date = SystemClock.currentThreadTimeMillis() }
    }) // returns an observable LiveData
    }
```
### toFlow
We can get a `Flow` which emits successful data if the response is a `ApiResponse.Success` and the data is not null.
```kotlin
disneyService.fetchDisneyPosterList()
  .onError {
    // stub error case
  }.onException {
    // stub exception case
  }.toFlow() // returns a coroutines flow
  .flowOn(Dispatchers.IO)
```
If we want tranform the original data and get a `flow` which contains transformed data using successful data if the response is a `ApiResponse.Success` and the data is not null.
```kotlin
val response = pokedexClient.fetchPokemonList(page = page)
response.toFlow { pokemons ->
  pokemons.forEach { pokemon -> pokemon.page = page }
  pokemonDao.insertPokemonList(pokemons)
  pokemonDao.getAllPokemonList(page)
}.flowOn(Dispatchers.IO)
```

### ResponseDataSource
ResponseDataSource is an implementation of the `DataSource` interface. <br>
 * Asynchronously send requests.
 * A temporarily response data holder from the REST API call for caching data on memory.
 * Observable for every response.
 * Retry fetching data when the request gets failure.
 * Concat another `DataSource` and request sequentially.
 * Disposable of executing works.

 #### Combine
Combine a `Call` and lambda scope for constructing the DataSource.
```kotlin
val disneyService = retrofit.create(DisneyService::class.java)

val dataSource = ResponseDataSource<List<Poster>>()
dataSource.combine(disneyService.fetchDisneyPosterList()) { response ->
    // stubs
}
```

#### Request
Request API network call asynchronously. <br>
If the request is successful, this data source will hold the success response model.<br>
In the next request after the success, request() returns the cached API response. <br>
If we need to fetch a new response data or refresh, we can use `invalidate()`.

```kotlin
dataSource.request()
```

#### Retry
Retry fetching data (re-request) if your request got failure.
```kotlin
// retry fetching data 3 times with 5000 milli-seconds time interval when the request gets failure.
dataSource.retry(3, 5000L)
```

#### ObserveResponse
Observes every response data `ApiResponse` from the API call request.
```kotlin
dataSource.observeResponse {
   Timber.d("observeResponse: $it")
}
```

#### RetainPolicy
We can limit the policy for retaining data on the temporarily internal storage.<br>
The default policy is no retaining any fetched data from the network, but we can set the policy using `dataRetainPolicy` method.
```kotlin
// Retain fetched data on the memory storage temporarily.
// If request again, returns the retained data instead of re-fetching from the network.
dataSource.dataRetainPolicy(DataRetainPolicy.RETAIN)
```

#### Invalidate
Invalidate a cached (holding) data and re-fetching the API request.

```kotlin
dataSource.invalidate()
```

#### Concat
Concat an another `DataSource` and request API call sequentially if the API call getting successful.

```kotlin
val dataSource2 = ResponseDataSource<List<PosterDetails>>()
dataSource2.retry(3, 5000L).combine(disneyService.fetchDetails()) {
    // stubs handling dataSource2 response
}

dataSource1
   .request() // request() must be called before concat. 
   .concat(dataSource2) // request dataSource2's API call after the success of the dataSource1.
   .concat(dataSource3) // request dataSource3's API call after the success of the dataSource2.
```

#### asLiveData
we can observe fetched data via `DataSource` as a `LiveData`.
```kotlin
val posterListLiveData: LiveData<List<Poster>>

init {
    posterListLiveData = disneyService.fetchDisneyPosterList().toResponseDataSource()
      .retry(3, 5000L)
      .dataRetainPolicy(DataRetainPolicy.RETAIN)
      .request {
        // ... //
      }.asLiveData()
}
```

#### Disposable
We can make it joins onto `CompositeDisposable` as a disposable using the `joinDisposable` function. It must be called before `request()` method. The below example is using in ViewModel. We can clear the `CompositeDisposable` in the `onCleared()` override method.
```kotlin
private val disposable = CompositeDisposable()

init {
    disneyService.fetchDisneyPosterList().toResponseDataSource()
      // retry fetching data 3 times with 5000L interval when the request gets failure.
      .retry(3, 5000L)
      // joins onto CompositeDisposable as a disposable and dispose onCleared().
      .joinDisposable(disposable)
      .request {
        // ... //
      }
}

override fun onCleared() {
    super.onCleared()
    if (!disposable.disposed) {
      disposable.clear()
    }
  }
```

Here is the example of the `ResponseDataSource` in the `MainViewModel`.
```kotlin
class MainViewModel constructor(
  private val disneyService: DisneyService
) : ViewModel() {

  // request API call Asynchronously and holding successful response data.
  private val dataSource = ResponseDataSource<List<Poster>>()

  val posterListLiveData = MutableLiveData<List<Poster>>()
  val toastLiveData = MutableLiveData<String>()
  private val disposable = CompositeDisposable()

  /** fetch poster list data from the network. */
  fun fetchDisneyPosters() {
    dataSource
      // retry fetching data 3 times with 5000 time interval when the request gets failure.
      .retry(3, 5000L)
      // joins onto CompositeDisposable as a disposable and dispose onCleared().
      .joinDisposable(disposable)
      // combine network service to the data source.
      .combine(disneyService.fetchDisneyPosterList()) { response ->
        // handle the case when the API request gets a success response.
        response.onSuccess {
          Timber.d("$data")
          posterListLiveData.postValue(data)
        }
          // handle the case when the API request gets a error response.
          // e.g. internal server error.
          .onError {
            Timber.d(message())

            // handling error based on status code.
            when (statusCode) {
              StatusCode.InternalServerError -> toastLiveData.postValue("InternalServerError")
              StatusCode.BadGateway -> toastLiveData.postValue("BadGateway")
              else -> toastLiveData.postValue("$statusCode(${statusCode.code}): ${message()}")
            }

            // map the ApiResponse.Failure.Error to a customized error model using the mapper.
            map(ErrorEnvelopeMapper) {
              Timber.d(this.toString())
            }
          }
          // handle the case when the API request gets a exception response.
          // e.g. network connection error.
          .onException {
            Timber.d(message())
            toastLiveData.postValue(message())
          }
      }
      // observe every API request responses.
      .observeResponse {
        Timber.d("observeResponse: $it")
      }
      // request API network call asynchronously.
      // if the request is successful, the data source will hold the success data.
      // in the next request after success, returns the cached API response.
      // if you want to fetch a new response data, use invalidate().
      .request()
  }

  override fun onCleared() {
    super.onCleared()
    if (!disposable.disposed) {
      disposable.clear()
    }
  }
}
```

### DataSourceCallAdapterFactory
We can get the `DataSource` directly from the Retrofit service. <br>
Add a call adapter factory `DataSourceCallAdapterFactory` to your Retrofit builder. <br>
And change the return type of your service `Call` to `DataSource`.
```kotlin
Retrofit.Builder()
    ...
    .addCallAdapterFactory(DataSourceCallAdapterFactory())
    .build()

interface DisneyService {
  @GET("DisneyPosters.json")
  fun fetchDisneyPosterList(): DataSource<List<Poster>>
}
```
Here is an example of the `DataSource` in the MainViewModel.
```kotlin
class MainViewModel constructor(disneyService: DisneyService) : ViewModel() {

  // request API call Asynchronously and holding successful response data.
  private val dataSource: DataSource<List<Poster>>

    init {
    Timber.d("initialized MainViewModel.")

    dataSource = disneyService.fetchDisneyPosterList()
      // retry fetching data 3 times with 5000L interval when the request gets failure.
      .retry(3, 5000L)
      .observeResponse(object : ResponseObserver<List<Poster>> {
        override fun observe(response: ApiResponse<List<Poster>>) {
          // handle the case when the API request gets a success response.
          response.onSuccess {
            Timber.d("$data")
            posterListLiveData.postValue(data)
          }
        }
      })
      .request() // must call request()
```

### CoroutinesDataSourceCallAdapterFactory
We can get the `DataSource` directly from the Retrofit service using with `suspend`. <br>
```kotlin
Retrofit.Builder()
    ...
    .addCallAdapterFactory(CoroutinesDataSourceCallAdapterFactory())
    .build()

interface DisneyService {
  @GET("DisneyPosters.json")
  fun fetchDisneyPosterList(): DataSource<List<Poster>>
}
```
Here is an exmaple of the `DataSource` in the MainViewModel.
```kotlin
class MainCoroutinesViewModel constructor(disneyService: DisneyCoroutinesService) : ViewModel() {

  val posterListLiveData: LiveData<List<Poster>>

  init {
    Timber.d("initialized MainViewModel.")

    posterListLiveData = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
      emitSource(disneyService.fetchDisneyPosterList().toResponseDataSource()
        // retry fetching data 3 times with 5000L interval when the request gets failure.
        .retry(3, 5000L)
        // a retain policy for retaining data on the internal storage
        .dataRetainPolicy(DataRetainPolicy.RETAIN)
        // request API network call asynchronously.
        .request {
          // handle the case when the API request gets a success response.
          onSuccess {
            Timber.d("$data")
          }.onError { // handle the case when the API request gets a error response.
              Timber.d(message())
            }.onException {  // handle the case when the API request gets a exception response.
              Timber.d(message())
            }
        }.asLiveData())
    }
  }
}
```

#### toResponseDataSource
We can change `DataSource` to `ResponseDataSource` after getting instance from network call using the below method.
```kotlin
private val dataSource: ResponseDataSource<List<Poster>>

  init {
    dataSource = disneyService.fetchDisneyPosterList().toResponseDataSource()

    //...
  }
```


## Find this library useful? :heart:
Support it by joining __[stargazers](https://github.com/skydoves/sandwich/stargazers)__ for this repository. :star: <br>
And __[follow](https://github.com/skydoves)__ me for my next creations! 🤩

# License
```xml
Copyright 2020 skydoves (Jaewoong Eum)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
