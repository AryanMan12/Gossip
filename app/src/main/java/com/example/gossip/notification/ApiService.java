package com.example.gossip.notification;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAACx2syQE:APA91bEAY9TMD36KMmtk4NohuNl9M_qrmWx2L2kqwjR-rvB9rfj5LNh-rGGDsgiDmuXUkNBRK7tR195gsPVqEJCdOSD8gjcyd8IvGHgp1WcK4nyB3VcRPjY56NmiENukACRGIPv3unkX" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
