package com.example.baatein.Firebase;

import android.content.Context;
import android.net.Uri;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.baatein.Activities.MessagesActivity;
import com.example.baatein.Model.User;
import com.example.baatein.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class UploadVideo {
    Context context;
    ProgressBar progressBar;
    boolean isChat;
    User user;

    public UploadVideo(Context context, ProgressBar progressBar, boolean isChat, User user) {
        this.context = context;
        this.progressBar = progressBar;
        this.isChat=isChat;
        this.user=user;

    }

    private String videoLink;

    public void upload(final Uri videoUri) {
        if (videoUri != null) {


            String time=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(System.currentTimeMillis());
            final StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            final StorageReference reference =storageRef.child("/newVideo" + "/"+time);
            UploadTask uploadTask = reference.putFile(videoUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,
                            "Upload failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(context, "Upload complete",
                                    Toast.LENGTH_LONG).show();

                            Task<Uri> url=reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    videoLink=downloadUrl.toString();
                                    if(isChat){
                                    sendMessage(videoLink,videoUri);
                                }}
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
            Toast.makeText(context, "Nothing to upload",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void sendMessage(String videoLink, Uri videoUri) {
        MessagesActivity messagesActivity=new MessagesActivity();
        FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        String senderNumber=fuser.getPhoneNumber();
        String receiverNumber=user.getNumber();
        String time=messagesActivity.getCurrentTime();
        messagesActivity.sendMessage(senderNumber,receiverNumber,"video","","",videoLink,videoUri.toString(),time,context,fuser);



    }

    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {

        @SuppressWarnings("VisibleForTests") long fileSize =
                taskSnapshot.getTotalByteCount();

        @SuppressWarnings("VisibleForTests")
        long uploadBytes = taskSnapshot.getBytesTransferred();

        long progress = (100 * uploadBytes) / fileSize;


        progressBar.setProgress((int) progress);
    }


}
