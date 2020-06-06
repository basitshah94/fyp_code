package com.fyp_project.bustracking;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mumayank.com.airlocationlibrary.AirLocation;


public class FindNearestBus extends AppCompatActivity implements OnMapReadyCallback {

    private static final int overview = 0;
    String parsedDistance;
    String response;
    private AirLocation airLocation;
    //user location
    double userx;
    double usery;
    String busNumber = "";
    private DatabaseReference getUserDatabaseReference;
    TextView busNumber_, routeName, routeTime, routeDistance, busCurrentLocation, busCurrentSpeed, estimatedDistance, estimatedTime, myCurrentLocation, upcomingRouteStop;
    private static final String API_KEY = "AIzaSyBqyA6QdiYIuj12H2QiOVW6AHPd936VtnQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_detail);

        busNumber = getIntent().getExtras().getString("busNumber");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        busNumber_ = findViewById(R.id.busNo);
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

    private void searchNearestBus(String bus_1) {


        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("BusDetail")
                .child(bus_1)
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

//                    Toast.makeText(FindNearestBus.this, "Database : " + latitude + ":" + longitude + speed, Toast.LENGTH_LONG).show();

                    Location location_ = new Location(LocationManager.GPS_PROVIDER);
                    location_.setLatitude(Double.parseDouble(latitude));
                    location_.setLongitude(Double.parseDouble(longitude));

                    List<Address> addresses;
                    geocoder = new Geocoder(FindNearestBus.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    airLocation = new AirLocation(FindNearestBus.this, true, true, new AirLocation.Callbacks() {
                        @Override
                        public void onSuccess(@NotNull Location location) {
                            userx = location.getLatitude();
                            usery = location.getLongitude();

                            LatLng latLng = new LatLng(userx, usery);

                            Geocoder geocoderUser;
                            List<Address> addressesUser = null;
                            geocoderUser = new Geocoder(FindNearestBus.this, Locale.getDefault());
                            try {
                                addressesUser = geocoderUser.getFromLocation(userx, usery, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String addressUser = addressesUser.get(0).getAddressLine(0);

                            try {
                                JSONObject locationJsonObject = new JSONObject();
                                locationJsonObject.put("origin", location);
                                locationJsonObject.put("destination", location_);
                                LatlngCalc(locationJsonObject);
                            } catch (Exception e) {

                            }


                            DirectionsResult results = getDirectionsDetails(location.toString(), location_.toString(), TravelMode.DRIVING);
                            if (results != null) {


                                busCurrentLocation.setText("Bus Current Location : " + address);
                                busCurrentSpeed.setText("Bus Current Location : " + speed);
                                myCurrentLocation.setText("My Current Location : " + addressUser);
//                            Log.v("Check", "" + addressUser);

                                String duration = results.routes[overview].legs[overview].duration.humanReadable;
                                String distance = results.routes[overview].legs[overview].distance.humanReadable;
                                estimatedDistance.setText("Estimated Distance (to bus current location) : " + distance);
                                estimatedTime.setText("Estimated Time (to bus current location) : " + duration);


                            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void LatlngCalc(JSONObject locationJsonObject) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(FindNearestBus.this);
        String url = "http://maps.googleapis.com/maps/api/distancematrix/" +
                "json?origins=" + locationJsonObject.getString("origin") + "&destinations=" + locationJsonObject.getString("destination") + "&mode=driving&" +
                "language=en-EN&sensor=false";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                //        Toast.makeText(FindNearestBus.this, "" + response, Toast.LENGTH_SHORT).show();
//                        mTextView.setText("Response is: " + response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  mTextView.setText("That didn't work!");
                Toast.makeText(FindNearestBus.this, "Not Working", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
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


        if (busNumber.contains("0")) {

            searchNearestBus("Bus_1");
  //          Toast.makeText(this, "Called", Toast.LENGTH_SHORT).show();
        }

    }


//    private String getEndLocationTitle(DirectionsResult results) {
//
//        estimatedDistance.setText("Estimated Distance (to bus current location) : " + getDistance(Double.parseDouble(latitude), Double.parseDouble(longitude), userx, usery));
//        estimatedTime.setText("Estimated Time (to bus current location) : " + getDurationForRoute(location_.toString(), location.toString()));
//
//        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
//    }

    private GeoApiContext getGeoContext() {
        return new GeoApiContext.Builder()
                .apiKey(getResources().getString(R.string.directionsapikey))
                .build();
    }
}
