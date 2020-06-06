package com.fyp_project.bustracking.Commuter;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fyp_project.bustracking.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import mumayank.com.airlocationlibrary.AirLocation;

public class BusInfo extends AppCompatActivity {

    Toolbar toolbar;
    private DatabaseReference getUserDatabaseReference;
    private AirLocation airLocation;
    double userx;
    double usery;
    String BusNumberPath="";
    private static final String API_KEY = "AIzaSyBqyA6QdiYIuj12H2QiOVW6AHPd936VtnQ";

    TextView busNumber, routeName, routeTime, routeDistance, busCurrentLocation, busCurrentSpeed, estimatedDistance, estimatedTime, myCurrentLocation, upcomingRouteStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_detail);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Find Nearest Bus");
        setSupportActionBar(toolbar);

        BusNumberPath = getIntent().getStringExtra("bus_number");

        init();

        getBusInfo();
    }

    private void init() {
        busNumber = findViewById(R.id.busNo);
        routeName = findViewById(R.id.busroute);
        routeTime = findViewById(R.id.routeTime);
        routeDistance = findViewById(R.id.routeDistance);
        busCurrentLocation = findViewById(R.id.busCurrentLocation);
        busCurrentSpeed = findViewById(R.id.busCurrentSpeed);
        estimatedDistance = findViewById(R.id.estimatedDistance);
        estimatedTime = findViewById(R.id.estimatedTime);
        myCurrentLocation = findViewById(R.id.myCurrentLocation);
        upcomingRouteStop = findViewById(R.id.upcomingtime);
    }

    private void getBusInfo() {

        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("BusDetail")
                .child(BusNumberPath)
                .child("current_location");
        getUserDatabaseReference.keepSynced(true);
        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String latitude = dataSnapshot.child("latitude").getValue().toString();
                    String longitude = dataSnapshot.child("longitude").getValue().toString();
                    String speed = dataSnapshot.child("speed").getValue().toString();
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(BusInfo.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    airLocation = new AirLocation(BusInfo.this, true, true, new AirLocation.Callbacks() {
                        @Override
                        public void onSuccess(@NotNull Location location) {
                            userx = location.getLatitude();
                            usery = location.getLongitude();
                        }

                        @Override
                        public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
                        }
                    });

                    Geocoder geocoderUser;
                    List<Address> addressesUser;
                    geocoderUser = new Geocoder(BusInfo.this, Locale.getDefault());
                    addressesUser = geocoderUser.getFromLocation(userx, usery, 1);
                    String addressUser = addressesUser.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    LatLng from = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                    LatLng to = new LatLng(userx, usery);

//                            Double distance = SphericalUtil.computeDistanceBetween(from, to);
//                            Log.v("Time",""+getDurationForRoute(String.valueOf(from),String.valueOf(to)));
//                            distance(Double.valueOf(latitude),Double.valueOf(longitude),userx,usery)
//                            getDistance(Double.valueOf(latitude), Double.valueOf(longitude), userx, usery)


                    AlertDialog.Builder builder = new AlertDialog.Builder(BusInfo.this);
                    builder.setTitle("Find My Bus")
                            .setMessage("Bus # 1" + "\n" +

                                    "Bus Current Location : " + address + "\n" +
                                    "Bus Current Speed : " + speed + "\n" +
                                    "My Current Location : " + addressUser + "\n" +
                                    "Estimated Distance (to bus current location) : " + distance(Double.valueOf(latitude), Double.valueOf(longitude), userx, usery) + " Km" + "\n" +
                                    "Estimated Time (to bus current location) : " + "\n" + ""
                            )
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            })
                            .setNegativeButton("Open Map", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });


                    AlertDialog alert = builder.create();
                    alert.show();


                    busCurrentLocation.setText("Bus Current Location : " + address);
                    busCurrentSpeed.setText("Bus Current Location : " + speed);
                    myCurrentLocation.setText("My Current Location : " + addressUser);
                    estimatedDistance.setText("Estimated Distance (to bus current location) : " + distance(Double.valueOf(latitude), Double.valueOf(longitude), userx, usery) + " Km");
                    estimatedTime.setText("Estimated Time (to bus current location) : Not Available");
                    Log.v("Check",""+addressUser);


                } catch (Exception ignored) {

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        double x = (dist) * 1.609344;
        return Double.parseDouble(new DecimalFormat("##.##").format(x));
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}
