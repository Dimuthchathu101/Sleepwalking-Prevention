
package com.example.safesleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StartSleep extends AppCompatActivity implements SensorEventListener {

    private TextView textViewCounter, textViewDetector;


    private Button btnStart,btnAwake;
    TextView sleepAwakeing;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor; // Use accelerometer sensor
    private boolean isAccelerometerSensorPresent;
    private int stepCount = 0;
    private MediaPlayer mediaPlayer, mediaplayer2;
    private Map<String, String> messagesMap;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<DataModel> dataList;

    EditText et_prefeeredTime;

    private boolean isRunning = false;

    String bedtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_sleep);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        messagesMap = new HashMap<>();

        textViewCounter = findViewById(R.id.txtstepconter);
        sleepAwakeing = findViewById(R.id.textSleepAwake);

        btnStart = findViewById(R.id.starttracking);
        btnAwake = findViewById(R.id.btnAwake);

        et_prefeeredTime = findViewById(R.id.editTextBedTime);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaplayer2 = MediaPlayer.create(this,R.raw.fusion);


        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference messagesRef = database.getReference("messages");
        DatabaseReference sleepTime = database.getReference("sleeptime");
        DatabaseReference awakeTime = database.getReference("awaketime");
        DatabaseReference sleeptime = database.getReference("sleeptime2");


        recyclerView = findViewById(R.id.recycler_view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        adapter = new MessageAdapter(dataList);
        recyclerView.setAdapter(adapter);


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bedtime = String.valueOf(et_prefeeredTime.getText());

                if (!bedtime.isEmpty()) { // Check if the input is not empty
                    try {
                        int bedtimeValue = Integer.parseInt(bedtime);

                        if (bedtimeValue >= 6 && bedtimeValue <= 10) {
                            try {
                                if (mediaplayer2.isPlaying()) {
                                    mediaplayer2.stop();
                                    btnStart.setText("START");
                                } else {
                                    mediaplayer2.start();
                                    btnStart.setText("STOP");
                                }

                                sleepTime.setValue(bedtime);
                            } catch (IllegalStateException e) {
                                // Handle media player state exception
                                e.printStackTrace();
                            }
                        } else {
                            // Show a toast indicating invalid bedtime value
                            Toast.makeText(getApplicationContext(), "Bedtime should be between 6 and 10", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        // Handle number format exception when parsing bedtime
                        Toast.makeText(getApplicationContext(), "Invalid bedtime value", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    // Show a toast indicating empty input
                    Toast.makeText(getApplicationContext(), "Bedtime value is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Add a ValueEventListener to fetch data from Firebase and update the RecyclerView
        DatabaseReference messagesRef2 = FirebaseDatabase.getInstance().getReference("messages");
        messagesRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    String value = childSnapshot.getValue(String.class);
                    dataList.add(new DataModel(key, value));
                }
                adapter.notifyDataSetChanged();

                Button goBackButton = findViewById(R.id.navigatebackstartsleep);
                goBackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Navigate to SleepPreferences activity
                        Intent intent = new Intent(StartSleep.this, SleepwalkerHome.class);
                        startActivity(intent);

                        // Finish the current activity
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error here if retrieval fails
                Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
            }
        });


        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> validSecondsSinceMidnightList = new ArrayList<>();
                Set<Integer> uniqueSecondsSinceMidnightSet = new HashSet<>();

                // Define the time period interval (20 minutes in seconds)
                int timePeriodInterval = 40 * 60;

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String messageKey = childSnapshot.getKey();

                    // Ensure the messageKey has the correct format "yyyyMMdd_HHmmss"
                    if (messageKey == null || messageKey.length() < 15 || !messageKey.contains("_")) {
                        Log.e("Firebase", "Invalid message key format: " + messageKey);
                        // You can handle this case as needed, for example, skip the message or show an error message.
                        continue;
                    }

                    // Sorting
                    Collections.sort(validSecondsSinceMidnightList);
                    // Get the time part (HHmmss) from the message key
                    String timePart = messageKey.substring(9); // Assuming format "yyyyMMdd_HHmmss"


                    try {
                        // Extract hours, minutes, and seconds
                        int hours = Integer.parseInt(timePart.substring(0, 2));
                        int minutes = Integer.parseInt(timePart.substring(2, 4));
                        int seconds = Integer.parseInt(timePart.substring(4, 6));

                        // Calculate the number of seconds since midnight for this message
                        int secondsSinceMidnight = hours * 3600 + minutes * 60 + seconds;

                        // Check if the time falls within the valid interval (e.g., 20 minutes)
                        if (secondsSinceMidnight % timePeriodInterval == 0) {
                            validSecondsSinceMidnightList.add(secondsSinceMidnight);
                        }

                        try {

                            try {
                                // Step 3: Check if the time falls within the valid interval (e.g., 20 minutes)
                                if (secondsSinceMidnight % timePeriodInterval == 0) {
                                    validSecondsSinceMidnightList.add(secondsSinceMidnight);
                                }
                            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                Log.e("Firebase", "Error parsing time from message key: " + messageKey);
                                // You can handle this case as needed, for example, skip the message or show an error message.
                            }
                            // Step 3: Check for duplicate seconds since midnight
                            if (uniqueSecondsSinceMidnightSet.add(secondsSinceMidnight)) {
                                // If it's a unique value, add it to the validSecondsSinceMidnightList
                                validSecondsSinceMidnightList.add(secondsSinceMidnight);
                            }
                        } catch (NumberFormatException | IndexOutOfBoundsException e) {
                            Log.e("Firebase", "Error parsing time from message key: " + messageKey);
                            // You can handle this case as needed, for example, skip the message or show an error message.
                        }

                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        Log.e("Firebase", "Error parsing time from message key: " + messageKey);
                        // You can handle this case as needed, for example, skip the message or show an error message.
                    }
                }

                // Sort the valid seconds since midnight list
                Collections.sort(validSecondsSinceMidnightList);
                if (validSecondsSinceMidnightList.isEmpty()) {
                    Log.e("Firebase", "No valid data entries found. Cannot calculate the median.");
                    // You can handle this case as needed, for example, show an error message or perform alternative calculations.
                    return;
                }

                // Calculate the median index
                int medianIndex = validSecondsSinceMidnightList.size() / 2;

                // Get the median seconds since midnight
                int medianSecondsSinceMidnight = validSecondsSinceMidnightList.get(medianIndex);

                // Calculate the time that is 30 minutes before the median time
                int timeBeforeMedianSecondsSinceMidnight = medianSecondsSinceMidnight - 30 * 60;
                if (timeBeforeMedianSecondsSinceMidnight < 0) {
                    timeBeforeMedianSecondsSinceMidnight += 24 * 3600; // Account for wrapping around to the previous day
                }
                int timeBeforeMedianHours = timeBeforeMedianSecondsSinceMidnight / 3600;
                int timeBeforeMedianMinutes = (timeBeforeMedianSecondsSinceMidnight % 3600) / 60;
                int timeBeforeMedianSeconds = timeBeforeMedianSecondsSinceMidnight % 60;

                // Format the time before median as "HH:mm:ss"
                String timeBeforeMedian = String.format("%02d:%02d:%02d", timeBeforeMedianHours, timeBeforeMedianMinutes, timeBeforeMedianSeconds);

                // Display the time before median in a Toast
                sleepAwakeing.setText("Awakening Time :" + timeBeforeMedian);
                sleepAwakeing.setTextColor(Color.RED);
                sleepAwakeing.setTextSize(18);

                Toast.makeText(StartSleep.this, " Awakeing Time: " + timeBeforeMedian, Toast.LENGTH_SHORT).show();

                DatabaseReference myRef = database.getReference("scheduledawakening");
                myRef.setValue(timeBeforeMedian);
                        btnAwake.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Get the time in milliseconds when the alarm should trigger
                                addToCalendar(timeBeforeMedian);

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
//                                calendar.set(Calendar.HOUR_OF_DAY, timeBeforeMedianHours);
//                                calendar.set(Calendar.MINUTE, timeBeforeMedianMinutes);
//                                calendar.set(Calendar.SECOND, timeBeforeMedianSeconds);

                                calendar.set(Calendar.HOUR_OF_DAY, timeBeforeMedianHours);
                                calendar.set(Calendar.MINUTE, timeBeforeMedianMinutes);
                                calendar.set(Calendar.SECOND, timeBeforeMedianSeconds);

                                // Create an intent for the AlarmReceiver class
                                Intent intent = new Intent(view.getContext(), AlarmReceiver.class);
                                intent.putExtra("ALARM_MESSAGE", "Wake up! It's time!");

                                // Create a PendingIntent to be triggered when the alarm goes off
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(view.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                // Get the AlarmManager service and set the alarm
                                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                if (alarmManager != null) {
                                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                    // Use setExact() to trigger the alarm at the exact time, even if the device is in doze mode.
                                    // For repeating alarms, use setRepeating().
                                }

                                // Show a Toast message to confirm that the alarm is set
                                Toast.makeText(view.getContext(), "Alarm set for " + timeBeforeMedian, Toast.LENGTH_SHORT).show();
                            }

                        });



                sleeptime.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Integer> validSecondsSinceMidnightList = new ArrayList<>();
                        Set<Integer> uniqueSecondsSinceMidnightSet = new HashSet<>();

                        // Define the time period interval (20 minutes in seconds)
                        int timePeriodInterval = 40 * 60;

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String messageKey = childSnapshot.getKey();

                            // Ensure the messageKey has the correct format "yyyyMMdd_HHmmss"
                            if (messageKey == null || messageKey.length() < 15 || !messageKey.contains("_")) {
                                Log.e("Firebase", "Invalid message key format: " + messageKey);
                                // You can handle this case as needed, for example, skip the message or show an error message.
                                continue;
                            }

                            // Sorting
                            Collections.sort(validSecondsSinceMidnightList);
                            // Get the time part (HHmmss) from the message key
                            String timePart = messageKey.substring(9); // Assuming format "yyyyMMdd_HHmmss"

                            try {
                                // Extract hours, minutes, and seconds
                                int hours = Integer.parseInt(timePart.substring(0, 2));
                                int minutes = Integer.parseInt(timePart.substring(2, 4));
                                int seconds = Integer.parseInt(timePart.substring(4, 6));

                                // Calculate the number of seconds since midnight for this message
                                int secondsSinceMidnight = hours * 3600 + minutes * 60 + seconds;

                                // Check if the time falls within the valid interval (e.g., 20 minutes)
                                if (secondsSinceMidnight % timePeriodInterval == 0) {
                                    validSecondsSinceMidnightList.add(secondsSinceMidnight);
                                }

                                try {

                                    try {
                                        // Step 3: Check if the time falls within the valid interval (e.g., 20 minutes)
                                        if (secondsSinceMidnight % timePeriodInterval == 0) {
                                            validSecondsSinceMidnightList.add(secondsSinceMidnight);
                                        }
                                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                        Log.e("Firebase", "Error parsing time from message key: " + messageKey);
                                        // You can handle this case as needed, for example, skip the message or show an error message.
                                    }
                                    // Step 3: Check for duplicate seconds since midnight
                                    if (uniqueSecondsSinceMidnightSet.add(secondsSinceMidnight)) {
                                        // If it's a unique value, add it to the validSecondsSinceMidnightList
                                        validSecondsSinceMidnightList.add(secondsSinceMidnight);
                                    }
                                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                    Log.e("Firebase", "Error parsing time from message key: " + messageKey);
                                    // You can handle this case as needed, for example, skip the message or show an error message.
                                }

                            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                Log.e("Firebase", "Error parsing time from message key: " + messageKey);
                                // You can handle this case as needed, for example, skip the message or show an error message.
                            }
                        }

                        // Sort the valid seconds since midnight list
                        Collections.sort(validSecondsSinceMidnightList);
                        if (validSecondsSinceMidnightList.isEmpty()) {
                            Log.e("Firebase", "No valid data entries found. Cannot calculate the median.");
                            // You can handle this case as needed, for example, show an error message or perform alternative calculations.
                            return;
                        }

                        // Calculate the median index
                        int medianIndex = validSecondsSinceMidnightList.size() / 2;

                        // Get the median seconds since midnight
                        int medianSecondsSinceMidnight = validSecondsSinceMidnightList.get(medianIndex);

                        // Calculate the time that is 30 minutes before the median time
                        int timeBeforeMedianSecondsSinceMidnight = medianSecondsSinceMidnight;
                        if (timeBeforeMedianSecondsSinceMidnight < 0) {
                            timeBeforeMedianSecondsSinceMidnight += 24 * 3600; // Account for wrapping around to the previous day
                        }
                        int timeBeforeMedianHours = timeBeforeMedianSecondsSinceMidnight / 3600;
                        int timeBeforeMedianMinutes = (timeBeforeMedianSecondsSinceMidnight % 3600) / 60;
                        int timeBeforeMedianSeconds = timeBeforeMedianSecondsSinceMidnight % 60;

                        // Format the time before median as "HH:mm:ss"
                        String timeBeforeMedian = String.format("%02d:%02d:%02d", timeBeforeMedianHours, timeBeforeMedianMinutes, timeBeforeMedianSeconds);

                        // Display the time before median in a Toast
