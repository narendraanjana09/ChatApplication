package com.example.baatein.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;

import com.example.baatein.ExtraClasses.ToastMessage;
import com.example.baatein.Model.User;
import com.example.baatein.R;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.baatein.Fragments.FragmentMessages;
import com.example.baatein.Fragments.FragmentUsers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {



    CircleImageView imageView;
    String number;
    FirebaseUser user;

    User userData;

 private   SharedPreferences preferences;
 private   String FirstTime;

 FFmpeg fFmpeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        try {
            loadFFmpegLibrary();
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }

        imageView=findViewById(R.id.userprofile);


        user= FirebaseAuth.getInstance().getCurrentUser();
        number=user.getPhoneNumber();



        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                userData=user;
                Glide.with(getApplicationContext()).load(user.getImagelink()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        TabLayout tabLayout=findViewById(R.id.tab_layout);
        ViewPager viewPager=findViewById(R.id.view_pager);


        ViewPagerAdapter   viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentMessages(),"Messages");
        viewPagerAdapter.addFragment(new FragmentUsers(),"Users");
        viewPagerAdapter.addFragment(new FragmentUsers(),"Notifications");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        getPermission();


    }

    public void loadFFmpegLibrary() throws FFmpegNotSupportedException {
        if(fFmpeg==null){
            fFmpeg=FFmpeg.getInstance(this);


            fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    new ToastMessage(getApplicationContext(),"Library Load Failed");

                }

                @Override
                public void onSuccess() {
                    new ToastMessage(getApplicationContext(),"Library Loaded");
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }
            });
        }
    }


    private void getPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS},1);











        }



    }

    public void getProfileActivity(View view) {
        Intent intent=new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra("Contact",userData);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.developerinfo :startActivity(new Intent(MainActivity.this,DeveloperActivity.class));
                return true;
            case R.id.anim :startActivity(new Intent(MainActivity.this,AnimationActivity.class));
                return true;


            default: return false;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private  ArrayList<String>  titles;
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);

        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    public void showpopup(View view) {
        PopupMenu popup=new PopupMenu(this,view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.settingsmenu);
        popup.show();
    }
    private void status(String status){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        HashMap<String,Object> data=new HashMap<>();
        data.put("status",status);

        reference.updateChildren(data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}