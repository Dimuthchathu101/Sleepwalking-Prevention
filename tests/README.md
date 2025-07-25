# 💤 Sleepwalking-Prevention Project & Appium Test Suite

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