//                        sleepAwakeing.setText("Awakening Time :" + timeBeforeMedian);
//                        sleepAwakeing.setTextColor(Color.RED);
//                        sleepAwakeing.setTextSize(18);

                        Toast.makeText(StartSleep.this, "Sleeping Time :" + timeBeforeMedian, Toast.LENGTH_SHORT).show();
                        DatabaseReference sleeping = database.getReference("sleepingtimesuggestion");
                        sleeping.setValue(timeBeforeMedian);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error here if retrieval fails
                        Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
                        // You can show an error message to the user if needed.
                    }

                });

                awakeTime.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Integer> validSecondsSinceMidnightList = new ArrayList<>();
                        Set<Integer> uniqueSecondsSinceMidnightSet = new HashSet<>();

                        // Define the time period interval (20 minutes in seconds)
                        int timePeriodInterval = 40 * 60;

                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String messageKey = childSnapshot.getKey();

                            // Ensure the messageKey has the correct format "yyyyMMdd_HHmmss"
                            if (messageKey == null || messageKey.length() < 15 || !messageKey.contains("_")) {
                                Log.e("Firebase", "Invalid message key format: " + messageKey);
                                // You can handle this case as needed, for example, skip the message or show an error message.
                                continue;
                            }

                            // Sorting
                            Collections.sort(validSecondsSinceMidnightList);
                            // Get the time part (HHmmss) from the message key
                            String timePart = messageKey.substring(9); // Assuming format "yyyyMMdd_HHmmss"

                            try {
                                // Extract hours, minutes, and seconds
                                int hours = Integer.parseInt(timePart.substring(0, 2));
                                int minutes = Integer.parseInt(timePart.substring(2, 4));
                                int seconds = Integer.parseInt(timePart.substring(4, 6));

                                // Calculate the number of seconds since midnight for this message
                                int secondsSinceMidnight = hours * 3600 + minutes * 60 + seconds;

                                // Check if the time falls within the valid interval (e.g., 20 minutes)
                                if (secondsSinceMidnight % timePeriodInterval == 0) {
                                    validSecondsSinceMidnightList.add(secondsSinceMidnight);
                                }

                                try {

                                    try {
                                        // Step 3: Check if the time falls within the valid interval (e.g., 20 minutes)
                                        if (secondsSinceMidnight % timePeriodInterval == 0) {
                                            validSecondsSinceMidnightList.add(secondsSinceMidnight);
                                        }
                                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                        Log.e("Firebase", "Error parsing time from message key: " + messageKey);
                                        // You can handle this case as needed, for example, skip the message or show an error message.
                                    }
                                    // Step 3: Check for duplicate seconds since midnight
                                    if (uniqueSecondsSinceMidnightSet.add(secondsSinceMidnight)) {
                                        // If it's a unique value, add it to the validSecondsSinceMidnightList
                                        validSecondsSinceMidnightList.add(secondsSinceMidnight);
                                    }
                                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                    Log.e("Firebase", "Error parsing time from message key: " + messageKey);
                                    // You can handle this case as needed, for example, skip the message or show an error message.
                                }

                            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                                Log.e("Firebase", "Error parsing time from message key: " + messageKey);
                                // You can handle this case as needed, for example, skip the message or show an error message.
                            }
                        }

                        // Sort the valid seconds since midnight list
                        Collections.sort(validSecondsSinceMidnightList);
                        if (validSecondsSinceMidnightList.isEmpty()) {
                            Log.e("Firebase", "No valid data entries found. Cannot calculate the median.");
                            // You can handle this case as needed, for example, show an error message or perform alternative calculations.
                            return;
                        }

                        // Calculate the median index
                        int medianIndex = validSecondsSinceMidnightList.size() / 2;

                        // Get the median seconds since midnight
                        int medianSecondsSinceMidnight = validSecondsSinceMidnightList.get(medianIndex);

                        // Calculate the time that is 30 minutes before the median time
                        int timeBeforeMedianSecondsSinceMidnight = medianSecondsSinceMidnight ;
                        if (timeBeforeMedianSecondsSinceMidnight < 0) {
                            timeBeforeMedianSecondsSinceMidnight += 24 * 3600; // Account for wrapping around to the previous day
                        }
                        int timeBeforeMedianHours = timeBeforeMedianSecondsSinceMidnight / 3600;
                        int timeBeforeMedianMinutes = (timeBeforeMedianSecondsSinceMidnight % 3600) / 60;
                        int timeBeforeMedianSeconds = timeBeforeMedianSecondsSinceMidnight % 60;

                        // Format the time before median as "HH:mm:ss"
                        String timeBeforeMedian = String.format("%02d:%02d:%02d", timeBeforeMedianHours, timeBeforeMedianMinutes, timeBeforeMedianSeconds);

                        // Display the time before median in a Toast
                        // sleepAwakeing.setText("Awakening Time :" + timeBeforeMedian);
