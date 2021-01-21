package com.example.baatein.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.baatein.Model.User;
import com.example.baatein.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

   private CircleImageView imageView;
   private TextView nametxt,numbertxt,dobtxt,gendertxt;


   private FirebaseUser user;

    User user1;

   private FloatingActionButton updateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user= FirebaseAuth.getInstance().getCurrentUser();

        imageView=findViewById(R.id.profile_image);
        nametxt=findViewById(R.id.nameTextView);
        numbertxt=findViewById(R.id.numberTextView);
        dobtxt=findViewById(R.id.dobTextView);
        gendertxt=findViewById(R.id.genderTextView);

        updateButton=findViewById(R.id.updateButton);
        updateButton.hide();

        Intent i = getIntent();
        user1 = (User)i.getSerializableExtra("Contact");
        if(user1.getNumber().equals(user.getPhoneNumber())){
            updateButton.show();
            updateButton.setClickable(true);

        }


      getDetails(user1);






    }

    private void getDetails(User user1) {
        Glide.with(getApplicationContext()).load(user1.getImagelink()).into(imageView);
        if(user1.getNumber().equals(user.getPhoneNumber())){
            user1.setName(user.getDisplayName());
        }
        setData(nametxt, user1.getName());
        setData(numbertxt, user1.getNumber());
        setData(dobtxt, user1.getDob());
        setData(gendertxt, user1.getGender());
    }

    private void setData(TextView textView,String text){
        String mystring=new String(text);
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        textView.setText(content);
    }




    public void zoomProfileImage(View view) {
        Intent intent=new Intent(ProfileActivity.this,ZoomImageActivity.class);
        intent.putExtra("ImageLink",user1.getImagelink());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void UpdateActivity(View view) {
        Intent intent=new Intent(ProfileActivity.this, UpdateProfileActivity.class);
        startActivity(intent);

    }
}