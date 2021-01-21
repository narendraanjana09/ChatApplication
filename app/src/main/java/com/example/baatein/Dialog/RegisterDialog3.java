package com.example.baatein.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.baatein.R;

public class RegisterDialog3 {

  private   Activity activity;
   private AlertDialog alertDialog;


    public RegisterDialog3(Activity activity){
        this.activity=activity;
    }
    public void startAlertDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);

        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.register_dialog,null));
        builder.setCancelable(false);


        alertDialog=builder.create();
try {
    alertDialog.show();
} catch (Exception e) {
    e.printStackTrace();
}


    }
   public void dismissDialog(){
try {
    alertDialog.dismiss();
}catch (Exception e){

}



    }

}
