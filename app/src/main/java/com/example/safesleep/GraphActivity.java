package com.example.safesleep;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.webkit.WebView;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class GraphActivity extends AppCompatActivity {
//
//    private DatabaseReference messagesRef;
//    private List<Integer> validSecondsSinceMidnightList = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_graph);
//
//        WebView webView = findViewById(R.id.webView);
//        webView.getSettings().setJavaScriptEnabled(true);
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        messagesRef = database.getReference("message");
//
//        messagesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                validSecondsSinceMidnightList.clear(); // Clear the list before populating
//
//                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
//                    String messageKey = childSnapshot.getKey();
//
//                    // Get the time part (HHmmss) from the message key
//                    String timePart = messageKey.substring(9); // Assuming format "yyyyMMdd_HHmmss"
//
//                    // Extract hours, minutes, and seconds
//                    int hours = Integer.parseInt(timePart.substring(0, 2));
//                    int minutes = Integer.parseInt(timePart.substring(2, 4));
//                    int seconds = Integer.parseInt(timePart.substring(4, 6));
//
//                    // Calculate seconds since midnight for this message
//                    int secondsSinceMidnight = hours * 3600 + minutes * 60 + seconds;
//
//                    // Add the calculated value to the list
//                    validSecondsSinceMidnightList.add(secondsSinceMidnight);
//                }
//
//                // After processing all data, proceed to load Google Chart in WebView
//                loadGoogleChartInWebView(webView);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
//            }
//        });
//    }
//
//    private void loadGoogleChartInWebView(WebView webView) {
//        String chartUrl = constructGoogleChartUrl();
//        webView.loadUrl(chartUrl);
//    }
//
//    private String constructGoogleChartUrl() {
//        StringBuilder chartData = new StringBuilder();
//        chartData.append("Date|Time\n"); // Column headers
//
//        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
//
//        for (int i = 0; i < validSecondsSinceMidnightList.size(); i++) {
//            int secondsSinceMidnight = validSecondsSinceMidnightList.get(i);
//
//            // Convert secondsSinceMidnight to a Date object
//            int hours = secondsSinceMidnight / 3600;
//            int minutes = (secondsSinceMidnight % 3600) / 60;
//            int seconds = secondsSinceMidnight % 60;
//            String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
//
//            // Assuming you have the date available as well
//            String date = "20230729"; // Replace with your actual date
//
//            // Format the date and time
//            Date dateTime;
//            try {
//                dateTime = dateFormatter.parse(date + "_" + formattedTime);
//                String formattedDate = new SimpleDateFormat("yyyyMMdd").format(dateTime);
//                chartData.append(formattedDate).append("|").append(formattedTime).append("\n");
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // Encode chart data for URL
//        String encodedData = Uri.encode(chartData.toString());
//
//        // Construct Google Chart URL
//        return "https://chart.googleapis.com/chart?" +
//                "cht=lc&chs=300x200&chd=t:" + encodedData +
//                "&chl=Date|Time";
//    }
//}
public class GraphActivity extends AppCompatActivity {

    private DatabaseReference messagesRef;
    private List<String> validTimestampList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        messagesRef = database.getReference("message");

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                validTimestampList.clear(); // Clear the list before populating

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String timestamp = childSnapshot.getValue(String.class);
                    validTimestampList.add(timestamp);
                }

                loadGoogleChartInWebView(webView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Data retrieval failed: " + databaseError.getMessage());
            }
        });
    }

    private void loadGoogleChartInWebView(WebView webView) {
        String chartUrl = constructGoogleChartUrl();
        webView.loadUrl(chartUrl);
    }

    private String constructGoogleChartUrl() {
        StringBuilder chartData = new StringBuilder();
        chartData.append("Date|Count\n"); // Column headers

        Map<String, Integer> dateCountMap = new HashMap<>();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd_HHmmss");

        for (String timestamp : validTimestampList) {
            try {
                Date dateTime = dateFormatter.parse(timestamp);
                String formattedDate = new SimpleDateFormat("dd").format(dateTime);

                if (dateCountMap.containsKey(formattedDate)) {
                    int count = dateCountMap.get(formattedDate);
                    dateCountMap.put(formattedDate, count + 1);
                } else {
                    dateCountMap.put(formattedDate, 1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<String, Integer> entry : dateCountMap.entrySet()) {
            chartData.append(entry.getKey()).append("|").append(entry.getValue()).append("\n");
        }

        String encodedData = Uri.encode(chartData.toString());
        return "https://chart.googleapis.com/chart?" +
                "cht=bvg&chs=500x300&chxt=y,x&chd=t:" + encodedData +
                "&chl=Count|Date";
    }
}

