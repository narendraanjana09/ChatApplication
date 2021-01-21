package com.example.baatein.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.baatein.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.regex.Pattern;

import com.example.baatein.Dialog.RegisterDialog3;
import com.example.baatein.Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class registerActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    CircleImageView image;
    EditText name,dob,gender;
    String Name ,Number,Dob,Gender,imagelink,status="online",statusVideoLink="";
    DatePickerDialog datePickerDialog;
    Uri imageuri;
    FirebaseUser user;

    private DatabaseReference mDatabase;
    RegisterDialog3 registerDialog3;
   private SharedPreferences preferences;
    StorageReference ref;
    Intent imgdata;

    private int a=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        check();

        registerDialog3=new RegisterDialog3(registerActivity.this);

        user=FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        image=findViewById(R.id.imageview);
        name=findViewById(R.id.nametxt);
        dob=findViewById(R.id.dobtxt);
        gender=findViewById(R.id.gendertxt);
    }

    private void check() {
      Boolean  isFirstRun  = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", false);

        if (isFirstRun) {

            startActivity(new Intent(registerActivity.this, MainActivity.class));
            finish();
        }





    }

    public void showpopup(View view) {
        PopupMenu popup=new PopupMenu(this,view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.gendermenu);
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.male :gender.setText("Male");
                return true;
            case R.id.female:gender.setText("Female");
                return true;

            default: return false;
        }
    }

    public void yourMethod(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        try{
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void register(View view) {
        getData();
        if(DataisCorrect()){
        registerDialog3.startAlertDialog();
         a=1;
            user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            uploadimage();


        }    }else{

        }

    }

    private boolean DataisCorrect() {

       if(image.getDrawable() == null){
           Toast.makeText(registerActivity.this,"Choose Profile Pic",Toast.LENGTH_SHORT).show();
           return  false;
       }


        if(Name.isEmpty()){
            Toast.makeText(registerActivity.this,"Enter Name",Toast.LENGTH_SHORT).show();
            return  false;

        }
        if(Dob.isEmpty()){
            Toast.makeText(registerActivity.this,"Enter Date",Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(gender.equals("Gender")){
            Toast.makeText(registerActivity.this,"Choose Gender",Toast.LENGTH_SHORT).show();
            return  false;

        }
        String patternst="/";
        Pattern pattern=Pattern.compile(patternst);
        String[] strings=pattern.split(Dob);
        int year=Integer.parseInt(strings[2]);

        if(!namecorrect()){

            Toast.makeText(registerActivity.this,"Check Your Name",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(year>2006){

            Toast.makeText(registerActivity.this,"You Must Be 14 Year old to use this app",Toast.LENGTH_SHORT).show();
            return false;
        }





        return true;
    }

    private boolean namecorrect() {

        return ((!Name.equals(""))
                && (Name != null)
                && (Name.matches("^[ a-zA-Z]*$")));
    }

    private void uploadimage() {

     final StorageReference reference=FirebaseStorage.getInstance().getReference("usersProfile").child(user.getPhoneNumber());

        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = reference.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imagelink=downloadUri.toString();
                  

                   uploadData();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void uploadData(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(Name)
                .setPhotoUri(Uri.parse(imagelink))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });

        User users = new User(Name,Number,Dob,Gender,imagelink,status,statusVideoLink);

        mDatabase.child("users").child(user.getUid()).setValue(users);

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", true).commit();
        newActivity();

    }

    private void newActivity() {
        Intent intent=new Intent(registerActivity.this,MainActivity.class);
if(a==1) {
    registerDialog3.dismissDialog();
}
        startActivity(intent);
        finish();
        return;
    }



    private void getData() {
        Name=name.getText().toString();
        Number=user.getPhoneNumber();
        Dob=dob.getText().toString();
        Gender=gender.getText().toString();
    }


    public void pickdate(View view) {
        Calendar calendar=Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DATE);

        datePickerDialog  =new DatePickerDialog(registerActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                String  date=day+"/"+month+"/"+year;
                dob.setText(date);
            }
        },year,month,day);
        datePickerDialog.show();
    }

    public void ChooseImage(View view) {

        CropImage.activity().start(registerActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imgdata=data;
            try
            {
                imageuri=result.getUri();

                image.setImageURI(imageuri);
                image.setBorderColor(this.getResources().getColor(R.color.Main));

            }

            catch (Exception e){
                startActivity(new Intent(registerActivity.this, registerActivity.class));

            }
        }
    }
}