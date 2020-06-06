package com.fyp_project.bustracking.BusModule;

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
import com.fyp_project.bustracking.UserDetail.ProfileInfo;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by csa on 3/7/2017.
 */

public class RecyclerAdapterListFeedback extends RecyclerView.Adapter<RecyclerAdapterListFeedback.MyHoder> {

    List<ProfileInfo> list;
    Context context;
    String userType = "";
    DatabaseReference getUserDatabaseReference;


    public RecyclerAdapterListFeedback(List<ProfileInfo> list, Context context, String userType) {
        this.list = list;
        this.context = context;
        this.userType = userType;
    }


    @Override
    public MyHoder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.complete_student_cardview_row, parent, false);
        MyHoder myHoder = new MyHoder(view);


        return myHoder;
    }

    @Override
    public void onBindViewHolder(MyHoder holder, int position) {
        final ProfileInfo mylist = list.get(position);

        holder.name.setText(mylist.getUser_id());
        holder.email.setText(mylist.getUser_email());
        holder.rollno.setText("Bus # "+mylist.getUser_mobile_number());
        holder.profileImage.setVisibility(View.GONE);
//        Picasso.get().load(mylist.getUser_profile_image()).into(holder.profileImage);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(userType + " Detail");
                builder.setMessage
                        (
                                          "Name : " + mylist.getUser_id() + "\n"
                                        + "Bus# : " + mylist.getUser_mobile_number() + "\n"
                                        + "Email : " + mylist.getUser_email() + "\n"
                                        + "Feedback : " + mylist.getUser_name() + "\n"
                                        + "Rating : " + mylist.getUser_account_status()
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


//        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                getUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child(userType);
//                getUserDatabaseReference.keepSynced(true);
//                getUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                            builder.setMessage("Are you sure you want to delete it?")
//                                    .setCancelable(false)
//                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//
//
//                                            Log.v("UserKey","");
//
//  //                                          String userName = list.get(which).toString();
////                                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
////                                            rootRef.child("Subject").child(subjectName).child("subject_name").removeValue();
////                                            rootRef.child("Subject").child(subjectName).child("subject_thumbnail").removeValue();
//                                        }
//                                    })
//                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//                            AlertDialog alert = builder.create();
//                            alert.show();
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//
//                return false;
//            }
//        });

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
        ImageView profileImage;
        CardView cardView;

        public MyHoder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.vname);
            email = itemView.findViewById(R.id.vemail);
            rollno = itemView.findViewById(R.id.vrollno);
            profileImage = itemView.findViewById(R.id.vimage);
            cardView = itemView.findViewById(R.id.profile_cardview);
        }
    }

}