# Global Handling

Sandwich provides seamless ways for globally handling responses across all your network and I/O tasks when creating `ApiResponse` instances.

## Global Operator

You can execute your [Operator](operator.md)s globally when creating `ApiResponse` instances by utilizing `SandwichInitializer.sandwichOperators`. For more information, check out the [Global Operator](https://skydoves.github.io/sandwich/operator/#global-operator).

## Global Error/Exception Mapper

You can map all your failure type (ApiResponse.Failure.Error and ApiResponse.Failure.Exception) into your preferred custom error types. For more information, check out the [Global Failure Mapper](https://skydoves.github.io/sandwich/mapper/#global-failure-mapper).

## Define Network Code Ranges

If you're using `sandwich-retrofit`, `sandwich-ktor`, or `sandwich-ktorfit`, you can specify the code range that determines whether your network response should be treated as a success or failure. The default range is between `200` and `299`, but you can adjust the range depending on your situation.

```kotlin
SandwichInitializer.successCodeRange = 200..310
```

## Global Coroutine Scope

Sandwich employs a dedicated global Coroutine scope when you need to perform [Operate](operator.md), [Mapper](mapper.md), or creating deferred responses using Retrofit. The default Coroutine scope is supervised and utilizes the IO dispatcher, but if you want to manage or inject your own Coroutine scope, you can change the scope like the code below:

```kotlin
SandwichInitializer.sandwichScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
```