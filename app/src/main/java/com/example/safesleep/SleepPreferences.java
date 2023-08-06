package com.example.safesleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SleepPreferences extends AppCompatActivity {

    private TextView tvSelectedDate;
    private TextView tvSelectedTime;
    private TextView tvSelectedAwake;
    private Calendar calendar;
    private EditText etcaretakerMobile, etcaretakerEmail;
    

    Button goBackButton, updatePreferences;
    String caretakerEmail, caretakerMobile;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference caretakerMobileFirebase = database.getReference("caretakermobile");
    DatabaseReference caretakerEmailFirebase= database.getReference("caretakeremail");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_preferences);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvSelectedAwake = findViewById(R.id.tvEndTime);

        etcaretakerEmail = findViewById(R.id.caretakeremail);
        caretakerEmail = String.valueOf(etcaretakerEmail.getText());

        etcaretakerMobile = findViewById(R.id.caretakermobile);
        caretakerMobile = String.valueOf(etcaretakerMobile.getText());

        calendar = Calendar.getInstance();

        goBackButton = findViewById(R.id.navigatebackbutton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SleepPreferences activity
                Intent intent = new Intent(SleepPreferences.this, SleepwalkerHome.class);
                startActivity(intent);

                // Finish the current activity
                finish();
            }
        });

       



        updatePreferences = findViewById(R.id.btnPreferncesupdate);
        updatePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SleepPreferences activity

                String caretakerEmail = etcaretakerEmail.getText().toString();
                String caretakerMobile = etcaretakerMobile.getText().toString();

                if (isValidEmail(caretakerEmail) && isValidMobile(caretakerMobile)) {
                    caretakerEmailFirebase.setValue(caretakerEmail);
                    caretakerMobileFirebase.setValue(caretakerMobile);
                } else {
                    // Show a toast indicating invalid email or mobile format
                    Toast.makeText(getApplicationContext(), "Invalid email or mobile format", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void showDatePicker(View view) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String selectedDate = dateFormat.format(calendar.getTime());
                        tvSelectedDate.setText("Selected Date: " + selectedDate);
                        tvSelectedDate.setVisibility(View.VISIBLE);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    public void showTimePicker(View view) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        String selectedTime = timeFormat.format(calendar.getTime());
                        tvSelectedTime.setText("Selected Time: " + selectedTime);
                        tvSelectedTime.setVisibility(View.VISIBLE);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }

    public void showTimePickerAwake(View view) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        String selectedTime = timeFormat.format(calendar.getTime());
                        tvSelectedAwake.setText("Selected Time: " + selectedTime);
                        tvSelectedAwake.setVisibility(View.VISIBLE);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }
    // Function to validate email format
    private boolean isValidEmail(String email) {
        // Use a regular expression to validate email format
        // This is a simple example; you might want to use a more comprehensive regex
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    // Function to validate mobile number format
    private boolean isValidMobile(String mobile) {
        // Use a regular expression to validate mobile number format
        // This is a simple example; you might want to use a more comprehensive regex
        String regex = "^[0-9]{10}$";
        return mobile.matches(regex);
    }
    
}
