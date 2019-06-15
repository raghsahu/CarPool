package com.fortin.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.UserPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.UserAPI;
import com.fortin.carpool.helper.ConnectionDetector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SplashScreen extends AppCompatActivity {

    private boolean mIsBackButtonPressed;
    private static final int SPLASH_DURATION = 3000; //3 second
    ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        connectionDetector = new ConnectionDetector(this);

            Handler handler = new Handler();

            // run a thread after 3 seconds to start the home screen
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if(!connectionDetector.isConnectingToInternet())
                    {
                        Toast.makeText(SplashScreen.this,"Internet network not Avaliable",Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        String id = M.getID(SplashScreen.this);
                        if (id.length() == 0) {
                            Intent mIntent = new Intent(SplashScreen.this, Login.class);
                            startActivity(mIntent);
                            finish();

                        } else {
                            register();
                        }
                    }
                }

            }, SPLASH_DURATION);

    }


    @Override
    public void onBackPressed() {

        // set the flag to true so the next activity won't start up
        mIsBackButtonPressed = true;
        super.onBackPressed();

    }

    public void register() {
        M.showLoadingDialog(SplashScreen.this);
        M.L(""+M.getID(SplashScreen.this));
        UserAPI mUsersAPI = APIService.createService(UserAPI.class, M.getToken(SplashScreen.this));
        mUsersAPI.getuser(M.getID(SplashScreen.this), new Callback<UserPojo>() {
            @Override
            public void success(UserPojo userItems, Response response) {
                M.L(response.getBody() + "");

                M.setID(userItems.getUser_id(), SplashScreen.this);
                M.setUsername(userItems.getUser_name(), SplashScreen.this);

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                finish();
                startActivity(i);

                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                M.L(getString(R.string.ServerError));
//                M.L(error.getMessage());

            }
        });
    }
}