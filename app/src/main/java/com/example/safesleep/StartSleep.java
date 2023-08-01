//package com.example.safesleep;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.view.WindowManager;
//import android.widget.TextView;
//
//public class StartSleep extends AppCompatActivity implements SensorEventListener {
//
//    private TextView textViewCounter, textViewDetector;
//    private SensorManager sensorManager;
//    private Sensor mStepCounter;
//    private boolean isCounterSensorPresent;
//    int stepCount = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_start_sleep);
//
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        textViewCounter = findViewById(R.id.txtstepconter);
//        textViewDetector = findViewById(R.id.txtStepDetector);
//
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//
//        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
//            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//        } else {
//            textViewCounter.setText("Counter Seonsor is Absent");
//            isCounterSensorPresent = false;
//        }
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//      if(sensorEvent.sensor == mStepCounter){
//          stepCount = (int) sensorEvent.values[0];
//          textViewCounter.setText(String.valueOf(stepCount));
//      }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
//            sensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
//            sensorManager.unregisterListener(this,mStepCounter);
//        }
//    }
//}
package com.example.safesleep;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StartSleep extends AppCompatActivity implements SensorEventListener {

    private TextView textViewCounter, textViewDetector;
    private Button btnStart;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor; // Use accelerometer sensor
    private boolean isAccelerometerSensorPresent;
    private int stepCount = 0;

    private MediaPlayer mediaPlayer;

    private Map<String, String> messagesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_sleep);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        messagesMap = new HashMap<>();

        textViewCounter = findViewById(R.id.txtstepconter);
        textViewDetector = findViewById(R.id.txtStepDetector);

        btnStart = findViewById(R.id.starttracking);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);

        // Check if accelerometer sensor is available
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorPresent = true;
        } else {
            textViewCounter.setText("Accelerometer Sensor is Absent");
            isAccelerometerSensorPresent = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (isAccelerometerSensorPresent && sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            // Calculate the magnitude of acceleration
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

            // Assuming a threshold of 10 units for a step (you can adjust this value)
            if (acceleration > 15) {
                stepCount++;
                textViewCounter.setText("Steps Detected: "+String.valueOf(stepCount));

                if (stepCount > 2) {

                    String currentTime = getCurrentTime();
                    // Show the time in the TextView
                    textViewDetector.setText("Sleepwalking Detected at " + currentTime);
                    textViewDetector.setTextColor(Color.RED);



                    // Get the selected message from the Spinner (or any other way you choose the message)
                    String selectedMessage = "Sleepwalking Detected";

                    // Add the message and time to the messagesMap with a unique key
                    String messageKey = currentTime; // Use the formatted current time as the key
                    messagesMap.put(messageKey, selectedMessage);

                    // Show a normal Toast with the message and time
                    // ...

                    // Update the Firebase database with the new message entry
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("messages");
                    myRef.child(messageKey).setValue(selectedMessage);

                    mediaPlayer.start();
                    // Show a normal Toast with the message and the time
                    Toast.makeText(StartSleep.this, "Sleepwalking Detected at " + currentTime, Toast.LENGTH_LONG).show();
                }
                }
        }
    }
    private String getCurrentTime() {
        // Get the current date and time
        Date currentTime = new Date();

        // Format the current time as a unique key (e.g., "yyyyMMdd_HHmmss")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(currentTime);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Validate the sensor type
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Check the accuracy level
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    // Handle unreliable sensor data, e.g., show a warning to the user.
                    Log.d("Sensor Accuracy", "Accelerometer accuracy is unreliable");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    // Handle low accuracy data, if required.
                    Log.d("Sensor Accuracy", "Accelerometer accuracy is low");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    // Handle medium or high accuracy data as needed.
                    Log.d("Sensor Accuracy", "Accelerometer accuracy is medium or high");
                    break;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isAccelerometerSensorPresent) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.release();
        if (isAccelerometerSensorPresent) {
            sensorManager.unregisterListener(this, accelerometerSensor);
        }
    }
}
