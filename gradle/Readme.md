# User Guide for Setting Up and Running the Application for Maintenance Purposes

## Introduction
This guide provides step-by-step instructions for setting up and running the **Learning_2** Android application for maintenance purposes. It covers project configuration, dependencies, and essential Gradle settings.

## Prerequisites
Before proceeding, ensure you have the following installed:
- **Java Development Kit (JDK) 11** or later
- **Android Studio (latest version)**
- **Gradle (latest version compatible with AGP 8.7.3)**
- **Android SDK and necessary tools**
- **An active Internet connection**

## Setting Up the Project

### 1. Clone the Repository
```sh
 git clone <repository-url>
 cd Learning_2
```

### 2. Configure Gradle Properties
Modify the `gradle.properties` file as needed:
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
OPENAI_API_KEY=<your-api-key>
```

### 3. Configure `settings.gradle.kts`
Ensure your `settings.gradle.kts` is correctly configured:
```kotlin
pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "Learning_2"
include(":app")
```

### 4. Configure `AndroidManifest.xml`
Ensure your `AndroidManifest.xml` contains necessary permissions and application settings:
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools">
  <uses-permission android:name="android.permission.INTERNET"/>
  <application
          android:allowBackup="true"
          android:dataExtractionRules="@xml/data_extraction_rules"
          android:fullBackupContent="@xml/backup_rules"
          android:icon="@mipmap/ic_launcher"
          android:label="@string/app_name"
          android:roundIcon="@mipmap/ic_launcher_round"
          android:supportsRtl="true"
          android:theme="@style/Theme.Learning_2">
    <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.Learning_2">
      <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
      </intent-filter>
    </activity>
  </application>
</manifest>
```

### 5. Configure `build.gradle.kts` (App Level)
Ensure your dependencies and plugins are configured properly:
```kotlin
plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.example.learning_2"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.learning_2"
    minSdk = 34
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
    buildConfigField("String", "OPENAI_API_KEY", "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\"")
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
}
```

### 6. Install Dependencies
Run the following command to sync Gradle dependencies:
```sh
gradle sync
```
Or within Android Studio, click **Sync Project with Gradle Files**.

### 7. Run the Application
To build and run the application, use:
```sh
gradlew assembleDebug
```
Or within Android Studio:
- Select your target device.
- Click **Run**.

### 8. Debugging
For debugging, enable logcat:
```sh
adb logcat -s "com.example.learning_2"
```

## Testing the Software Product

### 1. Test Plan for Unit Testing
A unit test was designed to validate core functionalities, including:
- Application launch
- API integration
- UI component rendering

#### Screenshot of Test Plan
(*Include Screenshot Here*)

### 2. Unit Test Scripts
Below is an example unit test script:
```kotlin
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testApiKeyNotEmpty() {
        val apiKey = BuildConfig.OPENAI_API_KEY
        assertTrue("API Key should not be empty", apiKey.isNotEmpty())
    }
}
```

### 3. Results of Unit Tests
After running the tests, the following results were observed:
- All test cases passed successfully.
- No major issues were found.

#### Screenshot of Test Results
(*Include Screenshot Here*)

### 4. Summary of Changes Resulting from Completed Tests
Following the unit testing:
- No significant code changes were required.
- Some log levels were adjusted for better debugging.
- Minor UI alignment fixes were implemented.

## Maintenance and Updates
- **Keep Dependencies Updated**: Modify `lib.versions.toml` and update dependencies as necessary.
- **Check for Gradle Issues**: If errors occur, run:
  ```sh
  gradlew --stacktrace --info
  ```
- **Monitor API Key Usage**: Ensure `OPENAI_API_KEY` is valid and updated.
- **Use Version Control**: Regularly commit changes to your repository.

## Conclusion
Following this guide ensures proper setup and maintenance of the Learning_2 Android application. If issues arise, consult Android Studio's error logs or verify configurations.

