//package com.example.safesleep;
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class AutomaticCallActivityTest {
//
//    @Mock
//    AppCompatActivity mockActivity;
//
//    @Mock
//    Bundle mockBundle;
//
//    @Mock
//    Intent mockIntent;
//
//    @Mock
//    Uri mockUri;
//
//    @Before
//    public void setup() {
//        when(mockActivity.getIntent()).thenReturn(mockIntent);
//        when(mockIntent.getStringExtra("phoneNumber")).thenReturn("1234567890");
//        when(mockIntent.getLongExtra("startTime", 0)).thenReturn(1234567890L);
//        when(mockActivity.checkSelfPermission(Mockito.anyString()))
//                .thenReturn(PackageManager.PERMISSION_GRANTED);
//        when(mockActivity.getPackageManager()).thenReturn(Mockito.mock(PackageManager.class));
//        when(mockIntent.setData(mockUri)).thenReturn(mockIntent);
//    }
//
//    @Test
//    public void onCreate_PermissionGranted_MakeCall() {
//        AutomaticCallActivity activity = new AutomaticCallActivity() {
//            @Override
//            protected void makeCall() {
//                // Mocking the call
//            }
//        };
//
//        activity.onCreate(mockBundle);
//
//        verify(activity).makeCall();
//    }
//
//    @Test
//    public void onRequestPermissionsResult_GrantPermission_MakeCall() {
//        AutomaticCallActivity activity = new AutomaticCallActivity() {
//            @Override
//            protected void makeCall() {
//                // Mocking the call
//            }
//        };
//
//        activity.onRequestPermissionsResult(1, new String[]{"android.permission.CALL_PHONE"},
//                new int[]{PackageManager.PERMISSION_GRANTED});
//
//        verify(activity).makeCall();
//    }
//
//    // Add more tests as needed
//}
