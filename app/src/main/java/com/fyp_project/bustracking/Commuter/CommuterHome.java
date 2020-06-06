package com.fyp_project.bustracking.Commuter;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fyp_project.bustracking.AboutApp;
import com.fyp_project.bustracking.BusNavigationActivity;
import com.fyp_project.bustracking.ComplainForm;
import com.fyp_project.bustracking.Driver.RouteInfo;
import com.fyp_project.bustracking.EditProfile;
import com.fyp_project.bustracking.FeedbackForm;
import com.fyp_project.bustracking.FinNearestBust_test;
import com.fyp_project.bustracking.MapsActivity;
import com.fyp_project.bustracking.R;
import com.fyp_project.bustracking.SubmitFeeForm;
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
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import mumayank.com.airlocationlibrary.AirLocation;

public class CommuterHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View parent_view;
    String m_Text = "";
    private AirLocation airLocation;
    String parsedDistance;
    String response;

    //user location
    double userx;
    double usery;

    String busNumber;

    //NavHeaderMain Componenets
    ImageView profileImageNav;
    TextView userNameNav;
    TextView userEmailNav;
    //Database reference
    private DatabaseReference getUserDatabaseReference;
    // Auth UserID
    private FirebaseAuth mAuth;
    String user_id;
    FirebaseAuth.AuthStateListener authStateListener;

    //Home Features Buttons
    Button findMyBus, feeBill, busRoute, complaintForm, contactGuardian, rateDriver, Emergency, busNavigation;
    private static final String API_KEY = "AIzaSyBqyA6QdiYIuj12H2QiOVW6AHPd936VtnQ";

    //real time tracking
    //emergency managemnet for any kindda emergency
    //reports (timings/routes/bus timings/student attendace on bus/speed limit alerts/ live cam view by mobile of bus)
    //speed limit reports
    String account_status;
    Toolbar toolbar;
    private String finalValueUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter").child(user_id);
        getUserDatabaseReference.keepSynced(true);
        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                account_status = dataSnapshot.child("user_account_status").getValue().toString();

                if (account_status.equals("false")) {

                    setContentView(R.layout.activity_account_status);

                } else {
                    setContentView(R.layout.activity_main);
                    toolbar = findViewById(R.id.toolbar);
                    toolbar.setTitle("Commuter Dashboard");
                    setSupportActionBar(toolbar);
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            CommuterHome.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    drawer.addDrawerListener(toggle);
                    toggle.syncState();
                    navigationView.setNavigationItemSelectedListener(CommuterHome.this);
                    settingNavHeaderDetail(navigationView);
                    initialization();
                    clickListenerHomeFeatures();
                    setupAuthListner();
                    parent_view = findViewById(android.R.id.content);

//        getCommuterUserLocation();
                    startService(new Intent(CommuterHome.this, TrackerService.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void initialization() {

        findMyBus = findViewById(R.id.findmybus);
        busRoute = findViewById(R.id.busroute);
        busNavigation = findViewById(R.id.busnav);
        complaintForm = findViewById(R.id.complaintform);
        contactGuardian = findViewById(R.id.contact_guardian);
        rateDriver = findViewById(R.id.ratedriver);
        Emergency = findViewById(R.id.emergency_btn);
        feeBill = findViewById(R.id.feebill);

    }

    private void clickListenerHomeFeatures() {

        findMyBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter").child(user_id);
                getUserDatabaseReference.keepSynced(true);
                getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            String BusNumber = dataSnapshot.child("bus_number").getValue().toString();
                            Intent intent = new Intent(CommuterHome.this, FinNearestBust_test.class);
                            intent.putExtra("busNumber", BusNumber);
                            startActivity(intent);

                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });
        busRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CommuterHome.this, RouteInfo.class);
                intent.putExtra("bus_number", busNumber);
                startActivity(intent);

            }
        });


        busNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CommuterHome.this, BusNavigationActivity.class);
                intent1.putExtra("bus_number", busNumber);
                startActivity(intent1);

            }
        });


        feeBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(CommuterHome.this);
                b.setTitle("Select Option : ");
                final String[] types = {"Get Bus Fee Challan", "Download University Fee Challan", "Submit Fee", "Check Fee Status"};
                b.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CommuterHome.this);
                                builder.setMessage("Please contact admin office for Fee Challan.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });
                                androidx.appcompat.app.AlertDialog alert = builder.create();
                                alert.show();

                                break;

                            case 1:

                                AlertDialog.Builder builder2 = new AlertDialog.Builder(CommuterHome.this);
                                builder2.setTitle("Enter Your registration ID");
                                final EditText input = new EditText(CommuterHome.this);
                                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                                builder2.setView(input);

                                builder2.setPositiveButton("View Fee Bill", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_Text = input.getText().toString();
                                        m_Text = m_Text.replace(" ", "");
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://portals.au.edu.pk/feesystem/ChallanForm.aspx?id=" + m_Text));
                                        startActivity(browserIntent);
                                    }
                                });
                                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                builder2.show();
                                break;
                            case 2:

                                androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(CommuterHome.this);
                                builder1.setMessage("You can submit the fee at any nearest Habib Metro Bank Branch.")
                                        .setCancelable(false)
                                        .setNegativeButton("Check Nearest Branch", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                Uri gmmIntentUri = Uri.parse("geo:0,0?q=habib+metro+bank+near+me");
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(gmmIntentUri.toString()));
                                                intent.setPackage("com.google.android.apps.maps");
                                                try {
                                                    startActivity(intent);
                                                } catch (ActivityNotFoundException ex) {
                                                    try {
                                                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gmmIntentUri.toString()));
                                                        startActivity(unrestrictedIntent);
                                                    } catch (ActivityNotFoundException innerEx) {
                                                        Toast.makeText(CommuterHome.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                        })
                                        .setPositiveButton("Submit Fee Challan Proof", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                startActivity(new Intent(CommuterHome.this, SubmitFeeForm.class));
                                            }
                                        })
                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                            }
                                        });

                                androidx.appcompat.app.AlertDialog alert1 = builder1.create();
                                alert1.show();


                                break;
                            case 3:

                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                String dateDatabase = new SimpleDateFormat("MM_yyyy", Locale.getDefault()).format(new Date());
                                rootRef = FirebaseDatabase.getInstance().getReference()
                                        .child("FeeChallan")
                                        .child(user_id);
                                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.hasChild("fee_challan_image")) {
                                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CommuterHome.this);
                                            builder.setTitle("Record Exist!");
                                            builder.setMessage("Please contact information desk for information.")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
