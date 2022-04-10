![sandwich](https://user-images.githubusercontent.com/24237865/162602054-2010d249-8a81-4673-b9ae-1edff1080ab7.png)<br>

<p align="center">
  <a href="https://devlibrary.withgoogle.com/products/android/repos/skydoves-Sandwich"><img alt="Google" src="https://skydoves.github.io/badges/google-devlib.svg"/></a><br>
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=16"><img alt="API" src="https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/skydoves/Sandwich/actions"><img alt="Build Status" src="https://github.com/skydoves/Sandwich/workflows/Android%20CI/badge.svg"/></a>
  <a href="https://skydoves.medium.com/handling-success-data-and-error-callback-responses-from-a-network-for-android-projects-using-b53a26214cef"><img alt="Medium" src="https://skydoves.github.io/badges/Story-Medium.svg"/></a>
  <a href="https://github.com/skydoves"><img alt="Profile" src="https://skydoves.github.io/badges/skydoves.svg"/></a>
  <a href="https://skydoves.github.io/libraries/sandwich/html/sandwich/com.skydoves.sandwich/index.html"><img alt="Dokka" src="https://skydoves.github.io/badges/dokka-sandwich.svg"/></a>
</p>

## Why Sandwich?
Sandwich was invented to construct standardized interfaces from the Retrofit network response. Sandwich allows you to handle the body data, errors, and exceptional cases obviously with useful operators in multi-layer architecture. You don't need to design and build wrapper classes such as `Resource` or `Result`, and it helps you to focus on your business codes. Sandwich supports [global responses handling](https://github.com/skydoves/sandwich#global-operator), [Mapper](https://github.com/skydoves/sandwich#mapper), [Operator](https://github.com/skydoves/sandwich#operator), and great compatibilities like [toLiveData](https://github.com/skydoves/sandwich#tolivedata) or [toFlow](https://github.com/skydoves/sandwich#toflow). Also, you can utilize Sandwich with [coroutines](https://github.com/skydoves/sandwich#apiresponse-for-coroutines) and [flow](https://github.com/skydoves/sandwich#suspendonsuccess-suspendonerror-suspendonexception).

## Download
[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/sandwich.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22sandwich%22)

🥪 Sandwich has been downloaded in more than __120k__ Android projects all over the globe! <br>

<img src="https://user-images.githubusercontent.com/24237865/103460609-f18ee000-4d5a-11eb-81e2-17696e3a5804.png" width="774" height="224"/>

### Gradle
Add the code below to your **root** `build.gradle` file:
```gradle
allprojects {
    repositories {
        mavenCentral()
    }
}
```
Next, add the dependency below to your **module**'s `build.gradle` file:
```gradle
dependencies {
    implementation "com.github.skydoves:sandwich:1.2.3"
}
```
> Note: Sandwich uses [Retrofit](https://github.com/skydoves/Sandwich/blob/main/dependencies.gradle#L32) and [OkHttp](https://github.com/skydoves/Sandwich/blob/main/dependencies.gradle#L33) internally, so please make sure your project uses the same versions.

## SNAPSHOT 
[![Sandwich](https://img.shields.io/static/v1?label=snapshot&message=sandwich&logo=apache%20maven&color=C71A36)](https://oss.sonatype.org/content/repositories/snapshots/com/github/skydoves/sandwich/) <br>

<details>
 <summary>See how to import the snapshot</summary>

### Including the SNAPSHOT
Snapshots of the current development version of Sandwich are available, which track [the latest versions](https://oss.sonatype.org/content/repositories/snapshots/com/github/skydoves/sandwich/).

To import snapshot versions on your project, add the code snippet below on your gradle file:
```Gradle
repositories {
   maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}
```

Next, add the dependency below to your **module**'s `build.gradle` file:
```gradle
dependencies {
    implementation "com.github.skydoves:sandwich:1.2.3-SNAPSHOT"
}
```

</details>

## Use Cases
You can also check out nice use cases of this library in the repositories below:
- [Pokedex](https://github.com/skydoves/pokedex) - 🗡️ Android Pokedex using Hilt, Motion, Coroutines, Flow, Jetpack (Room, ViewModel, LiveData) based on MVVM architecture.
- [DisneyMotions](https://github.com/skydoves/DisneyMotions) - 🦁 A Disney app using transformation motions based on MVVM (ViewModel, Coroutines, LiveData, Room, Repository, Koin) architecture.
- [MarvelHeroes](https://github.com/skydoves/marvelheroes) - ❤️ A sample Marvel heroes application based on MVVM (ViewModel, Coroutines, LiveData, Room, Repository, Koin)  architecture.
- [Neko](https://github.com/CarlosEsco/Neko) - Free, open source, unofficial MangaDex reader for Android.
- [TheMovies2](https://github.com/skydoves/TheMovies2) - 🎬 A demo project using The Movie DB based on Kotlin MVVM architecture and material design & animations.

## Table of contents
- [ApiResponse](https://github.com/skydoves/sandwich#apiresponse)
- [onSuccess, onError, onException](https://github.com/skydoves/sandwich#apiresponse-extensions)
- [ApiResponse for coroutines](https://github.com/skydoves/sandwich#apiresponse-for-coroutines)
- [ApiResponse Extensions for Coroutines](https://github.com/skydoves/sandwich#apiresponse-extensions-for-coroutines)
- [Retrieve success data](https://github.com/skydoves/sandwich#retrieve-success-data)
- [Mapper](https://github.com/skydoves/sandwich#mapper)
- [Operator](https://github.com/skydoves/sandwich#operator), [Operator for coroutines](https://github.com/skydoves/sandwich#operator-with-coroutines), [Global Operator](https://github.com/skydoves/sandwich#global-operator)
- [Merge](https://github.com/skydoves/sandwich#merge)
- [toLiveData](https://github.com/skydoves/sandwich#tolivedata), [toFlow](https://github.com/skydoves/sandwich#toflow)
- [ResponseDataSource](https://github.com/skydoves/sandwich#responsedatasource)

## Usage
### ApiResponse
`ApiResponse` is an interface to construct standardized responses from [Retrofit](https://github.com/square/retrofit) calls. It provides useful extensions for handling netowrk payload such as body data and exceptional cases. You can get `ApiResponse` with the `request` scope extension from the [Call](https://square.github.io/retrofit/2.x/retrofit/retrofit2/Call.html). The example below shows how to get an `ApiResponse` from an instance of the `Call`.

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
          livedata.post(response.data)
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

ApiResponse has three main types; **Success**, **Failure.Error**, and **Failure.Exception**.

### ApiResponse.Success
This represents the network request has been successful. You can get the body data of the response, and additional information such as `StatusCode`, `Headers`, and more from the `ApiResponse.Success`.

```kotlin
val data: List<Poster>? = response.data
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
```

### ApiResponse.Failure.Error
This represents the network request has been failed with bad requests or internal server errors. You can get an error message and additional information such as `StatusCode`, `Headers`, and more from the `ApiResponse.Failure.Error`. 

```kotlin
val message: String = response.message()
val errorBody: ResponseBody? = response.errorBody
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
```

### ApiResponse.Failure.Exception 
This represents the network request has been failed when unexpected exceptions occur while creating requests or processing a response from the client-side such as network connection failed. You can get an exception message from the `ApiResponse.Failure.Exception`. 

### ApiResponse Extensions
You can handle the `ApiResponse` with the extensions below:

- **onSuccess**: Takes if the `ApiResponse` is `ApiResponse.Success`. You can access body data directly in this scope.
- **onError**: Takes if the `ApiResponse` is `ApiResponse.Failure.Error`. You can access `message()` and `errorBody` in this scope.
- **onException**: Takes if the `ApiResponse` is `ApiResponse.Failure.Exception`. Toy can access `message()` in this scope.

The scope runs depending on the `ApiResponse` as the example below:

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

### ApiResponse for Coroutines
You can use the `suspend` keyword in your Retrofit services with `ApiResponse<*>` as a response type. First, build your `Retrofit` with the `CoroutinesResponseCallAdapterFactory` call adapter factory as following below:

```kotlin
.addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())
```

Next, you should define the service interface with the suspend keyword and `ApiResponse<*>` as a response type. So eventually you will get the `ApiResponse` from the Retrofit service call like the examples below:

```kotlin
interface DisneyCoroutinesService {

  @GET("DisneyPosters.json")
  suspend fun fetchDisneyPosterList(): ApiResponse<List<Poster>>
}
```

Finally, you can execute the defined service like the examples below:

```kotlin
class MainCoroutinesViewModel constructor(disneyService: DisneyCoroutinesService) : ViewModel() {

  val posterListLiveData: MutableLiveData<List<Poster>>

  init {
     val response = disneyService.fetchDisneyPosterList()
     response.onSuccess {
       // handles the success case when the API request gets a successful response.
       posterListLiveData.post(data)
      }.onError {
       // handles error cases when the API request gets an error response.
      }.onException {
       // handles exceptional cases when the API request gets an exception response.
      }
   }
}
```

### ApiResponse Extensions for Coroutines

You can handle the `ApiResponse` with the coroutines extensions, which you can run your suspend functions on the scopes.

- **suspendOnSuccess**: Takes if the `ApiResponse` is `ApiResponse.Success`. You can access body data directly in this scope.
- **suspendOnError**: Takes if the `ApiResponse` is `ApiResponse.Failure.Error`. You can access `message()` and `errorBody` in this scope.
- **suspendOnFailure**: Takes if the `ApiResponse` is `ApiResponse.Failure.Exception`. Toy can access `message()` in this scope.

The scope runs depending on the `ApiResponse` as the example below:

```kotlin
flow {
  val response = disneyService.fetchDisneyPosterList()
  response.suspendOnSuccess {
    posterDao.insertPosterList(data) // insertPosterList(data) is a suspend function.
    emit(data)
  }.suspendOnError {
    // handles error cases
  }.suspendOnFailure {
    // handles exceptional cases
  }
}.flowOn(Dispatchers.IO)
```

### Retrieve success data
If you want to retrieve the encapsulated body data from the `ApiResponse` directly, you can use the functionalities below.

#### getOrNull
Returns the encapsulated data if this instance represents `ApiResponse.Success` or returns null if this is failed.

```kotlin
val data: List<Poster>? = disneyService.fetchDisneyPosterList().getOrNull()
```

#### getOrElse
Returns the encapsulated data if this instance represents `ApiResponse.Success` or returns a default value if this is failed.

```kotlin
val data: List<Poster>? = disneyService.fetchDisneyPosterList().getOrElse(emptyList())
```

#### getOrThrow
Returns the encapsulated data if this instance represents `ApiResponse.Success` or throws the encapsulated `Throwable` exception if this is failed.

```kotlin
try {
  val data: List<Poster>? = disneyService.fetchDisneyPosterList().getOrThrow()
} catch (e: Exception) {
  e.printStackTrace()
}
```

### Mapper
Mapper is useful when you want to transform the `ApiResponse.Success` or `ApiResponse.Failure.Error` to your custom model in `ApiResponse` extension scopes.

#### ApiSuccessModelMapper
You can map the `ApiResponse.Success` model to your custom model with the `SuccessPosterMapper<T, R>` and `map` extension like the examples below:

```kotlin
object SuccessPosterMapper : ApiSuccessModelMapper<List<Poster>, Poster?> {

  override fun map(apiErrorResponse: ApiResponse.Success<List<Poster>>): Poster? {
    return apiErrorResponse.data.first()
  }
}

// Maps the success response data.
val poster: Poster? = map(SuccessPosterMapper)
```

You can use the `map` extension with a lambda like the examples below:

```kotlin
// Maps the success response data using a lambda.
map(SuccessPosterMapper) { poster ->
  emit(poster) // you can use the `this` keyword instead of the poster.
}
```

If you want to receive transformed body data in the scope, you can use the mapper as a parameter with the `onSuccess` or `suspendOnSuccess` extensions like the examples below:

```kotlin
.suspendOnSuccess(SuccessPosterMapper) {
    val poster = this
}
```

#### ApiErrorModelMapper
You can map the `ApiResponse.Failure.Error` model to your custom error model using the `ApiErrorModelMapper<T>` and `map` extension as the examples bleow:

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

// Maps an error response.
response.onError {
  // Maps an ApiResponse.Failure.Error to a custom error model using the mapper.
  map(ErrorEnvelopeMapper) {
     val code = this.code
     val message = this.message
  }
}
```

If you want to receive transformed data from in the scope, you can use the mapper as a parameter with the `onError` or `suspendOnError` extensions as the examples below:

```kotlin
.suspendOnError(ErrorEnvelopeMapper) {
    val message = this.message
}
```

### Operator
You can delegate the `onSuccess`, `onError`, and `onException` with the `operator` extension and `ApiResponseOperator`. **Operator** is very useful if you want to handle `ApiResponse`-s globally  and reduce the boilerplates for your `ViewModel` and `Repository` classes. Here are some examples below:

#### ViewModel
We can delegate and operate the `CommonResponseOperator` using the `operate` extension.
```kotlin
disneyService.fetchDisneyPosterList().operator(
      CommonResponseOperator(
        success = {
          emit(data)
          Timber.d("success data: $data")
        },
        application = getApplication()
      )
    )
```

#### CommonResponseOperator
The `CommonResponseOperator` extends `ApiResponseOperator` with the `onSuccess`, `onError`, and `onException` override methods. They will be executed depending on the `ApiResponse`.

```kotlin
/** A common response operator for handling [ApiResponse]s regardless of its type. */
class CommonResponseOperator<T> constructor(
  private val success: suspend (ApiResponse.Success<T>) -> Unit,
  private val application: Application
) : ApiResponseOperator<T>() {

  // handles error cases when the API request gets an error response.
  override fun onSuccess(apiResponse: ApiResponse.Success<T>) = success(apiResponse)

  // handles error cases depending on the status code.
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

  // handles exceptional cases when the API request gets an exception response.
  // e.g., network connection error, timeout.
  override fun onException(apiResponse: ApiResponse.Failure.Exception<T>) {
    apiResponse.run {
      Timber.d(message())
      toast(message())
    }
  }
}
```

### Operator for coroutines
If you want to operate and delegate a suspension lambda to the operator, you can use the `suspendOperator` extension and `ApiResponseSuspendOperator` class as the examples below:

#### ViewModel
You can use suspension functions like `emit` in the `success` scope.

```kotlin
flow {
  disneyService.fetchDisneyPosterList().suspendOperator(
      CommonResponseOperator(
        success = {
          emit(data)
          Timber.d("success data: $data")
        },
        application = getApplication()
      )
    )
}.flowOn(Dispatchers.IO)
```

#### CommonResponseOperator
The `CommonResponseOperator` extends `ApiResponseSuspendOperator` with suspend override methods as the examples below:

```kotlin
class CommonResponseOperator<T> constructor(
  private val success: suspend (ApiResponse.Success<T>) -> Unit,
  private val application: Application
) : ApiResponseSuspendOperator<T>() {

  // handles the success case when the API request gets a successful response.
  override suspend fun onSuccess(apiResponse: ApiResponse.Success<T>) = success(apiResponse)

  // ... //
```

### Global operator
You can operate an operator globally whole `ApiResponse`-s in your application with the `SandwichInitializer`. So you don't need to create every instance of the **Operator**s or use dependency injection for handling common operations. Here are some examples of handling a global operator for the `ApiResponse.Failure.Error` and `ApiResponse.Failure.Exception`.

#### Application class
First, you should initialize the global operator to the `SandwichInitializer.sandwichOperator`. It's highly recommended to initialize this in the Application class.

```kotlin
class SandwichDemoApp : Application() {

  override fun onCreate() {
    super.onCreate()
    
    // We will handle only the error and exceptional cases,
    // so we don't need to mind the generic type of the operator.
    SandwichInitializer.sandwichOperator = GlobalResponseOperator<Any>(this)

    // ... //
```

#### GlobalResponseOperator
Next, create your own `GlobalResponseOperator`, which extends operators such as `ApiResponseSuspendOperator` and `ApiResponseOperator` as the examples below:

```kotlin
class GlobalResponseOperator<T> constructor(
  private val application: Application
) : ApiResponseSuspendOperator<T>() {

  // The body is empty, because we will handle the success case manually.
  override suspend fun onSuccess(apiResponse: ApiResponse.Success<T>) { }

  // handles error cases when the API request gets an error response.
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

  // handles exceptional cases when the API request gets an exception response.
  // e.g., network connection error, timeout.
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
Finally, you don't need to use the `operator` expression anymore. The global operator will be operated, so you should handle only the `ApiResponse.Success`.

> Note: This example didn't implement for the `onSuccess` case.

```kotlin
flow {
  disneyService.fetchDisneyPosterList().
    suspendOnSuccess {
      emit(data)
    }
}.flowOn(Dispatchers.IO).asLiveData()
```

### Merge
You can merge multiple `ApiResponse`s as a single `ApiResponse` depending on policies. The example below shows how to merge three `ApiResponse` as a single one if each three `ApiResponse`s are successful.

```kotlin
disneyService.fetchDisneyPosterList(page = 0).merge(
   disneyService.fetchDisneyPosterList(page = 1),
   disneyService.fetchDisneyPosterList(page = 2),
   mergePolicy = ApiResponseMergePolicy.PREFERRED_FAILURE
).onSuccess { 
  // handles the success case when the API request gets a successful response.
}.onError { 
  // handles error cases when the API request gets an error response.
}
```

#### ApiResponseMergePolicy
`ApiResponseMergePolicy` is a policy for merging response data depend on the success or not.
- **IGNORE_FAILURE**: Regardless of the merging sequences, ignores failure responses in the responses.
- **PREFERRED_FAILURE** (default): Regardless of the merging sequences, prefers failure responses in the responses.

### toLiveData
You can get a `LiveData` that contains body data if the response is an `ApiResponse.Success`. This is very useful if your goal is only getting a `LiveData` from the `ApiResponse` which holds successful data as the examples below:

```kotlin
posterListLiveData = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
  emitSource(
    disneyService.fetchDisneyPosterList()
     .onError {
      // handles error cases when the API request gets an error response.
     }.onException {
      // handles exceptional cases when the API request gets an exception response.
     }.toLiveData()) // returns an observable LiveData
}
```

If you want to transform the original data and take a `LiveData` that contains transformed data, you can follow as the examples below:

```kotlin
posterListLiveData = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
  emitSource(
   disneyService.fetchDisneyPosterList()
    .onError {
      // handles error cases when the API request gets an error response.
    }.onException {
      // handles exceptional cases when the API request gets an exception response.
    }.toLiveData {
      this.onEach { poster -> poster.date = SystemClock.currentThreadTimeMillis() }
    }) // returns an observable LiveData
    }
```

### toFlow
You can get a `Flow` that emits body data if the response is an `ApiResponse.Success` and the data is not null.

```kotlin
disneyService.fetchDisneyPosterList()
  .onError {
    // handles error cases when the API request gets an error response.
  }.onException {
    // handles exceptional cases when the API request gets an exception response.
  }.toFlow() // returns a coroutines flow
  .flowOn(Dispatchers.IO)
```

If you want to transform the original data and take a `flow` that contains transformed data, you can follow as the examples below:

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
        // handles the success case when the API request gets a successful response.
        response.onSuccess {
          Timber.d("$data")
          posterListLiveData.postValue(data)
        }
          // handles error cases when the API request gets an error response.
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
          // handles exceptional cases when the API request gets an exception response.
          // e.g. network connection error, timeout.
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
    .addCallAdapterFactory(DataSourceCallAdapterFactory.create())
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
    .addCallAdapterFactory(CoroutinesDataSourceCallAdapterFactory.create())
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
