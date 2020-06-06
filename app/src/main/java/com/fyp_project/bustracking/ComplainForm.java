package com.fyp_project.bustracking;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class ComplainForm extends AppCompatActivity {

    EditText userEmail, userQuery;
    Button saveInfo;
    private FirebaseAuth mAuth;
    private DatabaseReference getUserDatabaseReference;
    String user_id;
    String userDetailsPath = "";

    String nameText = "";
    String idText = "";
    String emailText = "";
    String phoneText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userDetailsPath = getIntent().getStringExtra("designation");

        userQuery = findViewById(R.id.inputQuery);
        userEmail = findViewById(R.id.inputEmail);
        saveInfo = findViewById(R.id.saveInfoBtn);

        if (!userDetailsPath.equals("Guest")) {

            mAuth = FirebaseAuth.getInstance();
            user_id = mAuth.getCurrentUser().getUid();
            getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(userDetailsPath).child(user_id);
            getUserDatabaseReference.keepSynced(true);

            RetrieveDataFromDatabase();

            saveInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dateDatabase = new SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(new Date());
                    getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                            .child(userDetailsPath + "Complain")
                            .child(user_id)
                            .child(dateDatabase);
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("user_name", nameText);
                    taskMap.put("user_id", idText);
                    taskMap.put("user_email", emailText);
                    taskMap.put("user_mobile_number", phoneText);
                    taskMap.put("user_complain", userQuery.getText().toString());

                    getUserDatabaseReference.updateChildren(taskMap);


                    AlertDialog.Builder builder = new AlertDialog.Builder(ComplainForm.this);
                    builder.setMessage("Complain Registered Successfully!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });
        }

        if (userDetailsPath.equals("Guest")) {

            saveInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dateDatabase = new SimpleDateFormat("dd_MM_yyyy_mm_ss_hh", Locale.getDefault()).format(new Date());
                    getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                            .child(userDetailsPath + "Complain")
                            .child(dateDatabase);
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("user_email", userEmail.getText().toString());
                    taskMap.put("user_complain", userQuery.getText().toString());

                    getUserDatabaseReference.updateChildren(taskMap);


                    AlertDialog.Builder builder = new AlertDialog.Builder(ComplainForm.this);
                    builder.setMessage("Complain Registered Successfully!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });

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

    private void RetrieveDataFromDatabase() {

        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameText = dataSnapshot.child("user_name").getValue().toString();
                idText = dataSnapshot.child("user_id").getValue().toString();
                emailText = dataSnapshot.child("user_email").getValue().toString();
                phoneText = dataSnapshot.child("user_mobile_number").getValue().toString();
                String imageText = dataSnapshot.child("user_profile_image").getValue().toString();
                userEmail.setText(emailText);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

//                MDToast mdToast = MDToast.makeText(Compla.this, "Updating Failed.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
//                mdToast.show();
            }
        });

    }

}
