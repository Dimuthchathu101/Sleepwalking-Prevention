package com.example.safesleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SleepwalkerHome extends AppCompatActivity {

    Button btnPrferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleepwalker_home);

        btnPrferences = findViewById(R.id.sleepwpreferences);

        btnPrferences.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), StartSleep.class);
            startActivity(intent);
            finish();
        });
    }
}