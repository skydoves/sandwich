# R8 full mode strips signatures from non-kept items.
-keep,allowobfuscation,allowshrinking interface com.skydoves.sandwich.ApiResponse
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking interface kotlinx.coroutines.Deferred