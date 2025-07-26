# 💤 Sleepwalking-Prevention Project & Appium Test Suite

## 📁 Project Folder Structure

Below is a detailed explanation of the folder structure and what each part is for:

---

### Project Root
- **README.md**: Project and test suite documentation.
- **build.gradle, settings.gradle, gradle.properties, gradlew, gradlew.bat, gradle/**: Standard Gradle build system files for building and managing the project.
- **tests/**: Contains Appium-based automated UI tests for your Android app.
- **app/**: The main Android application module.
- **Daily Diary.docx/pdf, Finalized Topic, Gantt Chart.xlsx**: Project documentation and planning files.
- **.gitignore, .git/**: Git version control files and configuration.
- **.idea/**: IDE (Android Studio/IntelliJ) project settings.

---

### app/ (Android App Module)
- **build.gradle, proguard-rules.pro, google-services.json**: App-specific build config, ProGuard rules for code shrinking, and Firebase/Google services config.
- **libs/**: Third-party libraries (e.g., `weka.jar` for machine learning).
- **src/**: Main source code and resources.

---

#### app/src/ (Source Sets)
- **main/**: The main application code and resources.
- **test/**: Unit tests (Java, not Android-specific).
- **androidTest/**: Instrumented tests (run on Android device/emulator).

---

##### app/src/main/
- **AndroidManifest.xml**: Declares app components, permissions, and configuration.
- **java/com/example/safesleep/**: All Java source code for Activities, services, data models, and business logic.
- **res/**: App resources (UI layouts, images, strings, colors, etc.).
- **ic_launcher-playstore.png**: App icon for Play Store.

---

##### app/src/main/java/com/example/safesleep/
- **Activities**: Each `*Activity.java` file represents a screen (UI) in the app (e.g., `MainActivity`, `AutomaticCallActivity`, `SleepwalkerHome`).
- **Services**: E.g., `SensorMonitoringService.java` for background sensor monitoring.
- **Data & Logic**: E.g., `DataModel.java`, `Doctors.java`, `CollaborativeFiltering.java` for app logic and data handling.
- **Adapters**: E.g., `MessageAdapter.java`, `MyAdapter.java` for RecyclerViews or ListViews.
- **Receivers**: E.g., `AlarmReceiver.java`, `StartMonitoringReceiver.java` for handling system or app events.
- **Other**: Utility and helper classes.

---

##### app/src/main/res/ (Resources)
- **layout/**: XML files defining the UI for each Activity and custom views (e.g., `activity_main.xml`, `activity_automatic_call.xml`).
- **drawable/**: Images and shape resources (e.g., icons, backgrounds, JPEG/PNG files, XML shapes).
- **mipmap-***/: Launcher icons for different screen densities.
- **values/**: App-wide resources like `strings.xml` (text), `colors.xml`, and `themes.xml` (styling).
- **xml/**: Miscellaneous XML resources (e.g., backup rules).
- **raw/**: Audio or other raw files (e.g., `alarm.mp3`).
- **values-night/**: Night mode themes.

---

##### app/libs/
- **weka.jar**: A machine learning library used for sleep analysis or classification.

---

##### app/src/test/ and app/src/androidTest/
- **java/com/example/safesleep/**: Java test files for unit and instrumented tests, respectively.

---

##### tests/
- **Appium Test Files**: Each Activity has a corresponding Appium test file (e.g., `MainActivityAppiumTest.java`), plus a `README.md` for test setup and usage.

---

## 🌙 Project Overview
**Sleepwalking-Prevention** is an Android application designed to help users and caregivers monitor, analyze, and prevent sleepwalking incidents. The app leverages sensors, user preferences, and doctor collaboration to provide a comprehensive solution for sleep safety.

### Key Features
- **Automatic Emergency Calling**: Initiates calls to predefined contacts if sleepwalking is detected.
- **Sleep Posture Monitoring**: Uses device sensors to track and analyze sleep postures.
- **Doctor Collaboration**: Allows users to connect with doctors, share sleep records, and receive suggestions.
- **Data Analysis & Visualization**: Provides graphs and time analysis of sleep patterns.
- **User Preferences**: Customizable settings for sleep monitoring and emergency protocols.

### Codebase Structure
- `app/src/main/java/com/example/safesleep/` — Main Android app source code
  - `*Activity.java` — UI screens and user flows (e.g., `MainActivity`, `AutomaticCallActivity`)
  - `DataModel.java`, `Doctors.java`, etc. — Data handling and business logic
  - `SensorMonitoringService.java` — Background sensor monitoring
- `app/src/main/res/` — Layouts, drawables, and resources
- `tests/` — Automated UI tests using Appium (this folder)

---

# 🧪 Appium Test Suite

This suite uses [Appium](https://appium.io/) and JUnit to ensure your app's Activities work as expected across devices.

## 🚀 Test Suite Features
- **Automated Activity Launch Tests**: Each major Activity is tested for successful launch.
- **Easy Customization**: Add your own UI checks for buttons, text, and flows.
- **Cross-Platform**: Works with Android emulators and real devices.

## 📂 Test Files
Each Activity has a corresponding test file:
- `AutomaticCallActivityAppiumTest.java`
- `DetailActivityAppiumTest.java`
- `DoctorHomeActivityAppiumTest.java`
- `SleepPostureActivityAppiumTest.java`
- `MainActivityAppiumTest.java`
- `TimeAnalysisActivityAppiumTest.java`
- `UpdateActivityAppiumTest.java`
- `UploadActivityAppiumTest.java`
- `RegisterDoctorActivityAppiumTest.java`
- `GraphActivityAppiumTest.java`
- `SleepwalkerHomeAppiumTest.java`

---

## 🛠️ Setup Instructions

### 1. Prerequisites
- [Java JDK 8+](https://adoptopenjdk.net/)
- [Appium Server](https://appium.io/)
- [Android Studio & SDK Tools](https://developer.android.com/studio)
- [Node.js](https://nodejs.org/) (for Appium)
- Android Emulator or real device

### 2. Install Appium
```bash
npm install -g appium
```

### 3. Start Appium Server
```bash
appium
```

### 4. Configure APK Path
Edit the test files and set the correct path to your APK:
```
caps.setCapability(MobileCapabilityType.APP, "C:/Users/<YourUsername>/Desktop/your_app.apk");
```
Replace `<YourUsername>` with your Windows username.

### 5. Run the Tests
You can run the tests using your preferred IDE (like IntelliJ or Android Studio) or via command line:
```bash
./gradlew test
```
Or use JUnit runner in your IDE.

---

## ✏️ Customizing Tests
- Replace the placeholder UI checks with real element IDs from your layouts.
- Add more tests for user flows, button clicks, and data entry.

---

## 💡 Tips
- Make sure your emulator/device is running and unlocked before running tests.
- Keep your Appium server running in the background.
- For more advanced Appium usage, see the [Appium Docs](https://appium.io/docs/en/about-appium/intro/).

---

## 🤝 Contributing
Feel free to add more tests or improve existing ones! Pull requests are welcome.

---

## 📧 Support
For issues or questions, please open an issue in the main repository or contact the project maintainer.

---

**Happy Testing! 🧪** 