package com.example.safesleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterDoctorActivity extends AppCompatActivity {

    // Setting Variables
    Button btnRegister, btnLogin;
    EditText confirmPasswordDoctor;
    EditText        passworddoctor;
    EditText smlcRegistrationdoctor;
    EditText        qualificationsdoctor;
    EditText emaildoctor;
    EditText usernamedoctor;
    Button signupButtonDoctor;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_doctor);

        mAuth = FirebaseAuth.getInstance();

        // Setting up the UI
        btnLogin = findViewById(R.id.regLoginDoctor);
        confirmPasswordDoctor = findViewById(R.id.confirmPasswordDoctor);
        passworddoctor = findViewById(R.id.passworddoctor);
        smlcRegistrationdoctor = findViewById(R.id.smlcRegistrationdoctor);
        qualificationsdoctor = findViewById(R.id.qualificationsdoctor);
        emaildoctor = findViewById(R.id.emaildoctor);
        usernamedoctor = findViewById(R.id.usernamedoctor);
        signupButtonDoctor = findViewById(R.id.signupButtonDoctor);

        // Firebase initailization
        FirebaseApp.initializeApp(this);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("doctors");

        // Login on click listener
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Sign Up Button Onclick Listener
        signupButtonDoctor.setOnClickListener(view -> {
            String email, password, name, confirmPassword; // Fix the variable name here
            name = String.valueOf(usernamedoctor.getText());
            email = String.valueOf(emaildoctor.getText());
            password = String.valueOf(passworddoctor.getText());
            confirmPassword = String.valueOf(confirmPasswordDoctor.getText()); // Fix the variable name here

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(RegisterDoctorActivity.this, "Enter Your Name", Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegisterDoctorActivity.this, "Enter Email", Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterDoctorActivity.this, "Enter Password", Toast.LENGTH_LONG).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterDoctorActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterDoctorActivity.this, "Authentication passed",
                                    Toast.LENGTH_SHORT).show();

                            // TODO: Add any additional logic after successful registration
                            Intent intent = new Intent(getApplicationContext(), SleepwalkerHome.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterDoctorActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

            String qualifications = qualificationsdoctor.getText().toString();
            String username = usernamedoctor.getText().toString();

            // Create a Doctor object or use a Map to store the data
            Doctors doctor = new Doctors(username, email, qualifications);

            // Example: Storing the data under a "doctors" node with a generated key
            String key = databaseReference.push().getKey();
            databaseReference.child(key).setValue(doctor);

            // Display success or failure message
            Toast.makeText(getApplicationContext(), "Doctor registered successfully!", Toast.LENGTH_SHORT).show();
        });



    }
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