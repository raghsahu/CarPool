package com.fortin.carpool.WebServices;



import com.fortin.carpool.Model.ProfilePojo;
import com.fortin.carpool.Model.userRegPojo;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by SWIFT-3 on 08/02/16.
 */
public interface userRegAPI {

    @Multipart
    @POST("/userRegistration.php")
    void userRegistration(@Part("userid") String userid,
                          @Part("username") String username,
                          @Part("email") String email,
                          @Part("mobilenum") String mobilenum,
                          @Part("city") String city,
                          @Part("postal_code") String postal_code,
                          @Part("country") String country,
                          @Part("gender") String gender,
                          @Part("dob") String dob,
                          @Part("profileimg") TypedFile profileimg,
                          @Part("identityimg") TypedFile identityimg,
                          @Part("fbid") String fbid,
                          Callback<userRegPojo> posts);

    @FormUrlEncoded
    @POST("/userProfile.php")
    void getuserprofile(
            @Field("user_id") String user_id,
            Callback<ProfilePojo> response);

}
