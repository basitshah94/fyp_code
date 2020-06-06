package com.fyp_project.bustracking.Guest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fyp_project.bustracking.AboutApp;
import com.fyp_project.bustracking.ComplainForm;
import com.fyp_project.bustracking.Guardian.CurrentLocation;
import com.fyp_project.bustracking.R;
import com.fyp_project.bustracking.Route.RecyclerAdapterRoute;
import com.fyp_project.bustracking.Route.ViewModifyRoute;
import com.fyp_project.bustracking.WelcomeActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class GuestHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    private View parent_view;

    Button visitUniWeb, currentInfoDesk, myCurrentLoc, checkBus, checkRoute, emergencyRes, suggestForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        toolbar = findViewById(R.id.toolbar);


        toolbar.setTitle("Guest Dashboard");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        TextView userNameNav;
        TextView userEmailNav;
        userNameNav = hView.findViewById(R.id.userNameNav);
        userEmailNav = hView.findViewById(R.id.userEmailNav);

        userNameNav.setText("Guest");
        userEmailNav.setText("guest@gmail.com");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        init();
        clickListForm();

    }

    private void clickListForm() {

        visitUniWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://au.edu.pk/"));
                startActivity(browserIntent);

            }
        });

        currentInfoDesk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(GuestHome.this);
                b.setTitle("Contact Information Desk : ");
                final String[] types = {"Via Call-1", "Via Call-2", "Via Call-3"};
                b.setItems(types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:

                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:051-9262557"));
                                startActivity(intent);

                                break;
                            case 1:

                                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                                intent1.setData(Uri.parse("tel:051-9262558"));
                                startActivity(intent1);

                                break;
                            case 2:

                                Intent intent2 = new Intent(Intent.ACTION_DIAL);
                                intent2.setData(Uri.parse("tel:051-9262559"));
                                startActivity(intent2);

                                break;
                        }
                        dialog.dismiss();
                    }
                });
                b.show();


            }
        });

        myCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(GuestHome.this, CurrentLocation.class));


            }
        });

        checkRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(GuestHome.this, ViewModifyRoute.class));

            }
        });

        suggestForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(GuestHome.this, ComplainForm.class);
                intent.putExtra("designation", "Guest");
                startActivity(intent);

            }
        });


    }

    private void init() {
        visitUniWeb = findViewById(R.id.visit_university_website);
        currentInfoDesk = findViewById(R.id.contact_information_desk);
        myCurrentLoc = findViewById(R.id.my_current_location);
        checkBus = findViewById(R.id.check_buses);
        checkRoute = findViewById(R.id.check_routes);
        emergencyRes = findViewById(R.id.emergency_response);
        suggestForm = findViewById(R.id.suggestion_forum);
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
            finish();
            startActivity(new Intent(GuestHome.this, WelcomeActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            startActivity(new Intent(GuestHome.this, CurrentLocation.class));


        } else if (id == R.id.nav_gallery) {

            startActivity(new Intent(GuestHome.this, ViewModifyRoute.class));

        } else if (id == R.id.nav_about_app) {

            startActivity(new Intent(GuestHome.this, AboutApp.class));

        } else if (id == R.id.nav_send) {

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://au.edu.pk/"));
            startActivity(browserIntent);

        } else if (id == R.id.nav_share) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:management@gmail.com"));
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));

        } else if (id == R.id.nav_logout) {
            finish();
            startActivity(new Intent(GuestHome.this, WelcomeActivity.class));

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
