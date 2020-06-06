package com.fyp_project.bustracking.BusModule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fyp_project.bustracking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BusMaintenanceRecord extends AppCompatActivity {

    DatabaseReference databaseId;
    Activity context;
    //the listview
    ListView listView;
    BusAdapter adapter;
    //database reference to get uploads data
    DatabaseReference mDatabaseReference;
    DatabaseReference databaseReference;
    //list to store uploads data
    List<ModalBus> uploadList;
    String busNumber;

    private FirebaseAuth mAuth;
    private DatabaseReference getUserDatabaseReference;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_mainteanace_record);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        busNumber = getIntent().getExtras().getString("bus_number");

        setTitle("Bus Maintenance Record");

//        mAuth = FirebaseAuth.getInstance();
//        user_id = mAuth.getCurrentUser().getUid();


        uploadList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);


        //adding a clicklistener on listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                //getting the upload
                final ModalBus upload = uploadList.get(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(BusMaintenanceRecord.this);
                builder.setTitle("Bus Maintenance Record")
                        .setMessage(
                                "Distance Covered :" + upload.getDistance_covered() + "\n"
                                        + "Fuel Consumed :" + upload.getFuel_consumed() + "\n"
                                        + "Trip Time :" + upload.getTrip_time()
//                                         "\n"+ "Comments :" + upload.getExtra_query() + "\n"
                        )
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });


        //getting the database reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("BusMaintenance").child(busNumber);
        //retrieving upload data from firebase database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot1 : postSnapshot.getChildren()) {

                        ModalBus upload = postSnapshot1.getValue(ModalBus.class);
                        uploadList.add(upload);
                        String key = postSnapshot1.getKey();
                        upload.setDate_time(key);
                    }
                }

                String[] uploadsdatetime = new String[uploadList.size()];
                String[] uploadsBusNumber = new String[uploadList.size()];
                String[] uploadDistanceCovered = new String[uploadList.size()];
                String[] uploadExtraQuery = new String[uploadList.size()];
                String[] uploadFuelConsumed = new String[uploadList.size()];
                String[] uploadTripTime = new String[uploadList.size()];

                for (int i = 0; i < uploadsdatetime.length; i++) {

                    uploadsdatetime[i] = uploadList.get(i).getDate_time();
                    uploadsBusNumber[i] = uploadList.get(i).getBus_number();
                    uploadDistanceCovered[i] = uploadList.get(i).getDistance_covered();
                    uploadExtraQuery[i] = uploadList.get(i).getExtra_query();
                    uploadFuelConsumed[i] = uploadList.get(i).getFuel_consumed();
                    uploadTripTime[i] = uploadList.get(i).getTrip_time();

                    Log.v("DateTime", "" + uploadList.get(i).getDate_time());
                    //Toast.makeText(ViewUploadsActivity.this, "Toast :"+ uploadList.get(i).getFile_name(), Toast.LENGTH_SHORT).show();
                }

                BusAdapter adapter = new BusAdapter(BusMaintenanceRecord.this, uploadsdatetime);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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