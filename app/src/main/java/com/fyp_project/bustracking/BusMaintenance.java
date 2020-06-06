package com.fyp_project.bustracking;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class BusMaintenance extends AppCompatActivity {

    TextView dateTimeTv;
    String dateDatabase = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    String dateDatabaseDb = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(new Date());
    Button sendReport;
    private FirebaseAuth mAuth;
    private DatabaseReference getUserDatabaseReference;
    String user_id;

    EditText distanceCovered, fuelConsumed, tripTime, anyExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_maintenance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateTimeTv = findViewById(R.id.dateTimeToday);

        distanceCovered = findViewById(R.id.distanceCovered);
        fuelConsumed = findViewById(R.id.fuel_consumed);
        tripTime = findViewById(R.id.tripTime);
        anyExtra = findViewById(R.id.anyExtra);
        sendReport = findViewById(R.id.sendReport);
        dateTimeTv.setText(dateDatabase);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Driver").child(user_id);
                getUserDatabaseReference.keepSynced(true);
                getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {

                            String BusNumber = dataSnapshot.child("bus_number").getValue().toString();

                            String dateDatabase = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(new Date());
                            getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                    .child("BusMaintenance")
                                    .child(BusNumber)
                                    .child(user_id)
                                    .child(dateDatabase);
                            getUserDatabaseReference.keepSynced(true);
                            Map<String, Object> taskMap = new HashMap<>();
                            taskMap.put("fuel_consumed", fuelConsumed.getText().toString());
                            taskMap.put("bus_number", "nil");
                            taskMap.put("distance_covered", distanceCovered.getText().toString());
                            taskMap.put("trip_time", tripTime.getText().toString());
                            taskMap.put("extar_query", anyExtra.getText().toString());
                            getUserDatabaseReference.updateChildren(taskMap);


                            AlertDialog.Builder builder = new AlertDialog.Builder(BusMaintenance.this);
                            builder.setMessage("Maintenance Record Added Successfully!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
//                            MDToast mdToast = MDToast.makeText(BusMaintenance.this, "", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
//                            mdToast.show();

                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


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
