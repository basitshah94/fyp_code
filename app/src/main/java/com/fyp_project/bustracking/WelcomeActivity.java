package com.fyp_project.bustracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fyp_project.bustracking.Driver.TrackerService;
import com.fyp_project.bustracking.Guest.GuestHome;

public class WelcomeActivity extends AppCompatActivity {

    Button btnNext;
    RadioGroup radioGroup;
    RadioButton radioButton;
    private static final int PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        btnNext = findViewById(R.id.nextLogin);

        radioGroup = findViewById(R.id.radioMain);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedId);

                if (radioButton.getText().equals("Guest")) {
                    Intent intent = new Intent(WelcomeActivity.this, GuestHome.class);
                    startActivity(intent);

                } else {
                    String strUser = radioButton.getText().toString().toLowerCase();
                    if (radioButton.getText().toString().toLowerCase().equals("admin")) {
                        strUser = "administrator";
                    }
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    intent.putExtra("designation", strUser);
                    startActivity(intent);
                }
            }
        });
        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
//            finish();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
//            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }
//    private void startTrackerService() {
//        startService(new Intent(this, TrackerService.class));
//        finish();
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
//            startTrackerService();
        } else {
            finish();
        }
    }
}
