# TheoTable

TheoTable is a Jetpack Compose table library with a small Kotlin core module for table behavior and a Compose module for rendering table UI.

## Modules

- `core`: sorting and selection state logic.
- `compose`: Jetpack Compose table components.
- `app`: sample app for trying table options.

## Installation

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.hyunchul-kim:theotable-compose:0.2.0")
}
```

Use `theotable-core` directly only when you need the non-UI table logic without Compose.

```kotlin
dependencies {
    implementation("io.github.hyunchul-kim:theotable-core:0.2.0")
}
```

## Requirements

- Android minSdk 21+
- Jetpack Compose
- Kotlin 2.0+

## License

TheoTable is released under the Apache License 2.0. See [LICENSE](LICENSE).
