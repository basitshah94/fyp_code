package com.fyp_project.bustracking.Administrator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fyp_project.bustracking.AboutApp;
import com.fyp_project.bustracking.BusModule.BusMaintenanceRecord;
import com.fyp_project.bustracking.BusModule.ListGuestComplain;
import com.fyp_project.bustracking.BusModule.ListUserComplain;
import com.fyp_project.bustracking.BusModule.ListUserFeedback;
import com.fyp_project.bustracking.Commuter.CommuterHome;
import com.fyp_project.bustracking.Commuter.TrackerService;
import com.fyp_project.bustracking.R;
import com.fyp_project.bustracking.Route.AddRoute;
import com.fyp_project.bustracking.Route.ViewModifyRoute;
import com.fyp_project.bustracking.UserApprovalBoard.UserListApprovalBoard;
import com.fyp_project.bustracking.WelcomeActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdministratorHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button viewModifyUser, viewModifyBusRoute, userApprovalBoard, checkBuses, complaintForm, feedbackBtn, feeRecord;
    private View parent_view;

    Toolbar toolbar;
    private DatabaseReference getUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
        toolbar = findViewById(R.id.toolbar);


        toolbar.setTitle("Administrator Dashboard");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        init();
        clickListener();


    }

    private void clickListener() {

        viewModifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getUserList();
            }
        });

        viewModifyBusRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(AdministratorHome.this);
                b.setTitle("Select Operation : ");
                final String[] types = {"Add Route", "View/Modify Route"};
                b.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent i = new Intent(AdministratorHome.this, AddRoute.class);
                                startActivity(i);
                                break;

                            case 1:
                                Intent i1 = new Intent(AdministratorHome.this, ViewModifyRoute.class);
                                startActivity(i1);
                                break;

                        }
                        dialog.dismiss();
                    }
                });
                b.show();
            }
        });


        feeRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdministratorHome.this, UserListFeeRecord.class);
                i.putExtra("user_type", "Commuter");
                startActivity(i);

            }
        });

        userApprovalBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(AdministratorHome.this);
                b.setTitle("Select Option : ");
                final String[] types = {"Commuter", "Guardian"};
                b.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:

                                Intent i = new Intent(AdministratorHome.this, UserListApprovalBoard.class);
                                i.putExtra("user_type", "Commuter");
                                startActivity(i);

                                break;
                            case 1:
                                Intent i2 = new Intent(AdministratorHome.this, UserListApprovalBoard.class);
                                i2.putExtra("user_type", "Guardian");
                                startActivity(i2);

                                break;
                        }
                        dialog.dismiss();
                    }
                });
                b.show();
            }
        });

        checkBuses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                startActivity(new Intent(AdministratorHome.this, BusInfoAdmin.class));

                checkBusesFun();

            }
        });

        complaintForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(AdministratorHome.this);
                b.setTitle("Select : ");
                final String[] types = {"Commuter", "Driver", "Guardian", "Guest"};
                b.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:

                                Intent i = new Intent(AdministratorHome.this, ListUserComplain.class);
                                i.putExtra("user_type", "CommuterComplain");
                                startActivity(i);

                                break;
                            case 1:
                                Intent i1 = new Intent(AdministratorHome.this, ListUserComplain.class);
                                i1.putExtra("user_type", "DriverComplain");
                                startActivity(i1);

                                break;
                            case 2:
                                Intent i2 = new Intent(AdministratorHome.this, ListUserComplain.class);
                                i2.putExtra("user_type", "GuardianComplain");
                                startActivity(i2);

                                break;
                            case 3:
                                Intent i3 = new Intent(AdministratorHome.this, ListGuestComplain.class);
                                i3.putExtra("user_type", "GuestComplain");
                                startActivity(i3);

                                break;
                        }
                        dialog.dismiss();
                    }
                });
                b.show();


            }
        });

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(AdministratorHome.this);
                b.setTitle("Select : ");
                final String[] types = {"Commuter", "Guardian"};
                b.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:

                                Intent i = new Intent(AdministratorHome.this, ListUserFeedback.class);
                                i.putExtra("user_type", "Commuter Feedback");
                                startActivity(i);

                                break;
                            case 1:
                                Intent i1 = new Intent(AdministratorHome.this, ListUserFeedback.class);
                                i1.putExtra("user_type", "Guardian Feedback");
                                startActivity(i1);

                                break;
                        }
                        dialog.dismiss();
                    }
                });
                b.show();


            }
        });


    }

    private void checkBusesFun() {


        ArrayList<String> busNumberList = new ArrayList<String>();
        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("BusDetail");
        getUserDatabaseReference.keepSynced(true);
        getUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.e("Count :", "" + dataSnapshot1.getChildrenCount());
                        Log.e("Key :", "" + dataSnapshot1.getRef().getKey());
                        busNumberList.add(dataSnapshot1.getRef().getKey());
                    }
                }

                AlertDialog.Builder b = new AlertDialog.Builder(AdministratorHome.this);
                b.setTitle("Bus # :");
                b.setItems(busNumberList.toArray(new String[busNumberList.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String busNumber_ = busNumberList.get(which).toString();
                        Log.v("BusNumber", "" + busNumber_);

                        openBusesInfo(busNumber_);
                    }
                });
                b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                b.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void openBusesInfo(String busNumber_) {

        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("BusDetail")
                .child(busNumber_)
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
                    geocoder = new Geocoder(AdministratorHome.this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
                    String address = addresses.get(0).getAddressLine(0);

                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(AdministratorHome.this);
                    builder.setTitle("Bus Information")
                            .setMessage("Bus Speed : " + speed.toString().substring(0, 3) + " Kmh" + "\n" + "Bus Location : " + address)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                }
                            })
                            .setNegativeButton("Open Map", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.valueOf(latitude), Double.valueOf(longitude));
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    startActivity(intent);
                                }
                            });


                    androidx.appcompat.app.AlertDialog alert = builder.create();
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
        viewModifyUser = findViewById(R.id.view_modify_users);
        viewModifyBusRoute = findViewById(R.id.view_modify_busroute);
        userApprovalBoard = findViewById(R.id.user_approval_board);
        checkBuses = findViewById(R.id.checkbuses);
        complaintForm = findViewById(R.id.complaintform);
        feedbackBtn = findViewById(R.id.feedbackBtn);
        parent_view = findViewById(android.R.id.content);
        feeRecord = findViewById(R.id.feelist);

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
                doExitApp();
            }
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            finish();
            startActivity(new Intent(AdministratorHome.this, WelcomeActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bus_maintenance) {
            busMaintenace();

        } else if (id == R.id.nav_violation_record) {

        } else if (id == R.id.nav_reports) {


        } else if (id == R.id.nav_report_bug) {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:management@gmail.com"));
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));

        } else if (id == R.id.nav_uni) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://au.edu.pk/"));
            startActivity(browserIntent);

        } else if (id == R.id.nav_app) {
            startActivity(new Intent(AdministratorHome.this, AboutApp.class));

        } else if (id == R.id.nav_logout) {
            finish();
            startActivity(new Intent(AdministratorHome.this, WelcomeActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void busMaintenace() {

        ArrayList<String> busNumberList = new ArrayList<String>();

        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("BusMaintenance");
        getUserDatabaseReference.keepSynced(true);
        getUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.e("Count :", "" + dataSnapshot1.getChildrenCount());
                        Log.e("Key :", "" + dataSnapshot1.getRef().getKey());
                        busNumberList.add(dataSnapshot1.getRef().getKey());
                    }
                }

                AlertDialog.Builder b = new AlertDialog.Builder(AdministratorHome.this);
                b.setTitle("Bus # :");
                b.setItems(busNumberList.toArray(new String[busNumberList.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String busNumber_ = busNumberList.get(which).toString();
                        Log.v("BusNumber", "" + busNumber_);

                        Intent i = new Intent(getApplicationContext(), BusMaintenanceRecord.class);
                        i.putExtra("bus_number", busNumber_);
                        startActivity(i);
                    }
                });
                b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                b.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getUserList() {
        AlertDialog.Builder b = new AlertDialog.Builder(AdministratorHome.this);
        b.setTitle("Select Option : ");
        final String[] types = {"Commuter", "Driver", "Guardian"};
        b.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        Intent i = new Intent(AdministratorHome.this, ListUserRegistered.class);
                        i.putExtra("user_type", "Commuter");
                        startActivity(i);
                        break;

                    case 1:
                        Intent i1 = new Intent(AdministratorHome.this, ListUserRegistered.class);
                        i1.putExtra("user_type", "Driver");
                        startActivity(i1);
                        break;

                    case 2:
                        Intent i2 = new Intent(AdministratorHome.this, ListUserRegistered.class);
                        i2.putExtra("user_type", "Guardian");
                        startActivity(i2);
                        break;

                }
                dialog.dismiss();
            }
        });
        b.show();

    }
}
