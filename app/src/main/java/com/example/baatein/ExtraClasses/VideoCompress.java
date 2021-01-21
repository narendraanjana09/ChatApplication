package com.example.baatein.ExtraClasses;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.ProgressBar;


import com.abedelazizshe.lightcompressorlibrary.CompressionListener;
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor;
import com.abedelazizshe.lightcompressorlibrary.VideoQuality;
import com.example.baatein.Firebase.UploadVideo;
import com.example.baatein.Model.User;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;

import java.io.File;


public class VideoCompress extends Thread{

    ProgressBar progressBar;

    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    public static final int NONE = 3;
    public static final int RUNNING = 4;

    FFmpeg fFmpeg;

    private final Context context;

    private boolean isFinished;
    private int status = NONE;
    private String errorMessage = "Compression Failed!";

    boolean isChat;
    User user;

    public VideoCompress(Context context, ProgressBar progressBar, boolean isChat, User user) {
        this.context = context;
        this.progressBar=progressBar;
        this.isChat=isChat;
        this.user=user;

    }

    public void startCompressing(String inputPath, String outputPath) {
        if (inputPath == null || inputPath.isEmpty()) {
            status = NONE;
           return;
        }


        compressVideo( inputPath,outputPath);

    }

    private void compressVideo(final String inputPath, final String outputPath) {


        VideoCompressor.start(inputPath, outputPath, new CompressionListener() {
            @Override
            public void onStart() {
                new ToastMessage(context,"Compression Started");
            }

            @Override
            public void onSuccess() {
                new ToastMessage(context,"video Compressed");

                System.out.println(outputPath);


                File file=new File(outputPath);
                int msec = MediaPlayer.create(context, Uri.fromFile(file)).getDuration();
                System.out.println("Duration = "+msec);

                status = 1;
                System.out.println(file.getTotalSpace());
                UploadVideo uploadVideo=new UploadVideo(context,progressBar,isChat,user);
                uploadVideo.upload( Uri.fromFile(file));


            }

            @Override
            public void onFailure(String failureMessage) {
                new ToastMessage(context,"Compression Failed"+failureMessage);

                System.out.println(failureMessage);
            }

            @Override
            public void onProgress(float v) {
                int progress=(int) v;
                progressBar.setProgress(progress);

                // Update UI with progress value

            }

            @Override
            public void onCancelled() {
                // On Cancelled
            }
        }, VideoQuality.MEDIUM, false, true);
    }






    public boolean isDone() {
        return status == SUCCESS || status == NONE;
    }

}
