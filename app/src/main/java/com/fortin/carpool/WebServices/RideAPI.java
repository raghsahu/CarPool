package com.fortin.carpool.WebServices;


import com.fortin.carpool.Model.RidePojo;
import com.fortin.carpool.Model.VehiclePojo;
import com.fortin.carpool.Model.userRegPojo;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

public interface RideAPI {

    @FormUrlEncoded
    @POST("/getVehicleByUserId.php")
    void getVehicles(
            @Field("user_id") String user_id,
            Callback<List<VehiclePojo>> response);

    @FormUrlEncoded
    @POST("/myRides.php")
    void getMyRides(@Field("user_id") String user_id,
                    Callback<List<RidePojo>> response);

    @FormUrlEncoded
    @POST("/UpcomingRides.php")
    void getUpcomingRides(@Field("user_id") String user_id,
                          Callback<List<RidePojo>> response);

    @FormUrlEncoded
    @POST("/findRides.php")
    void getRides(
            @Field("pickup_lat") String pickup_lat,
            @Field("pickup_long") String pickup_long,
            @Field("drop_lat") String drop_lat,
            @Field("drop_long") String drop_long,
            @Field("onlyfemale") String onlyfemale,
            @Field("numtype") String numtype,
            @Field("seldate") String seldate,
            Callback<List<RidePojo>> response);

    @FormUrlEncoded
    @POST("/sendRequest.php")
    void sendRequest(
            @Field("user_id") String user_id,
            @Field("username") String user_name,
            @Field("trip_id") String trip_id,
            @Field("trip_depature_time") String trip_depature_time,
            @Field("rider_id") String rider_id,
            @Field("pickup_point") String pickup_point,
            @Field("drop_point") String drop_point,
            @Field("seats") String seats,
            @Field("comment") String comment,
            Callback<userRegPojo> response);

    @FormUrlEncoded
    @POST("/riderResponse.php")
    void riderResponse(
            @Field("tripdetail_id") String tripdetail_id,
            @Field("username") String username,
            @Field("triproute") String triproute,
            @Field("seat") String seat,
            @Field("trip_id") String trip_id,
            @Field("status") String status,//Accept,Reject
            @Field("expectedtime") String expectedtime,
            Callback<userRegPojo> response);

    @GET("/feedActivity.php")
    void getFeed(Callback<List<RidePojo>> response);

    @FormUrlEncoded
    @POST("/changeRecentlyRideStatus.php")
    void changestatus(
            @Field("trip_id") String trip_id,
            @Field("status") String status,//COMPLETED,CANCEL
            Callback<userRegPojo> response);

}
