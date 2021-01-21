package com.example.baatein.ExtraClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class GetImage {

  public void  getimage( final ImageView view, String number){
      StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(number);
      try{
          final File localFile=File.createTempFile(number,null);
          storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                  Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                  view.setImageBitmap(bitmap);



              }
          }).addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  System.out.println("ERror Occured");

              }
          });



      } catch (Exception e) {
          e.printStackTrace();
      }


  }



}
