package com.example.safesleep;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the alarm message from the intent
        String alarmMessage = intent.getStringExtra("ALARM_MESSAGE");

        // Get the time of the alarm from the intent
        int timeBeforeMedianHours = intent.getIntExtra("ALARM_HOUR", -1);
        int timeBeforeMedianMinutes = intent.getIntExtra("ALARM_MINUTE", -1);

        // Get the current time
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        // Get the phone number you want to call
//        String phoneNumber = "0375776312"; // Replace with the actual phone number
//
//        // Create the phone call intent
//        Intent callIntent = new Intent(Intent.ACTION_CALL);
//        callIntent.setData(Uri.parse("tel:" + phoneNumber));
//        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        try {
//            // Start the phone call
//            context.startActivity(callIntent);
//        } catch (SecurityException e) {
//            Log.e("AlarmReceiver", "Error starting phone call: " + e.getMessage());
//        }


        // Check if the current time is within five minutes of the alarm time
        if (Math.abs(currentHour - timeBeforeMedianHours) == 0 && Math.abs(currentMinute - timeBeforeMedianMinutes) <= 5) {
            // Display the alarm message as a Toast
            Toast.makeText(context, alarmMessage, Toast.LENGTH_LONG).show();

            // Vibrate the device for 4 seconds
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    // For devices below Android 10, use the deprecated vibrate method
                    vibrator.vibrate(4000);
                }
            }

            // Play the default ringtone
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
            if (ringtone != null) {
                ringtone.play();
            }
        }
    }
}

