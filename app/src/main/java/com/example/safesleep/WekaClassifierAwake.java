package com.example.safesleep;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.ArrayList;
import java.util.List;

public class WekaClassifierAwake {
    public static void classifyData(String[] firebaseData) {
        // Step 1: Load the dataset from Firebase
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages");
        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> firebaseDataList = new ArrayList<>();

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String data = childSnapshot.getValue(String.class);
                    if (data != null && !data.isEmpty()) {
                        firebaseDataList.add(data);
                    }
                }

                // Convert the list to an array
                String[] firebaseData = firebaseDataList.toArray(new String[0]);

                // Step 2: Prepare the classifier and train it with the dataset
                Instances dataset = loadDatasetFromFirebase(firebaseData);
                if (dataset == null) {
                    Log.d("WekaClassifierAwake", "Dataset is empty or contains invalid data. Cannot train the classifier.");
                    return;
                }
                dataset.setClassIndex(-1); // Set the class index to -1 (no class attribute)

                Classifier classifier = trainClassifier(dataset);

                // Step 3: Classify new instances (optional, depending on your requirements)
                classifyNewInstances(classifier, dataset);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event if needed
            }
        });
    }

    private static Instances loadDatasetFromFirebase(String[] firebaseData) {
        // Create attributes
        FastVector attributes = new FastVector();
        Attribute timeAttribute = new Attribute("TimeSecondsSinceMidnight");

        attributes.addElement(timeAttribute);

        // Create dataset
        Instances dataset = new Instances("DatasetName", attributes, 0);

        // Add instances from firebaseData using a for loop
        for (String data : firebaseData) {
            int secondsSinceMidnight = getSecondsSinceMidnight(data);
            if (secondsSinceMidnight >= 0) {
                Instance instance = new Instance(1);
                instance.setValue(timeAttribute, secondsSinceMidnight);
                dataset.add(instance);
            }
        }

        return dataset;
    }

    private static int getSecondsSinceMidnight(String timestamp) {
        // Assuming the timestamp format is "yyyyMMdd_HHmmss"
        try {
            int hours = Integer.parseInt(timestamp.substring(9, 11));
            int minutes = Integer.parseInt(timestamp.substring(11, 13));
            int seconds = Integer.parseInt(timestamp.substring(13, 15));

            return hours * 3600 + minutes * 60 + seconds;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            Log.e("WekaClassifierAwake", "Error parsing time from timestamp: " + timestamp);
            return -1; // Return -1 to indicate an error
        }
    }

    private static Classifier trainClassifier(Instances dataset) {
        // Create and train the classifier
        StringToWordVector filter = new StringToWordVector();
        try {
            filter.setInputFormat(dataset);
            dataset = Filter.useFilter(dataset, filter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Classifier classifier = new NaiveBayes();
        try {
            classifier.buildClassifier(dataset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return classifier;
    }

    private static void classifyNewInstances(Classifier classifier, Instances dataset) {
        // Classify new instances (optional, depending on your requirements)
        // For example, you can create a new instance and set its values, then use the classifier to classify it
        // For demonstration purposes, let's classify the first instance in the dataset

        if (dataset.numInstances() > 0) {
            Instance instanceToClassify = dataset.instance(0);

            try {
                double predictedClass = classifier.classifyInstance(instanceToClassify);

                // Print the predicted class value (for binary classification, usually 0 or 1)
                Log.d("WekaClassifierAwake", "Predicted Class: " + predictedClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("WekaClassifierAwake", "Dataset is empty. Cannot classify new instances.");
        }
    }
}
