package com.fyp_project.bustracking.BusModule;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp_project.bustracking.R;
import com.fyp_project.bustracking.UserDetail.ProfileInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListUserFeedback extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    List<ProfileInfo> list;
    RecyclerView recycle;
    SearchView searchView;
    String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_students_list_recyclerview);

        userType = getIntent().getExtras().getString("user_type");
        Initialization();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<ProfileInfo>();
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot2.getChildren()) {

                        ProfileInfo value = dataSnapshot1.getValue(ProfileInfo.class);
                        ProfileInfo fire = new ProfileInfo();

                        String user_bus = dataSnapshot1.child("bus_number").getValue().toString();
                        String user_driver_name = dataSnapshot1.child("driver_name").getValue().toString();
                        String user_email = value.getUser_email();
                        String user_feedback = dataSnapshot1.child("user_feedback").getValue().toString();
                        String user_rating = dataSnapshot1.child("user_rating").getValue().toString();

                        fire.setUser_mobile_number(user_bus);
                        fire.setUser_id(user_driver_name);
                        fire.setUser_email(user_email);
                        fire.setUser_name(user_feedback);
                        fire.setUser_account_status(user_rating);

                        list.add(fire);
                        DataloadRecyclerView();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

    }

    private void DataloadRecyclerView() {

        recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        RecyclerAdapterListFeedback recyclerAdapter = new RecyclerAdapterListFeedback(list, ListUserFeedback.this, userType);
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapter);

    }

    private void Initialization() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recycle = findViewById(R.id.recycle);
        searchView = findViewById(R.id.searchView);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(userType.replaceAll("\\s+", ""));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            //       dialogPromptChoice();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchUser(newText);
                    return true;
                }
            });

        }
    }

    private void searchUser(String newText) {

        ArrayList<ProfileInfo> mylist = new ArrayList<>();

        for (ProfileInfo object : list) {

            if (object.getUser_id().toLowerCase().contains(newText.toLowerCase())) {
                mylist.add(object);
            }

            RecyclerAdapterListFeedback recyclerAdapterListStudentRegistered = new RecyclerAdapterListFeedback(mylist, ListUserFeedback.this,userType);
            recycle.setAdapter(recyclerAdapterListStudentRegistered);

        }

    }

}