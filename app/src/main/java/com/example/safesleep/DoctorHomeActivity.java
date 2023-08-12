package com.example.safesleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DoctorHomeActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference sleepTime = database.getReference("sleepingtimesuggestion");
    DatabaseReference awakeTime = database.getReference("scheduledawakening");

    DatabaseReference lastepisode = database.getReference("lastepisode");

    TextView tvsuggestion01, tvSuggestion2, tvsuggestion03, tvSuggestion04;

    Button btnPatients, btnRecords, doctorSuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);

        tvsuggestion03 = findViewById(R.id.suggestion03);
        tvSuggestion04 = findViewById(R.id.suggestion04);
        tvsuggestion01 = findViewById(R.id.suggestion01);
        tvSuggestion2 = findViewById(R.id.suggestion02);
//        tvSuggestion4 = findViewById(R.id.suggest);
        btnPatients = findViewById(R.id.doctorPatients);
        btnRecords = findViewById(R.id.doctorRecords);
        doctorSuggestions = findViewById(R.id.doctorSuggestions);

        sleepTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
                tvsuggestion03.setText("Sleepwalker 01 Sleep In Time : "+ value);
                tvsuggestion03.setTextColor(Color.BLACK);
                tvsuggestion03.setTextSize(18);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        awakeTime.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
                tvSuggestion04.setText("Sleepwalker 01 Scheduled Awakening : "+ value);
                tvSuggestion04.setTextColor(Color.BLACK);
                tvSuggestion04.setTextSize(18);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        lastepisode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
//                Log.d(TAG, "Value is: " + value);
                tvsuggestion01.setText("Sleepwalker 01 Last Episode Detected : "+ value);
                tvsuggestion01.setTextColor(Color.BLACK);
                tvsuggestion01.setTextSize(18);

                tvSuggestion2.setText("Sleepwalker 02 Last Episode Detected : "+ value);
                tvSuggestion2.setTextColor(Color.BLACK);
                tvSuggestion2.setTextSize(18);



            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnPatients.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SleepRecords.class);
            startActivity(intent);
            finish();
        });

        btnRecords.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DoctorRecords.class);
            startActivity(intent);
            finish();
        });

        doctorSuggestions.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DoctorSuggestions.class);
            startActivity(intent);
            finish();
        });
    }
}