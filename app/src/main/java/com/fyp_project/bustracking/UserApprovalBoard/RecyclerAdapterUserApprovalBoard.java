package com.fyp_project.bustracking.UserApprovalBoard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fyp_project.bustracking.R;
import com.fyp_project.bustracking.UserDetail.ProfileInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by csa on 3/7/2017.
 */

public class RecyclerAdapterUserApprovalBoard extends RecyclerView.Adapter<RecyclerAdapterUserApprovalBoard.MyHoder> {

    List<ProfileInfo> list;
    Context context;
    String userType = "";
    DatabaseReference getUserDatabaseReference;


    public RecyclerAdapterUserApprovalBoard(List<ProfileInfo> list, Context context, String userType) {
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

        if (!mylist.getUser_account_status().equals("nil")) {
            holder.name.setText(mylist.getUser_name());
            holder.email.setText(mylist.getUser_email());
            holder.rollno.setText(mylist.getUser_id());
            Picasso.get().load(mylist.getUser_profile_image()).into(holder.profileImage);


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(userType + " Detail");
                    builder.setMessage
                            (
                                    "Name : " + mylist.getUser_name() + "\n"
                                            + "ID : " + mylist.getUser_id() + "\n"
                                            + "Account Status : " + mylist.getUser_account_status() + "\n"
                                            + "Email : " + mylist.getUser_email() + "\n"
                                            + "Mobile # " + mylist.getUser_mobile_number() + "\n"
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


            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("User/" + userType);
                    getUserDatabaseReference.orderByChild("user_email").equalTo(mylist.getUser_email()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    Log.v("Key ", " " + d.getRef().getKey());
                                    String keyValue = d.getRef().getKey();

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                            .child("User").child(userType).child(keyValue).child("user_account_status");
                                    ref.setValue("true");


                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("User/" + userType);
                    getUserDatabaseReference.orderByChild("user_email").equalTo(mylist.getUser_email()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    Log.v("Key ", " " + d.getRef().getKey());
                                    String keyValue = d.getRef().getKey();

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                            .child("User").child(userType).child(keyValue).child("user_account_status");
                                    ref.setValue("nil");


                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

            holder.report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    getUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("User/" + userType);
                    getUserDatabaseReference.orderByChild("user_email").equalTo(mylist.getUser_email()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    Log.v("Key ", " " + d.getRef().getKey());
                                    String keyValue = d.getRef().getKey();

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                            .child("User").child(userType).child(keyValue).child("user_account_status");
                                    ref.setValue("nil");


                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });
        } else {

            holder.cardView.setVisibility(View.GONE);
        }
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