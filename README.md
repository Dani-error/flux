<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/Dani-error/flux/refs/heads/main/.github/assets/logo-dark.svg">
  <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/Dani-error/flux/refs/heads/main/.github/assets/logo-light.svg">
  <img alt="Flux logo" src="https://raw.githubusercontent.com/Dani-error/flux/refs/heads/main/.github/assets/logo-dark.svg">
</picture>

<br/>

## Features

- **Lightweight** event bus with zero dependencies
- **Lambda and annotation** listener support
- **Prioritisation** and **cancellation** system
- **Kotlin-first**, with **Java interop**
- **Full unit test coverage**
- **Single module**: simple to use and integrate

---

## Usage

<details open>
  <summary><strong>Gradle</strong></summary>

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.dani-error:flux:1.0.0")
}
```
</details>

<details>
  <summary><strong>Maven</strong></summary>

```xml
<dependency>
  <groupId>io.github.dani-error</groupId>
  <artifactId>flux</artifactId>
  <version>1.0.0</version>
</dependency>
```

</details>

---

## Example


```kotlin
class ExampleEvent : Event

val bus = EventBus()

bus.register<ExampleEvent> { event ->
    println("Received event")
}

bus.post(ExampleEvent())
```

Or using annotations:

```kotlin
class Listener {
    @Subscribe
    fun handle(event: ExampleEvent) {
        println("Event received")
    }
}

val bus = EventBus()
bus.register(Listener())
bus.post(ExampleEvent())
```

---

## Documentation

Javadocs and examples can be found on the [GitHub wiki](https://github.com/Dani-error/flux/wiki).

---

## Contributing

Contributions welcome!
1. Fork the repo.
2. Create a feature branch:

```bash
git checkout -b feature/<your-feature>
```

3. Commit your changes and open a Pull Request to `main`.

Open an issue first for major changes.

---

## License

Flux is licensed under the [MIT License](https://github.com/Dani-error/flux/blob/main/LICENSE).
