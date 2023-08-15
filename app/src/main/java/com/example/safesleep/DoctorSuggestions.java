package com.example.safesleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorSuggestions extends AppCompatActivity {

    /**Firebase Database Options
     * */
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference getuptimesuggestion = database.getReference("getuptimesuggestion");
    DatabaseReference scheduledawakening = database.getReference("scheduledawakening");
    DatabaseReference sleepingtimesuggestion = database.getReference("sleepingtimesuggestion");
    DatabaseReference doctoradvise = database.getReference("doctoradvise");

    /**
     * Edit Text */
    EditText doctorrecomendations;
    EditText suggestedscheduledawake;
    EditText suggestedawake;
    EditText suggestedsleep;

    Button btndoctorupdate,navigatebackdoctorsugg;

    TextView suggestedcurrentrecomendations,suggestioncurrentwalk, suggestioncurrentawake, suggestionstxtsleeptime ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_suggestions);

        doctorrecomendations = findViewById(R.id.doctorrecomendations);
        suggestedscheduledawake = findViewById(R.id.suggestedscheduledawake);
        suggestedawake = findViewById(R.id.suggestedawake);
        suggestedsleep = findViewById(R.id.suggestedsleep);

        suggestedcurrentrecomendations = findViewById(R.id.suggestedcurrentrecomendations);
        suggestioncurrentwalk = findViewById(R.id.suggestioncurrentwalk);
        suggestioncurrentawake = findViewById(R.id.suggestioncurrentawake);
        suggestionstxtsleeptime = findViewById(R.id.suggestionstxtsleeptime);

        btndoctorupdate = findViewById(R.id.btndoctorupdate);
        navigatebackdoctorsugg = findViewById(R.id.navigatebackdoctorsugg);

        // Setting Values
        getuptimesuggestion.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);

                    try {
                        suggestioncurrentawake.setText("Current Getup Time: " + value);
                    } catch (Exception e) {
                        Log.e("TAG", "Error setting text: " + e.getMessage());
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Error reading data: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        // Read from the database ( Scheduled Awakeing)
        scheduledawakening.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);

                    try {
                        suggestioncurrentwalk.setText("Current Scheduled Awakening " + value);
                    } catch (Exception e) {
                        Log.e("TAG", "Error setting text: " + e.getMessage());
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Error reading data: " + e.getMessage());
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        // Sleeping Time Suggestion (Read from the database)
        sleepingtimesuggestion.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    Log.d("TAG", "Value is: " + value);

                    try {
                        suggestionstxtsleeptime.setText("Current Sleeping Time is" + value);
                    } catch (Exception e) {
                        Log.e("TAG", "Error setting text: " + e.getMessage());
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Error reading data: " + e.getMessage());
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        // Doctor Advise ( Read From Database)
        doctoradvise.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    Log.d("TAG", "Value is: " + value);

                    try {
                        suggestedcurrentrecomendations.setText("Current Sleeping Recommendation is: " + value);
                    } catch (Exception e) {
                        Log.e("TAG", "Error setting text: " + e.getMessage());
                    }
                } catch (Exception e) {
                    Log.e("TAG", "Error reading data: " + e.getMessage());
                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });

        // Button Doctor Update
        btndoctorupdate.setOnClickListener(view -> {
            String suggestion = String.valueOf(suggestedcurrentrecomendations.getText());
            doctoradvise.setValue(" "+suggestion);
        });
//
        // navigate to doctor suggestions
        navigatebackdoctorsugg.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DoctorHomeActivity.class);
            startActivity(intent);
            finish();
        });

    }
}