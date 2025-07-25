package com.example.safesleep;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class DoctorHomeActivityAppiumTest {
    private AndroidDriver<MobileElement> driver;

    @Before
    public void setUp() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        caps.setCapability(MobileCapabilityType.DEVICE_NAME, "Android Emulator");
        caps.setCapability(MobileCapabilityType.APP, "C:/Users/<YourUsername>/Desktop/your_app.apk"); // Update path
        caps.setCapability("appPackage", "com.example.safesleep");
        caps.setCapability("appActivity", "com.example.safesleep.DoctorHomeActivity");
        driver = new AndroidDriver<>(new URL("http://localhost:4723/wd/hub"), caps);
    }

    @Test
    public void testDoctorHomeActivityLaunches() {
        // Example: Check if the activity is launched by verifying a UI element
        // MobileElement someElement = driver.findElementById("com.example.safesleep:id/some_element_id");
        // assert(someElement.isDisplayed());
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
} 