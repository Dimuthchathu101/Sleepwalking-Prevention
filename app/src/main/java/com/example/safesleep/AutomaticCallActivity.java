package com.example.safesleep;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AutomaticCallActivity extends AppCompatActivity {

    private static final String TAG = "AutomaticCallActivity";

    private String phoneNumber;
    private long startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatic_call);

        // Phone numbers and Start Time
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        startTime = getIntent().getLongExtra("startTime", 0);

        Log.d(TAG, "phoneNumber: " + phoneNumber);
        Log.d(TAG, "startTime: " + startTime);

        // Requesting Permisions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission("android.permission.CALL_PHONE") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{"android.permission.CALL_PHONE"}, 1);
            } else {
                makeCall();
            }
        } else {
            makeCall();
        }

        // Go Back Button Function
        Button goBackButton = findViewById(R.id.navigatebackautomatedcall);
        goBackButton.setOnClickListener(v -> {
            // Navigate to SleepPreferences activity
            Intent intent = new Intent(AutomaticCallActivity.this, SleepwalkerHome.class);
            startActivity(intent);

            // Finish the current activity
            finish();
        });
    }

    // Make Call to Predefined Function by User
    private void makeCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    // Request Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeCall();
        }
    }
}