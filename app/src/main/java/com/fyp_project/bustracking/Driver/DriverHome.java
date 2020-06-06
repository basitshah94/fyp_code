package com.fyp_project.bustracking.Driver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fyp_project.bustracking.AboutApp;
import com.fyp_project.bustracking.BusMaintenance;
import com.fyp_project.bustracking.BusNavigationActivity;
import com.fyp_project.bustracking.Commuter.CommuterHome;
import com.fyp_project.bustracking.ComplainForm;
import com.fyp_project.bustracking.EditProfile;
import com.fyp_project.bustracking.Guardian.CurrentLocation;
import com.fyp_project.bustracking.MapsActivity;
import com.fyp_project.bustracking.R;
import com.fyp_project.bustracking.TrafficViolation;
import com.fyp_project.bustracking.UserDetail.ListStudentRegistered;
import com.fyp_project.bustracking.WelcomeActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import mumayank.com.airlocationlibrary.AirLocation;

public class DriverHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View parent_view;

    private AirLocation airLocation;
    String parsedDistance;
    String response;
    double userx;
    double usery;
    private FirebaseAuth mAuth;
    String user_id;
    FirebaseAuth.AuthStateListener authStateListener;
    Button findMyBus, busRoute, complaintForm, viewCommuter, Emergency, busNavigation;
    ImageView profileImageNav;
    TextView userNameNav;
    TextView userEmailNav;
    private DatabaseReference getUserDatabaseReference;
    String busNumber;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Driver Dashboard");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        settingNavHeaderDetail(navigationView);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        setupAuthListner();

        init();
        clickListener();

//        startService(new Intent(this, TrackerService.class));
    }

    private void clickListener() {

        findMyBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DriverHome.this, CurrentLocation.class));


//                Intent intent = new Intent(DriverHome.this, MyBusLocationDriver.class);
                //               intent.putExtra("busNumber", "0");
                //              startActivity(intent);
