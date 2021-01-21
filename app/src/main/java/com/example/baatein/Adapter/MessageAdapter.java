package com.example.baatein.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.baatein.ExtraClasses.FileUtils;
import com.example.baatein.Model.Messages;
import com.example.baatein.R;

import com.example.baatein.Activities.ZoomImageActivity;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static androidx.core.app.ActivityCompat.requestPermissions;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {
    private final int MSG_TYPE_LEFT=0;
private final int MSG_TYPE_RIGHT=1;
Boolean righthai,imagehai;
    private Context context;
    Messages deleteMessage;
    private List<Messages> chatList;
    ImageView image1;
    FirebaseUser user;
    long downloadID;
    ImageView imageView,downloadButton;
    VideoView videoView;

    int position;

    Messages chats;

    boolean downloadHai=false;
    FileUtils file=new FileUtils();

    Uri videoUri;


    public MessageAdapter(Context context, List<Messages> chat){
        this.context=context;
        this.chatList=chat;





    }




    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==MSG_TYPE_RIGHT){
            view= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
        }else{
             view= LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            RecyclerView.LayoutParams lp=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);

        }

        return new MessageAdapter.ViewHolder(view);
    }

    private void setLayoutParam(int a, int b, View view, boolean isCenter){
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(a,b);
        if(isCenter){
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        }
        view.setLayoutParams(layoutParams);
    }
    private void setVideo(final VideoView video, String path) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(220 * 2, 280 * 2);
        video.setLayoutParams(layoutParams);
        video.setVideoPath(path);
        setController(video);

    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder holder, final int position) {

        final Messages messages=chatList.get(position);
// Toast.makeText(context,"Right hai",Toast.LENGTH_SHORT).show();


        holder.timeTextView.setText(messages.getTime());

        if (getItemViewType(position) == MSG_TYPE_RIGHT) {

            if (messages.isIsseen()) {
                holder.timeTextView.setTextColor(context.getResources().getColor(R.color.Main));
            } else {
                holder.timeTextView.setTextColor(context.getResources().getColor(R.color.white));
            }

        }
        if (messages.isMessagedeleted()) {
            holder.showMessage.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.showMessage.setTextColor(context.getResources().getColor(R.color.Main));
        }

            holder.showMessage.setText(messages.getMessage());

        if (!righthai) {


                  if(!messages.getVideolink().equals("")){
                      holder.showMessage.setText("");
                      if(messages.getVideolink().substring(0,5).equals("https")){

                  try {
                      RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(
                              ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                      holder.image.setLayoutParams(layoutParams);
                      holder.image.setImageBitmap(getThumblineImage(messages.getVideolink()));
                      setLayoutParam(100*2,100*2,holder.downloadButton, true);
                      holder.downloadButton.setImageDrawable(context.getResources().getDrawable(R.drawable.downloadicon));
                      downloadHai=true;
                   } catch (Throwable throwable) {
                throwable.printStackTrace();
                    }
                      }else{
                          System.out.println("ye chlaaaa existtttttttttttttttttttttttttttt");

                          holder.downloadButton.setImageDrawable(null);
                          setVideo(holder.videoView,messages.getVideolink());
                          downloadHai=false;
                      }

        }
            if (!messages.getImagelink().equals("")) {
                imagehai = true;
                holder.showMessage.setText("");

                String imagelink = messages.getImagelink();

                Glide.with(context).load(imagelink).into(holder.image);
            }


            }else{


            if(!messages.getVideouri().equals("")){

                holder.showMessage.setText("");
                FileUtils fileUtils=new FileUtils();
                setVideo(holder.videoView,fileUtils.getPath(context,Uri.parse(messages.getVideouri())));





            }
            if (!messages.getImagelink().equals("")) {
                imagehai = true;
                holder.showMessage.setText("");

               Uri imageUri=Uri.parse(messages.getImageuri());
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                    holder.image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }



            }


        }


        if(!righthai){
            holder.downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Glide.with(context)
                            .load(context.getResources().getDrawable(R.drawable.loading))
                            .into(holder.downloadButton);

                    downloadVideo(messages,holder.videoView,holder.image,holder.downloadButton,position,holder.progressBar);

                }
            });
        }

        final String finalImagelink = messages.getImagelink();

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, ZoomImageActivity.class);
                    intent.putExtra("ImageLink", finalImagelink);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });


            if (!righthai) {

                holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        image1 = holder.image;
                        showpopup(holder.image, false);
                        return false;
                    }
                });

            } else {
                holder.chatRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {


                        deleteMessage = messages;
                        showpopup(holder.image, true);
                        return false;
                    }
                });
                holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        deleteMessage = messages;
                        showpopup(holder.image, true);
                        return false;
                    }
                });


            }





    }

    private void setController(VideoView videoView) {
       MediaController mediaController=new MediaController(context);
        videoView.setMediaController(mediaController);
            mediaController.setAnchorView(videoView);
    }


    private void videoDownloaded(final Messages chat, final String videoPath, final int position){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Chats").orderByChild("time").equalTo(chat.getTime());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Messages messages=snapshot.getValue(Messages.class);
                        if(messages.getMessage().equals(chat.getMessage())){
                            if(messages.getSender().equals(chat.getSender())&&
                                    messages.getReceiver().equals(chat.getReceiver())&&
                                    messages.getTime().equals(chat.getTime()))
                            {
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("videolink",videoPath);
                                snapshot.getRef().updateChildren(hashMap);
                                notifyItemChanged(position);

                            }

                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }
    private void downloadVideo(final Messages messages, final VideoView videoView,
                               final ImageView imageView, final ImageView downloadImage,
                               int position, final ProgressBar progressBar) {
        final ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("Downloading.......");
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        setLayoutParam(ViewGroup.LayoutParams.MATCH_PARENT,160,progressBar,false);
        final String time=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());
        final Handler handler=new Handler();
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder().url(messages.getVideolink()).build();
                Response response=null;
                try {
                    response=client.newCall(request).execute();
                    float fileSize=response.body().contentLength();

                    BufferedInputStream inputStream=new BufferedInputStream(response.body().byteStream());
                    File file=new File(Environment.getExternalStorageDirectory()
                            +"/DCIM/Baatein/Video/"+time+".mp4");
                    OutputStream stream=new FileOutputStream(file);
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
                    System.out.println(file.getPath()+" ttt");
                   // setVideo(videoView,stream.toString());
                    //imageView.setImageBitmap(null);
                    //downloadImage.setImageBitmap(null);
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
    BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (downloadID == id) {
                Toast.makeText(context, "Download Completed"+videoUri, Toast.LENGTH_SHORT).show();
                // imageView.setImageBitmap(null);
                downloadButton.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
                System.out.println(videoUri+" existtttttttttttttttttttttttttttt");
                videoDownloaded(chats,videoUri.toString(),position);



            }
        }
    };
    private void downloadvideo(Messages messages, VideoView videoView,
                               ImageView imageView, ImageView downloadImage,
                               int position, final ProgressBar progressBar) {
        this.position=position;
        this.chats=messages;
        this.videoView=videoView;
        this.imageView=imageView;
        this.downloadButton=downloadImage;
        context.registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        String time=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(messages.getVideolink()));
        request.setDescription("download");
        request.setTitle(time);

// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, "/Baatein/Video/"+time+".mp4");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"/Baatein/Video/"+time+".mp4");

        videoUri=Uri.fromFile(file);

// get download service and enqueue file
        final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
         downloadID = manager.enqueue(request);




    }
    public static Bitmap getThumblineImage(String videoPath)throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ e.getMessage());
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public void showpopup(View view, boolean isRight) {
        PopupMenu popup=new PopupMenu(context,view);
        popup.setOnMenuItemClickListener(this);
        if(isRight){
            popup.inflate(R.menu.delete_menu);
        }else {
            popup.inflate(R.menu.image_menu);
        }
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.downloadButton :downloadfile(image1);
                return true;
            case R.id.deleteButton: delete(deleteMessage);
            return  true;


            default: return false;
        }
    }

    private void delete(final Messages deleteMessage) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("Chats").orderByChild("time").equalTo(deleteMessage.getTime());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Messages messages=snapshot.getValue(Messages.class);
                        if(messages.getMessage().equals(deleteMessage.getMessage())){
                            if(messages.getSender().equals(deleteMessage.getSender())&&
                            messages.getReceiver().equals(deleteMessage.getReceiver())&&
                                 messages.getTime().equals(deleteMessage.getTime()))
                            {
                                if(!deleteMessage.getImagelink().equals("")||!deleteMessage.getVideolink().equals("")){
                                    deleteImage(deleteMessage);
                                }
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("imagelink","");
                                hashMap.put("imageuri","");
                                hashMap.put("videolink","");
                                hashMap.put("videouri","");
                                hashMap.put("message","message deleted!");
                                hashMap.put("messagedeleted",true);

                                snapshot.getRef().updateChildren(hashMap);

                                notifyDataSetChanged();






                            }

                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void deleteImage(Messages deleteMessage) {
        StorageReference reference;
        if(!deleteMessage.getImagelink().equals("")){
            reference = FirebaseStorage.getInstance().getReferenceFromUrl(deleteMessage.getImagelink());
        }else{
            reference = FirebaseStorage.getInstance().getReferenceFromUrl(deleteMessage.getVideolink());
        }

        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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


    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView showMessage,timeTextView;
        ProgressBar progressBar;
        VideoView videoView;
        ImageView image;
        ImageView downloadButton;
        RelativeLayout chatRelativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage=itemView.findViewById(R.id.showMessage);
            timeTextView=itemView.findViewById(R.id.timeTextView);
            image=itemView.findViewById(R.id.imageview);
            videoView=itemView.findViewById(R.id.videoView);
            chatRelativeLayout=itemView.findViewById(R.id.chatRelativeLayout);
            downloadButton=itemView.findViewById(R.id.download);
            progressBar=itemView.findViewById(R.id.downloadProgressBar);


        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public boolean onLongClick(View view) {
            if(!righthai&&imagehai){


            }

            return false;
        }
    }

  @Override
    public int getItemViewType(int position) {
        user= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(user.getPhoneNumber())){
            righthai=true;
            return MSG_TYPE_RIGHT;
        }else{
            righthai=false;
            return MSG_TYPE_LEFT;
        }
    }





    private void downloadfile(ImageView image) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                String[] permission={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions((Activity) context,permission,
                        1);
            }
            else{
                saveImage(image);
            }

        }

    }


    private void saveImage(ImageView imageView) {

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        String time=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(System.currentTimeMillis());
        File path=Environment.getExternalStorageDirectory();
        File dir=new File(path+"/DCIM/Baatein/Images");
        dir.mkdirs();
        String imagename=time+".png";
        File file=new File(dir,imagename);
        OutputStream out;
        InputStream inputStream;

        try {
            out=new FileOutputStream(file);



            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);

            out.flush();
            out.close();
            inputStream=new FileInputStream(file);
            byte[] imagedata = IOUtils.toByteArray(inputStream);
            Bitmap bitmap3 = BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length);
            imageView.setImageBitmap(bitmap3);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dir)));

            Toast.makeText(context,"Image Saved In Baatein",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();

        }


    }
}