package com.example.baatein.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baatein.Activities.WatchStatusActivity;
import com.example.baatein.Activities.ZoomImageActivity;
import com.example.baatein.AdapterViewHolder.StatusViewHolder;
import com.example.baatein.Model.User;
import com.example.baatein.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.baatein.Adapter.MessageAdapter.getThumblineImage;

public class StatusAdapter extends RecyclerView.Adapter<StatusViewHolder> {

   ArrayList<User> statusList;
   FirebaseUser fuser;
    private final int mainUser=0;
    private final int usersStatus=1;


    Context context;
    public StatusAdapter(Context context, ArrayList<User> statusList){
        this.context=context;
        this.statusList=statusList;


        fuser= FirebaseAuth.getInstance().getCurrentUser();

    }



    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view;
        if(viewType==mainUser){
            view=layoutInflater.inflate(R.layout.status_item1,parent,false);
        }else{
            view=layoutInflater.inflate(R.layout.status_item,parent,false);
        }


        return new StatusViewHolder(view);
    }
    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return mainUser;
        }else{
            return usersStatus;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, final int position) {

   if(position==0){
       if(statusList.get(position).getStatusVideoLink().equals("")) {
           Glide.with(context).load(statusList.get(position).getImagelink()).into(holder.getImageView());

       }else{
           try {
               holder.getImageView().setImageBitmap(getThumblineImage(statusList.get(position).getStatusVideoLink()));
           } catch (Throwable throwable) {
               throwable.printStackTrace();
           }
       }
   }else {

       try {
           holder.getImageView().setImageBitmap(getThumblineImage(statusList.get(position).getStatusVideoLink()));
       } catch (Throwable throwable) {
           throwable.printStackTrace();
       }

       holder.getNametxt().setText(statusList.get(position).getName());




           holder.getStatus_layout().setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent = new Intent(context, WatchStatusActivity.class);
                   intent.putExtra("User", statusList.get(position));
                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   context.startActivity(intent);
               }
           });

   }

    }

    @Override
    public int getItemCount() {


        return statusList.size();
    }



}
