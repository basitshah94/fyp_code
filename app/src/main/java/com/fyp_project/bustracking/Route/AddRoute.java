package com.fyp_project.bustracking.Route;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fyp_project.bustracking.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.HashMap;
import java.util.Map;

public class AddRoute extends AppCompatActivity {

    EditText routeName, routeDistance, routeTime, routeNumber;
    String routeName_ = "", routeDistance_ = "", routeTime_ = "", routeNumber_;
    Button submitBtn;
    DatabaseReference getUserDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_route);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        init();
        clickList();
    }


    private void init() {


        routeName = findViewById(R.id.routename);
        routeDistance = findViewById(R.id.routedistance);
        routeTime = findViewById(R.id.routetime);
        routeNumber = findViewById(R.id.routenumber);
        submitBtn = findViewById(R.id.saveInfoBtn);
    }

    private void clickList() {

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                routeName_ = routeName.getText().toString();
                routeDistance_ = routeDistance.getText().toString();
                routeTime_ = routeTime.getText().toString();
                routeNumber_ = routeNumber.getText().toString();

//                Log.v("Data", "" + routeDistance_ + routeNumber_);
                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("RouteInfo")
                        .child("Route" + routeNumber_.trim());
                Map<String, Object> taskMap = new HashMap<>();
                taskMap.put("route_number", routeNumber_);
                taskMap.put("route_name", routeName_);
                taskMap.put("route_distance", routeDistance_);
                taskMap.put("route_time", routeTime_);

                getUserDatabaseReference.updateChildren(taskMap);

                MDToast mdToast = MDToast.makeText(AddRoute.this, "Route Added.", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                mdToast.show();
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

}
