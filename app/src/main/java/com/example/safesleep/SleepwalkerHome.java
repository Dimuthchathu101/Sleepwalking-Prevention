package com.example.safesleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SleepwalkerHome extends AppCompatActivity {

    // Setting up the variables
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean shouldCheckMedian = true;

    Button btnPrferences, btnRecords,btnTrack;
    TextView suggestion01, suggestion02, suggestion04, suggestion05;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleepwalker_home);

        // Setting up the buttons
        btnPrferences = findViewById(R.id.sleepwpreferences);
        btnRecords = findViewById(R.id.sleepRecords);
        btnTrack = findViewById(R.id.sleepMotion);

        // Setting up the suggestions
        suggestion01 = findViewById(R.id.suggestion01sleep);
        suggestion02 = findViewById(R.id.suggestion02sleep);
        suggestion04 = findViewById(R.id.suggestion03sleep);
        suggestion05 = findViewById(R.id.suggestion04sleep);

        btnPrferences.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SleepPreferences.class);
            startActivity(intent);
            finish();
        });

        btnTrack.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SleepPostureActivity.class);
            startActivity(intent);
            finish();
        });

        // Firebase Database options
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference messagesRef = database.getReference("messages");
        DatabaseReference sleepTime = database.getReference("sleepingtimesuggestion");
        DatabaseReference awakeTime = database.getReference("scheduledawakening");
        DatabaseReference sleeptime = database.getReference("getuptimesuggestion");
        DatabaseReference doctoradvise = database.getReference("doctoradvise");



        // Sleepwalker Time
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Integer> validSecondsSinceMidnightList = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String messageKey = childSnapshot.getKey();

                    // Get the time part (HHmmss) from the message key
                    String timePart = messageKey.substring(9); // Assuming format "yyyyMMdd_HHmmss"

                    // Extract hours, minutes, and seconds
                    int hours = Integer.parseInt(timePart.substring(0, 2));
                    int minutes = Integer.parseInt(timePart.substring(2, 4));
                    int seconds = Integer.parseInt(timePart.substring(4, 6));

                    // Calculate the number of seconds since midnight for this message
                    int secondsSinceMidnight = hours * 3600 + minutes * 60 + seconds;

                    // Check if the time falls within the valid interval (e.g., 40 minutes)
                    if (secondsSinceMidnight % (40 * 60) == 0) {
                        validSecondsSinceMidnightList.add(secondsSinceMidnight);
                    }
                }

                // Remove duplicates and sort the list in ascending order
                Set<Integer> uniqueSortedSeconds = new TreeSet<>(validSecondsSinceMidnightList);

                // Convert the set back to a list for median calculation
                List<Integer> sortedSecondsList = new ArrayList<>(uniqueSortedSeconds);

                // Calculate the median index
                int medianIndex = sortedSecondsList.size() / 2;

                // Get the median seconds since midnight
                int medianSecondsSinceMidnight = sortedSecondsList.get(medianIndex);

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

                checkMedianTime(timeBeforeMedian);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error here if retrieval fails
                Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
            }
        });

        // Sleep Time Suggestion
        sleepTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
                suggestion01.setText("Your Bedtime is : "+ value);
                suggestion01.setTextColor(Color.BLACK);
                suggestion01.setTextSize(18);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Sleep Time Suggestion 02
        sleeptime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                suggestion02.setText("Your Getup Time is : "+ value);
                suggestion02.setTextColor(Color.BLACK);
                suggestion02.setTextSize(18);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Sleep Time
        awakeTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
              suggestion04.setText("Your Scheduled Awakeing is at : "+ value);
                suggestion04.setTextColor(Color.BLACK);
                suggestion04.setTextSize(18);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Doctor Advise
        doctoradvise.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
                suggestion05.setText("Doctor Suggestions : "+ value);
                suggestion05.setTextColor(Color.BLACK);
                suggestion05.setTextSize(18);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Records button onclick
        btnRecords.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), StartSleep.class);
            startActivity(intent);
            finish();

        });

    }

    // Check Median Time
private void checkMedianTime(String medianTime) {
    if (shouldCheckMedian) {
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);
        int currentSecond = currentTime.get(Calendar.SECOND);

        String currentTimeFormatted = String.format("%02d:%02d:%02d", currentHour, currentMinute, currentSecond);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference caretakerMobileFirebase = database.getReference("caretakermobile");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date medianTimeDate = sdf.parse(medianTime);
            Date currentTimeDate = sdf.parse(currentTimeFormatted);

            // Calculate the difference between median time and current time in seconds
            long timeDifference = (currentTimeDate.getTime() - medianTimeDate.getTime()) / 1000;

            // checking for 30 seconds interval
            if (Math.abs(timeDifference) <= 30) { // Check if the difference is within 30 seconds
                caretakerMobileFirebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
                        Intent intent = new Intent(SleepwalkerHome.this, AutomaticCallActivity.class);
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
                shouldCheckMedian = false; // Stop checking
            } else {
                //Toast.makeText(this, "This is not your sleepwalking time", Toast.LENGTH_SHORT).show();
            }

            // Check again after a delay (every 0.5 seconds)
            handler.postDelayed(() -> checkMedianTime(medianTime), 500);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        shouldCheckMedian = false; // Stop checking when the activity is destroyed
    }
}