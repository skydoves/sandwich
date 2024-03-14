# Overview

![banner](https://user-images.githubusercontent.com/24237865/162602054-2010d249-8a81-4673-b9ae-1edff1080ab7.png)

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

🥪  Sandwich is an adaptable and lightweight sealed API library designed for handling API responses and exceptions in Android for [Retrofit](retrofit.md), and Kotlin Multiplatform for [Ktor](ktor.md), and [Ktorfit](ktorfit.md).

## Why Sandwich?

Sandwich was conceived to streamline the creation of standardized interfaces to model responses from Retrofit, Ktor, and whatever. This library empowers you to handle body data, errors, and exceptional cases more succinctly, utilizing functional operators within a multi-layer architecture. With Sandwich, the need to create wrapper classes like Resource or Result is eliminated, allowing you to concentrate on your core business logic. Sandwich boasts features such as [global response handling](global.md), [Mapper](mapper.md), [Operator](operator.md), and exceptional compatibility, including [ApiResponse With Coroutines](https://skydoves.github.io/sandwich/sandwich/apiresponse/#apiresponse-extensions-with-coroutines).

## Download
[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/sandwich.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22sandwich%22)

Sandwich has achieved an impressive milestone, being downloaded in __over 300,000__ Android projects worldwide! <br>

### Gradle

<img src="https://user-images.githubusercontent.com/24237865/103460609-f18ee000-4d5a-11eb-81e2-17696e3a5804.png" width="774" height="224"/>

Add the dependency below into your **module**'s `build.gradle` file:

=== "Groovy"

    ```Groovy
    dependencies {
        implementation "com.github.skydoves:sandwich:$version"
    }
    ```

=== "KTS"

    ```kotlin
    dependencies {
        implementation("com.github.skydoves:sandwich:$version")
    }
    ```

For Kotlin Multiplatform, add the dependency below to your module's `build.gradle.kts` file:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation("com.github.skydoves:sandwich:$version")
        }
    }
}
```

## References

You can delve deeper into the art of modeling Retrofit responses through the following resources:

- [[YouTube]: Modeling Retrofit responses with sealed classes and coroutines with Jaewoong Eum](https://youtu.be/agjbbn9Swkc?feature=shared)
- [Modeling Retrofit Responses With Sealed Classes and Coroutines](https://getstream.io/blog/modeling-retrofit-responses/)
- [Handling success data and error callback responses from a network for Android projects using Sandwich](https://proandroiddev.com/handling-success-data-and-error-callback-responses-from-a-network-for-android-projects-using-b53a26214cef)

## Use Cases

You can also check out nice use cases of this library in the repositories below:

- [Pokedex](https://github.com/skydoves/pokedex): 🗡️ Android Pokedex using Hilt, Motion, Coroutines, Flow, Jetpack (Room, ViewModel, LiveData) based on MVVM architecture.
- [ChatGPT Android](https://github.com/skydoves/chatgpt-android): 📲 ChatGPT Android demonstrates OpenAI's ChatGPT on Android with Stream Chat SDK for Compose.
- [DisneyMotions](https://github.com/skydoves/DisneyMotions): 🦁 A Disney app using transformation motions based on MVVM (ViewModel, Coroutines, LiveData, Room, Repository, Koin) architecture.
- [MarvelHeroes](https://github.com/skydoves/marvelheroes): ❤️ A sample Marvel heroes application based on MVVM (ViewModel, Coroutines, LiveData, Room, Repository, Koin)  architecture.
- [Neko](https://github.com/CarlosEsco/Neko): Free, open source, unofficial MangaDex reader for Android.
- [TheMovies2](https://github.com/skydoves/TheMovies2): 🎬 A demo project using The Movie DB based on Kotlin MVVM architecture and material design & animations.