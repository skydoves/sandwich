# Snapshot

You can download snapshots of Sandwich, which reflect early adopted API changes before shipping to the stable release.

## Including the SNAPSHOT

[![Sandwich](https://img.shields.io/static/v1?label=snapshot&message=sandwich&logo=apache%20maven&color=C71A36)](https://oss.sonatype.org/content/repositories/snapshots/com/github/skydoves/sandwich/) <br>
Snapshots of the current development version of Sandwich are available, which track [the latest versions](https://oss.sonatype.org/content/repositories/snapshots/com/github/skydoves/sandwich/).

To import snapshot versions on your project, add the code snippet below on your gradle file:

=== "Groovy"

    ```Groovy
    repositories {
       maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
    }
    ```

=== "KTS"

    ```kotlin
    repositories {
       maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
    ```


Next, add the dependency below to your **module**'s `build.gradle` file:

=== "Groovy"

    ```Groovy
    dependencies {
        implementation "com.github.skydoves:sandwich:2.0.2-SNAPSHOT"
    }
    ```

=== "KTS"

    ```kotlin
    dependencies {
        implementation("com.github.skydoves:sandwich:2.0.1-SNAPSHOT")
    }
    ```
