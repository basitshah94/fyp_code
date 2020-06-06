package com.fyp_project.bustracking.Administrator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp_project.bustracking.R;
import com.fyp_project.bustracking.UserDetail.ProfileInfo;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by csa on 3/7/2017.
 */

public class RecyclerAdapterUserFeeRecord extends RecyclerView.Adapter<RecyclerAdapterUserFeeRecord.MyHoder> {

    List<ProfileInfo> list;
    Context context;
    String userType = "";
    DatabaseReference getUserDatabaseReference;


    public RecyclerAdapterUserFeeRecord(List<ProfileInfo> list, Context context, String userType) {
        this.list = list;
        this.context = context;
        this.userType = userType;
    }


    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_approval_cardview_row, parent, false);
        MyHoder myHoder = new MyHoder(view);
        return myHoder;
    }

    @Override
    public void onBindViewHolder(MyHoder holder, int position) {

        final ProfileInfo mylist = list.get(position);

        holder.name.setText(mylist.getUser_name() + " | " + mylist.getUser_id());
        holder.email.setVisibility(View.GONE);
        holder.rollno.setVisibility(View.GONE);

        holder.profileImage.setVisibility(View.GONE);
        holder.reject.setVisibility(View.GONE);
        holder.report.setVisibility(View.GONE);
        holder.accept.setVisibility(View.GONE);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Download the fee slip image?")
                        .setCancelable(false)
                        .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mylist.getUser_profile_image()));
                                context.startActivity(browserIntent);

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

        TextView name, email, rollno;
        Button accept, reject, report;
        ImageView profileImage;
        CardView cardView;

        public MyHoder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.vname);
            email = itemView.findViewById(R.id.vemail);
            rollno = itemView.findViewById(R.id.vrollno);
            accept = itemView.findViewById(R.id.accept_request);
            reject = itemView.findViewById(R.id.reject_request);
            report = itemView.findViewById(R.id.report_request);
            profileImage = itemView.findViewById(R.id.vimage);
            cardView = itemView.findViewById(R.id.profile_cardview);
        }
    }

}