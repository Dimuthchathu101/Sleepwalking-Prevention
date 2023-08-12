package com.example.safesleep;

public class Doctors {
    private String username;
    private String email;
    private String qualifications;

    public Doctors() {
        // Default constructor required for Firebase
    }

    public Doctors(String username, String email, String qualifications) {
        this.username = username;
        this.email = email;
        this.qualifications = qualifications;
    }

    // Add getters and setters
}