package com.example.baatein.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.baatein.Model.User;
import com.example.baatein.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class UpdateProfileActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    CircleImageView imageview;
    EditText nameTxt,dobtxt,gendertxt;
    TextView editImage;
    Button updateButton;

    String imagelink;

    User userData;


    FirebaseUser user;

    Intent imgdata;
    Uri imageuri;

    private Boolean imageChanged=false;

    DatePickerDialog datePickerDialog;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imageview=findViewById(R.id.profile_image);
        nameTxt=findViewById(R.id.nameEditText);
        dobtxt=findViewById(R.id.dobEditText);
        gendertxt=findViewById(R.id.genderEditText);
        editImage=findViewById(R.id.editImage);
        updateButton=findViewById(R.id.updateButton);

        String mystring=new String("edit Image");
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        editImage.setText(content);



         user= FirebaseAuth.getInstance().getCurrentUser();
         number=user.getPhoneNumber();


            getData();
    }

    private void getData() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                userData=user;
                Glide.with(getApplicationContext()).load(user.getImagelink()).into(imageview);
                imagelink=user.getImagelink();
                nameTxt.setText(user.getName());
                dobtxt.setText(user.getDob());
                gendertxt.setText(user.getGender());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void zoomProfileImage(View view) {
        Intent intent=new Intent(UpdateProfileActivity.this,ZoomImageActivity.class);
        intent.putExtra("Number",number);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void editname(View view) {
        nameTxt.setFocusable(true);
        nameTxt.setFocusableInTouchMode(true);
        nameTxt.setClickable(true);
        nameTxt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        updateButton.setVisibility(View.VISIBLE);


    }

    public void editDob(View view) {
        Calendar calendar=Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DATE);

        datePickerDialog  =new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                String  date=day+"/"+month+"/"+year;
                dobtxt.setText(date);
                updateButton.setVisibility(View.VISIBLE);
            }
        },year,month,day);
        datePickerDialog.show();

    }

    public void editGender(View view) {
        PopupMenu popup=new PopupMenu(this,view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.gendermenu);
        updateButton.setVisibility(View.VISIBLE);
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.male :gendertxt.setText("Male");
                return true;
            case R.id.female:gendertxt.setText("Female");
                return true;

            default: return false;
        }
    }

    public void updateData(View view) {
        if(imageChanged){
            deleteImage(number);
            uploadImage(number);
        }else{
            uploadData();
        }

        Toast.makeText(UpdateProfileActivity.this,"Image Changed = "+imageChanged,Toast.LENGTH_SHORT).show();
    }

    private void uploadImage(String number) {
        final StorageReference reference=FirebaseStorage.getInstance().getReference("usersProfile").child(number);

        imageview.setDrawingCacheEnabled(true);
        imageview.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
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
        String Name=nameTxt.getText().toString();
        String Number=user.getPhoneNumber();
        String Dob=dobtxt.getText().toString();
        String Gender=gendertxt.getText().toString();
        String status="online";
        User users = new User(Name,Number,Dob,Gender,imagelink,status,"");
        DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
        userData=users;
        mDatabase.child("users").child(user.getUid()).setValue(users);

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", true).commit();
        newActivity();

    }

    private void newActivity() {
        Intent intent=new Intent(UpdateProfileActivity.this, ProfileActivity.class);
        intent.putExtra("Contact",userData);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void deleteImage(String number) {
        StorageReference photoRef = FirebaseStorage.getInstance().getReference("usersProfile").child(number);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d(TAG, "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete file");
            }
        });

    }

    public void getImage(View view) {

        CropImage.activity().start(UpdateProfileActivity.this);
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

                imageview.setImageURI(imageuri);
                imageChanged=true;
                updateButton.setVisibility(View.VISIBLE);
                imageview.setBorderColor(this.getResources().getColor(R.color.Main));

            }

            catch (Exception e){
                Intent intent=new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
                intent.putExtra("Number",number);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                e.printStackTrace();

            }
        }
    }
}