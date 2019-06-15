package com.fortin.carpool.WebServices;




import com.fortin.carpool.AppConst;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class APIService {
    public APIService() {

    }

    public static <S> S createService(Class<S> serviceClass, final String token) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(AppConst.MAIN);
        builder.setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {

                request.addHeader("token", token);

            }
        });
        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(AppConst.MAIN);
        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);
    }
}
