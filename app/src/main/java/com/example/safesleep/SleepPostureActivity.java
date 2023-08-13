package com.example.safesleep;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.Experimental;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

public class SleepPostureActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    int PERMISSION_REQUESTS = 1;

    PreviewView previewView;

    // Base pose detector with streaming frames, when depending on the pose-detection sdk
    PoseDetectorOptions options =
            new PoseDetectorOptions.Builder()
                    .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                    .build();

    PoseDetector poseDetector = PoseDetection.getClient(options);

    Canvas canvas;

    Paint mPaint = new Paint();

    Display display;

    Bitmap bitmap4Save;

    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<Bitmap> bitmap4DisplayArrayList = new ArrayList<>();

    ArrayList<Pose> poseArrayList = new ArrayList<>();

    boolean isRunning = false;

    private Map<String, String> messagesMap;

    private MediaPlayer mediaPlayer;

    @ExperimentalGetImage
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_posture);

        messagesMap = new HashMap<>();

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        previewView = findViewById(R.id.previewView);

        display = findViewById(R.id.displayOverlay);

        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(10);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }


    Runnable RunMlkit = new Runnable() {
        @Override
        public void run() {
            poseDetector.process(InputImage.fromBitmap(bitmapArrayList.get(0), 0)).addOnSuccessListener(new OnSuccessListener<Pose>() {
                @Override
                public void onSuccess(Pose pose) {
                    try {
                        if (pose != null) {
                            poseArrayList.add(pose);
                            PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
                            PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
                            PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
                            PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
                            PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
                            PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);

                            if (leftShoulder != null && rightShoulder != null && leftHip != null && rightHip != null
                                    && leftKnee != null && rightKnee != null) {
                                // Determine sleep posture based on landmark positions
                                try {
                                    boolean isSleepingOnSide = leftShoulder.getPosition().y > rightShoulder.getPosition().y
                                            || rightShoulder.getPosition().y > leftShoulder.getPosition().y;

                                    boolean isSleepingOnFront = leftHip.getPosition().y > rightHip.getPosition().y
                                            && leftKnee.getPosition().y > rightKnee.getPosition().y;

                                    boolean isSleepingOnBack = rightHip.getPosition().y > leftHip.getPosition().y
                                            && rightKnee.getPosition().y > leftKnee.getPosition().y;

                                    if (isSleepingOnSide) {
//                                        Toast.makeText(SleepPostureActivity.this, "Sleeping on Side", Toast.LENGTH_SHORT).show();
                                    } else if (isSleepingOnFront) {
//                                        Toast.makeText(SleepPostureActivity.this, "Sleeping on Front", Toast.LENGTH_SHORT).show();
                                    } else if (isSleepingOnBack) {
//                                        Toast.makeText(SleepPostureActivity.this, "Sleeping on Back", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // Handle exceptions related to pose landmark comparison
                                }
                            } else {
                                // Handle the case where some landmarks are missing
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String currentTime = getCurrentTime();
                                String messageKey = currentTime; // Use the formatted current time as the key
                                messagesMap.put(messageKey, messageKey);
                                DatabaseReference myRef = database.getReference("messages");
                                myRef.child(messageKey).setValue(messageKey);
//                                Toast.makeText(SleepPostureActivity.this, "Incomplete pose data", Toast.LENGTH_SHORT).show();
//                                mediaPlayer = MediaPlayer.create(SleepPostureActivity.this, R.raw.alarm);

                                DatabaseReference lastepisode = database.getReference("lastepisode");

                                lastepisode.setValue(messageKey);

                                DatabaseReference caretakerMobileFirebase = database.getReference("caretakermobile");
                                caretakerMobileFirebase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // This method is called once with the initial value and again
                                        // whenever data at this location is updated.
                                        String value = dataSnapshot.getValue(String.class);
                                        Intent intent = new Intent(SleepPostureActivity.this, AutomaticCallActivity.class);
                                        intent.putExtra("phoneNumber", value);
                                        intent.putExtra("startTime", 1616048600000L);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        // Failed to read value
                                        Log.w("TAG", "Failed to read value.", error.toException());
                                    }
                                });

                            }
                        } else {
                            // Handle the case where no pose was detected
                            Toast.makeText(SleepPostureActivity.this, "No pose detected", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Handle any exceptions that may occur during pose processing
                    }
                }
            })
.addOnFailureListener(e -> {
    // Handle failure
    Toast.makeText(SleepPostureActivity.this, "Pose detection failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
});
        }
    };

    private String getCurrentTime() {
        // Get the current date and time
        Date currentTime = new Date();

        // Format the current time as a unique key (e.g., "yyyyMMdd_HHmmss")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(currentTime);
    }


    @ExperimentalGetImage
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
//                         enable the following line if RGBA output is needed.
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ActivityCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                // insert your code here.
                // after done, release the ImageProxy object
                ByteBuffer byteBuffer = imageProxy.getImage().getPlanes()[0].getBuffer();
                byteBuffer.rewind();
                Bitmap bitmap = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(byteBuffer);

                Matrix matrix = new Matrix();
                matrix.postRotate(270);
                matrix.postScale(-1,1);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,0,0,imageProxy.getWidth(), imageProxy.getHeight(),matrix,false);

                bitmapArrayList.add(rotatedBitmap);

                if (poseArrayList.size() >= 1) {
                    canvas = new Canvas(bitmapArrayList.get(0));

                    for (PoseLandmark poseLandmark : poseArrayList.get(0).getAllPoseLandmarks()) {
                        canvas.drawCircle(poseLandmark.getPosition().x, poseLandmark.getPosition().y,5,mPaint);
                    }

                    bitmap4DisplayArrayList.clear();
                    bitmap4DisplayArrayList.add(bitmapArrayList.get(0));
                    bitmap4Save = bitmapArrayList.get(bitmapArrayList.size()-1);
                    bitmapArrayList.clear();
                    bitmapArrayList.add(bitmap4Save);
                    poseArrayList.clear();
                    isRunning = false;
                }

                if (poseArrayList.size() == 0 && bitmapArrayList.size() >= 1 && !isRunning) {
                    RunMlkit.run();
                    isRunning = true;
                }

                if (bitmap4DisplayArrayList.size() >= 1) {
                    display.getBitmap(bitmap4DisplayArrayList.get(0));
                }
                imageProxy.close();
            }
        });

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }


    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }
}