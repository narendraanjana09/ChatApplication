package com.example.baatein.ExtraClasses;

import android.content.Context;
import android.widget.Toast;

public class ToastMessage {

    Context context;
    String message;

    public ToastMessage(Context context, String message) {
        this.context = context;
        this.message = message;

        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}
