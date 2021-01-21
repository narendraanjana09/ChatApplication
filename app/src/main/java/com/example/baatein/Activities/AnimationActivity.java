package com.example.baatein.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;


import com.example.baatein.ExtraClasses.FileUtils;
import com.example.baatein.ExtraClasses.VideoCompress;
import com.example.baatein.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AnimationActivity extends AppCompatActivity {


    String inputPath;
    VideoView videoView;
    Uri videouri;

    private StorageReference videoref;
    ProgressDialog pd;
    private static final int RESULT_CODE_COMPRESS_VIDEO = 3;

    private ProgressBar progressBar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


          progressDialog=new ProgressDialog(this);
          progressDialog.setTitle("Downloading.......");
          progressDialog.setMax(100);
          progressDialog.setCancelable(false);
          progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);


        videoView=findViewById(R.id.videoView);
        pd=new ProgressDialog(AnimationActivity.this);
        pd.setMessage("Loading Video");

        progressBar=findViewById(R.id.progressBar2);


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        videoref =storageRef.child("/videos" + "/userIntro.3gp");




      //   VideoCompress mVideoCompressor = new VideoCompress(this,progressBar, true, user1);


    }
    public void upload() {
        if (videouri != null) {
            UploadTask uploadTask = videoref.putFile(videouri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AnimationActivity.this,
                            "Upload failed: " + e.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AnimationActivity.this, "Upload complete",
                                    Toast.LENGTH_LONG).show();
                           Task<Uri> url=videoref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   Uri downloadUrl = uri;
                                   playVideo(downloadUrl);
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
            Toast.makeText(AnimationActivity.this, "Nothing to upload",
                    Toast.LENGTH_LONG).show();
        }
    }
    public void playVideo(Uri uploadSessionUri){
        pd.show();
       // String url="https://firebasestorage.googleapis.com/v0/b/baatein-30241.appspot.com/o/videos%2FuserIntro.3gp?alt=media&token=a3b56405-9bb9-4ca4-a7c2-4dbc66b50cf2";
        //Uri uri=Uri.parse(url);
      videoView.setVideoURI(uploadSessionUri);
        //String url=uploadSessionUri.toString();
        //videoView.setVideoPath(url);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                pd.dismiss();
            }
        });

    }

    public void updateProgress(UploadTask.TaskSnapshot taskSnapshot) {

        @SuppressWarnings("VisibleForTests") long fileSize =
                taskSnapshot.getTotalByteCount();

        @SuppressWarnings("VisibleForTests")
        long uploadBytes = taskSnapshot.getBytesTransferred();

        long progress = (100 * uploadBytes) / fileSize;

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setProgress((int) progress);
    }

    public void record(View view) {


//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("video/*");
//        startActivityForResult(intent, RESULT_CODE_COMPRESS_VIDEO);

//        YoYo.with(Techniques.FlipOutY)
//                .duration(1000)
//                .repeat(0)
//                .playOn(linearLayout);
//            CountryCodePicker ccp = findViewById(R.id.ccp);
//            ccp.setVisibility(View.VISIBLE);
//            YoYo.with(Techniques.FlipInX)
//                    .duration(1000)
//                    .repeat(0)
//                    .playOn(ccp);
//
//        button.setText("sendcode");
        final Handler handler=new Handler();
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder().url("https://firebasestorage.googleapis.com/v0/" +
                        "b/baatein-30241.appspot.com/o/chats%2F20200827_124730?alt=media&tok" +
                        "en=acbdbd6f-1749-40ca-829e-f6c494569ec9").build();
                Response response=null;
                try {
                    response=client.newCall(request).execute();
                    float fileSize=response.body().contentLength();

                    BufferedInputStream inputStream=new BufferedInputStream(response.body().byteStream());
                    OutputStream stream=new FileOutputStream(Environment.getExternalStorageDirectory()
                            +"/DCIM/Baatein/newDownload.mp4");
                    byte data[]=new byte[8192];
                    float total=0;
                    int read_bytes=0;


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.show();

                        }
                    });

                    while ((read_bytes=inputStream.read(data))!=-1){
                        total=total+read_bytes;
                        stream.write(data,0,read_bytes);
                        progressDialog.setProgress((int) ((total/fileSize)*100));
                    }
                    progressDialog.dismiss();
                    stream.flush();
                    stream.close();
                    response.body().close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();


    }






    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_CODE_COMPRESS_VIDEO) {
            if (resultCode == RESULT_OK) {
                FileUtils fileUtils=new FileUtils();
                 inputPath=fileUtils.getPath(getApplicationContext(),data.getData());
                compressVideo(inputPath);
                File file=new File(inputPath);
                int duration = MediaPlayer.create(getApplicationContext(), Uri.fromFile(file)).getDuration();

                 String a=String.format("%d:%d:%d",
                         TimeUnit.MILLISECONDS.toHours(duration),
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
                );
                System.out.println("Duration = "+a+" existtttttttttttttttttttttttttttttttt");


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }}

    private void compressVideo(String inputPath) {
        String time=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());
        File file = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DCIM),"/Baatein/CompressedVideo/"+time+".mp4");
        String outputPath=file.getPath();
       // VideoCompress videoCompress=new VideoCompress(getApplicationContext(),progressBar, true, user1);

        //videoCompress.startCompressing(inputPath, outputPath);


    }


}
