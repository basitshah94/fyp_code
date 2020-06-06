package com.fyp_project.bustracking.Guardian;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.fyp_project.bustracking.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class CurrentLocation extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 5445;

    TextView busNumber_, busCurrentLocation, busCurrentSpeed, estimatedDistance, estimatedTime, myCurrentLocation;

    String currentLocationCommuter = "";
    String keyCommuter = "";

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker currentLocationMarker;
    private Location currentLocation;
    private boolean firstTimeFlag = true;
    private ImageButton imageButton;
    private DatabaseReference getUserDatabaseReference;
    LatLng latLng;
    Geocoder geocoder;
    List<Address> addresses;


    private final View.OnClickListener clickListener = view -> {
        if (view.getId() == R.id.currentLocationImageButton && googleMap != null && currentLocation != null)
            animateCamera(currentLocation);
    };

    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLastLocation() == null)
                return;
            currentLocation = locationResult.getLastLocation();
            if (firstTimeFlag && googleMap != null) {
                animateCamera(currentLocation);
                firstTimeFlag = false;
            }
            showMarker(currentLocation);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_nearest_bus);

        Intent in = getIntent();
        Bundle extras = in.getExtras();
        if (extras != null) {
            currentLocationCommuter = extras.getString("currentLocationCommuter");
            keyCommuter = extras.getString("keyCommuter");

        }
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        findViewById(R.id.currentLocationImageButton).setOnClickListener(clickListener);

        busNumber_ = findViewById(R.id.busNo);
        busCurrentLocation = findViewById(R.id.busCurrentLocation);
        busCurrentSpeed = findViewById(R.id.busCurrentSpeed);
        estimatedDistance = findViewById(R.id.estimatedDistance);
        estimatedTime = findViewById(R.id.estimatedTime);
        myCurrentLocation = findViewById(R.id.myCurrentLocation);
        imageButton = findViewById(R.id.currentLocationImageButton);

        busNumber_.setVisibility(View.GONE);
        busCurrentLocation.setVisibility(View.GONE);
        busCurrentSpeed.setVisibility(View.GONE);
        estimatedDistance.setVisibility(View.GONE);
        estimatedTime.setVisibility(View.GONE);
        imageButton.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageButton.setImageDrawable(getDrawable(R.drawable.location_icon));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    private void startCurrentLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CurrentLocation.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                return;
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status)
            return true;
        else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(this, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                Toast.makeText(this, "Permission denied by uses", Toast.LENGTH_SHORT).show();
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startCurrentLocationUpdates();
        }
    }

    private void animateCamera(@NonNull Location location) {

        if (currentLocationCommuter.equals("Guardian")) {

            getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("CommuterDetail").child(keyCommuter).child("current_location");
            getUserDatabaseReference.keepSynced(true);
            getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        String latitude = dataSnapshot.child("latitude").getValue().toString();
                        String longitude = dataSnapshot.child("longitude").getValue().toString();
                        Log.v("CommuterLatLng:", "" + latitude + ":" + longitude);

                        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));

                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


        } else {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
        }
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void showMarker(@NonNull Location currentLocation) {


        try {
            geocoder = new Geocoder(CurrentLocation.this, Locale.getDefault());


            if (currentLocationCommuter.equals("Guardian")) {

                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("CommuterDetail").child(keyCommuter).child("current_location");
                getUserDatabaseReference.keepSynced(true);
                getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            String latitude = dataSnapshot.child("latitude").getValue().toString();
                            String longitude = dataSnapshot.child("longitude").getValue().toString();
                            Log.v("CommuterLatLng:", "" + latitude + ":" + longitude);
                            latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);


                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


            } else {
                latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
            }

            String latLngEnd = "" + Double.parseDouble(String.valueOf(currentLocation.getLatitude())) + "," + Double.parseDouble(String.valueOf(currentLocation.getLongitude())) + "";

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            myCurrentLocation.setText("My Location : " + address + ":" + city + ":" + knownName);

            if (currentLocationMarker == null)
                currentLocationMarker = googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(latLng));
            else
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, latLng, new LatLngInterpolator.Spherical());

        } catch (Exception e) {

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            startCurrentLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient = null;
        googleMap = null;
    }
}