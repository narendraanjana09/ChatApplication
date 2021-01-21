package com.example.baatein.Activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.baatein.Model.User;
import com.example.baatein.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseUser;

public class WatchStatusActivity extends AppCompatActivity {
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.watch_status_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        Intent i = getIntent();
        User user = (User)i.getSerializableExtra("User");


        String videoLink=user.getStatusVideoLink();
        videoView=findViewById(R.id.statusView);



        videoView.setVideoPath(videoLink);
        videoView.start();





    }


}