//                                                            finish();
                                                        }
                                                    });
                                            androidx.appcompat.app.AlertDialog alert = builder.create();
                                            alert.show();
                                        } else {

                                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CommuterHome.this);
                                            builder.setTitle("No Record Exist!");
                                            builder.setMessage("Please contact information desk for information.")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
//                                                            finish();
                                                        }
                                                    });
                                            androidx.appcompat.app.AlertDialog alert = builder.create();
                                            alert.show();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                break;
                        }
                        dialog.dismiss();
                    }
                });
                b.show();


            }
        });

        Emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(CommuterHome.this);
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
                                sms_intent.putExtra("sms_body", "Emergency -");
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
                                    Toast.makeText(CommuterHome.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }

                                break;
                        }
                        dialog.dismiss();
                    }
                });
                b.show();


            }
        });
        rateDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CommuterHome.this, FeedbackForm.class);
                intent.putExtra("designation", "Commuter");
                startActivity(intent);
            }
        });
        contactGuardian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter").child(user_id);
                getUserDatabaseReference.keepSynced(true);
                getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            String userEmail = dataSnapshot.child("user_email").getValue().toString();
                            String userID = dataSnapshot.child("user_id").getValue().toString();
                            getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Guardian");
                            getUserDatabaseReference.orderByChild("commuter_email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {


                                        for (DataSnapshot d : dataSnapshot.getChildren()) {

                                            String keyValue = d.getRef().getKey();
                                            Log.v("Guardian Key", "" + keyValue);
                                            String mobileNumber = d.child("user_mobile_number").getValue().toString();
                                            String userName = d.child("user_name").getValue().toString();

                                            AlertDialog.Builder b = new AlertDialog.Builder(CommuterHome.this);
                                            b.setTitle("Contact Guardian : ");
                                            final String[] types = {"Via Call", "Via SMS"};
                                            b.setItems(types, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    switch (which) {
                                                        case 0:

                                                            Intent intent = new Intent(Intent.ACTION_DIAL);
                                                            intent.setData(Uri.parse("tel:" + mobileNumber));
                                                            startActivity(intent);

                                                            break;
                                                        case 1:

                                                            Uri sms_uri = Uri.parse("smsto:" + mobileNumber);
                                                            Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                                                            sms_intent.putExtra("sms_body", "Emergency -");
                                                            startActivity(sms_intent);

                                                            break;
                                                    }
                                                    dialog.dismiss();
                                                }
                                            });
                                            b.show();


                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.v("ERROR", "" + databaseError);
                                }
                            });


                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });
        complaintForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CommuterHome.this, ComplainForm.class);
                intent.putExtra("designation", "Commuter");
                startActivity(intent);

            }
        });

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

        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter").child(user_id);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private long exitTime = 0;


    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Snackbar.make(parent_view, R.string.press_again_exit_app, Snackbar.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            stopService(new Intent(CommuterHome.this, TrackerService.class));
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            stopService(new Intent(CommuterHome.this, TrackerService.class));
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

            Intent intent = new Intent(CommuterHome.this, EditProfile.class);
            intent.putExtra("designation", "Commuter");
            startActivity(intent);

        } else if (id == R.id.nav_guardian_profile) {
            getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter").child(user_id);
            getUserDatabaseReference.keepSynced(true);
            getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        String userEmail = dataSnapshot.child("user_email").getValue().toString();
                        String userID = dataSnapshot.child("user_id").getValue().toString();
                        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Guardian");
                        getUserDatabaseReference.orderByChild("commuter_email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {


                                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                                        String keyValue = d.getRef().getKey();
                                        Log.v("Guardian Key", "" + keyValue);
                                        String mobileNumber = d.child("user_mobile_number").getValue().toString();
                                        String userName = d.child("user_name").getValue().toString();


                                        AlertDialog.Builder builder = new AlertDialog.Builder(CommuterHome.this);
                                        builder.setTitle("Guardian Profile");
                                        builder.setMessage
                                                (
                                                        "Name : " + userName + "\n" + "Mobile # " + mobileNumber
                                                )
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {

                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.v("ERROR", "" + databaseError);
                            }
                        });


                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } else if (id == R.id.nav_track_my_bus) {
            startActivity(new Intent(CommuterHome.this, MapsActivity.class));

        } else if (id == R.id.compalaint_driver) {

            startActivity(new Intent(CommuterHome.this, ComplainForm.class));

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_app) {

            startActivity(new Intent(CommuterHome.this, AboutApp.class));

        } else if (id == R.id.nav_uni) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://au.edu.pk/"));
            startActivity(browserIntent);

        } else if (id == R.id.nav_share) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:management@gmail.com"));
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));

        } else if (id == R.id.nav_logout) {
            stopService(new Intent(CommuterHome.this, TrackerService.class));
            mAuth.addAuthStateListener(authStateListener);
            mAuth.signOut();
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        airLocation.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults);

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

    public String getDurationForRoute(String origin, String destination) throws InterruptedException, ApiException, IOException {
        // - We need a context to access the API
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        // - Perform the actual request
        DirectionsResult directionsResult = DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.DRIVING)
                .origin(origin)
                .destination(destination)
                .await();

        // - Parse the result
        DirectionsRoute route = directionsResult.routes[0];
        DirectionsLeg leg = route.legs[0];
        Duration duration = leg.duration;
        return duration.humanReadable;
    }

    private void LatlngCalc(JSONObject locationJsonObject) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(CommuterHome.this);
        String url = "https://maps.googleapis.com/maps/api/distancematrix/" +
                "json?origins=" + locationJsonObject.getString("origin") + "&destinations=" + locationJsonObject.getString("destination") + "&mode=driving&key=AIzaSyBqyA6QdiYIuj12H2QiOVW6AHPd936VtnQ";
//                "language=en-EN&sensor=false";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //                 Toast.makeText(CommuterHome.this, "Response :" + response, Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CommuterHome.this, "Error :", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric&mode=driving");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance = distance.getString("text");
                    //              Toast.makeText(CommuterHome.this, "Response :" + parsedDistance, Toast.LENGTH_SHORT).show();


                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance;
    }

    private void getCommuterUserLocation() {

        airLocation = new AirLocation(CommuterHome.this, true, true, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(@NotNull Location location) {
                usery = location.getLatitude();
                userx = location.getLongitude();

                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("User")
                        .child("Commuter")
                        .child(user_id)
                        .child("current_location");
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("longitude", userx);
                taskMap.put("latitude", usery);
                getUserDatabaseReference.updateChildren(taskMap);

            }

            @Override
            public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {
                // do something
            }
        });


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
}

