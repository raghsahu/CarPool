package com.fortin.carpool.WebServices;


import com.fortin.carpool.Model.userRegPojo;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

public interface vehicleRegAPI {

    @Multipart
    @POST("/vehicleRegistration.php")
    void vehicleReg(@Part("user_id") String user_id,
                    @Part("vehicle_category_id") String vehicle_category_id,
                    @Part("model") String model,
                    @Part("vehicle_number") String vehicle_number,
                    @Part("seats") String seats,
                    @Part("type") String type,
                    @Part("vehicleimage") TypedFile vehicleimg,
                    Callback<userRegPojo> posts);

    @FormUrlEncoded
    @POST("/deleteVehicle.php")
    void deletevehicle(
            @Field("vehicle_id") String vehicle_id,
            Callback<userRegPojo> response);

    @Multipart
    @POST("/updateVehicle.php")
    void updateVehicle(@Part("vehicle_id") String vehicle_id,
                       @Part("vehicle_category_id") String vehicle_category_id,
                       @Part("model") String model,
                       @Part("vehicle_number") String vehicle_number,
                       @Part("seats") String seats,
                       @Part("type") String type,
                       @Part("vehicleimg") TypedFile vehicleimage,
                       Callback<userRegPojo> posts);
}
