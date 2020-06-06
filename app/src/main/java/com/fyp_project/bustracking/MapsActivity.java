package com.fyp_project.bustracking;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
    DatabaseReference ref;
    DatabaseReference ref2;
    TextView current_speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        current_speed = findViewById(R.id.current_speed);
        subscribeToUpdates();
    }

    private void subscribeToUpdates() {
        ref = FirebaseDatabase.getInstance().getReference().child("BusDetail").child("Bus_1").child("current_location");
        ref2 = FirebaseDatabase.getInstance().getReference().child("BusDetail").child("Bus_1");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String speed = dataSnapshot.child("current_speed").getValue().toString();
                if (speed != null) {
                    current_speed.setText(speed + " Km/h");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String key = dataSnapshot.getKey();

//                HashMap<Long, Object> value = (HashMap<Long, Object>) dataSnapshot.getValue();

                double lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                double lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());

//                double lat = Double.parseDouble(value.get("latitude").toString());
//                double lng = Double.parseDouble(value.get("longitude").toString());
                LatLng location = new LatLng(lat, lng);
                if (!mMarkers.containsKey(key)) {
                    mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)));
                } else {
                    mMarkers.get(key).setPosition(location);
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : mMarkers.values()) {
                    builder.include(marker.getPosition());
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Islamabad"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
