package com.example.safesleep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SleepPreferences extends AppCompatActivity {

    // Setting up the variables
    private TextView tvSelectedDate;
    private TextView tvSelectedTime;
    private TextView tvSelectedAwake;
    private Calendar calendar;
    private EditText etcaretakerMobile, etcaretakerEmail;

    private TextView tvCaretakerMobile, tvCaretakerEmail;

    Button goBackButton, updatePreferences;
    String caretakerEmail, caretakerMobile;

    // Firebase Options
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference caretakerMobileFirebase = database.getReference("caretakermobile");
    DatabaseReference caretakerEmailFirebase = database.getReference("caretakeremail");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_preferences);

        // Setting up the UI
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvSelectedAwake = findViewById(R.id.tvEndTime);
        tvCaretakerEmail = findViewById(R.id.caretakeremailfirebase);
        tvCaretakerMobile = findViewById(R.id.caretakermobilefirebase);

        etcaretakerEmail = findViewById(R.id.caretakeremail);
        caretakerEmail = String.valueOf(etcaretakerEmail.getText());

        etcaretakerMobile = findViewById(R.id.caretakermobile);
        caretakerMobile = String.valueOf(etcaretakerMobile.getText());

        calendar = Calendar.getInstance();

        // Go Back Button Options
        goBackButton = findViewById(R.id.navigatebackbutton);
        goBackButton.setOnClickListener(v -> {
            // Navigate to SleepPreferences activity
            Intent intent = new Intent(SleepPreferences.this, SleepwalkerHome.class);
            startActivity(intent);

            // Finish the current activity
            finish();
        });

        // Adding the mobile number to firebase database
        caretakerMobileFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);

                    try {
                        tvCaretakerMobile.setText("Current Caretaker Number: " + value);
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

        // Setting the caretaker email in the firebase database
        caretakerEmailFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);

                    try {
                        tvCaretakerEmail.setText("Current Caretaker Email: " + value);
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

        // Update the Preferences Button
        updatePreferences = findViewById(R.id.btnPreferncesupdate);
        updatePreferences.setOnClickListener(v -> {
            // Build and show a confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(SleepPreferences.this);
            builder.setTitle("Confirm Changes");
            builder.setMessage("Are you sure you want to make these changes?");
            builder.setPositiveButton("OK", (dialog, which) -> {
                // User clicked OK, proceed with changes
                String caretakerEmail = etcaretakerEmail.getText().toString();
                String caretakerMobile = etcaretakerMobile.getText().toString();

                if (isValidEmail(caretakerEmail) && isValidMobile(caretakerMobile)) {
                    caretakerEmailFirebase.setValue(caretakerEmail);
                    caretakerMobileFirebase.setValue(caretakerMobile);
                    // Show a toast indicating successful changes
                    Toast.makeText(getApplicationContext(), "Changes applied", Toast.LENGTH_SHORT).show();
                } else {
                    // Show a toast indicating invalid email or mobile format
                    Toast.makeText(getApplicationContext(), "Invalid email or mobile format", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                // User clicked Cancel, do nothing or show a message
                Toast.makeText(getApplicationContext(), "Changes canceled", Toast.LENGTH_SHORT).show();
            });
            builder.show();
        });


        DatabaseReference sleepdatepreference = database.getReference("sleepdatepreference");
        sleepdatepreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);

                    try {
                        tvSelectedDate.setText("Selected Date: " + value);
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

            }
        });

        DatabaseReference sleeptimepreference = database.getReference("sleeptimepreference");

        sleeptimepreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);

                    try {
                        tvSelectedTime.setText("Bed Time: " + value);
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

            }
        });

        DatabaseReference awaketimepreference = database.getReference("awaketimepreference");

        awaketimepreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);

                    try {
                        tvSelectedAwake.setText("Awake Time: " + value);
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

            }
        });


    }

    // Show Date Picker Option to select the date
    public void showDatePicker(View view) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view1, year1, month1, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, month1);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String selectedDate = dateFormat.format(calendar.getTime());

                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(this);
                    confirmationDialog.setTitle("Confirm Date Change");
                    confirmationDialog.setMessage("Do you want to change the preffered date?");
                    confirmationDialog.setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference sleepdatepreference = database.getReference("sleepdatepreference");
                        sleepdatepreference.setValue(selectedDate);

                        // You can display a success message or perform other actions here
                    });
                    confirmationDialog.setNegativeButton("No", (dialog, which) -> {
                        // User chose not to add the data, you can handle this as needed
                    });
                    confirmationDialog.show();
                }, year, month, day);

        datePickerDialog.show();
    }


    // show time picker option to select the time
    public void showTimePicker(View view) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view1, hourOfDay, minute1) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute1);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String selectedTime = timeFormat.format(calendar.getTime());

                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(this);
                    confirmationDialog.setTitle("Confirm Time Change");
                    confirmationDialog.setMessage("Do you want to change the time of sleep preference?");
                    confirmationDialog.setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference sleeptimepreference = database.getReference("sleeptimepreference");
                        sleeptimepreference.setValue(selectedTime);

                        // You can display a success message or perform other actions here
                    });
                    confirmationDialog.setNegativeButton("No", (dialog, which) -> {
                        // User chose not to add the data, you can handle this as needed
                    });
                    confirmationDialog.show();
                }, hour, minute, true);

        timePickerDialog.show();
    }


    // Time Picker to Awake
    public void showTimePickerAwake(View view) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view1, hourOfDay, minute1) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute1);

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String selectedTime = timeFormat.format(calendar.getTime());

                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(this);
                    confirmationDialog.setTitle("Confirm Awake Time Change");
                    confirmationDialog.setMessage("Do you want to change the selected time?");
                    confirmationDialog.setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference awaketimepreference = database.getReference("awaketimepreference");
                        awaketimepreference.setValue(selectedTime);

                        // You can display a success message or perform other actions here
                    });
                    confirmationDialog.setNegativeButton("No", (dialog, which) -> {
                        // User chose not to add the data, you can handle this as needed
                    });
                    confirmationDialog.show();
                }, hour, minute, true);

        timePickerDialog.show();
    }

    // Function to validate email format

    // Regular Epxressions
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
