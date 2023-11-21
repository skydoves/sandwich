![sandwich](https://user-images.githubusercontent.com/24237865/162602054-2010d249-8a81-4673-b9ae-1edff1080ab7.png)<br>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/skydoves/Sandwich/actions"><img alt="Build Status" src="https://github.com/skydoves/sandwich/actions/workflows/build.yml/badge.svg"/></a><br>
  <a href="https://devlibrary.withgoogle.com/products/android/repos/skydoves-Sandwich"><img alt="Google" src="https://skydoves.github.io/badges/google-devlib.svg"/></a>
  <a href="https://skydoves.medium.com/handling-success-data-and-error-callback-responses-from-a-network-for-android-projects-using-b53a26214cef"><img alt="Medium" src="https://skydoves.github.io/badges/Story-Medium.svg"/></a>
  <a href="https://github.com/skydoves"><img alt="Profile" src="https://skydoves.github.io/badges/skydoves.svg"/></a>
  <a href="https://youtu.be/agjbbn9Swkc"><img alt="Profile" src="https://skydoves.github.io/badges/youtube-android-worldwide.svg"/></a> 
  <a href="https://skydoves.github.io/libraries/sandwich/html/sandwich/com.skydoves.sandwich/index.html"><img alt="Dokka" src="https://skydoves.github.io/badges/dokka-sandwich.svg"/></a>
</p>

## Why Sandwich?
Sandwich was conceived to streamline the creation of standardized interfaces to model responses from [Retrofit](https://skydoves.github.io/sandwich/sandwich/retrofit/), [Ktor](https://skydoves.github.io/sandwich/sandwich/ktor/), and whatever. This library empowers you to handle body data, errors, and exceptional cases more succinctly, utilizing functional operators within a multi-layer architecture. With Sandwich, the need to create wrapper classes like Resource or Result is eliminated, allowing you to concentrate on your core business logic. Sandwich boasts features such as [global response handling](https://skydoves.github.io/sandwich/operator#global-operator), [Mapper](https://skydoves.github.io/sandwich/mapper), [Operator](https://skydoves.github.io/sandwich/operator), and exceptional compatibility, including [ApiResponse With Coroutines](https://skydoves.github.io/sandwich/apiresponse/#apiresponse-extensions-with-coroutines).

## Download
[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/sandwich.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22sandwich%22)

Sandwich has achieved an impressive milestone, being downloaded in __over 300,000__ Android projects worldwide! <br>

<img src="https://user-images.githubusercontent.com/24237865/103460609-f18ee000-4d5a-11eb-81e2-17696e3a5804.png" width="774" height="224"/>

### Gradle

Add the dependency below into your **module**'s `build.gradle` file:

```gradle
dependencies {
    implementation "com.github.skydoves:sandwich:2.0.3"
}
```

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
    implementation "com.github.skydoves:sandwich:1.3.10-SNAPSHOT"
}
```

</details>

## R8 / ProGuard
The specific rules are [already bundled](sandwich/src/main/resources/META-INF/proguard/sandwich.pro) into the JAR which can be interpreted by R8 automatically.

## Documentation

For comprehensive details about Sandwich, please refer to the complete [documentation available here](https://skydoves.github.io/sandwich/).

## Use Cases
You can also check out nice use cases of this library in the repositories below:
- [Pokedex](https://github.com/skydoves/pokedex): 🗡️ Android Pokedex using Hilt, Motion, Coroutines, Flow, Jetpack (Room, ViewModel, LiveData) based on MVVM architecture.
- [ChatGPT Android](https://github.com/skydoves/chatgpt-android): 📲 ChatGPT Android demonstrates OpenAI's ChatGPT on Android with Stream Chat SDK for Compose.
- [DisneyMotions](https://github.com/skydoves/DisneyMotions): 🦁 A Disney app using transformation motions based on MVVM (ViewModel, Coroutines, LiveData, Room, Repository, Koin) architecture.
- [MarvelHeroes](https://github.com/skydoves/marvelheroes): ❤️ A sample Marvel heroes application based on MVVM (ViewModel, Coroutines, LiveData, Room, Repository, Koin)  architecture.
- [Neko](https://github.com/CarlosEsco/Neko): Free, open source, unofficial MangaDex reader for Android.
- [TheMovies2](https://github.com/skydoves/TheMovies2): 🎬 A demo project using The Movie DB based on Kotlin MVVM architecture and material design & animations.

## Usage

For comprehensive details about Sandwich, please refer to the complete [documentation available here](https://skydoves.github.io/sandwich/).

- [Retrofit Integration](https://skydoves.github.io/sandwich/retrofit)
- [Ktor Integration](https://skydoves.github.io/sandwich/ktor)
- [Ktorfit Integration](https://skydoves.github.io/sandwich/ktorfit)

### ApiResponse

`ApiResponse` serves as an interface designed to create consistent responses from API or I/O calls, such as network, database, or whatever. It offers convenient extensions to manage your payloads, encompassing both body data and exceptional scenarios. `ApiResponse` encompasses three distinct types: **Success**, **Failure.Error**, and **Failure.Exception**.

#### ApiResponse.Success

This represents a successful response from API or I/O tasks. You can create an instance of [ApiResponse.Success] by giving the generic type and data.

```kotlin
val apiResponse = ApiResponse.Success(data = myData)
val data = apiResponse.data
```

Depending on your model designs, you can also utilize `tag` property. The `tag` is an additional value that can be held to distinguish the origin of the [data] or to facilitate post-processing of successful data.

```kotlin
val apiResponse = ApiResponse.Success(data = myData, tag = myTag)
val tag = apiResponse.tag
```

#### ApiResponse.Failure.Exception

This signals a failed tasks captured by unexpected exceptions during API request creation or response processing on the client side, such as a network connection failure. You can obtain exception details from the `ApiResponse.Failure.Exception`.

```kotlin
val apiResponse = ApiResponse.Failure.Exception(exception = HttpTimeoutException())
val exception = apiResponse.exception
val message = apiResponse.message
```

#### ApiResponse.Failure.Error

This denotes a failed API or I/O request, typically due to bad requests or internal server errors. You can additionally put an error payload that can contain detailed error information.

```kotlin
val apiResponse = ApiResponse.Failure.Error(payload = errorBody)
val payload = apiResponse.payload
```

You can also define custom error responses that extend `ApiResponse.Failure.Error` or `ApiResponse.Failure.Exception`, as demonstrated in the example below:

```kotlin
data object LimitedRequest : ApiResponse.Failure.Error(
  payload = "your request is limited",
)

data object WrongArgument : ApiResponse.Failure.Error(
  payload = "wrong argument",
)

data object HttpException : ApiResponse.Failure.Exception(
  throwable = RuntimeException("http exception")
)
```

The custom error response is very useful when you want to explicitly define and handle error responses, especially when working with map extensions.

```kotlin
val apiResponse = service.fetchMovieList()
apiResponse.onSuccess {
    // ..
}.flatMap {
  // if the ApiResponse is Failure.Error and contains error body, then maps it to a custom error response.  
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
apiResponse.onError {
  when (this) {
    LimitedRequest -> // update your UI
    WrongArgument -> // update your UI
  }
}
```

You might not want to use the `flatMap` extension for all API requests. If you aim to standardize custom error types across all API requests, you can explore the [Global Failure Mapper](https://skydoves.github.io/sandwich/mapper/#global-failure-mapper).

#### Creation of ApiResponse

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

> **Note**: If you intend to utilize the global operator or global ApiResponse mapper in Sandwich, you should create an `ApiResponse` using the `ApiResponse.of` method to ensure the application of these global functions.

#### ApiResponse Extensions

You can effectively handling `ApiResponse` using the following extensions:

- **onSuccess**: Executes when the `ApiResponse` is of type `ApiResponse.Success`. Within this scope, you can directly access the body data.
- **onError**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Error`. Here, you can access the `messareOrNull` and `payload` here.
- **onException**: Executes when the `ApiResponse` is of type `ApiResponse.Failure.Exception`. You can access the `messareOrNull` and `exception` here.
- **onFailure**: Executes when the `ApiResponse` is either `ApiResponse.Failure.Error` or `ApiResponse.Failure.Exception`. You can access the `messareOrNull` here.

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

#### ApiResponse Extensions With Coroutines

With the `ApiResponse` type, you can leverage [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) extensions to handle responses seamlessly within coroutine scopes. These extensions provide a convenient way to process different response types. Here's how you can use them:

- **suspendOnSuccess**: This extension runs if the `ApiResponse` is of type `ApiResponse.Success`. You can access the body data directly within this scope.

- **suspendOnError**: This extension is executed if the `ApiResponse` is of type `ApiResponse.Failure.Error`. You can access the error message and the error body in this scope.

- **suspendOnException**: If the `ApiResponse` is of type `ApiResponse.Failure.Exception`, this extension is triggered. You can access the exception message in this scope.

- **suspendOnFailure**: This extension is executed if the `ApiResponse` is either `ApiResponse.Failure.Error` or `ApiResponse.Failure.Exception`. You can access the error message in this scope.

Each extension scope operates based on the corresponding `ApiResponse` type. By utilizing these extensions, you can handle responses effectively within different coroutine contexts.

```kotlin
flow {
  val response = disneyService.fetchDisneyPosterList()
  response.suspendOnSuccess {
    posterDao.insertPosterList(data) // insertPosterList(data) is a suspend function.
    emit(data)
  }.suspendOnError {
    // handles error cases
  }.suspendOnException {
    // handles exceptional cases
  }
}.flowOn(Dispatchers.IO)
```

#### Flow

Sandwich offers some useful extensions to transform your `ApiResponse` into a [Flow](https://kotlinlang.org/docs/flow.html) by using the `toFlow` extension:

```kotlin
val flow = disneyService.fetchDisneyPosterList()
  .onError {
    // handles error cases when the API request gets an error response.
  }.onException {
    // handles exceptional cases when the API request gets an exception response.
  }.toFlow() // returns a coroutines flow
  .flowOn(Dispatchers.IO)
```

If you want to transform the original data and work with a `Flow` containing the transformed data, you can do so as shown in the examples below:

```kotlin
val response = pokedexClient.fetchPokemonList(page = page)
response.toFlow { pokemons ->
  pokemons.forEach { pokemon -> pokemon.page = page }
  pokemonDao.insertPokemonList(pokemons)
  pokemonDao.getAllPokemonList(page)
}.flowOn(Dispatchers.IO)
```

### Retrieving

Sandwich provides effortless methods to directly extract the encapsulated body data from the `ApiResponse`. You can take advantage of the following functionalities:

#### getOrNull
Returns the encapsulated data if this instance represents `ApiResponse.Success` or returns null if this is failed.

```kotlin
val data: List<Poster>? = disneyService.fetchDisneyPosterList().getOrNull()
```

#### getOrElse
Returns the encapsulated data if this instance represents `ApiResponse.Success` or returns a default value if this is failed.

```kotlin
val data: List<Poster> = disneyService.fetchDisneyPosterList().getOrElse(emptyList())
```

#### getOrThrow
Returns the encapsulated data if this instance represents `ApiResponse.Success` or throws the encapsulated `Throwable` exception if this is failed.

```kotlin
try {
  val data: List<Poster> = disneyService.fetchDisneyPosterList().getOrThrow()
} catch (e: Exception) {
  e.printStackTrace()
}
```

### Retry

Sandwich offers seamless ways to run and retry tasks. To execute and retry network or I/O requests, you can employ the `RetryPolicy` interface along with the `runAndRetry` extension, as demonstrated in the code below:

```kotlin
val retryPolicy = object : RetryPolicy {
  override fun shouldRetry(attempt: Int, message: String?): Boolean = attempt <= 3

  override fun retryTimeout(attempt: Int, message: String?): Int = 3000
}

val apiResponse = runAndRetry(retryPolicy) { attempt, reason ->
  mainRepository.fetchPosters()
}.onSuccess {
  // Handle a success case
}.onFailure {
  // Handle failure cases
}
```

### Sequential

Sandwich provides sequential solutions for scenarios where you require sequential execution of network requests.

#### then and suspendThen

If you have a scenario where you need to execute tasks A, B, and C in a dependent sequence, for example, where task B depends on the completion of task A, and task C depends on the completion of task B, you can effectively utilize the `then` or `suspendThen` extensions, as demonstrated in the example below:

```kotlin
service.getUserToken(id) suspendThen { tokenResponse ->
    service.getUserDetails(tokenResponse.token) 
} suspendThen { userResponse ->
    service.queryPosters(userResponse.user.name)
}.mapSuccess { posterResponse ->
  posterResponse.posters
}.onSuccess {
    posterStateFlow.value = data
}.onFailure {
    Log.e("sequential", message())
}
```

### Operator

The **Operator** feature stands out as one of the most powerful capabilities provided by Sandwich. It empowers you to establish well-defined, preconfigured processors for your `ApiResponse` instances. This enables you to encapsulate and reuse a consistent sequence of procedures across your API requests.

You can streamline the handling of `onSuccess`, `onError`, and `onException` scenarios by utilizing the `operator` extension alongside the `ApiResponseOperator`. **Operator** proves particularly valuable when you're aiming for global handling of `ApiResponse` instances and wish to minimize boilerplate code within your `ViewModel` and `Repository` classes. Here are a few illustrative examples:

```kotlin
/** A common response operator for handling [ApiResponse]s regardless of its type. */
class CommonResponseOperator<T>(
  private val success: suspend (ApiResponse.Success<T>) -> Unit
) : ApiResponseOperator<T>() {

  // handles error cases when the API request gets an error response.
  override fun onSuccess(apiResponse: ApiResponse.Success<T>) = success(apiResponse)

  // handles error cases depending on the status code.
  // e.g., internal server error.
  override fun onError(apiResponse: ApiResponse.Failure.Error) {
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
  override fun onException(apiResponse: ApiResponse.Failure.Exception) {
    apiResponse.run {
      Timber.d(message())
    }
  }
}

disneyService.fetchDisneyPosterList().operator(
    CommonResponseOperator(
      success = {
        emit(data)
        Timber.d("success data: $data")
     }
    )
)
```

By embracing the **Operator** pattern, you can significantly simplify the management of various `ApiResponse` outcomes and promote cleaner, more maintainable code within your application's architecture.

#### Operator With Coroutines

For scenarios where you aim to delegate and operate a suspension lambda using the operator pattern, the `suspendOperator` extension and the `ApiResponseSuspendOperator` class come into play. These tools facilitate the process, as showcased in the examples below:

```kotlin
class CommonResponseOperator<T>(
  private val success: suspend (ApiResponse.Success<T>) -> Unit
) : ApiResponseSuspendOperator<T>() {

  // handles the success case when the API request gets a successful response.
  override suspend fun onSuccess(apiResponse: ApiResponse.Success<T>) = success(apiResponse)

  // ... //
}
```

You can use suspend functions like `emit` in the `success` scope.

```kotlin
val response = disneyService.fetchDisneyPosterList().suspendOperator(
    CommonResponseOperator(
      success = {
        emit(data)
        Timber.d("success data: $data")
      }
    )
)
```

Incorporating the **suspendOperator** extension alongside the **ApiResponseSuspendOperator** class allows you to efficiently manage suspension lambdas in conjunction with the operator pattern, promoting a more concise and maintainable approach within your codebase.

#### Global Operator

The global operator is undoubtedly a robust feature offered by Sandwich. It empowers you to operate on operators globally across all `ApiResponse` instances in your application by employing the `SandwichInitializer`. This way, you can avoid the necessity of creating operator instances for every API call or employing dependency injection for common operations. The following examples illustrate how to use a global operator to handle both `ApiResponse.Failure.Error` and `ApiResponse.Failure.Exception` scenarios. You can leverage the global operator to refresh your user token or implement any other additional processes necessary for specific API requests within your application. The example below demonstrates how you can automatically check and refresh the user token depending on the response status using Sandwich's global operator:

##### Initialize Global Operator

First, it's highly recommended to initialize the global operator in the Application class or using another initialization solution like [App Startup](https://developer.android.com/topic/libraries/app-startup). This ensures that the global operator is set up before any API requests are made.

```kotlin
class SandwichDemoApp : Application() {

  override fun onCreate() {
    super.onCreate()
    
    // We will handle only the error and exceptional cases,
    // so we don't need to mind the generic type of the operator.
    SandwichInitializer.sandwichOperators += listOf(TokenRefreshGlobalOperator<Any>(this))

    // ... //
  }
}
```

By configuring the global operator within `SandwichInitializer`, you enable your application to consistently process and handle various `ApiResponse` situations. This can include tasks such as managing success cases, handling errors, or dealing with exceptions, all on a global scale.

##### Implement Your Global Operator

Create your custom `GlobalResponseOperator` class that extends operators such as `ApiResponseSuspendOperator` and `ApiResponseOperator`. This operator will allow you to define common response handling logic that can be applied globally.

```kotlin
class TokenRefreshGlobalOperator<T> @Inject constructor(
  private val context: Context,
  private val authService: AuthService,
  private val userDataStore: UserDataStore,
  coroutineScope: CoroutineScope,
) : ApiResponseSuspendOperator<T>() {

  private var userToken: UserToken? = null

  init {
    coroutineScope.launch {
      userDataStore.tokenFlow.collect { token ->
        userToken = token
      }
    }
  }

  override suspend fun onError(apiResponse: ApiResponse.Failure.Error) {
    // verify whether the current request was previously issued as an authenticated request
    apiResponse.headers["Authorization"] ?: return

    // refresh an access token if the error response is Unauthorized or Forbidden
    when (apiResponse.statusCode) {
      StatusCode.Unauthorized, StatusCode.Forbidden -> {
        userToken?.let { token ->
          val result = authService.refreshToken(token)
          result.onSuccessSuspend { data ->
            userDataStore.updateToken(
              UserToken(
                accessToken = data.accessToken,
                refreshToken = data.refreshToken,
              ),
            )
            toast(R.string.toast_refresh_token_succeed)
          }.onFailureSuspend {
            toast(R.string.toast_refresh_token_failed)
          }
        }
      }
      else -> Unit
    }
  }

  override suspend fun onSuccess(apiResponse: ApiResponse.Success<T>) = Unit

  override suspend fun onException(apiResponse: ApiResponse.Failure.Exception) = Unit

  private suspend fun toast(@StringRes resource: Int) = withContext(Dispatchers.Main) {
    Toast.makeText(context, resource, Toast.LENGTH_SHORT).show()
  }
}
```

In this example, the global operator's `onError` function is used to automatically check for `Unauthorized` and `Forbidden` status code (HTTP 401 and 403) in the error response. If an unauthorized error occurs, the user token is refreshed, and the failed request is retried with the updated token using runAndRetry. This way, you can seamlessly manage token expiration and refresh for your API requests.

#### Global Operator With Hilt and App Startup

If you want to initialize the global operator by using with [Hilt](https://dagger.dev/hilt/) and [App Startup](https://developer.android.com/topic/libraries/app-startup), you can follow the instructions below.


##### 1. Implement an Entry Point

First, you should implement an entry point for injecting the global operator into an App Startup initializer.

```kotlin
@EntryPoint
@InstallIn(SingletonComponent::class)
interface NetworkEntryPoint {

  fun inject(networkInitializer: NetworkInitializer)

  companion object {

    fun resolve(context: Context): NetworkEntryPoint {
      val appContext = context.applicationContext ?: throw IllegalStateException(
        "applicationContext was not found in NetworkEntryPoint",
      )
      return EntryPointAccessors.fromApplication(
        appContext,
        NetworkEntryPoint::class.java,
      )
    }
  }
}
```

##### 2. Provide Global Operator Dependency

Next, provide your global operator with Hilt like the exambple below:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideTokenRefreshGlobalOperator(
    @ApplicationContext context: Context,
    authService: AuthService,
    userDataStore: UserDataStore
  ): TokenRefreshGlobalOperator<Any> {
    return TokenRefreshGlobalOperator(
      context = context,
      authService = authService,
      userDataStore = userDataStore,
      coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    )
  }
}
```

##### 3. Implement App Startup Initializer

Finally, implement the App Startup Initializer and initialize the Initializer following the [App Startup guidance](https://developer.android.com/topic/libraries/app-startup#manual).

```kotlin
public class NetworkInitializer : Initializer<Unit> {

  @set:Inject
  internal lateinit var tokenRefreshGlobalOperator: TokenRefreshGlobalOperator<Any>

  override fun create(context: Context) {
    NetworkEntryPoint.resolve(context).inject(this)

    SandwichInitializer.sandwichOperators += listOf(tokenRefreshGlobalOperator)
  }

  override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
```

This setup allows you to define a retry policy that determines whether a retry attempt should occur and specifies the retry timeout. The `runAndRetry` extension then encapsulates the execution logic, applying the defined policy, and providing the response in a clean and structured manner.

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
