package com.fyp_project.bustracking.Guardian;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.fyp_project.bustracking.Commuter.CommuterHome;
import com.fyp_project.bustracking.Commuter.TrackerService;
import com.fyp_project.bustracking.ComplainForm;
import com.fyp_project.bustracking.EditProfile;
import com.fyp_project.bustracking.FeedbackForm;
import com.fyp_project.bustracking.FinNearestBust_test;
import com.fyp_project.bustracking.R;
import com.fyp_project.bustracking.WelcomeActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class GuardianHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private View parent_view;

    Toolbar toolbar;
    private FirebaseAuth mAuth;
    String user_id;
    FirebaseAuth.AuthStateListener authStateListener;
    ImageView profileImageNav;
    TextView userNameNav;
    TextView userEmailNav;
    private DatabaseReference getUserDatabaseReference;

    String commuterEmail, commuterID;
    Button myCurrentLocation, commuterBusLocation, commuterCurrentLocation, contactCommuter, emergencyResponse, complainForum, feedback;

    String BusNumber = "";
    String account_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Guardian").child(user_id);
        getUserDatabaseReference.keepSynced(true);
        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                account_status = dataSnapshot.child("user_account_status").getValue().toString();

                if (account_status.equals("false")) {

                    setContentView(R.layout.activity_account_status);

                } else {

                    setContentView(R.layout.activity_guardian);
                    toolbar = findViewById(R.id.toolbar);
                    toolbar.setTitle("Guardian Dashboard");
                    setSupportActionBar(toolbar);
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    NavigationView navigationView = findViewById(R.id.nav_view);

                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            GuardianHome.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    drawer.addDrawerListener(toggle);
                    toggle.syncState();
                    navigationView.setNavigationItemSelectedListener(GuardianHome.this);

                    setupAuthListner();
                    settingNavHeaderDetail(navigationView);
                    init();
                    onClickListener();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void onClickListener() {

        myCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuardianHome.this, CurrentLocation.class));
            }
        });
        commuterBusLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Guardian").child(user_id);
                getUserDatabaseReference.keepSynced(true);
                getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            commuterEmail = dataSnapshot.child("commuter_email").getValue().toString();
                            commuterID = dataSnapshot.child("commuter_reg_id").getValue().toString();

                            getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter");
                            getUserDatabaseReference.orderByChild("user_email").equalTo(commuterEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                                            String keyValue = d.getRef().getKey();
                                            Log.v("Commuter Key", "" + keyValue);
                                            BusNumber = d.child("bus_number").getValue().toString();
                                            Intent intent = new Intent(GuardianHome.this, FinNearestBust_test.class);
                                            intent.putExtra("busNumber", BusNumber);
                                            startActivity(intent);

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
        commuterCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Guardian").child(user_id);
                getUserDatabaseReference.keepSynced(true);
                getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            commuterEmail = dataSnapshot.child("commuter_email").getValue().toString();
                            commuterID = dataSnapshot.child("commuter_reg_id").getValue().toString();
                            getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter");
                            getUserDatabaseReference.orderByChild("user_email").equalTo(commuterEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {


                                        for (DataSnapshot d : dataSnapshot.getChildren()) {

                                            String keyValue = d.getRef().getKey();
                                            Log.v("Commuter Key", "" + keyValue);

                                            Intent intent = new Intent(GuardianHome.this, CurrentLocation.class);
                                            intent.putExtra("currentLocationCommuter", "Guardian");
                                            intent.putExtra("keyCommuter", keyValue);
                                            startActivity(intent);


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


//        contactCommuter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Guardian").child(user_id);
//                getUserDatabaseReference.keepSynced(true);
//                getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        try {
//                            commuterEmail = dataSnapshot.child("commuter_email").getValue().toString();
//                            commuterID = dataSnapshot.child("commuter_reg_id").getValue().toString();
//                            getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter");
//                            getUserDatabaseReference.orderByChild("user_email").equalTo(commuterEmail).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//
//
//                                        for (DataSnapshot d : dataSnapshot.getChildren()) {
//
//                                            String keyValue = d.getRef().getKey();
//                                            Log.v("Commuter Key", "" + keyValue);
//                                            String mobileNumber = d.child("user_mobile_number").getValue().toString();
//
//                                            AlertDialog.Builder b = new AlertDialog.Builder(GuardianHome.this);
//                                            b.setTitle("Contact Via : ");
//                                            final String[] types = {"Dial Call", "Send SMS"};
//                                            b.setItems(types, new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//
//                                                    switch (which) {
//                                                        case 0:
//
//                                                            Intent intent = new Intent(Intent.ACTION_DIAL);
//                                                            intent.setData(Uri.parse("tel:" + mobileNumber));
//                                                            startActivity(intent);
//
//                                                            break;
//                                                        case 1:
//
//                                                            Uri sms_uri = Uri.parse("smsto:" + mobileNumber);
//                                                            Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
//                                                            sms_intent.putExtra("sms_body", "");
//                                                            startActivity(sms_intent);
//
//                                                            break;
//                                                    }
//                                                    dialog.dismiss();
//                                                }
//                                            });
//                                            b.show();
//
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    Log.v("ERROR", "" + databaseError);
//                                }
//                            });
//
//
//                        } catch (Exception e) {
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
//            }
//        });


        contactCommuter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Guardian").child(user_id);
                getUserDatabaseReference.keepSynced(true);
                getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            commuterEmail = dataSnapshot.child("commuter_email").getValue().toString();
                            commuterID = dataSnapshot.child("commuter_reg_id").getValue().toString();
                            getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter");
                            getUserDatabaseReference.orderByChild("user_email").equalTo(commuterEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {


                                        for (DataSnapshot d : dataSnapshot.getChildren()) {

                                            String keyValue = d.getRef().getKey();
                                            Log.v("Commuter Key", "" + keyValue);
                                            String mobileNumber = d.child("user_mobile_number").getValue().toString();

                                            AlertDialog.Builder b = new AlertDialog.Builder(GuardianHome.this);
                                            b.setTitle("Contact Via : ");
                                            final String[] types = {"Dial Call", "Send SMS"};
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
                                                            sms_intent.putExtra("sms_body", "");
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

        emergencyResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(GuardianHome.this);
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
                                sms_intent.putExtra("sms_body", "");
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
                                    Toast.makeText(GuardianHome.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }

                                break;
                        }
                        dialog.dismiss();
                    }
                });
                b.show();


            }
        });


        complainForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GuardianHome.this, ComplainForm.class);
                intent.putExtra("designation", "Guardian");
                startActivity(intent);

            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GuardianHome.this, FeedbackForm.class);
                intent.putExtra("designation", "Guardian");
                startActivity(intent);
            }
        });

    }

    private void init() {

        myCurrentLocation = findViewById(R.id.my_current_location);
        commuterBusLocation = findViewById(R.id.commuter_bus_location);
        commuterCurrentLocation = findViewById(R.id.commuter_current_location);
        contactCommuter = findViewById(R.id.contact_commuter);
        emergencyResponse = findViewById(R.id.emergency_response);
        complainForum = findViewById(R.id.complain_forum);
        feedback = findViewById(R.id.feedback);
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
                stopService(new Intent(GuardianHome.this, TrackerService.class));
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

            Intent intent = new Intent(GuardianHome.this, EditProfile.class);
            intent.putExtra("designation", "Guardian");
            startActivity(intent);

        } else if (id == R.id.nav_home) {
            startActivity(new Intent(GuardianHome.this, CurrentLocation.class));


        } else if (id == R.id.nav_slideshow) {

            getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Guardian").child(user_id);
            getUserDatabaseReference.keepSynced(true);
            getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {
                        commuterEmail = dataSnapshot.child("commuter_email").getValue().toString();
                        commuterID = dataSnapshot.child("commuter_reg_id").getValue().toString();
                        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter");
                        getUserDatabaseReference.orderByChild("user_email").equalTo(commuterEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {


                                    for (DataSnapshot d : dataSnapshot.getChildren()) {

                                        String keyValue = d.getRef().getKey();
                                        Log.v("Commuter Key", "" + keyValue);
                                        String mobileNumber = d.child("user_mobile_number").getValue().toString();

                                        AlertDialog.Builder b = new AlertDialog.Builder(GuardianHome.this);
                                        b.setTitle("Contact Via : ");
                                        final String[] types = {"Dial Call", "Send SMS"};
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
                                                        sms_intent.putExtra("sms_body", "");
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


        } else if (id == R.id.nav_app) {
            startActivity(new Intent(GuardianHome.this, AboutApp.class));

        } else if (id == R.id.nav_uni) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://au.edu.pk/"));
            startActivity(browserIntent);

        } else if (id == R.id.nav_share) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:management@gmail.com"));
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));

        } else if (id == R.id.nav_logout) {
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

        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Guardian").child(user_id);
        getUserDatabaseReference.keepSynced(true);
        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    String nameText = dataSnapshot.child("user_name").getValue().toString();
                    String emailText = dataSnapshot.child("user_email").getValue().toString();
                    String imageText = dataSnapshot.child("user_profile_image").getValue().toString();
                    if (nameText != null || emailText != null || imageText != null) {
                        userNameNav.setText(nameText);
                        userEmailNav.setText(emailText);
                        Picasso.get().load(imageText).into(profileImageNav);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
