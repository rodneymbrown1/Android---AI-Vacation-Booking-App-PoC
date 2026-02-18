# Android AI Vacation Booking App (PoC)

A proof-of-concept Android application that lets users plan vacations and automatically generates excursion suggestions using OpenAI's GPT-4o-mini API. Built with Jetpack Compose, Room Database, and Kotlin Coroutines.

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
| Language | Kotlin + Java (OpenAI client) |
| UI | Jetpack Compose (Material 3) |
| Database | Room (SQLite) |
| Navigation | Jetpack Navigation Compose |
| Async | Kotlin Coroutines |
| HTTP | OkHttp3 |
| AI | OpenAI Chat Completions API (GPT-4o-mini) |
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

   The API key is currently hardcoded in `app/src/main/java/com/example/learning_2/components/HTTP/OpenAIConnection.java`. For local development, replace the `API_KEY` and `ORG_ID` constants with your own credentials:

   ```java
   private static final String API_KEY = "your-openai-api-key";
   private static final String ORG_ID = "your-org-id";
   ```

   > **Note:** For production use, move API keys to `gradle.properties` or environment variables and access them via `BuildConfig`.

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

## Known Limitations

This is a proof-of-concept and has the following limitations:

- API keys are hardcoded in source (should use secure storage or a backend proxy)
- No user authentication
- No offline caching of AI responses
- No network error retry logic
- Single-module architecture (not modularized)
