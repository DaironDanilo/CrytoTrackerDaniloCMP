# CryptoTracker — Kotlin Multiplatform

A cryptocurrency tracking app built with **Compose Multiplatform 1.11.0**, targeting Android, iOS, Desktop (JVM), and Web (Wasm). It displays live coin prices and historical charts sourced from the [CoinAPI](https://www.coinapi.io/) REST API.

<table>
  <tr>
    <th>Android</th>
    <th>iOS</th>
  </tr>
  <tr>
    <td><video src="https://github.com/user-attachments/assets/addecced-d20f-42d6-a02d-27293ce14f4d"></td>
    <td><video src="https://github.com/user-attachments/assets/d69b64ae-29c7-4c4c-8144-edc40bcc315f"></td>
  </tr>
  <tr>
    <th colspan="2">Desktop</th>
  </tr>
  <tr>
    <td colspan="2"><video src="https://github.com/user-attachments/assets/0e80f837-89d0-407e-a636-1b4a4b92390f"></td>
  </tr>
  <tr>
    <th colspan="2">Web</th>
  </tr>
  <tr>
    <td colspan="2"><video src="https://github.com/user-attachments/assets/48bc0159-ca51-45b8-b8b4-b79aa8e8f44b"></td>
  </tr>
</table>

---

## Features

- Live cryptocurrency list with price and 24 h change
- Coin detail screen with a custom line chart of historical prices
- Adaptive two-pane layout on large screens (tablet / desktop)
- Shared UI and business logic across all four platforms
- Native iOS text input (introduced in CMP 1.11.0)

---

## Tech Stack

| Layer | Library |
|---|---|
| UI | [Compose Multiplatform 1.11.0](https://github.com/JetBrains/compose-multiplatform) · Material 3 |
| Navigation | [Navigation Compose (multiplatform)](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-navigation-routing.html) · Adaptive layout |
| Networking | [Ktor 3.4.3](https://ktor.io/) (OkHttp on Android/Desktop · Darwin on iOS · JS on Web) |
| Serialization | [kotlinx.serialization 1.11.0](https://github.com/Kotlin/kotlinx.serialization) |
| Image loading | [Coil 3.4.0](https://coil-kt.github.io/coil/) |
| DI | [Koin 4.2.1](https://insert-koin.io/) |
| Date/time | [kotlinx-datetime 0.8.0](https://github.com/Kotlin/kotlinx-datetime) |
| Build secrets | [BuildKonfig 0.20.0](https://github.com/yshrsmz/BuildKonfig) |

---

## Prerequisites

| Tool | Required version |
|---|---|
| JDK | 17+ (11 minimum; 17+ needed for native desktop packaging) |
| Android Studio | Meerkat (2024.3) or newer — includes the KMP plugin |
| Android SDK | compileSdk **37** · minSdk **24** |
| Xcode | 16+ (macOS only, required for iOS targets) |
| Kotlin Multiplatform plugin | Bundled with Android Studio Meerkat+ |

> **iOS note:** The project supports `iosArm64` (physical device) and `iosSimulatorArm64` (Apple Silicon simulator). The `iosX64` target was removed in CMP 1.11.0.

---

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/DaironDanilo/CrytoTrackerDaniloCMP.git
cd CrytoTrackerDaniloCMP
```

### 2. Obtain a CoinAPI key

1. Go to [coinapi.io](https://www.coinapi.io/) and create a free account.
2. Copy your API key from the dashboard.

### 3. Create `secrets.properties`

Create a file named `secrets.properties` in the **root** of the project (it is already listed in `.gitignore` and will never be committed):

```properties
COIN_API_KEY=your_api_key_here
```

The key is injected at build time via [BuildKonfig](https://github.com/yshrsmz/BuildKonfig) and is accessed inside the app as `BuildKonfig.COIN_API_KEY`. Without this file the build will fail.

### 4. Configure Android SDK path (first-time only)

Android Studio usually creates `local.properties` automatically. If it is missing, create it in the project root:

```properties
sdk.dir=/path/to/your/Android/Sdk
```

---

## Running the App

### Android

Open the project in Android Studio and run the **androidApp** configuration, or from the terminal:

```bash
./gradlew :androidApp:installDebug
```

### iOS (macOS only)

Open the project in Android Studio (or Fleet) and run the **iosApp** scheme, which builds the shared framework and launches the Xcode-managed app. Alternatively, open `iosApp/iosApp.xcodeproj` in Xcode and run from there.

Before the first iOS run, set your development team in Xcode:

1. Open `iosApp/iosApp.xcodeproj`
2. Select the **iosApp** target → **Signing & Capabilities**
3. Choose your Apple Developer team

### Desktop (JVM)

```bash
./gradlew :composeApp:run
```

To produce a distributable package (`.dmg` / `.msi` / `.deb`):

```bash
./gradlew :composeApp:packageDistributionForCurrentOS
```

### Web (Kotlin/Wasm)

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

Open the URL printed in the console (typically `http://localhost:8080`).

---

## Project Structure

```
├── androidApp/                  # Android application entry point
│   └── src/
│       └── main/
│           └── kotlin/          # MainActivity → sets up Koin + Compose
├── composeApp/                  # Shared KMP module (library)
│   └── src/
│       ├── commonMain/          # Shared UI, domain, and data code
│       │   └── kotlin/com/cryptodanilo/project/
│       │       ├── core/        # Networking, navigation, utilities
│       │       ├── crypto/      # Feature: coin list & coin detail
│       │       │   ├── data/    # DTOs, mappers, RemoteCoinDataSource
│       │       │   ├── domain/  # Coin, CoinPrice, CoinDataSource
│       │       │   └── presentation/ # ViewModels, State, Actions, Events
│       │       ├── di/          # Koin modules
│       │       └── ui/theme/    # Material 3 theme, colors, typography
│       ├── androidMain/         # Android-specific implementations
│       ├── iosMain/             # iOS-specific implementations
│       ├── desktopMain/         # Desktop entry point (main.kt)
│       └── wasmJsMain/          # Web entry point (main.kt)
├── gradle/
│   └── libs.versions.toml       # Version catalog
├── secrets.properties           # API key — NOT committed (see .gitignore)
└── local.properties             # Android SDK path — NOT committed
```

### Architecture

The shared module follows a clean architecture with an MVI-inspired presentation layer:

```
UI (Compose) ──► Action ──► ViewModel ──► UseCase / DataSource ──► Ktor / API
                  ◄─── State ◄───────────────────────────────────────────────
                  ◄─── Event (one-shot) ◄──────────────────────────────────
```

- **`domain`** — plain Kotlin models and interfaces (`CoinDataSource`)
- **`data`** — Ktor-based implementation, DTOs, mappers
- **`presentation`** — `ViewModel` + `UiState` + `Action` + `Event` per screen
- **`core/navigation`** — `AdaptiveCoinListDetailPane` uses Material 3 adaptive layout for a responsive two-pane experience on wide screens

---

## Gradle Tasks Reference

| Task | Description |
|---|---|
| `./gradlew :androidApp:installDebug` | Build and install Android debug APK |
| `./gradlew :composeApp:run` | Run Desktop app |
| `./gradlew :composeApp:wasmJsBrowserDevelopmentRun` | Run Web app in browser |
| `./gradlew :composeApp:packageDistributionForCurrentOS` | Package Desktop distributable |
| `./gradlew build` | Build all targets |

---

## Contributing

1. Fork the repository and create a feature branch (`git checkout -b feat/my-feature`).
2. Ensure `secrets.properties` is in place before building.
3. Run the app on at least one target to verify your changes.
4. Open a pull request against `main`.

---

## License

This project is open source. See [LICENSE](LICENSE) for details.