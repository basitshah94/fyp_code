package com.fyp_project.bustracking;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class TrafficViolation extends AppCompatActivity {


//    String emailID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_violation_record);

//        emailID = getIntent().getExtras().getString("emailID");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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

    public void contactUs(View view) {

//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.setType("message/rfc822");
//        i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailID});
//        i.putExtra(Intent.EXTRA_SUBJECT, "");
//        i.putExtra(Intent.EXTRA_TEXT, "");
//        try {
//            startActivity(Intent.createChooser(i, "Send mail..."));
//        } catch (android.content.ActivityNotFoundException ex) {
////            Toast.makeText(ContactUs.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//        }
//
    }


}
