package com.example.baatein.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.baatein.Adapter.AllContactsAdapter;


import com.example.baatein.ExtraClasses.GetAllContacts;
import com.example.baatein.ExtraClasses.ToastMessage;
import com.example.baatein.Model.User;
import com.example.baatein.Model.UserObject;
import com.example.baatein.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public class AllContactActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AllContactsAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerviewlayoutManager;
    ArrayList<UserObject> allContacts=new ArrayList<>();
    ArrayList<UserObject> notactiveUserslist;


LinearLayout searchlayout;
ImageView searchButton;
EditText searchtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contact);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        recyclerView=findViewById(R.id.allContctsRecycler);
        recyclerView.setHasFixedSize(false);
        recyclerviewlayoutManager=new LinearLayoutManager(AllContactActivity.this,recyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(recyclerviewlayoutManager);
        searchlayout=findViewById(R.id.searchlayout);
        searchButton=findViewById(R.id.search_button);

        GetAllContacts gi=new GetAllContacts(getApplicationContext());
        allContacts.clear();
        allContacts.addAll(gi.getAllContacts());
        notactiveUserslist=new ArrayList<>();
        getUsersLists();

        searchtxt=findViewById(R.id.searchtxt);

        searchtxt.addTextChangedListener(new TextWatcher() {
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





    }

    private void getUsersLists() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notactiveUserslist=new ArrayList<>();
                notactiveUserslist.clear();
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        User post = snapshot.getValue(User.class);
                        for(int i=0;i<allContacts.size();i++){
                            UserObject contact=allContacts.get(i);
                            if(post.getNumber().equals(contact.getPhone())){
                                   allContacts.remove(i);
                                    System.out.println(contact.getPhone()+" existttttttttttttttttttttttttt");
                            }
                        }



                    }
                    new ToastMessage(getApplicationContext(),allContacts.size()+"");

                    recyclerViewAdapter=new AllContactsAdapter(AllContactActivity.this,allContacts);
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

    private void searchusers(String s) {
        ArrayList<UserObject> searchuser=new ArrayList<>();
        for(UserObject userObject:allContacts){
            if(userObject.getName().toLowerCase().contains(s.toLowerCase())||
                    userObject.getPhone().contains(s)){
                searchuser.add(userObject);
            }
        }
        recyclerViewAdapter=new AllContactsAdapter(AllContactActivity.this,searchuser);
        recyclerView.setAdapter(recyclerViewAdapter);
    }



    public void yourMethod(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        try{
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void goback(View view) {
        finish();
    }

    public void search(View view) {
        anim(120);

        searchButton.setVisibility(View.INVISIBLE);

    }
    public void anim(int mt){

        ObjectAnimator animation = ObjectAnimator.ofFloat(recyclerView, "translationY", mt);
        animation.setDuration(500);
        animation.start();

    }

    public void cancelsearch(View view) {

        anim(0);
     searchtxt.setText("");
     yourMethod(view);


        searchButton.setVisibility(View.VISIBLE);



    }
}