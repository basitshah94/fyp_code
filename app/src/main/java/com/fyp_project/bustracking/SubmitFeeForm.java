package com.fyp_project.bustracking;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubmitFeeForm extends AppCompatActivity {


    private static final int GALLERY_PICK_CODE = 1;
    private Uri mImageProfileUri;
    private StorageTask mUploadTask;
    private ProgressDialog progressDialog;
    private StorageReference mStorageRef;

    EditText regID;
    Button saveBtn;
    Button uploadPhoto;

    private FirebaseAuth mAuth;
    private DatabaseReference getUserDatabaseReference;
    String user_id;
    ImageButton uploadOrNot;

    String emailText, idText, nameText, phoneText, busnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_fee_form);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child("Commuter").child(user_id);
        getUserDatabaseReference.keepSynced(true);

        mStorageRef = FirebaseStorage.getInstance().getReference("FeeChallanProof/");

        uploadOrNot = findViewById(R.id.show_upload_loading);
        uploadPhoto = findViewById(R.id.upload_photo);
        regID = findViewById(R.id.reg_id);
        saveBtn = findViewById(R.id.saveInfoBtn);

        RetrieveDataFromDatabase();


    }


    private void completeFunction() {

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                if (mImageProfileUri != null) {
                    StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageProfileUri));
                    mUploadTask = fileReference.putFile(mImageProfileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploadOrNot.setImageResource(R.drawable.checked);
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String profileImageURL = uri.toString();
                                    String dateDatabase = new SimpleDateFormat("MM_yyyy", Locale.getDefault()).format(new Date());
                                    getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                            .child("FeeChallan")
                                            .child(user_id);
                                    Map<String, Object> taskMap = new HashMap<>();
                                    taskMap.put("bus_number", busnumber);
                                    taskMap.put("user_email", emailText);
                                    taskMap.put("fee_challan_image", profileImageURL);
                                    taskMap.put("user_name", nameText);
                                    taskMap.put("month_year", dateDatabase);
                                    taskMap.put("user_id", regID.getText().toString());
                                    getUserDatabaseReference.updateChildren(taskMap);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SubmitFeeForm.this);
                                    builder.setMessage("Record Submitted Successfully!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    finish();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadOrNot.setImageResource(R.drawable.cancel);
                            MDToast mdToast = MDToast.makeText(SubmitFeeForm.this, "Uploading Failed.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            uploadOrNot.setImageResource(R.drawable.loading);
                        }
                    });
                } else {
                    MDToast mdToast = MDToast.makeText(SubmitFeeForm.this, "No Image File Selected.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                    mdToast.show();
                }

            }
        });


        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, GALLERY_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageProfileUri = data.getData();
//            Picasso.get().load(mImageProfileUri).into(profilePictureVerify);
//             profilePictureVerify.setVisibility(View.VISIBLE);
            uploadOrNot.setImageResource(R.drawable.checked);

        } else if (requestCode != GALLERY_PICK_CODE || resultCode != RESULT_OK || data == null && data.getData() == null) {
            uploadOrNot.setImageResource(R.drawable.cancel);

        } else {
            uploadOrNot.setImageResource(R.drawable.loading);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mp = MimeTypeMap.getSingleton();
        return mp.getExtensionFromMimeType(cr.getType(uri));
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
                busnumber = dataSnapshot.child("bus_number").getValue().toString();

                completeFunction();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