//                getCurrentBusLocation();

            }
        });
        busRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DriverHome.this, RouteInfo.class);
                intent.putExtra("bus_number", busNumber);
                startActivity(intent);

            }
        });

        complaintForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DriverHome.this, ComplainForm.class);
                intent.putExtra("designation", "Driver");
                startActivity(intent);

            }
        });
        viewCommuter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DriverHome.this, ListStudentRegistered.class);
                intent.putExtra("user_type", "Commuter");
                startActivity(intent);

            }
        });
        Emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emergencyReport();

            }
        });

        busNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DriverHome.this, BusNavigationActivity.class);
                intent1.putExtra("bus_number", busNumber);
                startActivity(intent1);

            }
        });
    }

    private void emergencyReport() {

        AlertDialog.Builder b = new AlertDialog.Builder(DriverHome.this);
        b.setTitle("Emergency Report : ");
        final String[] types = {"Via Call", "Via SMS", "Via Email"};
        b.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:

                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:+92123456789"));
                        startActivity(intent);

                        break;
                    case 1:

                        Uri sms_uri = Uri.parse("smsto:+92123456789");
                        Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                        sms_intent.putExtra("sms_body", "Emergency : ");
                        startActivity(sms_intent);

                        break;
                    case 2:

                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"example@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "");
                        i.putExtra(Intent.EXTRA_TEXT, "");
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(DriverHome.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                dialog.dismiss();
            }
        });
        b.show();

    }

    private void getCurrentBusLocation() {

        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("BusDetail")
                .child(busNumber)
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
                    geocoder = new Geocoder(DriverHome.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    airLocation = new AirLocation(DriverHome.this, true, true, new AirLocation.Callbacks() {
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
                    geocoderUser = new Geocoder(DriverHome.this, Locale.getDefault());
                    addressesUser = geocoderUser.getFromLocation(userx, usery, 1);
                    String addressUser = addressesUser.get(0).getAddressLine(0);
                    Log.v("Check", "" + address);
                    AlertDialog.Builder builder = new AlertDialog.Builder(DriverHome.this);
                    builder.setTitle("Find Nearest Bus")
                            .setMessage(busNumber.toUpperCase() + "\n" +

                                            "Bus Current Location : " + address + "\n" +
//                                                        "Bus Current Speed : " + speed + "\n" +
                                            "My Current Location : " + addressUser + "\n" +
                                            "Estimated Distance (to bus current location) : " + distance(Double.valueOf(latitude), Double.valueOf(longitude), userx, usery) + " Km" + "\n"
//                                                        "Estimated Time (to bus current location) : " + "\n" + ""
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


                } catch (Exception ignored) {

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void init() {

        findMyBus = findViewById(R.id.findmybus);
        busRoute = findViewById(R.id.busroute);
        complaintForm = findViewById(R.id.complaintform);
        viewCommuter = findViewById(R.id.view_commuter);
        Emergency = findViewById(R.id.emergency_btn);
        busNavigation = findViewById(R.id.busnav);
        parent_view = findViewById(android.R.id.content);

    }


    private long exitTime = 0;


    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Snackbar.make(parent_view, R.string.press_again_exit_app, Snackbar.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {

        try {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                stopService(new Intent(DriverHome.this, com.fyp_project.bustracking.Commuter.TrackerService.class));
                doExitApp();
            }
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            stopService(new Intent(DriverHome.this, com.fyp_project.bustracking.Commuter.TrackerService.class));
            mAuth.addAuthStateListener(authStateListener);
            mAuth.signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

            Intent intent = new Intent(DriverHome.this, EditProfile.class);
            intent.putExtra("designation", "Driver");
            startActivity(intent);

        } else if (id == R.id.nav_view_students) {


            AlertDialog.Builder b = new AlertDialog.Builder(DriverHome.this);
            b.setTitle("Select Option : ");
            final String[] types = {"View Commuter", "View Guardian"};
            b.setItems(types, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {
                        case 0:

                            Intent intent = new Intent(DriverHome.this, ListStudentRegistered.class);
                            intent.putExtra("user_type", "Commuter");
                            startActivity(intent);


                            break;
                        case 1:

                            Intent intent1 = new Intent(DriverHome.this, ListStudentRegistered.class);
                            intent1.putExtra("user_type", "Guardian");
                            startActivity(intent1);

                            break;
                    }
                    dialog.dismiss();
                }
            });
            b.show();

        } else if (id == R.id.compalaint_driver) {

            startActivity(new Intent(DriverHome.this, ComplainForm.class));

        } else if (id == R.id.nav_app) {

            startActivity(new Intent(DriverHome.this, AboutApp.class));

        } else if (id == R.id.nav_uni) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://au.edu.pk/"));
            startActivity(browserIntent);

        } else if (id == R.id.nav_bus_maintenance) {

            startActivity(new Intent(DriverHome.this, BusMaintenance.class));

        } else if (id == R.id.nav_track_my_bus) {
            startActivity(new Intent(DriverHome.this, MapsActivity.class));

        } else if (id == R.id.nav_violation_record) {
            startActivity(new Intent(DriverHome.this, TrafficViolation.class));
        } else if (id == R.id.nav_share) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:management@gmail.com"));
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));
        } else if (id == R.id.nav_emergency) {
            emergencyReport();
        }else if (id == R.id.nav_logout) {
            stopService(new Intent(DriverHome.this, TrackerService.class));
            mAuth.addAuthStateListener(authStateListener);
            mAuth.signOut();
            return true;
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupAuthListner() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
//                    Toast.makeText(getApplicationContext(), "Logout failed!!", Toast.LENGTH_SHORT).show();

                } else {
//                    Toast.makeText(getApplicationContext(), "Logout success!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        };

    }


    private void settingNavHeaderDetail(NavigationView navigationView) {

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        View hView = navigationView.getHeaderView(0);
        profileImageNav = hView.findViewById(R.id.profileImageNav);
        userNameNav = hView.findViewById(R.id.userNameNav);
        userEmailNav = hView.findViewById(R.id.userEmailNav);
        retrieveDataNavHeaderMain();

    }

    private void retrieveDataNavHeaderMain() {

        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(user_id);
        getUserDatabaseReference.keepSynced(true);
        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nameText = dataSnapshot.child("user_name").getValue().toString();
                String emailText = dataSnapshot.child("user_email").getValue().toString();
                String imageText = dataSnapshot.child("user_profile_image").getValue().toString();
                busNumber = dataSnapshot.child("bus_number").getValue().toString();


                if (nameText != null || emailText != null || imageText != null) {
                    userNameNav.setText(nameText);
                    userEmailNav.setText(emailText);
                    Picasso.get().load(imageText).into(profileImageNav);
                }


                Intent intent = new Intent(DriverHome.this, TrackerService.class);
                intent.putExtra("bus_number", busNumber);
                startService(intent);

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
