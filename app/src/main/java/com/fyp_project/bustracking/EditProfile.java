package com.fyp_project.bustracking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.valdesekamdem.library.mdtoast.MDToast;

public class EditProfile extends AppCompatActivity {

    EditText name, id, email, mobilenum, driverBusNumber;
    Button editProfile;
    ImageView squareImageView;
    private FirebaseAuth mAuth;
    private DatabaseReference getUserDatabaseReference;
    String user_id;
    String userDetailsPath = "";

    EditText cEmail, cID, driverBusNumber_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userDetailsPath = getIntent().getStringExtra("designation");

        Initialization(userDetailsPath);
        OnClickListener();
        RetrieveDataFromDatabase();

    }

    private void RetrieveDataFromDatabase() {

        getUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nameText = dataSnapshot.child("user_name").getValue().toString();
                String idText = dataSnapshot.child("user_id").getValue().toString();
                String emailText = dataSnapshot.child("user_email").getValue().toString();
                String phoneText = dataSnapshot.child("user_mobile_number").getValue().toString();
                String imageText = dataSnapshot.child("user_profile_image").getValue().toString();

                if (userDetailsPath.equals("Guardian")) {
                    String cEmail_ = dataSnapshot.child("commuter_email").getValue().toString();
                    String cID_ = dataSnapshot.child("commuter_reg_id").getValue().toString();
                    cEmail.setText(cEmail_);
                    cID.setText(cID_);

                }
                if (userDetailsPath.equals("Driver") || userDetailsPath.equals("Commuter")) {
                    String cBusNumber = dataSnapshot.child("bus_number").getValue().toString();
                    driverBusNumber.setText(cBusNumber);

                }


                name.setText(nameText);
                id.setText(idText);
                email.setText(emailText);
                mobilenum.setText(phoneText);
                Picasso.get().load(imageText).into(squareImageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                MDToast mdToast = MDToast.makeText(EditProfile.this, "Updating Failed.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                mdToast.show();
            }
        });

    }

    private void OnClickListener() {

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uName = name.getText().toString();
                String uPhone = mobilenum.getText().toString();
                saveInformation(uName, uPhone);
            }
        });


    }


    private void saveInformation(String uName, String uPhone) {

        if (TextUtils.isEmpty(uName)) {
            MDToast mdToast = MDToast.makeText(EditProfile.this, "Name is required...", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();
        } else if (uName.length() < 3 || uName.length() > 40) {
            MDToast mdToast = MDToast.makeText(EditProfile.this, "Your name should contain 3 to 40 characters...", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();
        } else if (TextUtils.isEmpty(uPhone)) {
            MDToast mdToast = MDToast.makeText(EditProfile.this, "Mobile # is required...", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();
        } else if (uPhone.length() < 11) {
            MDToast mdToast = MDToast.makeText(EditProfile.this, "Mobile Number should be greater than 11 digits...", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();
        } else {
            getUserDatabaseReference.child("user_name").setValue(uName);
            getUserDatabaseReference.child("user_mobile_number").setValue(uPhone);
            getUserDatabaseReference.child("bus_number").setValue("Bus_"+driverBusNumber.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            MDToast mdToast = MDToast.makeText(EditProfile.this, "Updated.", Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                            mdToast.show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    MDToast mdToast = MDToast.makeText(EditProfile.this, "Unable to update...", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            });
        }
    }

    private void Initialization(String userDetailsPath) {

        name = findViewById(R.id.user_display_name);
        id = findViewById(R.id.id_profile);
        email = findViewById(R.id.userEmail);
        mobilenum = findViewById(R.id.phone);
        editProfile = findViewById(R.id.saveInfoBtn);
        squareImageView = findViewById(R.id.profile_img);

        cEmail = findViewById(R.id.c_email);
        cID = findViewById(R.id.c_id);
        driverBusNumber = findViewById(R.id.c_bus_number);

        if (userDetailsPath.equals("Guardian")) {
            cEmail.setVisibility(View.VISIBLE);
            cID.setVisibility(View.VISIBLE);
        }

        if (userDetailsPath.equals("Commuter") || userDetailsPath.equals("Driver")) {
            driverBusNumber.setVisibility(View.VISIBLE);
        }


        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(userDetailsPath).child(user_id);
        getUserDatabaseReference.keepSynced(true);


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
