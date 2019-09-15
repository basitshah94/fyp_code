package com.fyp_project.bustracking;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    ImageView showPassIcon;
    EditText userPassword;
    TextView linkSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Initialization();
        OnClickListener();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void OnClickListener() {

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
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void Initialization() {
        showPassIcon = findViewById(R.id.show_pass_icon);
        userPassword = findViewById(R.id.inputPassword);
        linkSignUp = findViewById(R.id.tvSignUp);
    }
}
