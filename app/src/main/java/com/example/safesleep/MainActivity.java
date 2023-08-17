
package com.example.safesleep;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    // Variables for data preprocessing
    private float maxAcceleration = 0.0f;

    EditText loginEmail;
    EditText loginPassword;
    Button loginButton, loginSignUp;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Write a message to the database (you can remove this if not needed for testing)
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");

        // Initialize the views
        loginEmail = findViewById(R.id.loginemail);
        loginPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.roundButton);
        loginSignUp = findViewById(R.id.loginSignUp);

        // Set click listeners for login and sign-up buttons
        loginButton.setOnClickListener(view -> loginUser());

        loginSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterScreen.class);
            startActivity(intent);
            finish();
        });

    }

    // Login User Function
    private void loginUser() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        // Check if email and password fields are not empty
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_LONG).show();
            return;
        }

        // Sign in with email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Authentication success",
                                Toast.LENGTH_SHORT).show();
                        moveToSleepwalkerHome();
                    } else {
                        // If sign-in fails, display a message to the user.
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // On Start Method

    /**
     * In this method user will be directed to home screen of doctor or sleepwalker
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            moveToSleepwalkerHome();
        }
    }

    private void moveToSleepwalkerHome() {
        Intent intent = new Intent(getApplicationContext(), SleepwalkerHome.class);
        startActivity(intent);
        finish();

    }
}
