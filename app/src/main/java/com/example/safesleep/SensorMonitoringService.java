//package com.example.safesleep;
//
//import android.app.AlarmManager;
//import android.app.Service;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//
//public class SensorMonitoringService extends Service implements SensorEventListener {
//
//    // Sensor handling code
//
//    @Override
//    public int onStartCommand() {
//        // Start monitoring
//        sensorManager.registerListener(this, sensor, samplingPeriod);
//
//        // Schedule stopSelf() at 6:30 PM
//        long stopTime = getStopTime();
//        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC, stopTime, pendingIntent);
//
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // Stop monitoring
//        sensorManager.unregisterListener(this);
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }
//}