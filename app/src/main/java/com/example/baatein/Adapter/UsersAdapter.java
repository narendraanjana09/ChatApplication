package com.example.baatein.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.baatein.Activities.MessagesActivity;
import com.example.baatein.Model.Messages;
import com.example.baatein.Model.User;
import com.example.baatein.Activities.ProfileActivity;
import com.example.baatein.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import com.example.baatein.Activities.ZoomImageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
   public ArrayList<User> contactList;

    Context context;
    String time;

    String theLastMessage;
    Boolean isseen,isChat;

    public UsersAdapter(Context context, ArrayList<User> contactList, boolean isChat){
        this.contactList=contactList;
        this.context=context;
        this.isChat=isChat;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.active_users_recycler,null,false);
        RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        ViewHolder vh=new ViewHolder(view);
        return vh;
    }


    @Override
    public int getItemCount() {
        Set<User> s=new LinkedHashSet<>(contactList);
        contactList.clear();
        contactList.addAll(s);
        return contactList.size();
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String name=contactList.get(position).getName();
        holder.name.setText(name);
        final String number=contactList.get(position).getNumber();
        holder.phone.setText(number);
        Glide.with(context).load(contactList.get(position).getImagelink()).into(holder.image);
        if(isChat) {

            lastMessage(number, holder.lastMessage,holder.timeTextView,contactList.get(position));
            online(number,holder.name);
            holder.message_View.setVisibility(View.INVISIBLE);
        }else{


        }


        holder.message_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(context, MessagesActivity.class);
                intent.putExtra("Contact", contactList.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent=new Intent(context, ZoomImageActivity.class);
                    intent.putExtra("ImageLink",contactList.get(position).getImagelink());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
        });

        holder.usersRecyclerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChat) {
                    Intent intent = new Intent(context, MessagesActivity.class);
                    intent.putExtra("Contact", contactList.get(position));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    Intent intent=new Intent(context, ProfileActivity.class);
                    intent.putExtra("Contact",contactList.get(position));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });


    }

    private void online(String number, final TextView name) {
     DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
        Query query=ref.orderByChild("number").equalTo(number);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        User user1=dataSnapshot.getValue(User.class);
                        if(user1.getStatus().equals("online")){
                            name.setTextColor((context.getResources().getColor(R.color.Main)));



                        }else{

                            name.setTextColor((context.getResources().getColor(R.color.white)));

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }






    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image,message_View;
        public TextView name,phone,lastMessage,timeTextView;
        public RelativeLayout usersRecyclerLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.imageview);
            message_View=itemView.findViewById(R.id.message_View);

            name=itemView.findViewById(R.id.nametextview);
            lastMessage=itemView.findViewById(R.id.lastMessage);
            timeTextView=itemView.findViewById(R.id.timeTextView);
            phone=itemView.findViewById(R.id.numberTextView);
            usersRecyclerLayout=itemView.findViewById(R.id.usersRecyclerLayout);

        }
    }
    private void lastMessage(final String number, final TextView last_msg, final TextView time_txt, final User user){
        theLastMessage="default";
        isseen=false;

        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Messages messages=dataSnapshot.getValue(Messages.class);
                    if(messages.getReceiver().equals(fuser.getPhoneNumber())&&messages.getSender().equals(number)||
                            messages.getReceiver().equals(number)&&messages.getSender().equals(fuser.getPhoneNumber())){
                        theLastMessage=messages.getMessage();
                        time=messages.getTime();
                        if(messages.getSender().equals(fuser.getPhoneNumber())) {
                            if (messages.isIsseen()) {
                                isseen = true;

                            } else {
                                isseen = false;
                            }
                        }

                    }
                }

                switch (theLastMessage){
                    case "default":
                      contactList.remove(user);
                        notifyDataSetChanged();
                        last_msg.setText("");
                        time_txt.setText("");
                        break;

                    default:

                        last_msg.setText(theLastMessage);
                        time_txt.setText(time);

                        if(isseen){
                            time_txt.setTextColor(context.getResources().getColor(R.color.Main));
                        }else{
                            time_txt.setTextColor(context.getResources().getColor(R.color.white));
                        }
                }
                theLastMessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
