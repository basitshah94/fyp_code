package com.fyp_project.bustracking.Driver;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.fyp_project.bustracking.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mumayank.com.airlocationlibrary.AirLocation;

public class MyBusLocationDriver extends FragmentActivity implements OnMapReadyCallback {

    private static final int overview = 0;
    TextView busNumber_, busCurrentLocation, busCurrentSpeed, estimatedDistance, estimatedTime, myCurrentLocation;
    Marker marker;
    private AirLocation airLocation;
    //user location
    double userx;
    String speed;
    double usery;
    private DatabaseReference getUserDatabaseReference;
    private static final String API_KEY = "AIzaSyBqyA6QdiYIuj12H2QiOVW6AHPd936VtnQ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_nearest_bus);


        busNumber_ = findViewById(R.id.busNo);
        busCurrentLocation = findViewById(R.id.busCurrentLocation);
        busCurrentSpeed = findViewById(R.id.busCurrentSpeed);
        estimatedDistance = findViewById(R.id.estimatedDistance);
        estimatedTime = findViewById(R.id.estimatedTime);
        myCurrentLocation = findViewById(R.id.myCurrentLocation);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("BusDetail")
                .child("Bus_1")
                .child("current_location");
        getUserDatabaseReference.keepSynced(true);
        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String latitude = dataSnapshot.child("latitude").getValue().toString();
                    String longitude = dataSnapshot.child("longitude").getValue().toString();
                    speed = dataSnapshot.child("speed").getValue().toString();
                    Geocoder geocoder;
                    String latLngEnd = "" + Double.parseDouble(latitude) + "," + Double.parseDouble(longitude) + "";
                    List<Address> addresses;
                    geocoder = new Geocoder(MyBusLocationDriver.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    airLocation = new AirLocation(MyBusLocationDriver.this, true, true, new AirLocation.Callbacks() {
                        @Override
                        public void onSuccess(@NotNull Location location) {
                            userx = location.getLatitude();
                            usery = location.getLongitude();
                            googleMap.clear();

//                            LatLng latLngStart = new LatLng(userx, usery);


                            String latLngStart = "" + userx + "," + usery + "";

                            Geocoder geocoderUser;
                            List<Address> addressesUser = null;
                            geocoderUser = new Geocoder(MyBusLocationDriver.this, Locale.getDefault());
                            try {
                                addressesUser = geocoderUser.getFromLocation(userx, usery, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String addressUser = addressesUser.get(0).getAddressLine(0);


                            setupGoogleMapScreenSettings(googleMap);

//                            Toast.makeText(FinNearestBust_test.this, "+"+latLng.toString(), Toast.LENGTH_LONG).show();
 //                           Toast.makeText(MyBusLocationDriver.this, "_" + latLngStart, Toast.LENGTH_LONG).show();
                            DirectionsResult results = getDirectionsDetails(latLngEnd, latLngStart, TravelMode.DRIVING);
                            if (results != null) {
                                addPolyline(results, googleMap);
                                positionCamera(results.routes[overview], googleMap);

//                                Double speed = location.getSpeed() * 3.6;
                                addMarkersToMap(results, googleMap, Double.parseDouble(speed) * 3.6);

                            }
//
//                            DirectionsResult results = getDirectionsDetails(location.toString(), location_.toString(), TravelMode.DRIVING);
//                            if (results != null) {
//
//
//                                busCurrentLocation.setText("Bus Current Location : " + address);
//                                busCurrentSpeed.setText("Bus Current Location : " + speed);
//                                myCurrentLocation.setText("My Current Location : " + addressUser);
////                            Log.v("Check", "" + addressUser);
//
//                                String duration = results.routes[overview].legs[overview].duration.humanReadable;
//                                String distance = results.routes[overview].legs[overview].distance.humanReadable;
//                                estimatedDistance.setText("Estimated Distance (to bus current location) : " + distance);
//                                estimatedTime.setText("Estimated Time (to bus current location) : " + duration);
//
//
//                            }
                        }

                        @Override
                        public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
                        }
                    });


                } catch (Exception ignored) {

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap, Double speed) {

//
//        if (marker != null) {
//            marker.remove();
//        }

//        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat, results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat, results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].endAddress).snippet(getEndLocationTitle(results)));


        String duration = results.routes[overview].legs[overview].duration.humanReadable;
        String distance = results.routes[overview].legs[overview].distance.humanReadable;
        String startAddress = results.routes[overview].legs[overview].startAddress;
        String endAddress = results.routes[overview].legs[overview].endAddress;
//        busCurrentLocation.setText("Bus Location :" + startAddress);
        busCurrentLocation.setVisibility(View.GONE);
        myCurrentLocation.setText("My Location : " + endAddress);
        busCurrentSpeed.setText("Bus Speed : " + speed.toString().substring(0, 3) + " Kmh");



//        estimatedDistance.setText("Distance (to bus current location) : " + distance);
        estimatedDistance.setVisibility(View.GONE);
        estimatedTime.setVisibility(View.GONE);
//        estimatedTime.setText("Estimated Time (to bus current location) : " + duration);


    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 12));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private String getEndLocationTitle(DirectionsResult results) {
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private GeoApiContext getGeoContext() {
        return new GeoApiContext.Builder()
                .apiKey(getResources().getString(R.string.directionsapikey))
                .build();
    }
}