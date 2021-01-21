package com.example.baatein.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.bumptech.glide.Glide;

import com.example.baatein.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseUser;

public class ZoomImageActivity extends AppCompatActivity {
FirebaseUser user;
    PhotoView imageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imageview   =  findViewById(R.id.photo_view);



        Bundle extras = getIntent().getExtras();

            String imagelink=extras.getString("ImageLink");
            Glide.with(ZoomImageActivity.this).load(imagelink).into(imageview);



    }


}