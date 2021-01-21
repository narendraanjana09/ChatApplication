package com.example.baatein.ExtraClasses;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.baatein.Model.User;
import com.example.baatein.Model.UserObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GetAllContacts {
    List<UserObject> contactlist;
    List<UserObject> userslist =new ArrayList<>();
    List<UserObject> othercontactslist=new ArrayList<>();
Context context;
int a=0;
    public GetAllContacts(Context context) {
        this.context = context;

        contactlist = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

        } else {
            getContacts(context);
        }}


      public void  getContacts(Context context){



   Cursor phones= context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


int a = -1;
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            number = number.replaceAll("\\s", "");
            number = number.replaceAll("\\(", "");
            number = number.replaceAll("\\)", "");
            number = number.replaceAll("\\-", "");
            if (!number.substring(0, 1).equals("+")) {
                String num = "+91";
                number = num + number;
            }


            UserObject userObject = new UserObject(name, number);



            if (contactlist.size() != 0) {
                if (!contactlist.get(a).getPhone().equals(number)) {
                    contactlist.add(userObject);


                    a++;
                }

            } else {
                contactlist.add(userObject);



                a++;


            }
        }
    }




    public List<UserObject> getAllContacts(){
        return contactlist;
    }


    }