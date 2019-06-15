package com.fortin.carpool.WebServices;



import com.fortin.carpool.Model.PushNotificationHistory;
import com.fortin.carpool.Model.ResponseModel;
import com.fortin.carpool.Model.UserPojo;
import com.fortin.carpool.Model.userRegPojo;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by SmartCoder on 17/04/2015.
 */
public interface UserAPI {

    @FormUrlEncoded
    @POST("/Twilio/send_sms.php")
    void generateOTP(
            @Field("mobile_no") String mobile_no,
            Callback<ResponseModel> response);

    @FormUrlEncoded
    @POST("/getUserDetailsById.php")
    void getuser(
            @Field("user_id") String user_id,
            Callback<UserPojo> response);

    @FormUrlEncoded
    @POST("/loginWithSMS.php")
    void register(@Field("mobile_no") String mobile_no,
                  @Field("otp") String otp,
                  @Field("country") String country,
                  @Field("device_type") String device_type,
                  @Field("gcm_id") String gcm_id,
                  Callback<UserPojo> response);

    @FormUrlEncoded
    @POST("/addFollowers.php")
    void addFollowers(
            @Field("userid") String user_id,
            @Field("followerid") String followerid,
            Callback<userRegPojo> response);

    @FormUrlEncoded
    @POST("/checkFollowingRider.php")
    void checkFollowing(
            @Field("userid") String user_id,
            @Field("followerid") String followerid,
            Callback<userRegPojo> response);

    @FormUrlEncoded
    @POST("/getAllMessageByUserid.php")
    void getPushHistory(
            @Field("user_id") String mobile_no,
            Callback<List<PushNotificationHistory>> response);

    @FormUrlEncoded
    @POST("/updateGCMId.php")
    void updateRegID(@Field("user_id") String userid,
                     @Field("gcmid") String gcmid,
                     @Field("devicetype") String devicetype,
                     Callback<ResponseModel> response);
}
