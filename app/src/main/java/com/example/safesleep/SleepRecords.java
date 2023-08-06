package com.example.safesleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SleepRecords extends AppCompatActivity {
    private FloatingActionButton fab;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private RecyclerView recyclerView;
    private List<DataClass> dataList;
    private MyAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_records);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        dataList = new ArrayList<>();
        adapter = new MyAdapter(this, dataList);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials");
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {

@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
    dataList.clear();

    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
        // Check if the data exists
        if (itemSnapshot.exists()) {
            DataClass dataClass = itemSnapshot.getValue(DataClass.class);
            if (dataClass != null) {
                dataClass.setKey(itemSnapshot.getKey());
                dataList.add(dataClass);
            } else {
                // Display error toast for parsing failure
                Toast.makeText(SleepRecords.this, "Failed to parse DataClass from snapshot: " + itemSnapshot.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Display error toast for snapshot not existing
            Toast.makeText(SleepRecords.this, "Snapshot does not exist.", Toast.LENGTH_SHORT).show();
        }
    }

    // Notify the adapter about data changes
    if (adapter != null) {
        adapter.notifyDataSetChanged();
    } else {
        // Display error toast for null adapter
        Toast.makeText(SleepRecords.this, "Adapter is null.", Toast.LENGTH_SHORT).show();
    }
}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error here if the data retrieval is canceled
                // Display error toast for data retrieval cancellation
                Toast.makeText(SleepRecords.this, "Data retrieval canceled: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                // You can add your own error handling or show an error message to the user if needed.
            }

        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SleepRecords.this, UploadActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseReference != null && eventListener != null) {
            databaseReference.removeEventListener(eventListener);
        } else {
            Toast.makeText(this, "Database Reference Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void searchList(String text) {
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass : dataList) {
            if (dataClass.getDataTitle().toLowerCase().contains(text.toLowerCase())) {
                searchList.add(dataClass);
            } else {
                Toast.makeText(this, "dataclass error", Toast.LENGTH_SHORT).show();
            }
        }
        adapter.searchDataList(searchList);
    }
}
