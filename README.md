# AI Vacation Planner — Android

![CI](https://github.com/rodneymbrown1/Android---AI-Vacation-Booking-App-PoC/actions/workflows/android-ci.yml/badge.svg)

An Android application that helps users plan vacations and generate AI-powered excursion suggestions using the OpenAI API. Built with Clean Architecture, MVVM, Hilt DI, Jetpack Compose, Room, and a modularized Gradle build.

## Architecture

This project follows **Clean Architecture** with an **MVVM** presentation pattern, **Unidirectional Data Flow (UDF)**, and a **multi-module Gradle build** — the same core/feature module shape used by Google's recommended app architecture (core/data, core/database, core/network, core/ui, core/common, feature/\*).

```
┌───────────────────────────────────────────────────────────────────┐
│                              :app                                  │
│        MainActivity · VacationApplication · Nav Host                │
└───────────────────┬─────────────────────────────┬─────────────────┘
                     │                             │
        ┌────────────▼────────────┐   ┌────────────▼────────────┐
        │   :feature:vacation      │──►│   :feature:excursion     │
        │  VacationViewModel        │   │  ExcursionViewModel      │
        │  VacationManager/Detail   │   │  ExcursionSelectionRow   │
        └────────────┬─────────────┘   └────────────┬─────────────┘
                     │                                │
        ┌────────────▼────────────────────────────────▼────────────┐
        │                        :core:data                          │
        │     VacationRepository / ExcursionRepository (+ impls)      │
        │     Flow-based reads, typed AppError mapping                │
        └──────────────┬───────────────────────────┬─────────────────┘
                        │                            │
          ┌─────────────▼─────────────┐  ┌───────────▼─────────────┐
          │      :core:database        │  │      :core:network       │
          │  Room: AppDatabase/DAOs     │  │  Ktor: AiService (OpenAI) │
          │  entities: Vacation,        │  │  timeout + retry/backoff  │
          │  Excursion                  │  │                            │
          └─────────────┬───────────────┘  └───────────┬─────────────┘
                        │                                │
                        └───────────────┬────────────────┘
                                        ▼
                              ┌───────────────────┐
                              │    :core:common     │
                              │ AppError · DateValidator │
                              └───────────────────┘

              :core:ui — shared Compose theme + LabeledInputField
              (used by both feature modules)
```

Only `:app` applies the Hilt Gradle plugin; every library/feature module adds `hilt-android` + the KSP compiler per Hilt's documented multi-module setup, contributing its own `@Module`/`@Binds`/`@Provides` (`DatabaseModule`, `RepositoryModule`) to the graph that `:app` aggregates.

**Design decisions:**
- ViewModels survive configuration changes — no data re-fetched on rotation
- Repository interfaces mean tests mock the interface, not the database
- Room DAOs expose `Flow`-based observing queries; `VacationViewModel` and `ExcursionViewModel` are fully reactive — no manual reload calls after insert/update/delete, Room's invalidation tracking does it
- A single sealed `AppError` (network / API / parse / database / validation) replaces raw exception strings, so the UI can show a specific, actionable message instead of `e.message`
- StateFlow + UDF means one source of truth for each screen, no inconsistent intermediate states
- Hilt (KSP) manages the full dependency graph with zero manual wiring, split across per-module DI modules instead of one monolithic `AppModule`

---

## What It Does

This app provides a complete vacation planning workflow:

1. **Create Vacations** - Enter a destination title, hotel name, and start/end dates
2. **AI-Generated Excursions** - Once vacation details are saved, the app calls the OpenAI API to automatically suggest 3 unique excursions tailored to the destination and dates
3. **Manage Excursions** - Add, edit, or delete excursions manually alongside the AI-generated ones
4. **Share Itineraries** - Copy vacation details and excursions to the clipboard for sharing

### App Flow

```
Vacation Manager Screen                  Vacation Detail Screen
┌─────────────────────────┐    Next     ┌──────────────────────────┐
│ - Add/Edit/Delete       │ ─────────►  │ - View vacation details  │
│   vacations             │             │ - AI-generated excursions│
│ - Date validation       │  ◄──────── │ - Manual excursion CRUD  │
│ - List all vacations    │    Back     │ - Share to clipboard     │
└─────────────────────────┘             └──────────────────────────┘
```

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture, 8-module Gradle build |
| DI | Hilt (KSP), per-module DI modules |
| Navigation | Jetpack Navigation Compose |
| Database | Room (SQLite), `Flow`-based reactive queries |
| Networking | Ktor Client (OkHttp engine), timeout + exponential-backoff retry |
| Serialization | kotlinx.serialization |
| Async | Kotlin Coroutines + StateFlow |
| AI | OpenAI GPT-4o-mini |
| Testing | JUnit 4, MockK, Robolectric, kotlinx-coroutines-test |
| Static analysis | ktlint, detekt, Android Lint |
| Coverage | Jacoco (report generation; see [SUGGESTIONS.md](SUGGESTIONS.md) for the coverage-gate roadmap) |
| CI/CD | GitHub Actions — static analysis, unit tests, emulator instrumented tests, signed release pipeline |
| Dependency hygiene | Dependabot (Gradle + GitHub Actions, weekly) |
| Release | Signed AAB + GitHub Release on tag push (Play Store publishing intentionally not wired — see SUGGESTIONS.md) |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |

## Module Graph

```
:app                    application shell, DI composition root, nav host
:core:common            AppError, DateValidator
:core:database           Room: AppDatabase, DAOs, entities
:core:network           Ktor: AiService, OpenAI DTOs, timeout + retry
:core:data              Repository interfaces + impls
:core:ui                Compose theme, shared components
:feature:vacation       Vacation screens/ViewModel/UiState
:feature:excursion      Excursion screens/ViewModel/UiState
```

`:feature:vacation` depends on `:feature:excursion` (the vacation detail screen composes the excursion selection UI); there are no other feature-to-feature or core-to-feature dependencies.

## Project Structure

```
core/common/src/main/java/.../core/common/
├── AppError.kt                        # Sealed network/API/parse/database/validation errors
└── DateValidator.kt                   # Single source of truth for YYYY-MM-DD validation

core/database/src/main/java/.../core/database/
├── AppDatabase.kt
├── dao/{VacationDao,ExcursionDao}.kt   # suspend + Flow-based observe queries
├── entity/{Vacation,Excursion}.kt
└── di/DatabaseModule.kt

core/network/src/main/java/.../core/network/
├── AiService.kt                       # Ktor client: HttpTimeout + HttpRequestRetry
└── model/OpenAIModels.kt

core/data/src/main/java/.../core/data/
├── repository/{Vacation,Excursion}Repository[Impl].kt
└── di/RepositoryModule.kt

core/ui/src/main/java/.../core/ui/
├── theme/{Color,Theme,Type}.kt
└── components/LabeledInputField.kt

feature/vacation/src/main/java/.../feature/vacation/
├── VacationManager.kt · VacationDetailView.kt · ShareVacationDetails.kt
└── VacationViewModel.kt · VacationUiState.kt

feature/excursion/src/main/java/.../feature/excursion/
├── ExcursionDropdown.kt · ExcursionSelectionRow.kt
└── ExcursionViewModel.kt · ExcursionUiState.kt

app/src/main/java/com/example/learning_2/
├── MainActivity.kt                    # Entry point
├── VacationApplication.kt             # @HiltAndroidApp
└── components/VacationApp.kt          # Navigation host
```

## Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 17**
- **Android SDK 34** installed
- **An Android device or emulator** running API 26+
- **OpenAI API key** (for AI excursion generation)

## Setup & Configuration

1. **Clone the repository**
   ```bash
   git clone https://github.com/<your-username>/Android---AI-Vacation-Booking-App-PoC.git
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select **File > Open** and navigate to the cloned project directory
   - Wait for Gradle sync to complete

3. **Configure the OpenAI API key**

   Add your key to `local.properties` (this file is git-ignored and must never be committed):

   ```properties
   OPENAI_API_KEY=sk-your-key-here
   ```

   The build system injects this into `core:network`'s `BuildConfig.OPENAI_API_KEY` automatically. For CI, add it as a GitHub Actions repository secret named `OPENAI_API_KEY` — the workflow forwards it to Gradle as `ORG_GRADLE_PROJECT_OPENAI_API_KEY`, which is what actually makes it visible to `project.findProperty("OPENAI_API_KEY")`.

## How to Run

### Run the App

1. Connect an Android device (with USB debugging enabled) or start an emulator
2. In Android Studio, select your target device from the toolbar
3. Click **Run** (green play button) or press `Shift + F10`

Alternatively, from the command line:
```bash
./gradlew installDebug
```

### Run Static Analysis

```bash
./gradlew ktlintCheck detekt lint
```

### Run Unit Tests + Coverage

```bash
./gradlew testDebugUnitTest jacocoTestReport
```

### Run Instrumented Tests

Requires a connected device or running emulator:
```bash
./gradlew connectedDebugAndroidTest
```

### Cut a Release

Push a `v*.*.*` tag — `android-release.yml` builds a signed AAB (falls back to debug signing if `KEYSTORE_BASE64` isn't configured) and attaches it to a GitHub Release. Play Store publishing isn't wired up (no Play Console account for this project); see [SUGGESTIONS.md](SUGGESTIONS.md).

## Database Schema

The app uses two Room entities with a foreign key relationship:

```
┌──────────────────┐       ┌──────────────────────┐
│     Vacation      │       │      Excursion        │
├──────────────────┤       ├──────────────────────┤
│ id (PK, auto)    │◄──┐   │ id (PK, auto)        │
│ title            │   └───│ vacation_id (FK)      │
│ hotel            │       │ name                  │
│ start_date       │       │ description           │
│ end_date         │       │ date                  │
└──────────────────┘       └──────────────────────┘
                           (CASCADE on delete)
```

## Key Features

- **AI-Powered Suggestions** - Automatically generates 3 excursions per vacation using GPT-4o-mini, with timeout + exponential-backoff retry on transient failures
- **Date Validation** - Enforces YYYY-MM-DD format and ensures excursion dates fall within the vacation range (single shared `DateValidator`)
- **Cascade Deletion** - Deleting a vacation removes all associated excursions
- **Deletion Constraints** - Vacations with excursions prompt the user before deletion
- **Clipboard Sharing** - Share formatted vacation itineraries
- **Material 3 Theming** - Supports light/dark mode and Android 12+ dynamic colors

## Security

API credentials are never hardcoded in source. The build reads `OPENAI_API_KEY` from:
- **Local development:** `local.properties` (git-ignored)
- **CI:** GitHub Actions repository secret

## Known Limitations / Future Work

See [SUGGESTIONS.md](SUGGESTIONS.md) for the full roadmap (Baseline Profiles, WorkManager offline sync, Paging 3, Crashlytics, accessibility testing, a `build-logic` convention-plugin setup, real Play Console deployment, and more). Headline items:

- No user authentication
- No offline caching of AI responses
- No coverage-floor gate yet (Jacoco reports are generated, but nothing fails the build below a threshold until there's a real baseline)
