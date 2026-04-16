# Testing

Sandwich provides a dedicated testing module called **sandwich-test** that offers fake factories and an assertion DSL for testing `ApiResponse` handling in your domain, repository, and presentation layers without requiring any HTTP infrastructure (no MockWebServer, no Ktor MockEngine).

To utilize the testing utilities, add the following dependency:

[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/sandwich.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22sandwich%22)

=== "Groovy"

    ```Groovy
    dependencies {
        testImplementation "com.github.skydoves:sandwich-test:$version"
    }
    ```

=== "KTS"

    ```kotlin
    dependencies {
        testImplementation("com.github.skydoves:sandwich-test:$version")
    }
    ```

For Kotlin Multiplatform, add the dependency below to your module's `build.gradle.kts` file:

```kotlin
sourceSets {
    val commonTest by getting {
        dependencies {
            implementation("com.github.skydoves:sandwich-test:$version")
        }
    }
}
```

## Fake Factories

The `sandwich-test` module provides factory functions on `ApiResponse.Companion` to create deterministic `ApiResponse` instances for testing. Unlike the production factories (`ApiResponse.of`, `ApiResponse.exception`), these factories bypass `SandwichInitializer` global operators and failure mappers, ensuring predictable test behavior.

### fakeSuccess

Creates a fake `ApiResponse.Success` with the given data and optional tag:

```kotlin
val response = ApiResponse.fakeSuccess(data = listOf("Frozen", "Moana"))
val tagged = ApiResponse.fakeSuccess(data = user, tag = "cache")
```

### fakeError

Creates a fake `ApiResponse.Failure.Error` with an optional payload:

```kotlin
val response = ApiResponse.fakeError(payload = "Not found")
val withStatusCode = ApiResponse.fakeError(payload = StatusCode.NotFound)
val empty = ApiResponse.fakeError()
```

### fakeException

Creates a fake `ApiResponse.Failure.Exception` from a `Throwable` or a message string:

```kotlin
val response = ApiResponse.fakeException(throwable = IOException("timeout"))
val fromMessage = ApiResponse.fakeException(message = "network error")
```

## Assertion DSL

The assertion DSL provides type-safe extensions on `ApiResponse` that eliminate manual `instanceOf` checks and unsafe casts. Each assertion returns the narrowed type for chaining. The assertions throw `AssertionError` on failure, making them compatible with any test framework (kotlin.test, JUnit 4/5, Kotest).

### Type Assertions

Assert the response type and get the narrowed instance in one call:

```kotlin
val success = response.assertSuccess()
assertEquals("Frozen", success.data.name)

val error = response.assertError()
assertEquals("Not found", error.payload)

val exception = response.assertException()
assertIs<IOException>(exception.throwable)

val failure = response.assertFailure() // matches both Error and Exception
```

### Block-Style Assertions

Assert the type and run custom assertions within a scoped block:

```kotlin
response.assertSuccess {
    assertEquals("Frozen", data.name)
    assertNotNull(tag)
}

response.assertError {
    assertEquals(StatusCode.Forbidden, payload)
}

response.assertException {
    assertEquals("timeout", message)
}

response.assertFailure {
    // executes for both Error and Exception
}
```

### Convenience Assertions

Common one-liner assertions for the most frequent checks:

```kotlin
// Assert success and verify data equality
response.assertSuccessData(expectedUser)

// Assert exception and verify message
response.assertExceptionMessage("timeout")
```

## Complete Testing Example

Here's a practical example of testing a repository that uses Sandwich:

```kotlin
class UserRepositoryTest {

    private val fakeApi = FakeUserApi()
    private val repository = UserRepository(fakeApi)

    @Test
    fun fetchUserReturnsUserOnSuccess() {
        fakeApi.response = ApiResponse.fakeSuccess(data = User("Alice", 30))

        val result = repository.fetchUser("alice")

        result.assertSuccess {
            assertEquals("Alice", data.name)
            assertEquals(30, data.age)
        }
    }

    @Test
    fun fetchUserReturnsErrorOnNotFound() {
        fakeApi.response = ApiResponse.fakeError(payload = "user not found")

        val result = repository.fetchUser("unknown")

        result.assertError {
            assertEquals("user not found", payload)
        }
    }

    @Test
    fun fetchUserReturnsExceptionOnNetworkFailure() {
        fakeApi.response = ApiResponse.fakeException(message = "network timeout")

        val result = repository.fetchUser("alice")

        result.assertExceptionMessage("network timeout")
    }
}
```
