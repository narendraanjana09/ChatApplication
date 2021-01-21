package com.example.baatein.Fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.baatein.Adapter.UsersAdapter;
import com.example.baatein.Activities.AllContactActivity;

import com.example.baatein.ExtraClasses.GetAllContacts;

import com.example.baatein.Model.User;
import com.example.baatein.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import com.example.baatein.Model.UserObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class FragmentUsers extends Fragment {
    private RecyclerView recyclerView;
    private UsersAdapter recyclerViewAdapter;

    ArrayList<User> activeUserslist=new ArrayList<>();;



    FloatingActionButton getAllContacts,show_fab,searchbtn,refreshbtn;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;

    TextView allcontact,searchtxt,refreshtxt;
    ProgressBar pb;

    Boolean isOpen = false;
    View view1;
    Context context;

    ImageView cancelsearch;
    EditText searchEditText;

    LinearLayout searchLayout;



    ArrayList<UserObject> allContacts=new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_users, container, false);
       this.view1=view;
       context=view.getContext();


        GetAllContacts gi=new GetAllContacts(context);
        allContacts.clear();
        allContacts.addAll(gi.getAllContacts());


        Boolean  isFirstRun  = context.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", false);

        recyclerView=view1.findViewById(R.id.UsersRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);


        pb=view.findViewById(R.id.pbar);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isOpen)
                   anim();
                return false;
            }
        });
       getAllContacts=view.findViewById(R.id.allContactsButton);
        show_fab=view.findViewById(R.id.addbtn);

        searchbtn=view.findViewById(R.id.search_button);
        refreshbtn=view.findViewById(R.id.refreshButton);

        searchtxt=view.findViewById(R.id.searchtxt);
        refreshtxt=view.findViewById(R.id.refreshtxt);
        allcontact=view.findViewById(R.id.getallcontatctstxt);

        cancelsearch=view.findViewById(R.id.cancelSearch);
        searchEditText=view.findViewById(R.id.searchtext);

        searchLayout=view.findViewById(R.id.searchlayout);

        fab_close = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_rotate_anticlock);

        getUsersLists();


       getAllContacts.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               anim();
               allContact();


           }
       });
        show_fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                anim();
                if(isOpen){
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            anim();

                        }
                    }, 3000);
                }

            }
        });


        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsersLists();
            }
        });
        cancelsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelsearch(view);
            }
        });


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchusers(editable.toString());
            }
        });





        return view;
    }



    private void getUsersLists() {



        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               activeUserslist.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        User post = snapshot.getValue(User.class);
                        for(UserObject contact:allContacts){
                            if(post.getNumber().equals(contact.getPhone())){
                                post.setName(contact.getName());
                            activeUserslist.add(post);
                            }
                        }



                    }

                    recyclerViewAdapter=new UsersAdapter(view1.getContext(),activeUserslist,false);
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
        public ArrayList<User> getActiveUserslist(){
        return activeUserslist;
        }

    private void searchusers(String s) {
        ArrayList<User> searchuser=new ArrayList<>();
        for(User activeUsers:activeUserslist){
            if(activeUsers.getName().toLowerCase().contains(s.toLowerCase())||
            activeUsers.getNumber().contains(s.toLowerCase())
            ){
                searchuser.add(activeUsers);
            }
        }  recyclerViewAdapter=new UsersAdapter(view1.getContext(),searchuser,false);
        recyclerView.setAdapter(recyclerViewAdapter);

    }

    public void search() {

        anim(120);
        searchLayout.setVisibility(View.VISIBLE);


    }
    public void anim(int mt){

        ObjectAnimator animation = ObjectAnimator.ofFloat(recyclerView, "translationY", mt);
        animation.setDuration(500);
        animation.start();

    }

    public void cancelsearch(View view) {

        anim(0);
        searchLayout.setVisibility(View.INVISIBLE);
        searchEditText.setText("");







    }



    private void allContact() {
        startActivity(new Intent(view1.getContext(), AllContactActivity.class));
    }




    private void anim(){
    if (isOpen) {
        searchbtn.startAnimation(fab_close);
        searchtxt.startAnimation(fab_close);
        getAllContacts.startAnimation(fab_close);
        refreshbtn.startAnimation(fab_close);
        allcontact.startAnimation(fab_close);
        refreshtxt.startAnimation(fab_close);
        show_fab.startAnimation(fab_anticlock);
        getAllContacts.setClickable(false);
        refreshbtn.setClickable(false);
        searchbtn.setClickable(false);


        isOpen = false;
    } else {
        searchbtn.startAnimation(fab_open);
        searchtxt.startAnimation(fab_open);
        getAllContacts.startAnimation(fab_open);
        refreshtxt.startAnimation(fab_open);
        allcontact.startAnimation(fab_open);
        show_fab.startAnimation(fab_clock);
        getAllContacts.setClickable(true);
        refreshbtn.startAnimation(fab_open);
        searchbtn.setClickable(true);
        refreshbtn.setClickable(true);
        isOpen = true;
    }



}




}