package com.example.baatein.Fragments;

import com.example.baatein.Notifications.MyResponse;
import com.example.baatein.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAuyYDXxw:APA91bERJot1H-Czrc45Ppnjx6VyLAnsCGjW298DOKIWzI4O5H_RG-tTYd3Q_qUUCk8647TfWp6pR2RxGHBLDIodR9P8htPRA-PB5YBY5X8NiuZUwxPRYa6CVxpamczdGF0fV9_OpCCM"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
