package com.example.safesleep;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
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

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        startTime = getIntent().getLongExtra("startTime", 0);

        Log.d(TAG, "phoneNumber: " + phoneNumber);
        Log.d(TAG, "startTime: " + startTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission("android.permission.CALL_PHONE") != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{"android.permission.CALL_PHONE"}, 1);
            } else {
                makeCall();
            }
        } else {
            makeCall();
        }

        Button goBackButton = findViewById(R.id.navigatebackautomatedcall);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SleepPreferences activity
                Intent intent = new Intent(AutomaticCallActivity.this, DoctorHomeActivity.class);
                startActivity(intent);

                // Finish the current activity
                finish();
            }
        });
    }

    private void makeCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeCall();
        }
    }
}
