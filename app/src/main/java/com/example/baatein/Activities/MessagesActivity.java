package com.example.baatein.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.baatein.ExtraClasses.FileUtils;
import com.example.baatein.ExtraClasses.ToastMessage;
import com.example.baatein.ExtraClasses.VideoCompress;
import com.example.baatein.Model.Messages;
import com.example.baatein.Model.User;

import com.example.baatein.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.example.baatein.Adapter.MessageAdapter;

import com.example.baatein.Fragments.APIService;
import com.example.baatein.Notifications.Client;
import com.example.baatein.Notifications.Data;
import com.example.baatein.Notifications.MyResponse;
import com.example.baatein.Notifications.Sender;
import com.example.baatein.Notifications.Token;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesActivity extends AppCompatActivity {
private String reciverName="", receiverNumber,senderNumber;
private CircleImageView imageview;
private TextView nametxt;

  private  FloatingActionButton  addButton,addImageButton,addVoiceButton;
  private  EditText messagetxt;
  
  private FirebaseUser fuser;
  private DatabaseReference reference;


    private RecyclerView messageRecyclerView;
   private MessageAdapter messageAdapter;

   String abc="0";



   ValueEventListener seenListener;

   private List<Messages> chatList;

   APIService apiService;
  private boolean notify=false,sendhai;

    LinearLayout buttonsLayout;

    private Animation  fab_clock, fab_anticlock;



    Uri imageUri;
    String imageLink;
    User user1;
    private Uri videoUri;
    private String videoLink;
    private StorageReference videoref;
    private static final int REQUEST_CODE = 101;
    private static final int RESULT_CODE_COMPRESS_VIDEO = 3;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);



        String time=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        videoref =storageRef.child("/chats" + "/"+time);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);
        messageRecyclerView=findViewById(R.id.message_View);
        messageRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);

        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference();

        senderNumber=fuser.getPhoneNumber();

        buttonsLayout =findViewById(R.id.ButtonsLayout);

        imageview=findViewById(R.id.imageview);
        nametxt=findViewById(R.id.nametextview);


        addButton=findViewById(R.id.addButton);

        messagetxt=findViewById(R.id.messagetxt);


        Intent i = getIntent();
        user1 = (User)i.getSerializableExtra("Contact");
        receiverNumber=user1.getNumber();
        nametxt.setText(user1.getName());
        Glide.with(getApplicationContext()).load(user1.getImagelink()).into(imageview);



        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
        Query query=ref.orderByChild("number").equalTo(receiverNumber);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        User user1=dataSnapshot.getValue(User.class);
                        if(user1.getStatus().equals("online")){
                    nametxt.setTextColor((getResources().getColor(R.color.Main)));



                    }else{

                           nametxt.setTextColor((getResources().getColor(R.color.white)));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(senderNumber,receiverNumber);



        readMessage(senderNumber,receiverNumber);


        messagetxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(i2==1){
                    abc="1";
                    close();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().toLowerCase().equals("")){

                    addButton.setImageResource(R.drawable.add);
                    sendhai=false;
                }else{

                    addButton.setImageResource(R.drawable.send);
                    sendhai=true;


                }

            }
        });
    }
    public void showButtons(View view) {
        if(!sendhai){
            close();
        } else{
            notify=true;
            String msg=messagetxt.getText().toString();
            if(!msg.equals("")){
                sendMessage(senderNumber,receiverNumber,msg,"", "","","", getCurrentTime(), null, fuser);


            }else{
                Toast.makeText(MessagesActivity.this,"You Can`t Send Empty Message",Toast.LENGTH_SHORT).show();
            }
            messagetxt.setText("");
        }
    }
    public void close(){
        int ab=Integer.parseInt(abc);
        ab++;
        if(ab%2!=0){
            addButton.startAnimation(fab_clock);
            anim(-380,20);
        }else{
            addButton.startAnimation(fab_anticlock);
            anim(0,0);
        }
        abc=ab+"";
    }
    public void anim(int a,int b){



        ObjectAnimator animation = ObjectAnimator.ofFloat(buttonsLayout, "translationX", a);
        animation.setDuration(250);
        animation.start();
    }


    private void seenMessage(final String senderNumber, final String receiverNumber){
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Messages messages=snapshot.getValue(Messages.class);
                    if(messages.getReceiver().equals(senderNumber)&&messages.getSender().equals(receiverNumber)){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void sendMessage(String senderNumber, final String reciverNumber, String message, String imagelink, String imageuri, String videolink, String videouri, String Time, Context context, FirebaseUser fuser){
      DatabaseReference  reference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> data=new HashMap<>();

        data.put("sender",senderNumber);
        data.put("receiver",reciverNumber);
        data.put("message",message);
        data.put("imagelink",imagelink);
        data.put("imageuri",imageuri);
        data.put("videolink",videolink);
        data.put("videouri",videouri);
        data.put("time",Time);
        data.put("isseen",false);
        data.put("messagedeleted",false);



        reference.child("Chats").push().setValue(data);
        final Context context1;

        if(context==null){
            context1=getApplicationContext();
        }else{
            context1=context;

        }
       new ToastMessage(context1,"message sent");





        final String msg=message;

        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(notify) {
                    sendNotification(reciverNumber, user.getName(), msg,context1);
                }
                notify=false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    private void sendNotification(final String reciverNumber, final String reciverName, final String msg, final Context context1) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=tokens.orderByKey().equalTo(reciverNumber);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token=dataSnapshot.getValue(Token.class);

                    Data  data=new Data(fuser.getPhoneNumber(),reciverName,R.mipmap.ic_launcher,reciverName+": "+msg,"New Message",reciverNumber);

                    Sender sender=new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success!=1){
                                           Toast.makeText(context1,"Failed!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage(final String senderNumber, final String receiverNumber) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

        }

        chatList=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               chatList.clear();
               for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                  Messages messages=dataSnapshot.getValue(Messages.class);
                  if(messages.getSender().equals(senderNumber)&&messages.getReceiver().equals(receiverNumber)||
                          messages.getSender().equals(receiverNumber)&&messages.getReceiver().equals(senderNumber)){
                      chatList.add(messages);


                  }


                  messageAdapter=new MessageAdapter(MessagesActivity.this,chatList);
                  messageRecyclerView.setAdapter(messageAdapter);




               }


           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }





    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        String time = sdf.format(cal.getTime())+"";

        try {

            Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("hh:mm a").format(dateObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void getProfileActivity(View view) {
        Intent intent=new Intent(MessagesActivity.this, ProfileActivity.class);
        intent.putExtra("Contact",user1);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void goback(View view) {
        finish();
    }

    private void status(String status){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(fuser.getUid());
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
        reference.removeEventListener(seenListener);
        status("offline");
    }



    public void addImage(View view) {
        try{
        CropImage.activity().start(MessagesActivity.this);
    } catch (Exception e) {
            Intent intent=new Intent(MessagesActivity.this, MessagesActivity.class);
            intent.putExtra("Contact",user1);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            e.printStackTrace();
        }

    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            try
            {
                imageUri=result.getUri();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                uploadimage(bitmap);


            }

            catch (Exception e){
                Intent intent=new Intent(MessagesActivity.this, MessagesActivity.class);
                intent.putExtra("Contact",user1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        }else if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (requestCode == RESULT_CODE_COMPRESS_VIDEO) {
                if (uri != null) {
                    videoUri=uri;
                    //upload(videoUri);

                    FileUtils fileUtils=new FileUtils();
                    String inputPath=fileUtils.getPath(getApplicationContext(),data.getData());
                    compressVideo(inputPath);

                    Toast.makeText(getApplicationContext(),videoUri+"",Toast.LENGTH_SHORT).show();

                Toast.makeText(this, "Video saved to:\n" + videoUri, Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    private void compressVideo(String inputPath) {
        String time=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());
        File file = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DCIM),"/Baatein/CompressedVideo/"+time+".mp4");
        String outputPath=file.getPath();
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbar);
        VideoCompress videoCompress=new VideoCompress(getApplicationContext(),progressBar,true,user1);
        videoCompress.startCompressing(inputPath, outputPath);


    }

    private void uploadimage(Bitmap bitmap1) {
        String time=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());
        final StorageReference reference= FirebaseStorage.getInstance().getReference("chats").child(time+".png");
        Bitmap bitmap = bitmap1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
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
                    imageLink=downloadUri.toString();


                    sendMessage(senderNumber,receiverNumber,"image",imageLink,imageUri.toString(),"", "", getCurrentTime(), null, fuser);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    public void addVideo(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, RESULT_CODE_COMPRESS_VIDEO);
    }
    public void upload(final Uri videoUri) {
        if (videoUri != null) {

           /* String compressFilePath = "";
            try {
                compressFilePath = SiliCompressor.with(getApplicationContext()).compressVideo(videoUri.toString(), mDatabase.toString());

            } catch (URISyntaxException e) {
                e.printStackTrace();
                Log.v("Error", e.getMessage());
            }
*/
            String time=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(System.currentTimeMillis());
            final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            final StorageReference reference =storageRef.child("/chats" + "/"+time);
            UploadTask uploadTask = reference.putFile(videoUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessagesActivity.this,
                            "Upload failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MessagesActivity.this, "Upload complete",
                                    Toast.LENGTH_LONG).show();

                            Task<Uri> url=reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    videoLink=downloadUrl.toString();
                                    sendMessage(senderNumber,receiverNumber,"video","","",videoLink,videoUri.toString(),getCurrentTime(), null, fuser);
                                }
                            });



                        }
                    }).addOnProgressListener(
                    new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            updateProgress(taskSnapshot);

                        }
                    });
        } else {
            Toast.makeText(MessagesActivity.this, "Nothing to upload",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {

        @SuppressWarnings("VisibleForTests") long fileSize =
                taskSnapshot.getTotalByteCount();

        @SuppressWarnings("VisibleForTests")
        long uploadBytes = taskSnapshot.getBytesTransferred();

        long progress = (100 * uploadBytes) / fileSize;

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pbar);
        progressBar.setProgress((int) progress);
    }


    }



