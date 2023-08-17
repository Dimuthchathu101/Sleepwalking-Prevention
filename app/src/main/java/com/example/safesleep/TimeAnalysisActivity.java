package com.example.safesleep;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TimeAnalysisActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_analysis);

        databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the values from the Firebase database
                String scheduledAwakeningTime = dataSnapshot.child("scheduledawakening").getValue(String.class);
                String getUpTimeSuggestion = dataSnapshot.child("getuptimesuggestion").getValue(String.class);
                String sleepingTimeSuggestion = dataSnapshot.child("sleepingtimesuggestion").getValue(String.class);

                // Convert times to seconds since midnight
                int scheduledAwakeningSeconds = timeToSeconds(scheduledAwakeningTime);
                int getUpTimeSeconds = timeToSeconds(getUpTimeSuggestion);
                int sleepingTimeSeconds = timeToSeconds(sleepingTimeSuggestion);

                // Calculate time differences
                int sleepingToAwakeningDifference = scheduledAwakeningSeconds - sleepingTimeSeconds;
                int sleepingToGetUpDifference = getUpTimeSeconds - sleepingTimeSeconds;
                int awakeningToGetUpDifference = getUpTimeSeconds - scheduledAwakeningSeconds;

                // Calculate the fraction of the difference
                double fraction = (double) sleepingToAwakeningDifference / sleepingToGetUpDifference;

                // Convert time differences to HH:mm:ss format
                String sleepingToAwakeningDifferenceFormatted = secondsToTimeFormat(sleepingToAwakeningDifference);
                String sleepingToGetUpDifferenceFormatted = secondsToTimeFormat(sleepingToGetUpDifference);
                String awakeningToGetUpDifferenceFormatted = secondsToTimeFormat(awakeningToGetUpDifference);

                // Update TextViews with calculated differences
                TextView awakeningToSleepingDifferenceTextView = findViewById(R.id.awakeningToSleepingDifferenceTextView);
                awakeningToSleepingDifferenceTextView.setText("Time difference between sleeping and scheduled awakening: " + sleepingToAwakeningDifferenceFormatted);

                TextView sleepingToGetUpDifferenceTextView = findViewById(R.id.sleepingToGetUpDifferenceTextView);
                sleepingToGetUpDifferenceTextView.setText("Time difference between sleeping and getting up: " + sleepingToGetUpDifferenceFormatted);

                TextView awakeningToGetUpDifferenceTextView = findViewById(R.id.awakeningToGetUpDifferenceTextView);
                awakeningToGetUpDifferenceTextView.setText("Time difference between scheduled awakening and getting up: " + awakeningToGetUpDifferenceFormatted);

                // Update the fraction TextView
                TextView fractionTextView = findViewById(R.id.fractionTextView);
                fractionTextView.setText("Fraction of the difference: " + fraction);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
            }
        });
    }

    private int timeToSeconds(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }

    private String secondsToTimeFormat(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
}
