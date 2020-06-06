package com.fyp_project.bustracking;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.Timer;
import java.util.TimerTask;


public class ForgotPassword extends AppCompatActivity {
    private EditText forgotEmail;
    private Button resetPassButton;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        auth = FirebaseAuth.getInstance();
        forgotEmail = findViewById(R.id.forgotEmail);
        resetPassButton = findViewById(R.id.resetPassButton);
        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = forgotEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    MDToast mdToast = MDToast.makeText(ForgotPassword.this, "Email is required.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    MDToast mdToast = MDToast.makeText(ForgotPassword.this, "Email format is not valid.", Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                    mdToast.show();
                } else {
                    // send email to reset password
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                emailSentSuccessPopUp();

                                // LAUNCH activity after certain time period
                                new Timer().schedule(new TimerTask() {
                                    public void run() {
                                        ForgotPassword.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                auth.signOut();

                                                Intent mainIntent = new Intent(ForgotPassword.this, LoginActivity.class);
                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(mainIntent);
                                                finish();

                                                MDToast mdToast = MDToast.makeText(ForgotPassword.this, "Please check your email.", Toast.LENGTH_SHORT, MDToast.TYPE_INFO);
                                                mdToast.show();

                                            }
                                        });
                                    }
                                }, 8000);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            MDToast mdToast = MDToast.makeText(ForgotPassword.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT, MDToast.TYPE_ERROR);
                            mdToast.show();
                        }
                    });
                }
            }
        });

    }

    private void emailSentSuccessPopUp() {
        // Custom Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
        View view = LayoutInflater.from(ForgotPassword.this).inflate(R.layout.register_success_popup, null);
        TextView successMessage = view.findViewById(R.id.successMessage);
        successMessage.setText("Password reset link has been sent.\nPlease check your email.");
        builder.setCancelable(true);
        builder.setView(view);
        builder.show();
    }

}
