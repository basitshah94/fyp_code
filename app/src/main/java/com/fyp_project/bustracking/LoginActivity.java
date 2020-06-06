package com.fyp_project.bustracking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fyp_project.bustracking.Administrator.AdministratorHome;
import com.fyp_project.bustracking.Commuter.CommuterHome;
import com.fyp_project.bustracking.Driver.DriverHome;
import com.fyp_project.bustracking.Guardian.GuardianHome;
import com.fyp_project.bustracking.Guest.GuestHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    String commuter = "commuter", guardian = "guardian", driver = "driver", administrator = "administrator", guest = "guest";
    ImageView showPassIcon;
    EditText userPassword, userEmail;
    TextView linkSignUp, forgotPassword;
    String designationValue = "";
    Button btnSignIn;
    String capitalFinal = "";

    ProgressBar loadingSignIn;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        designationValue = getIntent().getStringExtra("designation");
//                designationValue = "Guest";
//        designationValue = "Guardian";
//        designationValue = "Driver";

//        designationValue = "Commuter";
//        designationValue = "Manager";
        Initialization();
        OnClickListener();

//        loginUserAccount("rajafiverrinfo@gmail.com","1234567");
//        loginUserAccount("fypbustracking@gmail.com","1234567");
//        startActivity(new Intent(LoginActivity.this, AdministratorHome.class));
//        loginUserAccount("160666@students.au.edu.pk", "1234567");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void OnClickListener() {

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
            }
        });

        showPassIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:
                        userPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        showPassIcon.setImageResource(R.drawable.ic_visibility);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        userPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        showPassIcon.setImageResource(R.drawable.ic_visibility_off);
                        break;
                }
                return true;
            }
        });
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("designation", designationValue);
                startActivity(intent);
            }
        });


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingSignIn.setVisibility(View.VISIBLE);
                String email = userEmail.getText().toString();
                String password = userPassword.getText().toString();
                loginUserAccount(email, password);
//                loginUserAccount("rajafiverrinfo@gmail.com","1234567");

//                if (designationValue.equals(commuter)) {
//                    startActivity(new Intent(LoginActivity.this, CommuterHome.class));
//
//                } else if (designationValue.equals(guardian)) {
//                    startActivity(new Intent(LoginActivity.this, GuardianHome.class));
//
//                } else if (designationValue.equals(driver)) {
//                    startActivity(new Intent(LoginActivity.this, DriverHome.class));
//
//                } else if (designationValue.equals(guest)) {
//                    startActivity(new Intent(LoginActivity.this, GuestHome.class));
//
//                } else if (designationValue.equals(administrator)) {
//                    startActivity(new Intent(LoginActivity.this, AdministratorHome.class));
//                }
            }
        });
    }


    private void loginUserAccount(final String email, final String password) {

        if (TextUtils.isEmpty(email)) {
            MDToast mdToast = MDToast.makeText(LoginActivity.this, "Email is required.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();
            loadingSignIn.setVisibility(View.INVISIBLE);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            MDToast mdToast = MDToast.makeText(LoginActivity.this, "Invalid Email.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();
            loadingSignIn.setVisibility(View.INVISIBLE);
        } else if (TextUtils.isEmpty(password)) {
            MDToast mdToast = MDToast.makeText(LoginActivity.this, "Password is required.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();
            loadingSignIn.setVisibility(View.INVISIBLE);
        } else if (password.length() < 6) {
            MDToast mdToast = MDToast.makeText(LoginActivity.this, "Invalid Password.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();
            loadingSignIn.setVisibility(View.INVISIBLE);
        } else {
            capitalFinal = designationValue.substring(0, 1).toUpperCase() + designationValue.substring(1);
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(capitalFinal);

            if (capitalFinal.equals("Administrator") && password.equals("1234567") && email.equals("manager@gmail.com")) {
                startActivity(new Intent(LoginActivity.this, AdministratorHome.class));
            } else if (capitalFinal.equals("Guest") && password.equals("1234567") && email.equals("guest@gmail.com")) {
                startActivity(new Intent(LoginActivity.this, GuestHome.class));
            }


            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                final String userUID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference userNameRef = rootRef.child("User").child(capitalFinal).child(userUID);
                                ValueEventListener eventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            if (task.isSuccessful()) {

                                                String userDeviceToken = FirebaseInstanceId.getInstance().getToken();
                                                userDatabaseReference.child(userUID).child("device_token").setValue(userDeviceToken)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                checkVerifiedEmail();
                                                                loadingSignIn.setVisibility(View.INVISIBLE);
                                                            }
                                                        });
                                            } else {
                                                MDToast mdToast = MDToast.makeText(LoginActivity.this, "Your email and password may be incorrect. Please try again.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                                                mdToast.show();
                                                loadingSignIn.setVisibility(View.INVISIBLE);
                                            }

                                        } else {
                                            MDToast mdToast = MDToast.makeText(LoginActivity.this, "No account found in designation as " + capitalFinal + ".", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                                            mdToast.show();
                                            loadingSignIn.setVisibility(View.INVISIBLE);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                };
                                userNameRef.addListenerForSingleValueEvent(eventListener);

                            } else {

                                if (capitalFinal.equals("Administrator") && password.equals("1234567") && email.equals("manager@gmail.com")) {


                                } else {
                                    MDToast mdToast = MDToast.makeText(LoginActivity.this, "Your email and password may be incorrect. Please try again.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                                    mdToast.show();
                                    loadingSignIn.setVisibility(View.INVISIBLE);
                                }

                            }
                        }
                    });
        }

    }


    private void Initialization() {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        loadingSignIn = findViewById(R.id.loadingSignIn);
        showPassIcon = findViewById(R.id.show_pass_icon);
        userPassword = findViewById(R.id.inputPassword);
        userEmail = findViewById(R.id.inputEmail);
        linkSignUp = findViewById(R.id.tvSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        forgotPassword = findViewById(R.id.tvForgotPass);
    }


    private void checkVerifiedEmail() {
        mUser = mAuth.getCurrentUser();
        boolean isVerified = false;
        if (mUser != null) {
            isVerified = mUser.isEmailVerified();
        }
        if (isVerified) {
            final String UID = mAuth.getCurrentUser().getUid();
            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    switch (capitalFinal) {
                        case "Commuter":
                            userDatabaseReference.child(UID).child("user_verified").setValue("true");
                            Intent intent = new Intent(LoginActivity.this, CommuterHome.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            break;

                        case "Guardian":
                            userDatabaseReference.child(UID).child("user_verified").setValue("true");
                            Intent intent1 = new Intent(LoginActivity.this, GuardianHome.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent1);
                            finish();
                            break;

                        case "Driver":
                            userDatabaseReference.child(UID).child("user_verified").setValue("true");
                            Intent intent3 = new Intent(LoginActivity.this, DriverHome.class);
                            intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent3);
                            finish();
                            break;

                        default:
                            MDToast mdToast = MDToast.makeText(LoginActivity.this, "Invalid Operation", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                            mdToast.show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    MDToast mdToast = MDToast.makeText(LoginActivity.this, "Error : Please try again.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                    mdToast.show();
                }
            });
        } else {
            MDToast mdToast = MDToast.makeText(LoginActivity.this, "Email is not verified. Please verify your email.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
            mdToast.show();
            mAuth.signOut();
        }
    }
}
