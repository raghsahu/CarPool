package com.fortin.carpool.WebServices;



import com.fortin.carpool.Model.MemberPojo;
import com.fortin.carpool.Model.RequestedRidePojo;
import com.fortin.carpool.Model.SendPojo;
import com.fortin.carpool.Model.userRegPojo;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by SWIFT-3 on 22/02/16.
 */
public interface RequestedRideAPI {

    @FormUrlEncoded
    @POST("/passengerRequestToRider.php")
    void getRequest(
            @Field("user_id") String user_id,
            Callback<List<RequestedRidePojo>> response);

    @FormUrlEncoded
    @POST("/cancelRide.php")
    void cancelRide(
            @Field("trip_id") String trip_id,
            Callback<userRegPojo> response);

    @FormUrlEncoded
    @POST("/sendRequestHistory.php")
    void getHistory(
            @Field("user_id") String user_id,
            Callback<List<SendPojo>> response);

    @FormUrlEncoded
    @POST("/memberDetailByTrip.php")
    void getMember(
            @Field("tripid") String tripid,
            Callback<List<MemberPojo>> response);

    @FormUrlEncoded
    @POST("/cancelSendRequest.php")
    void cancelSendRequest(
            @Field("tripdetail_id") String tripdetail_id,
            @Field("username") String username,
            @Field("triproute") String triproute,
            @Field("status") String status,
            Callback<userRegPojo> response);
}
