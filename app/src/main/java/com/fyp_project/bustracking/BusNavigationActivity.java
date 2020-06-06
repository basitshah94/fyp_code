package com.fyp_project.bustracking;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.annotations.NotNull;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mumayank.com.airlocationlibrary.AirLocation;

public class BusNavigationActivity extends AppCompatActivity {

    private static final int ADDRESS_PICKER_REQUEST_FINAL = 1;
    private static final int ADDRESS_PICKER_REQUEST_START = 2;
    String busNumber = "";
    private static final int overview = 0;
    TextView busNumber_, estimatedTime_, estimatedDistance_, navigateInfo_;
    Button openMap_, changeBusStop_, changeBusStart_;
    private AirLocation airLocation;
    Double userLat, userLong, finalLat, finalLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_navigation);
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Bus Navigation");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        busNumber = getIntent().getExtras().getString("bus_number");

        busNumber_ = findViewById(R.id.bus_number);
        estimatedTime_ = findViewById(R.id.bus_time);
        estimatedDistance_ = findViewById(R.id.bus_distance);
        navigateInfo_ = findViewById(R.id.navigation_information);

        openMap_ = findViewById(R.id.open_map);
        changeBusStop_ = findViewById(R.id.change_bus_stop);
        changeBusStart_ = findViewById(R.id.change_bus_start);
        busNumber_.setText("Bus # " + busNumber.substring(busNumber.length() - 1));

        changeBusStart();
        changeBusStart_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBusStart();
            }
        });

        openMap_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", userLat, userLong, "Starting Point", finalLat, finalLong, "Final Point");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                } catch (Exception e) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(BusNavigationActivity.this);
                    builder.setMessage("Please select destination or check the location permission!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        changeBusStop_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBusStop();
            }
        });

    }

    private void setLocationAddress(Double finalLat, Double finalLong) {
        Geocoder geocoder;
        String latLngEnd = "" + (finalLat) + "," + (finalLong) + "";
        List<Address> addresses = null;
        geocoder = new Geocoder(BusNavigationActivity.this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation((finalLat), (finalLong), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        airLocation = new AirLocation(BusNavigationActivity.this, true, true, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NotNull Location location) {
                userLat = location.getLatitude();
                userLong = location.getLongitude();
                String latLngStart = "" + userLat + "," + userLong + "";
                Geocoder geocoderUser;
                List<Address> addressesUser = null;
                geocoderUser = new Geocoder(BusNavigationActivity.this, Locale.getDefault());
                try {
                    addressesUser = geocoderUser.getFromLocation(userLat, userLong, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String addressUser = addressesUser.get(0).getAddressLine(0);
                DirectionsResult results = getDirectionsDetails(latLngEnd, latLngStart, TravelMode.DRIVING);
                if (results != null) {
                    addMarkersToMap(results);
                }
            }

            @Override
            public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
            }
        });
    }


    private void addMarkersToMap(DirectionsResult results) {

        String duration = results.routes[overview].legs[overview].duration.humanReadable;
        String distance = results.routes[overview].legs[overview].distance.humanReadable;
        String startAddress = results.routes[overview].legs[overview].startAddress;
        String endAddress = results.routes[overview].legs[overview].endAddress;

        estimatedDistance_.setText(distance);
        estimatedTime_.setText(duration);
        navigateInfo_.setText("You'll reach " + startAddress + " in Estimated Time " + duration + " and the Estimated Distance is " + distance + " from " + endAddress);

    }

    private GeoApiContext getGeoContext() {
        return new GeoApiContext.Builder()
                .apiKey(getResources().getString(R.string.directionsapikey))
                .build();
    }

    private void changeBusStop() {
        MapUtility.apiKey = getResources().getString(R.string.google_maps_key);
        Intent i = new Intent(BusNavigationActivity.this, LocationPickerActivity.class);
        startActivityForResult(i, ADDRESS_PICKER_REQUEST_FINAL);
    }

    private void changeBusStart() {
        MapUtility.apiKey = getResources().getString(R.string.google_maps_key);
        Intent i = new Intent(BusNavigationActivity.this, LocationPickerActivity.class);
        startActivityForResult(i, ADDRESS_PICKER_REQUEST_START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_PICKER_REQUEST_START) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    String address = data.getStringExtra(MapUtility.ADDRESS);
                    finalLat = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    finalLong = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    setLocationAddress(finalLat, finalLong);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (requestCode == ADDRESS_PICKER_REQUEST_FINAL) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    String address = data.getStringExtra(MapUtility.ADDRESS);
                    userLat = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    userLong = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
