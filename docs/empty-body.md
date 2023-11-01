# Empty Body (No Content 204)

Sandwich seamlessly handles responses with an empty body ([204 No Content](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/204)) without requiring any additional effort.

You can simply set the `ApiResponse` type to `Unit`, as shown in the example below:

```kotlin
interface AuthService {
    @Delete("/auth/user")
    suspend fun deleteToken(@Body userToken: UserToken): ApiResponse<Unit>
}
```