//                        sleepAwakeing.setTextColor(Color.RED);
//                        sleepAwakeing.setTextSize(18);

                        Toast.makeText(StartSleep.this, "Getup Time : " + timeBeforeMedian, Toast.LENGTH_SHORT).show();
                        DatabaseReference myRef = database.getReference("getuptimesuggestion");
                        myRef.setValue(timeBeforeMedian);
                    }




                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error here if retrieval fails
                        Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
                        // You can show an error message to the user if needed.
                    }

                });



//                new WekaClassificationTask().execute(dataList);
            }




            private void addToCalendar(String timeBeforeMedian) {
                // ... (your existing code)

                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.Events.TITLE, "Sleep Awakening")
                        .putExtra(CalendarContract.Events.DESCRIPTION, "Time to wake up before sleepwalking")
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getEventTimeInMillis(timeBeforeMedian))
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getEventTimeInMillis(timeBeforeMedian))
                        .putExtra(CalendarContract.Events.ALL_DAY, false)
                        .putExtra(CalendarContract.Events.HAS_ALARM, true);

                startActivity(intent);

            }


            private long getEventTimeInMillis(String timeBeforeMedian) {
                // Parse the timeBeforeMedian string to get the hours, minutes, and seconds
                String[] timeParts = timeBeforeMedian.split(":");
                int hours = Integer.parseInt(timeParts[0]);
                int minutes = Integer.parseInt(timeParts[1]);
                int seconds = Integer.parseInt(timeParts[2]);

                // Get the current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Set the event time to the time before median
                calendar.set(year, month, day, hours, minutes, seconds);

                // Add one day to the event date
                calendar.add(Calendar.DAY_OF_MONTH, 1);

                return calendar.getTimeInMillis();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error here if retrieval fails
                Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
                // You can show an error message to the user if needed.
            }

        });


        // Record Display Ends Heer

        // Check if accelerometer sensor is available
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorPresent = true;
        } else {
            textViewCounter.setText("Accelerometer Sensor is Absent");
            isAccelerometerSensorPresent = false;
        }


    }



    private class WekaClassificationTask extends AsyncTask<List<DataModel>, Void, Void> {
        @Override
        protected Void doInBackground(List<DataModel>... dataLists) {
            // Get the dataList from the first element of the array
            List<DataModel> dataList = dataLists[0];

            // Extract the data from dataList and convert it to the array
            String[] firebaseData = new String[dataList.size()];
            for (int i = 0; i < dataList.size(); i++) {
                firebaseData[i] = dataList.get(i).getValue(); // Assuming DataModel has a getValue() method for retrieving the value
            }

            // Call the classifier
            WekaClassifierAwake.classifyData(firebaseData);

            return null;
        }
    }


