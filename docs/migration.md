# Migration Guide

This is a migration guide from **1.x** to **2.0.0**.


## 1. Dependencies

First, you should use add additional libraries to fully migrate from 1.x version.

```groovy
implementation("com.github.skydoves:sandwich:$version")
implementation("com.github.skydoves:sandwich-retrofit:$version")
implementation("com.github.skydoves:sandwich-retrofit-serialization:$version")
```

## 2. AdapterFactory and Interceptor

If you were using `ApiResponseCallAdapterFactory` and `EmptyBodyInterceptor`, the package name should be change like so:

```diff
- import com.skydoves.sandwich.adapters.ApiResponseCallAdapterFactory
+ import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory

- import com.skydoves.sandwich.interceptors.EmptyBodyInterceptor
+ import com.skydoves.sandwich.retrofit.interceptors.EmptyBodyInterceptor
```

## 3. Operator

If you're using [Operator](operator.md), the override function should be change like the code below:

```diff
class TestApiResponseSuspendOperator<T>(
  private val onSuccess: suspend () -> Unit,
  private val onError: suspend () -> Unit,
  private val onException: suspend () -> Unit,
) : ApiResponseSuspendOperator<T>() {

  override suspend fun onSuccess(apiResponse: ApiResponse.Success<T>) = onSuccess()

-  override suspend fun onError(apiResponse: ApiResponse.Failure.Error<T>) = onError()
+  override suspend fun onError(apiResponse: ApiResponse.Failure.Error) = onError()

-  override suspend fun onException(apiResponse: ApiResponse.Failure.Exception<T>) = onException()
+  override suspend fun onException(apiResponse: ApiResponse.Failure.Exception) = onException()
}
```

## 4. ApiResponse.error(throwable)

The previous `ApiResponse.error(throwable)` function was renamed to `ApiResponse.exception(throwable)`.

```diff
- val apiResponse = ApiResponse.error(throwable)
+ val apiResponse = ApiResponse.exception(throwable)
```