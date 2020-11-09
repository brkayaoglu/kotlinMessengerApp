package com.example.kotlinmessengerclone.Fragments;

import com.example.kotlinmessengerclone.Notifications.MyResponse;
import com.example.kotlinmessengerclone.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAkRLA3Vg:APA91bHILqsxDIDHIYBR2InMUZilyXwsxlvBf0WuGSdUkrhLEYv_EtGYACgHeE7nPm_uFlFYtRenCa6kR1yqjL9fyidYQ3QW8ZFm8yATFnlDRyw9iVvzkYsZEe3ZZanEprdsneg6kADW"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
