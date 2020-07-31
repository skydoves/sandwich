
<h1 align="center">Sandwich</h1></br>

<p align="center"> 
🥪 A lightweight Android network response API for handling data and error response with <br>transformation extensions using Retrofit.
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
<img src="https://user-images.githubusercontent.com/24237865/85877362-00c42c80-b812-11ea-863a-56a4f6aa439d.png" width="794" height="404"/>
</p>

## Download
[![Download](https://api.bintray.com/packages/devmagician/maven/sandwich/images/download.svg) ](https://bintray.com/devmagician/maven/sandwich/_latestVersion)
[![Jitpack](https://jitpack.io/v/skydoves/Sandwich.svg)](https://jitpack.io/#skydoves/Sandwich)
### Gradle
Add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "com.github.skydoves:sandwich:1.0.4"
}
```

## Usecase
You can reference the use cases of this library in the below repositories.
- [DisneyMotions](https://github.com/skydoves/DisneyMotions) - 🦁 A Disney app using transformation motions based on MVVM (ViewModel, Coroutines, LiveData, Room, Repository, Koin) architecture.
- [MarvelHeroes](https://github.com/skydoves/marvelheroes) - ❤️ A sample Marvel heroes application based on MVVM (ViewModel, Coroutines, LiveData, Room, Repository, Koin)  architecture.
- [TheMovies2](https://github.com/skydoves/TheMovies2) - 🎬 A demo project using The Movie DB based on Kotlin MVVM architecture and material design & animations.

## Usage
### ApiResponse
`ApiResponse` is an interface of the retrofit response for handling data and error response with useful extensions. <br>
We can get `ApiResponse` using the scope extension `request` from the `Call`.

```kotlin
interface DisneyService {
  @GET("/")
  fun fetchDisneyPosterList(): Call<List<Poster>>
}

val disneyService = retrofit.create(DisneyService::class.java)
// Asynchronously request REST call and get an ApiResponse model.
disneyService.fetchDisneyPosterList().request { response ->
      when (response) {
        is ApiResponse.Success -> {
          // stub success case
          livedata.post(response.data)
        }
        is ApiResponse.Failure.Error -> {
          // stub error case
        }
        is ApiResponse.Failure.Exception -> {
          // stub exception case
        }
      }
    }
```
#### ApiResponse.Success
API success response class from retrofit. <br>
We can get the response body data, `StatusCode`, `Headers` and etc from the `ApiResponse.Success`.

```kotlin
val data: List<Poster>? = response.data
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
```

#### ApiResponse.Failure.Error
API format does not match or applications need to handle errors.
e.g. Internal server error.

```kotlin
val errorBody: ResponseBody? = response.errorBody
val statusCode: StatusCode = response.statusCode
val headers: Headers = response.headers
```

#### ApiResponse.Failure.Exception 
Gets called when an unexpected exception occurs while creating the request or processing <br>the response in client. e.g. Network connection error.

### ApiResponse Extensions
We can handle response cases more handy using extensions.

#### onSuccess, onError, onException
We can use these scope functions to the `ApiResponse`, it reduces the usage of the if/when clause.
```kotlin
disneyService.fetchDisneyPosterList().request { response ->
      response.onSuccess {
        // stub success case
        livedata.post(response.data)
      }.onError {
        // stub error case
      }.onException {
        // stub exception case
      }
    }
```

#### suspendOnSuccess, suspendOnError, suspendOnException
We can use suspension extensions for using suspend functions inside the lambda.<br>
In this case, we should use with [CoroutinesResponseCallAdapterFactory](https://github.com/skydoves/sandwich#apiresponse-with-coroutines).
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
    }
```


### ApiErrorModelMapper
We can map `ApiResponse.Failure.Error` model to our customized error model using the mapper.

```kotlin
data class ErrorEnvelope(
  val code: Int,
  val message: String
)

object ErrorEnvelopeMapper : ApiErrorModelMapper<ErrorEnvelope> {

  override fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): ErrorEnvelope {
    return ErrorEnvelope(apiErrorResponse.statusCode.code, apiErrorResponse.message())
  }
}

// hadling the error response case.
response.onError {
  // map the ApiResponse.Failure.Error to a customized error model using the mapper.
  map(ErrorEnvelopeMapper) {
     val code = this.code
     val message = this.message
  }
}
```

### ApiResponse with coroutines
We can use the `suspend` keyword in our service.<br>
Build your retrofit using with `CoroutinesResponseCallAdapterFactory` call adapter factory.
```kotlin
.addCallAdapterFactory(CoroutinesResponseCallAdapterFactory())
```
And use the `suspend` keyword in our service functions. The response type must be `ApiResponse`.
```kotlin
interface DisneyCoroutinesService {

  @GET("DisneyPosters.json")
  suspend fun fetchDisneyPosterList(): ApiResponse<List<Poster>>
}
```
We can use like this; An example of using `toLiveData`.
```kotlin
class MainCoroutinesViewModel constructor(disneyService: DisneyCoroutinesService) : ViewModel() {

  val posterListLiveData: LiveData<List<Poster>>

  init {
    posterListLiveData = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
      emitSource(disneyService.fetchDisneyPosterList()
        .onSuccess {
          // stub success case
          livedata.post(response.data)
        }.onError {
          // stub error case
        }.onException {
          // stub exception case
        }.toLiveData()) // returns an observable LiveData
    }
  }
}
```

### ResponseDataSource
ResponseDataSource is an implementation of the `DataSource` interface. <br>
 * Asynchronously send requests.
 * A temporarily response data holder from the REST API call for caching data on memory.
 * Observable for every response.
 * Retry fetching data when the request gets failure.
 * Concat another `DataSource` and request sequentially.

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
        // ...
      }.asLiveData()
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

  /** fetch poster list data from the network. */
  fun fetchDisneyPosters() {
    dataSource
      // retry fetching data 3 times with 5000 time interval when the request gets failure.
      .retry(3, 5000L)
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
