package com.fyp_project.bustracking.Route;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp_project.bustracking.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by csa on 3/7/2017.
 */

public class RecyclerAdapterRoute extends RecyclerView.Adapter<RecyclerAdapterRoute.MyHoder> {

    List<Route> list;
    Context context;
    DatabaseReference getUserDatabaseReference;


    public RecyclerAdapterRoute(List<Route> list, Context context) {
        this.list = list;
        this.context = context;

    }


    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.complete_student_cardview_row, parent, false);
        MyHoder myHoder = new MyHoder(view);


        return myHoder;
    }

    @Override
    public void onBindViewHolder(MyHoder holder, int position) {
        final Route mylist = list.get(position);

        holder.name.setText(mylist.getRoute_name());
        holder.rollno.setText("Route # " + mylist.getRoute_number());
//        Picasso.get().load(mylist.getUser_profile_image()).into(holder.profileImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Route Detail");
                builder.setMessage
                        (
                                "Route Name : " + mylist.getRoute_name() + "\n"
                                        + "Route Number : " + mylist.getRoute_number() + "\n"
                                        + "Route Distance : " + mylist.getRoute_distance() + "\n"
                                        + "Route Time : " + mylist.getRoute_time() + "\n"
                        )
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });


    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (list.size() == 0) {


                arr = 0;

            } else {

                arr = list.size();
            }


        } catch (Exception e) {


        }

        return arr;

    }

    class MyHoder extends RecyclerView.ViewHolder {
        TextView name, hide, rollno;
        ImageView profileImage;
        CardView cardView;

        public MyHoder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.vname);
            hide = itemView.findViewById(R.id.vemail);
            rollno = itemView.findViewById(R.id.vrollno);
            profileImage = itemView.findViewById(R.id.vimage);
            profileImage.setVisibility(View.GONE);
            hide.setVisibility(View.GONE);
            cardView = itemView.findViewById(R.id.profile_cardview);
        }
    }

}