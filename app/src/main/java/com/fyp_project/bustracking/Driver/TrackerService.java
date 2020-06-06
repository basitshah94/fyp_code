package com.fyp_project.bustracking.Driver;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    String BusNumber = "";
    Location location;
    DatabaseReference ref;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        buildNotification();
        requestLocationUpdates();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            buildNotification();
        else
            startForeground(1, new Notification());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BusNumber = (String) intent.getExtras().get("bus_number");
        return super.onStartCommand(intent, flags, startId);
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
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
                .setContentTitle("Bus location updating...")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);


//        registerReceiver(stopReceiver, new IntentFilter(stop));
//        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
//                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
//        // Create the persistent notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                .setContentTitle(getString(R.string.app_name_service))
//                .setContentText(getString(R.string.notification_text_new))
//                .setOngoing(true)
//                .setContentIntent(broadcastIntent)
//                .setSmallIcon(R.drawable.bus_logo);
//        startForeground(1, builder.build());

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

                    location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "location update " + location);
                         ref = FirebaseDatabase.getInstance().getReference()
                                .child("BusDetail").child(BusNumber).child("current_location");
                        ref.setValue(location);
                    }
                    if (location.hasSpeed()) {
                        ref = FirebaseDatabase.getInstance().getReference()
                                .child("BusDetail").child(BusNumber).child("current_speed");
                        Log.d("Speed Check", "speed update " + location.getSpeed() * 3.6);
                        ref.setValue(location.getSpeed() * 3.6);
                    }
                }

            }, null);
        }
    }
}
