package com.fyp_project.bustracking;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText registerPassword;
    ImageView showPassIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        OnClickListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void OnClickListener() {

        registerPassword = findViewById(R.id.registerPassword);
        showPassIcon = findViewById(R.id.show_pass_icon);


        showPassIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_UP:
                        registerPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        showPassIcon.setImageResource(R.drawable.ic_visibility);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        registerPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        showPassIcon.setImageResource(R.drawable.ic_visibility_off);
                        break;
                }
                return true;
            }
        });
    }
}