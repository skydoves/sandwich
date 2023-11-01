# Merge

You can merge `ApiResponse`s that contain a `List` as their generic type into a single `ApiResponse` based on specific policies. The example below shows how to merge three `ApiResponse` as a single one if each three `ApiResponse`s are successful.

## Merging Multiple ApiResponse

Sandwich allows you to merge multiple `ApiResponse` instances into a single one based on predefined policies. This is particularly useful when you need to combine the results of multiple API calls. The following example demonstrates how to merge three `ApiResponse` instances into a single one, provided that all three `ApiResponse` instances are successful:

```kotlin
disneyService.fetchDisneyPosterList(page = 0).merge(
   disneyService.fetchDisneyPosterList(page = 1),
   disneyService.fetchDisneyPosterList(page = 2),
   mergePolicy = ApiResponseMergePolicy.PREFERRED_FAILURE
).onSuccess { 
  // handles the success case when the merged API requests all receive successful responses.
}.onError { 
  // handles error cases when at least one of the merged API requests gets an error response.
}
```

## ApiResponseMergePolicy

`ApiResponseMergePolicy` is an enum that defines how merging should be performed based on the success or failure of the responses. There are two policies available:

- **IGNORE_FAILURE**: This policy ignores failure responses and considers only the successful ones when merging the `ApiResponse` instances, regardless of the order in which they are merged.
- **PREFERRED_FAILURE** (default): This policy prefers failure responses over success responses when merging. Even if there is one failure response in the merged list, the final merged `ApiResponse` will be marked as a failure.

By choosing an appropriate merge policy, you can tailor the merging behavior to your specific requirements, ensuring that the merged `ApiResponse` accurately represents the combined results of the individual API calls.
