package com.fyp_project.bustracking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity {

    private static final int GALLERY_PICK_CODE = 1;


    EditText commuterRegID, commuterEmail, driverBusNumber;
    String commuterEmail_, commuterRegID_, driverBusNumber_;

    Button registerSignUpBtn;

    private Uri mImageProfileUri;

    ImageView showPassIcon;
    String designationValue = "";
    EditText name, id, mobileNumber, email, password;
    CheckBox checkBox;
    ProgressBar progressBar;
    TextView signInButton;

    Button uploadPhoto;
    ImageButton uploadOrNot;

    private StorageTask mUploadTask;
    private ProgressDialog progressDialog;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference storeDefaultDatabaseReference;

    String capitalFinal = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialization();
        OnClickListener();
    }

    private void initialization() {
        designationValue = getIntent().getStringExtra("designation");
        confirmDesignation(designationValue);


        showPassIcon = findViewById(R.id.show_pass_icon);
        name = findViewById(R.id.name);
        id = findViewById(R.id.id);
        mobileNumber = findViewById(R.id.mobile_number);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        checkBox = findViewById(R.id.checkbox);
        uploadPhoto = findViewById(R.id.registerUploadphoto);
        uploadOrNot = findViewById(R.id.show_upload_loading);

        commuterEmail = findViewById(R.id.commuter_email);
        commuterRegID = findViewById(R.id.commuter_reg_id);
        driverBusNumber = findViewById(R.id.driver_bus_number);

        if (designationValue.equals("guardian")) {
            commuterRegID.setVisibility(View.VISIBLE);
            commuterEmail.setVisibility(View.VISIBLE);
        }
        if (designationValue.equals("driver") || designationValue.equals("commuter")) {
            driverBusNumber.setVisibility(View.VISIBLE);
        }


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);


        mStorageRef = FirebaseStorage.getInstance().getReference("ProfilePicture/");
        progressBar = findViewById(R.id.loadingSignUp);
        signInButton = findViewById(R.id.tvSignIn);
        registerSignUpBtn = findViewById(R.id.registerButton);


        registerSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName, userId, userMobileNumber, userEmail, userPassword;
                userName = name.getText().toString();
                userId = id.getText().toString();
                userMobileNumber = mobileNumber.getText().toString();
                userEmail = email.getText().toString();
                userPassword = password.getText().toString();


                if (designationValue.equals("guardian")) {
                    commuterEmail_ = commuterEmail.getText().toString();
                    commuterRegID_ = commuterRegID.getText().toString();
                }

                if (designationValue.equals("driver")) {
                    driverBusNumber_ = driverBusNumber.getText().toString();
                }


                if (mImageProfileUri != null) {
                    StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageProfileUri));
                    mUploadTask = fileReference.putFile(mImageProfileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploadOrNot.setImageResource(R.drawable.checked);
                            // SweetToast.success(getApplicationContext(), "Image Uploaded.");
                            //  String profileImageURL = taskSnapshot.getStorage().getDownloadUrl().toString();

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String profileImageURL = uri.toString();
                                    validateSignUp(userName, userId, userMobileNumber, userEmail, userPassword, checkBox, profileImageURL);

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
                            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Uploading Failed.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            uploadOrNot.setImageResource(R.drawable.loading);
                        }
                    });
                } else {
                    MDToast mdToast = MDToast.makeText(RegisterActivity.this, "No Image File Selected.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            }
        });
    }

    private void confirmDesignation(String designationValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        capitalFinal = designationValue.substring(0, 1).toUpperCase() + designationValue.substring(1);

        builder.setMessage("You're Signing Up as " + capitalFinal)
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(RegisterActivity.this, WelcomeActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void validateSignUp(final String userName, final String userId, final String userMobileNumber, final String userEmail, String userPassword, CheckBox checkBox, final String upload) {
        Log.v("Name:", "" + userName);

        if (TextUtils.isEmpty(userName)) {

            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Name field is empty.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();

        } else if (userName.length() < 3 || userName.length() > 40) {

            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Name is too short.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();

        } else if (TextUtils.isEmpty(userEmail)) {

            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Email field is empty.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();

        } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {

            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Invalid email format.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();

        } else if (TextUtils.isEmpty(userMobileNumber)) {

            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Mobile number field is empty.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();

        } else if (userMobileNumber.length() < 11) {

            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Mobile number is too short.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();

        } else if (TextUtils.isEmpty(userPassword)) {

            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Password field is empty.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();

        } else if (userPassword.length() < 6) {

            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Password is too short.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();

        } else if (TextUtils.isEmpty(userId)) {

            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "ID field is empty.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();

        } else if (!checkBox.isChecked()) {

            MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Checkbox is not checked.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();

        } else {

            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String current_userID = mAuth.getCurrentUser().getUid();
                                String userDeviceToken = FirebaseInstanceId.getInstance().getToken();
                                storeDefaultDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(capitalFinal).child(current_userID);
                                storeDefaultDatabaseReference.child("user_name").setValue(userName);
                                storeDefaultDatabaseReference.child("user_id").setValue(userId);
                                storeDefaultDatabaseReference.child("user_email").setValue(userEmail);
                                storeDefaultDatabaseReference.child("user_mobile_number").setValue(userMobileNumber);
                                storeDefaultDatabaseReference.child("user_profile_image").setValue(upload);
                                storeDefaultDatabaseReference.child("device_token").setValue(userDeviceToken);
                                storeDefaultDatabaseReference.child("user_verified").setValue("false");

                                if (designationValue.equals("guardian")) {

                                    storeDefaultDatabaseReference.child("commuter_email").setValue(commuterEmail_);
                                    storeDefaultDatabaseReference.child("commuter_reg_id").setValue(commuterRegID_);

                                }
                                if (designationValue.equals("driver") || designationValue.equals("commuter")) {
                                    storeDefaultDatabaseReference.child("bus_number").setValue("Bus_" + driverBusNumber_);
                                }

                                storeDefaultDatabaseReference.child("user_account_status").setValue("false")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mUser = mAuth.getCurrentUser();
                                                    if (mUser != null) {
                                                        mUser.sendEmailVerification()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {

                                                                            registerSuccessPopUp();
                                                                            MDToast mdToast = MDToast.makeText(RegisterActivity.this, getString(R.string.register_success_popup), Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                                                            mdToast.show();

                                                                            new Timer().schedule(new TimerTask() {
                                                                                public void run() {
                                                                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                                                                        public void run() {
                                                                                            mAuth.signOut();
                                                                                            Intent mainIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                            startActivity(mainIntent);
                                                                                            finish();
                                                                                            MDToast mdToast = MDToast.makeText(RegisterActivity.this, getString(R.string.email_check), Toast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
                                                                                            mdToast.show();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }, 8000);
                                                                        } else {
                                                                            mAuth.signOut();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });
                            } else {
                                String message = task.getException().getMessage();
                                MDToast mdToast = MDToast.makeText(RegisterActivity.this, "Error occurred : " + message, Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                                mdToast.show();
                            }
                            progressDialog.dismiss();
                        }
                    });
            progressDialog.setTitle("Creating new account..");
            progressDialog.setMessage("Please wait for a moment...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }

    private void registerSuccessPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        View view = LayoutInflater.from(RegisterActivity.this).inflate(R.layout.register_success_popup, null);
        builder.setCancelable(false);
        builder.setView(view);

        try {
            builder.show();
        } catch (WindowManager.BadTokenException e) {
            Log.v("Crash dialogbox ", "" + e);
        }

    }


    @SuppressLint("ClickableViewAccessibility")
    private void OnClickListener() {

        showPassIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        showPassIcon.setImageResource(R.drawable.ic_visibility);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        password.setInputType(InputType.TYPE_CLASS_TEXT);
                        showPassIcon.setImageResource(R.drawable.ic_visibility_off);
                        break;
                }
                return true;
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


}