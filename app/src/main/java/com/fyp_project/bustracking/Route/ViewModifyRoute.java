package com.fyp_project.bustracking.Route;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp_project.bustracking.R;
import com.fyp_project.bustracking.UserDetail.ListStudentRegistered;
import com.fyp_project.bustracking.UserDetail.ProfileInfo;
import com.fyp_project.bustracking.UserDetail.RecyclerAdapterListStudentRegistered;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewModifyRoute extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    List<Route> list;
    RecyclerView recycle;
    SearchView searchView;
    String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_students_list_recyclerview);
//        userType = getIntent().getExtras().getString("user_type");

        Initialization();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<Route>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Route value = dataSnapshot1.getValue(Route.class);
                    Route fire = new Route();

                    String route_name = value.getRoute_name();
                    String route_number = value.getRoute_number();
                    String route_distance = value.getRoute_distance();
                    String route_time = value.getRoute_time();

                    fire.setRoute_name(route_name);
                    fire.setRoute_number(route_number);
                    fire.setRoute_distance(route_distance);
                    fire.setRoute_time(route_time);

                    list.add(fire);
                    DataloadRecyclerView();
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
        RecyclerAdapterRoute recyclerAdapterRoute = new RecyclerAdapterRoute(list, ViewModifyRoute.this);
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(recyclerAdapterRoute);

    }

    private void Initialization() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recycle = findViewById(R.id.recycle);
        searchView = findViewById(R.id.searchView);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("RouteInfo");

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

        ArrayList<Route> mylist = new ArrayList<>();

        for (Route object : list) {

            if (object.getRoute_name().toLowerCase().contains(newText.toLowerCase())) {
                mylist.add(object);
            }

            RecyclerAdapterRoute recyclerAdapterRoute = new RecyclerAdapterRoute(mylist, ViewModifyRoute.this);
            recycle.setAdapter(recyclerAdapterRoute);

        }

    }

}
