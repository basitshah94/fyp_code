package com.fyp_project.bustracking.Commuter;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.fyp_project.bustracking.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    String user_id;
    private FirebaseAuth mAuth;

    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        requestLocationUpdates();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            buildNotification();
        else
            startForeground(1, new Notification());

    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    private void buildNotification() {
        String NOTIFICATION_CHANNEL_ID = "com.fyp_project.bustracking";
        String channelName = "Driver Background Service";

        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLightColor(Color.BLUE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.bus_logo)
                .setContentTitle("Commuter location updating...")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "location update " + location);
                        DatabaseReference getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("CommuterDetail")
                                .child(user_id)
                                .child("current_location");
                        getUserDatabaseReference.setValue(location);
                    }
                }

            }, null);
        }


        DatabaseReference getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter").child(user_id);
        getUserDatabaseReference.keepSynced(true);
        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameText = dataSnapshot.child("user_name").getValue().toString();
                String emailText = dataSnapshot.child("user_email").getValue().toString();
                String idText = dataSnapshot.child("user_id").getValue().toString();

                DatabaseReference getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("CommuterDetail")
                        .child(user_id)
                        .child("profile");
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("user_name", nameText);
                taskMap.put("user_email", emailText);
                taskMap.put("user_id", idText);
                getUserDatabaseReference.updateChildren(taskMap);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
