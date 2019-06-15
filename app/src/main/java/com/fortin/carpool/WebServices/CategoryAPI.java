package com.fortin.carpool.WebServices;


import com.fortin.carpool.Model.CategoryPojo;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by SWIFT-3 on 08/02/16.
 */
public interface CategoryAPI {


    @GET("/getAllVehicleCategory.php")
    void getCategory(Callback<List<CategoryPojo>> posts);

}
