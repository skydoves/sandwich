# BOM

Sandwich publishes several artifacts that must move together. The Bill of Materials pins them all
from one place, so only the BOM version has to be updated on a release.

```kotlin
dependencies {
    implementation(platform("com.github.skydoves:sandwich-bom:$version"))

    implementation("com.github.skydoves:sandwich")
    implementation("com.github.skydoves:sandwich-retrofit")
    testImplementation("com.github.skydoves:sandwich-test")
}
```

For Kotlin Multiplatform:

```kotlin
sourceSets {
    val commonMain by getting {
        dependencies {
            implementation(project.dependencies.platform("com.github.skydoves:sandwich-bom:$version"))

            implementation("com.github.skydoves:sandwich")
            implementation("com.github.skydoves:sandwich-ktor")
            implementation("com.github.skydoves:sandwich-ktor-serialization")
        }
    }
}
```

The BOM constrains every published artifact:

- `sandwich`
- `sandwich-ktor`
- `sandwich-ktor-serialization`
- `sandwich-ktorfit`
- `sandwich-retrofit`
- `sandwich-retrofit-datasource`
- `sandwich-retrofit-serialization`
- `sandwich-test`

A BOM only supplies versions. It does not add any dependency on its own, so each artifact still has
to be declared.
