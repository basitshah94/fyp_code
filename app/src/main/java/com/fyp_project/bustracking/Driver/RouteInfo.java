package com.fyp_project.bustracking.Driver;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.baoyachi.stepview.VerticalStepView;
import com.fyp_project.bustracking.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.SphericalUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RouteInfo extends AppCompatActivity {

    Toolbar toolbar;
    VerticalStepView verticalStepView;
    DatabaseReference getUserDatabaseReference;
    int count = 0;
    TextView upcomingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_detail);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Route Information");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        verticalStepView = findViewById(R.id.vertical_stepview);
        upcomingTime = findViewById(R.id.upcomingtime);

        getValueMap();

        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstrun){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(RouteInfo.this);
            builder.setMessage("Feature is only available for Bus # 1\nPlease use Bus Navigation for better results.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            androidx.appcompat.app.AlertDialog alert = builder.create();
            alert.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)
                    .commit();
        }
    }

    private void getValueMap() {

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
                    Log.v("Firebase Value x:", "" + latitude);
                    Log.v("Firebase Value y:", "" + longitude);


                    testStepViewFun(Double.valueOf(latitude), Double.valueOf(longitude));
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void verticalStepViewFun() {
        final List<String> list = new ArrayList<>();

        // starting coordinates
        double x1 = 33.713810;
        double y1 = 73.024621;

        // ending coordinates
        double x2 = 33.623750;
        double y2 = 73.100148;


        list.add("University Bus Stop"); //Air University Islamabad Service Road E-9 / E-8, Islamabad, Pakistan، E-9, Islamabad, Islamabad Capital Territory 44000, Pakistan 33.713810, 73.024621
        list.add("G6 Bus Stop");
        list.add("Serena Hotel Bus Stop");
        list.add("Taramry Bus Stop");
        list.add("Khanna Pul  Bus Stop");
        list.add("Falcon Complex  Bus Stop"); //Falcon Complex Chaklala Cantt., Rawalpindi, Islamabad Capital Territory 46000, Pakistan 33.623750, 73.100148

        Log.v("Distance:", distance(x1, -y1, x2, -y2, "M") + " Miles\n");
        Log.v("Distance:", distance(x1, -y1, x2, -y2, "K") + " Kilometers\n");
        Log.v("Distance:", distance(x1, -y1, x2, -y2, "N") + " Nautical Miles\n");

        //G 7/3  G-7, Islamabad, Islamabad Capital Territory, Pakistan  33.715767, 73.073026
        final Handler handler = new Handler();

        final Runnable updateTask = new Runnable() {
            @Override
            public void run() {

                Log.v("Timer Called", "" + "called");
                verticalStepView.setStepsViewIndicatorComplectingPosition(count++)
                        .reverseDraw(false)
                        .setStepViewTexts(list)
                        .setLinePaddingProportion(0.85f)
                        .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                        .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                        .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                        .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                        .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                        .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                        .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));

                handler.postDelayed(this, 3000);
            }
        };

        handler.postDelayed(updateTask, 3000);

    }

    private void testStepViewFun(Double latitude, Double longitude) {
        final List<String> list = new ArrayList<>();

        //routes stop double array declaration
        double[] xLatitude = {12, 4, 5, 2, 5};
        double[] yLongitude = {12, 4, 5, 2, 5};


        // starting coordinates
        double x1 = 33.713810;
        double y1 = 73.024621;

        // ending coordinates
        double x2 = 33.623750;
        double y2 = 73.100148;

        //Total Time 1 Hour 15 Minutes = 75 minutes // 33.2Km Distance
        //Estimated Time = in minutes
        //Air University Islamabad Service Road E-9 / E-8, Islamabad, Pakistan، E-9, Islamabad, Islamabad Capital Territory 44000, Pakistan 33.713810, 73.024621

        int estimatedTime = 4;
        LatLng currentBuslocation = new LatLng(latitude, longitude);
        LatLng nextBuslocation = new LatLng(31.60171, 73.036705);

        int estimatedDistance = (int) SphericalUtil.computeDistanceBetween(currentBuslocation, nextBuslocation);
        double finalDistance = 34000 / 1000;
        int timetext = 70;
//        int finalTime = (int) ((finalDistance / 60) * 60);
//        String extraString = " | " + finalTime + " Minutes | " + finalDistance + " Km";
        String NextstopLocation = "";


        list.add("University Entrance");
        list.add("Main Margalla Road");
        list.add("7th Avenue Road");
        list.add("G6 Road");
        list.add("Murree Road");
        list.add("Rawal Dam/Town Colony");
        list.add("Park Road");
        list.add("Shahzad Town");
        list.add("Tarlai Kalan");
        list.add("Lehtrar Road");
        list.add("Khanna Pul");
        list.add("KRL Road");
        list.add("Falcon Complex");
        Collections.reverse(list);


        //Falcon Complex Chaklala Cantt., Rawalpindi, Islamabad Capital Territory 46000, Pakistan 33.623750, 73.100148
//        LatLng test1 = (new LatLng(x1, y1));
//        LatLng test2 = (new LatLng(x2, y2));

        Log.v("Distance:", distance(x1, y1, x2, y2, "M") + " Miles\n");
        Log.v("Distance:", distance(x1, y1, x2, y2, "K") + " Kilometers\n");
        Log.v("Distance:", distance(x1, y1, x2, y2, "N") + " Nautical Miles\n");
//        Log.v("API TIME:", getDurationForRoute(test1, test2));

        //G 7/3  G-7, Islamabad, Islamabad Capital Territory, Pakistan  33.715767, 73.073026
        //E-9/4 E-9, Islamabad - اسلام آباد, Islamabad, Islamabad Capital Territory, Pakistan 33.714218, 73.023798

        //University Entrance
//        double t1 = 33.714218;
//        double t2 = 73.023798;
        double t1 = 33.714218;
        double t2 = 73.023798;


        //Main Margalla Road
//        double mainMargallaRoadx = 33.724336;
//        double mainMargallaRoady = 73.047906;
        double mainMargallaRoadx = 33.724336;
        double mainMargallaRoady = 73.047906;


        //7th Avenue Road
//        double SeventhAvenueRoadx = 33.720882;
//        double SeventhAvenueRoady = 73.069300;
        double SeventhAvenueRoadx = 33.720882;
        double SeventhAvenueRoady = 73.069300;

        //G6 Road
//        double G6Roadx = 33.713422;
//        double G6RoadRoady = 73.082389;
        double G6Roadx = 33.713422;
        double G6RoadRoady = 73.082389;

        //Murree Road
//        double MurreeRoadx = 33.697570;
//        double MurreeRoadRoady = 73.110327;
        double MurreeRoadx = 33.697570;
        double MurreeRoadRoady = 73.110327;


        //RawalDam/RawalTown
//        double RawalRoadx = 33.688358;
//        double RawalRoady = 73.117837;
        double RawalRoadx = 33.688358;
        double RawalRoady = 73.117837;

        //Park Road
//        double ParkRoadx = 33.680073;
//        double ParkRoady = 73.138994;
        double ParkRoadx = 33.680073;
        double ParkRoady = 73.138994;

        //Shahzad Town
//        double ShahzadTownx = 33.675252;
//        double ShahzadTowny = 73.143200;
        double ShahzadTownx = 33.675252;
        double ShahzadTowny = 73.143200;

        //TarlaiKalan Town
//        double TarlaiKalanx = 33.639352;
//        double TarlaiKalany = 73.153242;
        double TarlaiKalanx = 33.639352;
        double TarlaiKalany = 73.153242;

        //LehtrarRoad
//        double LehtrarRoadx = 33.632392;
//        double LehtrarRoady = 73.123971;
        double LehtrarRoadx = 33.632392;
        double LehtrarRoady = 73.123971;


        //KhannaRoad
//        double KhannaRoadx = 33.629400;
//        double KhannaRoady = 73.111736;
        double KhannaRoadx = 33.629400;
        double KhannaRoady = 73.111736;


        //KRLRoad
//        double KRLRoadx = 33.623336;
//        double KRLRoady = 73.107666;
        double KRLRoadx = 33.623336;
        double KRLRoady = 73.107666;


        //FalconComplexRoad
//        double FalconComplexRoadx = 33.623665;
//        double FalconComplexRoady = 73.100182;
        double FalconComplexRoadx = 33.623665;
        double FalconComplexRoady = 73.100182;

        Random randomTime = new Random();
        int value;

        if (FalconComplexRoadx == latitude || FalconComplexRoady == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(1)
                    .reverseDraw(true)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (KRLRoadx == latitude || KRLRoady == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(2)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
        }
        if (KhannaRoadx == latitude || KhannaRoady == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(3)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (LehtrarRoadx == latitude || LehtrarRoady == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(4)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (TarlaiKalanx == latitude || TarlaiKalany == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(5)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (ShahzadTownx == latitude || ShahzadTowny == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(6)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (ParkRoadx == latitude || ParkRoady == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(7)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (RawalRoadx == latitude || RawalRoady == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(8)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (MurreeRoadx == latitude || MurreeRoadRoady == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(9)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (G6Roadx == latitude || G6RoadRoady == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(10)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (SeventhAvenueRoadx == latitude || SeventhAvenueRoady == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(11)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (mainMargallaRoadx == latitude || mainMargallaRoady == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(12)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        }
        if (t1 == latitude || t2 == longitude) {
            verticalStepView.setStepsViewIndicatorComplectingPosition(13)
                    .reverseDraw(false)
                    .setStepViewTexts(list)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
            value = randomTime.nextInt((4 - 2) + 1) + 1;
            upcomingTime.setText(value + " Minutes");
        } else {
//            upcomingTime.setText("Service Not Available");
        }

//        else {
//            verticalStepView.setStepsViewIndicatorComplectingPosition(14)
//                    .reverseDraw(false)
//                    .setStepViewTexts(list)
//                    .setLinePaddingProportion(0.85f)
//                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
//                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
//                    .setStepViewComplectedTextColor(ContextCompat.getColor(RouteInfo.this, android.R.color.white))
//                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(RouteInfo.this, R.color.uncompleted_text_color))
//                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.complted))
//                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.default_icon))
//                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(RouteInfo.this, R.drawable.attention));
//            upcomingTime.setText("Service Not Available");
//        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit == "K") {
                dist = dist * 1.609344;
            } else if (unit == "N") {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    private final String API_KEY = "AIzaSyBqyA6QdiYIuj12H2QiOVW6AHPd936VtnQ";

    public String getDurationForRoute(LatLng origin, LatLng destination) {

        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();

        // - Perform the actual request
        DirectionsResult directionsResult = null;
        try {
            directionsResult = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.DRIVING)
                    .origin(String.valueOf(origin))
                    .destination(String.valueOf(destination))
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // - Parse the result
        DirectionsRoute route = directionsResult.routes[0];
        DirectionsLeg leg = route.legs[0];
        Duration duration = leg.duration;
        return duration.humanReadable;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
