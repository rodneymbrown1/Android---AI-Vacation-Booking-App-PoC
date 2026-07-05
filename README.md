# AI Vacation Planner — Android

![CI](https://github.com/rodneymbrown1/Android---AI-Vacation-Booking-App-PoC/actions/workflows/android-ci.yml/badge.svg)

An Android application that helps users plan vacations and generate AI-powered excursion suggestions using the OpenAI API. Built with Clean Architecture, MVVM, Hilt DI, Jetpack Compose, and Room.

## Architecture

This project follows **Clean Architecture** with an **MVVM** presentation pattern and **Unidirectional Data Flow (UDF)**.

```
┌─────────────────────────────────────────────┐
│              Presentation Layer              │
│  VacationViewModel  |  ExcursionViewModel   │
│  VacationUiState    |  ExcursionUiState     │
│         StateFlow -> collectAsState()        │
└──────────────────┬──────────────────────────┘
                   │ depends on interfaces
┌──────────────────▼──────────────────────────┐
│               Domain Layer                   │
│  VacationRepository  (interface)            │
│  ExcursionRepository (interface)            │
└──────────────────┬──────────────────────────┘
                   │ implemented by
┌──────────────────▼──────────────────────────┐
│                Data Layer                    │
│  VacationRepositoryImpl  (Room DAOs)        │
│  ExcursionRepositoryImpl (Room DAOs)        │
│  AiService               (Ktor / OpenAI)    │
└─────────────────────────────────────────────┘
         All wired together by Hilt DI
```

**Design decisions:**
- ViewModels survive configuration changes — no data re-fetched on rotation
- Repository interfaces mean tests mock the interface, not the database
- StateFlow + UDF means one source of truth for each screen, no inconsistent intermediate states
- Hilt (KSP) manages the full dependency graph with zero manual wiring

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
| Language | Kotlin (primary), Java (legacy HTTP client) |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt (KSP) |
| Navigation | Jetpack Navigation Compose |
| Database | Room (SQLite) |
| Networking | Ktor Client (OkHttp engine) |
| Serialization | kotlinx.serialization |
| Async | Kotlin Coroutines + StateFlow |
| AI | OpenAI GPT-4o-mini |
| Testing | JUnit 4, MockK, kotlinx-coroutines-test |
| CI/CD | GitHub Actions |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |

## Project Structure

```
app/src/main/java/com/example/learning_2/
├── MainActivity.kt                    # Entry point
├── components/
│   ├── VacationApp.kt                 # Navigation host
│   ├── VacationManager.kt            # Vacation CRUD screen
│   ├── VacationDetailView.kt         # Detail view + AI excursions
│   ├── VacationForm.kt               # Vacation input form
│   ├── VacationList.kt               # Vacation list display
│   ├── ExcursionDropdown.kt          # Excursion selection dropdown
│   ├── ExcursionListView.kt          # Excursion list management
│   ├── ExcursionSelectionRow.kt      # Dropdown + edit/delete controls
│   ├── ShareVacationDetails.kt       # Clipboard sharing
│   ├── functions/
│   │   └── LabeledInputField.kt      # Reusable text input component
│   └── HTTP/
│       └── OpenAIConnection.java      # OpenAI API client
├── dao/
│   ├── VacationDao.kt                 # Vacation data access
│   └── ExcursionDao.kt               # Excursion data access
├── database/
│   ├── AppDatabase.kt                 # Room database definition
│   └── AppDatabaseProvider.kt         # Singleton database provider
└── entities/
    ├── Vacation.kt                    # Vacation entity (id, title, hotel, dates)
    └── Excursion.kt                   # Excursion entity (id, name, description, date, vacationId)
```

## Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 11** or higher
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

   The build system injects this into `BuildConfig.OPENAI_API_KEY` automatically. For CI, add it as a GitHub Actions repository secret named `OPENAI_API_KEY`.

## How to Run

### Run the App

1. Connect an Android device (with USB debugging enabled) or start an emulator
2. In Android Studio, select your target device from the toolbar
3. Click **Run** (green play button) or press `Shift + F10`

Alternatively, from the command line:
```bash
./gradlew installDebug
```

### Run Unit Tests

```bash
./gradlew test
```

This runs:
- **HelloWorldTest** - Basic sanity test
- **DatabaseTest** - Room database CRUD operations (uses Robolectric)
- **OpenAIConnectionTest** - Validates API connectivity and response format

### Run Instrumented Tests

Requires a connected device or running emulator:
```bash
./gradlew connectedAndroidTest
```

This runs:
- **ExampleInstrumentedTest** - Verifies app context and resource loading

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

- **AI-Powered Suggestions** - Automatically generates 3 excursions per vacation using GPT-4o-mini
- **Date Validation** - Enforces YYYY-MM-DD format and ensures excursion dates fall within the vacation range
- **Cascade Deletion** - Deleting a vacation removes all associated excursions
- **Deletion Constraints** - Vacations with excursions prompt the user before deletion
- **Clipboard Sharing** - Share formatted vacation itineraries
- **Material 3 Theming** - Supports light/dark mode and Android 12+ dynamic colors

## Security

API credentials are never hardcoded in source. The build reads `OPENAI_API_KEY` from:
- **Local development:** `local.properties` (git-ignored)
- **CI:** GitHub Actions repository secret

## Known Limitations / Future Work

- No user authentication
- No offline caching of AI responses (future: Room cache for AI suggestions)
- Single-module architecture (future: feature-based modularization)
- Ktor retry logic not yet implemented for transient network failures