public void onSensorChanged(SensorEvent sensorEvent) {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference caretakerMobileFirebase = database.getReference("caretakermobile");
    DatabaseReference caretakerEmailFirebase= database.getReference("caretakeremail");
    if (isAccelerometerSensorPresent && sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        // Calculate the magnitude of acceleration
        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

        // Assuming a threshold of 10 units for a step (you can adjust this value)
        if (acceleration > 15) {
            stepCount++;
            textViewCounter.setText("Steps Detected: " + String.valueOf(stepCount));

            if (stepCount > 2) {
                String currentTime = getCurrentTime();

                // Add the message and time to the messagesMap with a unique key
                String messageKey = currentTime; // Use the formatted current time as the key
                messagesMap.put(messageKey, messageKey);

                mediaPlayer.start();
                // Show a normal Toast with the message and time
                Toast.makeText(StartSleep.this, messageKey + " at " + currentTime, Toast.LENGTH_LONG).show();

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                caretakerMobileFirebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
                        Intent intent = new Intent(StartSleep.this, AutomaticCallActivity.class);
                        intent.putExtra("phoneNumber", value);
                        intent.putExtra("startTime", 1616048600000L);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w("TAG", "Failed to read value.", error.toException());
                    }
                });


                // Update the Firebase database with the new message entry

                DatabaseReference myRef = database.getReference("messages");

                myRef.child(messageKey).setValue(messageKey);

                // Assuming you have initialized the "mediaPlayer" variable

            }
        }
    }
}

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (!isRunning) {
//            isRunning = true;
//            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        }
//        return START_STICKY;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        sensorManager.unregisterListener(this);
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
        mediaplayer2.release();
        if (isAccelerometerSensorPresent) {
            sensorManager.unregisterListener(this, accelerometerSensor);
        }
    }
}
