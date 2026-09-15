# CommApp – User Documentation


## 1. Overview

The main purpose of this project was to try and create an **architecture** reflecting one used in practice. Therefore the content of this application is not very rich, but the underlying architecture could be used to provide many more functions in the future. The application itself aspires to provide a place where people can learn and sharpen their knowledge of **communication principles** such as Non-Violent Communication, Active Listening, Storytelling, and more.

**Components:**
- `backend-server/` – Spring Boot REST API (Java, Maven)
- `CommAppAndroid/` – Android client app (Kotlin, Gradle)

## 2. Requirements / Prerequisites

- Java: `JDK 25`
- Maven: `Apache Maven 3.9.11 (Red Hat 3.9.11-11)`
- Android Studio: `Quail 3 2026.1.3`
- Android SDK / minimum Android version supported by the app: `26`
- Any other tools: An Android device with at least `Android 8.0` (physical or emulated) should be available for the user (Tested on Pixel 10 with API 37.1)

## 3. Getting the Project

```
git clone https://github.com/kocarOnd/communication-app.git
```

## 4. Building & Running the Backend Server

1. Navigate to the backend directory:
   ```
   cd backend-server
   ```
2. Build the project:
   ```
   ./mvnw clean package
   ```
3. Run the server:
   ```
   ./mvnw spring-boot:run
   ```
4. Confirm it's running:
   - Navigate to `http://localhost:8080/api/nvc/scenarios/random`. It should yield a JSON with a scenario.

### 4.1 Configuration

- The database is in-memory, with configuration to be found at `backend-server/src/main/resources/application.properties`

## 5. Building & Running the Android App

1. Open `CommAppAndroid/` in Android Studio.
2. Make sure the backend server is running (see Section 4.)
3. Start an emulated device (see [this article](https://developer.android.com/studio/run/emulator) for reference)
4. Run the app through the run (▶) button

### 5.1 Connecting the App to the Backend


- The base URL is hardcoded within the NvcScenarioApiClient
- See `CommAppAndroid/app/src/main/java/cz/cuni/mff/kocaro/comm_app/commappandroid/network/NvcScenarioApiClient.kt` for implementation details

## 6. Testing the Application


1. The application opens (important step, sometimes overlooked)
2. Upon seeing the menu screen, try clicking the "Start NVC Scenario Exercise" button
3. A Loading screen is shown. Wait for it to change.
4. In case the exercise is not loading, the backend server is probably down (there is no explicit exception warning for the user as of now)
5. If exercise loads, try selecting every button that apply. Upon submitting, feedback (both visual in terms of changing colours and textual under the card content) should be shown. Clicking the submitting button again should continue to the next phase.
6. Upon reaching the request phase, a different exercise should be shown, the Swipe Exercise.
7. This exercise contains several cards that should be swept either right (accept) or left (deny). Upon finishing the last card, this exercise's summary should be shown in a fashion similar to the previous exercises.
8. Clicking the submit button shows the final report page with 4 cards representing every stage of the NVC process (observation, feeling, need and request).
9. Clicking the finish exercise button should move the user back to home screen.
10. If at any point in the exercise user clicks the return button, he should be returned to the main screen instead of the previous phase.

### 6.1 Automated Tests 

- Backend Server Testing
```
cd backend-server/   (change to the repository with pom.xml, otherwise the following command does not work)
./mvnw test
```

- Android Client Testing
```
cd CommAppAndroid/   (change to the repository with settings.gradle.kts, otherwise the following command does not work)
./gradlew test
```

## 7. Features

- Multiselection exercise
- Exercises using swiping movements
- Correct navigation within the exercise
- Providing random scenarios through web requests in JSON
- Saving to database through POST requests
- Global exception JSON response upon unexpected events

## 8. Known Issues / Limitations

- The swipe exercise boundary is hardcoded at 300 pixels. 
- The URL of the backend server is hardcoded within the client. 
- The ScenarioUIState is an implementation rather than an interface, closely coupled with both the ViewModel and the exercises.
- There is currently zero information for the user upon errors happening within the app. 
- The app uses device ID for identification. 

## 9. Project Structure 

```
final.zip
├── backend-server/            Repository containing the backend implementation with Maven information
|   ├── pom.xml                Maven settings file
|   └── src/                   Source code and tests for the backend implementation
├── CommAppAndroid/            Repository containing the Android client implementation with Gradle information
|   ├── settings.gradle.kts    Gradle settings file
|   ├── app/                   The app build information and source code
|   |   ├── build.gradle.kts   Information about libraries used in build + SDK and Java versions
|   |   └── src/               Source code and tests for the frontend Android implementation
|   └── gradle/                Gradle settings, most importantly the versions of libraries used within the code
└── README.md                  This User Documentation
```

## 10. AI Usage

This has been by far the hardest project I have ever attended to finish since I had 0 previous experience with Kotlin or Android development. Therefore I do not intend to hide the fact that I have relied heavily on LLMs during the creation and refactoring. Nevertheless I tried my best to always at least learn all of the code's classes, the patterns used, typical usage and alternatives before continuing. I have also used LLM to create the structure of this documentation which I then filled out with my own words. 
