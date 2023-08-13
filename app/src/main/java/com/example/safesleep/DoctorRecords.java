package com.example.safesleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorRecords extends AppCompatActivity {

    private RecyclerView recyclerView;

    private MessageAdapter adapter;
    private List<DataModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_records);

        // Data options ( RecylcerView)
        recyclerView = findViewById(R.id.recycler_viewdoctor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapter = new MessageAdapter(dataList);
        recyclerView.setAdapter(adapter);

        // Sleep Awakeing Time Suggestion
        DatabaseReference messagesRef2 = FirebaseDatabase.getInstance().getReference("messages");
        messagesRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String key = childSnapshot.getKey();
                    String value = childSnapshot.getValue(String.class);
                    dataList.add(new DataModel(key, value));
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error here if retrieval fails
                Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
            }
        });

        // Go Back Button Handling
        Button goBackButton = findViewById(R.id.navigatebackdoctor);
        goBackButton.setOnClickListener(v -> {
            // Navigate to SleepPreferences activity
            Intent intent = new Intent(DoctorRecords.this, DoctorHomeActivity.class);
            startActivity(intent);

            // Finish the current activity
            finish();
        });
    }

}