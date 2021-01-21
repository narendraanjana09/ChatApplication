package com.example.baatein.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.example.baatein.Adapter.StatusAdapter;
import com.example.baatein.ExtraClasses.GetAllContacts;
import com.example.baatein.Model.ActiveUsers;
import com.example.baatein.Model.Messages;
import com.example.baatein.Model.User;
import com.example.baatein.Model.UserObject;
import com.example.baatein.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.example.baatein.Adapter.UsersAdapter;
import com.example.baatein.Notifications.Token;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class FragmentMessages extends Fragment {


    private RecyclerView recyclerView,statusRecyclerView;
    private UsersAdapter recyclerViewAdapter;
    private StatusAdapter statusAdapter;
    private RecyclerView.LayoutManager recyclerviewlayoutManager;
    private RecyclerView.LayoutManager statusrecyclerviewlayoutManager;
    private ArrayList<User> activeUsersList=new ArrayList<>();

    FirebaseUser fuser;

    Context context;



    ArrayList<User> statusList;
    ArrayList<UserObject> allContacts=new ArrayList<>();
    List<String> chats=new LinkedList<>();
    ArrayList<User> list=new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_messages, container, false);
        context=view.getContext();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        statusList=new ArrayList<>();
        getuserData();
        GetAllContacts gi=new GetAllContacts(context);
        allContacts.clear();
        allContacts.addAll(gi.getAllContacts());

        statusRecyclerView=view.findViewById(R.id.statusRecyclerView);

        statusrecyclerviewlayoutManager=new LinearLayoutManager(context,recyclerView.HORIZONTAL,false);
        statusRecyclerView.setLayoutManager(statusrecyclerviewlayoutManager);
        recyclerView = view.findViewById(R.id.MessagesRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerviewlayoutManager=new LinearLayoutManager(context,recyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(recyclerviewlayoutManager);








        getUsers();


        updateToken(FirebaseInstanceId.getInstance().getToken());


        return view;
    }

    private void getChats() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chats.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Messages messages=snapshot.getValue(Messages.class);
                        if(messages.getSender().equals(fuser.getPhoneNumber())){
                            if(!chats.contains(messages.getReceiver())){
                            chats.add(messages.getReceiver());
                            }
                        }
                        if(messages.getReceiver().equals(fuser.getPhoneNumber())){
                            if(!chats.contains(messages.getSender())){
                            chats.add(messages.getSender());
                        }}



                    }



                    list.clear();

                    User user11 = null;
                       for(String number:chats){
                           Boolean exist=false;
                           for(User user:activeUsersList){
                               if(number.equals(user.getNumber())){
                                   user11=user;
                                   exist=true;
                                   break;
                               }
                       }

                           if(!exist){
                               Toast.makeText(context,"number nhi hai",Toast.LENGTH_SHORT).show();
                               getUnknownUserDetail(number);


                           }else{
                               if(list.size()!=0){
                               if(!list.get(0).getNumber().contains(user11.getNumber())){


                               list.add(0,user11);
                           }
                               }else{
                                   list.add(0,user11);
                               }
                           }
                   }



                    recyclerViewAdapter=new UsersAdapter(context,list,true);

                    recyclerView.setAdapter(recyclerViewAdapter);




                }}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        reference.addValueEventListener(postListener);
    }




    private void getUnknownUserDetail(final String number) {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        Query query=reference.orderByChild("number").equalTo(number);
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if(datasnapshot.exists()){

                    for(DataSnapshot snapshot:datasnapshot.getChildren()){
                        User userInfo =snapshot.getValue(User.class);
                        userInfo.setName(number);
                        Toast.makeText(context,"numbe"+ userInfo.getName(),Toast.LENGTH_SHORT).show();

                            list.add(0, userInfo);

                       recyclerViewAdapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);

    }

    private void getUsers() {


        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activeUsersList.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        User post = snapshot.getValue(User.class);
                        for(UserObject contact:allContacts){
                            if(post.getNumber().equals(contact.getPhone())){
                                post.setName(contact.getName());
                                activeUsersList.add(post);
                            }
                        }



                    }
                    getChats();
                    getStatus();



                }}

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        reference.addListenerForSingleValueEvent(postListener);




    }

    private void getStatus() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if(datasnapshot.exists()){
                    for(DataSnapshot snapshot:datasnapshot.getChildren()){
                        User user=snapshot.getValue(User.class);
                        for(int i=0;i<activeUsersList.size();i++){
                            User user1=activeUsersList.get(i);
                            if(user.getNumber().equals(user1.getNumber())){
                                if(!user.getStatusVideoLink().equals("")){

                                    for(int j=0;j<statusList.size();j++) {
                                        if(!statusList.get(j).getNumber().equals(user.getNumber())){
                                        statusList.add(1, user);
                                        statusAdapter.notifyItemInserted(1);
                                        }
                                    }
                                    }



                                }

                            }
                        }
                    }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void getuserData() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                        User user = snapshot.getValue(User.class);
                        user.setName("You");
                    if(statusList.size()!=0) {
                        statusList.remove(0);
                        statusList.add(0, user);

                    }else{
                        statusList.add(0, user);
                    }
                }
                statusAdapter=new StatusAdapter(context,statusList);
                statusRecyclerView.setAdapter(statusAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void updateToken(String token){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        reference.child(fuser.getPhoneNumber()).setValue(token1);
    }












}