# Operator

The **Operator** feature stands out as one of the most powerful capabilities provided by Sandwich. It empowers you to establish well-defined, preconfigured processors for your `ApiResponse` instances. This enables you to encapsulate and reuse a consistent sequence of procedures across your API requests.

You can streamline the handling of `onSuccess`, `onError`, and `onException` scenarios by utilizing the `operator` extension alongside the `ApiResponseOperator`. **Operator** proves particularly valuable when you're aiming for global handling of `ApiResponse` instances and wish to minimize boilerplate code within your `ViewModel` and `Repository` classes. Here are a few illustrative examples:

```kotlin
/** A common response operator for handling [ApiResponse]s regardless of its type. */
class CommonResponseOperator<T> constructor(
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

## Operator With Coroutines

For scenarios where you aim to delegate and operate a suspension lambda using the operator pattern, the `suspendOperator` extension and the `ApiResponseSuspendOperator` class come into play. These tools facilitate the process, as showcased in the examples below:

```kotlin
class CommonResponseOperator<T> constructor(
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

## Global Operator

The global operator is undoubtedly a robust feature offered by Sandwich. It empowers you to operate on operators globally across all `ApiResponse` instances in your application by employing the `SandwichInitializer`. This way, you can avoid the necessity of creating operator instances for every API call or employing dependency injection for common operations. The following examples illustrate how to use a global operator to handle both `ApiResponse.Failure.Error` and `ApiResponse.Failure.Exception` scenarios. You can leverage the global operator to refresh your user token or implement any other additional processes necessary for specific API requests within your application. The example below demonstrates how you can automatically check and refresh the user token depending on the response status using Sandwich's global operator:

### Initialize Global Operator

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

### Implement Your Global Operator

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

## Global Operator With Hilt and App Startup

If you want to initialize the global operator by using with [Hilt](https://dagger.dev/hilt/) and [App Startup](https://developer.android.com/topic/libraries/app-startup), you can follow the instructions below.


### 1. Implement an Entry Point

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

### 2. Provide Global Operator Dependency

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

### 3. Implement App Startup Initializer

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
