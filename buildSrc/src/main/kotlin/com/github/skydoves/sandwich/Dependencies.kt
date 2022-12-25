package com.github.skydoves.sandwich

object Versions {
  internal const val ANDROID_GRADLE_PLUGIN = "7.3.1"
  internal const val ANDROID_GRADLE_SPOTLESS = "6.7.0"
  internal const val GRADLE_NEXUS_PUBLISH_PLUGIN = "1.1.0"
  internal const val KOTLIN = "1.7.21"
  internal const val KOTLIN_SERIALIZATION_JSON = "1.4.0"
  internal const val KOTLIN_GRADLE_DOKKA = "1.7.20"
  internal const val KOTLIN_BINARY_VALIDATOR = "0.12.1"

  internal const val RETROFIT = "2.9.0"
  internal const val OKHTTP = "4.10.0"
  internal const val COROUTINES = "1.6.4"
  internal const val MOSHI = "1.14.0"

  internal const val APPCOMPAT = "1.5.0"
  internal const val MATERIAL = "1.5.0"
  internal const val GLIDE = "4.13.1"
  internal const val LIFECYCLE = "2.5.1"
  internal const val TIMBER = "5.0.1"

  internal const val JUNIT = "4.13.1"
  internal const val ARCH_TEST = "2.1.0"
  internal const val MOCKITO = "3.5.13"
  internal const val MOCKITO_KOTLIN = "2.2.0"
}

object Dependencies {
  const val androidGradlePlugin =
    "com.android.tools.build:gradle:${Versions.ANDROID_GRADLE_PLUGIN}"
  const val gradleNexusPublishPlugin =
    "io.github.gradle-nexus:publish-plugin:${Versions.GRADLE_NEXUS_PUBLISH_PLUGIN}"
  const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}"
  const val spotlessGradlePlugin =
    "com.diffplug.spotless:spotless-plugin-gradle:${Versions.ANDROID_GRADLE_SPOTLESS}"
  const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.KOTLIN_GRADLE_DOKKA}"
  const val kotlinBinaryValidator =
    "org.jetbrains.kotlinx:binary-compatibility-validator:${Versions.KOTLIN_BINARY_VALIDATOR}"
  const val kotlinSerializationPlugin =
    "org.jetbrains.kotlin:kotlin-serialization:${Versions.KOTLIN}"
  const val kotlinSerializationJson =
    "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KOTLIN_SERIALIZATION_JSON}"

  const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
  const val retrofitMoshi = "com.squareup.retrofit2:converter-moshi:${Versions.RETROFIT}"
  const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.OKHTTP}"
  const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}"
  const val moshi = "com.squareup.moshi:moshi-kotlin:${Versions.MOSHI}"
  const val moshiGen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.MOSHI}"

  const val junit = "junit:junit:${Versions.JUNIT}"
  const val mockitoCore = "org.mockito:mockito-core:${Versions.MOCKITO}"
  const val mockitoInline = "org.mockito:mockito-inline:${Versions.MOCKITO}"
  const val archTest = "androidx.arch.core:core-testing:${Versions.ARCH_TEST}"
  const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.OKHTTP}"
  const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.MOCKITO_KOTLIN}"
  const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES}"

  const val appcompat = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
  const val material = "com.google.android.material:material:${Versions.MATERIAL}"
  const val liveDataKTX = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFECYCLE}"
  const val viewModelKTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFECYCLE}"
  const val glide = "com.github.bumptech.glide:glide:${Versions.GLIDE}"
  const val timber = "com.jakewharton.timber:timber:${Versions.TIMBER}"
}
