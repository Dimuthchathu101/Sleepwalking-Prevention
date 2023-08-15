//package com.example.safesleep;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//import weka.classifiers.functions.Logistic;
//
//public class RegisterScreen extends AppCompatActivity {
//
//    EditText editEmail, editPassword, editName, editConfirmPassowrd;
//    Button btnReg,regLogin;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register_screen);
//
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//
//        editName = findViewById(R.id.reguserName);
//        editEmail = findViewById(R.id.regemail);
//        editPassword = findViewById(R.id.regpassword);
//
//        btnReg = findViewById(R.id.regsignupButton);
//        regLogin = findViewById(R.id.regLogin);
//
//        regLogin.setOnClickListener(view -> {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        });
//
//        btnReg.setOnClickListener(view -> {
//            String email, password, name, confirmpassword;
//            name = String.valueOf(editName.getText());
//            email = String.valueOf(editEmail.getText());
//            password = String.valueOf(editPassword.getText());
//            confirmpassword = String.valueOf(editConfirmPassowrd.getText());
//
//
//            if (TextUtils.isEmpty(name)) {
//                Toast.makeText(RegisterScreen.this, "Enter Your Name", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//
//            if (TextUtils.isEmpty(email)) {
//                Toast.makeText(RegisterScreen.this, "Enter Email", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            if (TextUtils.isEmpty(password)) {
//                Toast.makeText(RegisterScreen.this, "Enter Password", Toast.LENGTH_LONG).show();
//                return;
//            }
//            if (TextUtils.isEmpty(confirmpassword)) {
//                Toast.makeText(RegisterScreen.this, "Confirm Your Password", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//
//            mAuth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(RegisterScreen.this, "Authentication passed",
//                                        Toast.LENGTH_SHORT).show();
//
//                                // TODO: Add any additional logic after successful registration
//                                Intent intent = new Intent(getApplicationContext(), SleepwalkerHome.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        }
//                    });
//        });
//    }
//}
package com.example.safesleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterScreen extends AppCompatActivity {

    // Setting up the variables
    EditText editEmail, editPassword, editName, editConfirmPassword; // Fix the variable name here
    Button btnReg, regLogin, docregister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        editName = findViewById(R.id.reguserName);
        editEmail = findViewById(R.id.regemail);
        editPassword = findViewById(R.id.regpassword);
        editConfirmPassword = findViewById(R.id.regconfirmPassword); // Fix the variable binding here

        btnReg = findViewById(R.id.regsignupButton);
        regLogin = findViewById(R.id.regLogin);
        docregister = findViewById(R.id.regLoginDoctor);

        // Login Button onClickListener
        regLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Resgister Button Onclick Listener
        docregister.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterDoctorActivity.class);
            startActivity(intent);
            finish();
        });

        // Register Button Onclick Functions
        btnReg.setOnClickListener(view -> {
            String email, password, name, confirmPassword; // Fix the variable name here
            name = String.valueOf(editName.getText());
            email = String.valueOf(editEmail.getText());
            password = String.valueOf(editPassword.getText());
            confirmPassword = String.valueOf(editConfirmPassword.getText()); // Fix the variable name here

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(RegisterScreen.this, "Enter Your Name", Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegisterScreen.this, "Enter Email", Toast.LENGTH_LONG).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterScreen.this, "Enter Password", Toast.LENGTH_LONG).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterScreen.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterScreen.this, "Authentication passed",
                                    Toast.LENGTH_SHORT).show();

                            // TODO: Add any additional logic after successful registration
                            Intent intent = new Intent(getApplicationContext(), SleepwalkerHome.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
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
