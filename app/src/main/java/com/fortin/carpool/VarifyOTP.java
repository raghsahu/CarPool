package com.fortin.carpool;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.UserPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.UserAPI;
import com.fortin.carpool.helper.ConnectionDetector;
import com.fortin.carpool.helper.NoInternetDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VarifyOTP extends AppCompatActivity implements View.OnClickListener{

    ConnectionDetector connectionDetector;
    Button btnvarify;
    String mobileNumber,country,userid="",TAG="Varify",gcmid="";
    EditText etotp;
    FirebaseAuth mAuth;
    Context context;
    String vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varify_otp);
        connectionDetector = new ConnectionDetector(this);
        context=VarifyOTP.this;
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.confirm);
        try {
            FirebaseApp.getInstance();
        } catch (IllegalStateException ex) {
            FirebaseApp.initializeApp(context);
        }
        mAuth=FirebaseAuth.getInstance();
//        if(!connectionDetector.isConnectingToInternet())
//        {
//            Toast.makeText(VarifyOTP.this, getString(R.string.nointdesc), Toast.LENGTH_SHORT).show();
//            finish();
//        }else {

        if(getIntent().getExtras()!=null){
            mobileNumber = getIntent().getStringExtra(getString(R.string.phone));
            country = getIntent().getStringExtra(getString(R.string.country));
            vid=getIntent().getStringExtra("verificationid");
        }else{
            finish();
        }
        if(Login.isverify){
            register();
        }else{
            //M.savePreferences(getString(R.string.country), country, VarifyOTP.this);
            System.out.println("111country--" + country);
            etotp = (EditText) findViewById(R.id.etotp);
            btnvarify = (Button) findViewById(R.id.btnvarify);

            btnvarify.setOnClickListener(this);

            etotp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() == 6) {
                     //   register();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            register();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(context,"Mobile No and OTP did not match",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {

        if(v==btnvarify)
        {
            if(NoInternetDialogue.isconnection(VarifyOTP.this)) {
                try {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(vid, etotp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }catch (Exception e){

                }
            }
        }
    }


    public void register() {
        if(connectionDetector.isConnectingToInternet()) {
            M.showLoadingDialog(VarifyOTP.this);
            String verificationcode = "990990";//etotp.getText().toString();
            if (AppConst.fcm_id != null)
                gcmid = AppConst.fcm_id;
            Log.d(TAG, "data:" + mobileNumber + " " + verificationcode + " " + country + " " + gcmid);
            UserAPI mUsersAPI = APIService.createService(UserAPI.class, M.getToken(VarifyOTP.this));
            mUsersAPI.register(mobileNumber, verificationcode, country, "android", gcmid, new Callback<UserPojo>() {
                @Override
                public void success(UserPojo userItems, Response response) {
                    M.hideLoadingDialog();
                    if (userItems.getUser_id() != null && !userItems.getUser_id().isEmpty()) {
                        if (userItems.getUser_id().equals("-1")) {
                            Toast.makeText(VarifyOTP.this, "Mobile No and OTP did not match", Toast.LENGTH_SHORT).show();
                        } else {
                            userid = userItems.getUser_id();
                            if (userid.length() > 0) {

                                M.setID(userid, VarifyOTP.this);
                                M.savePreferences(getString(R.string.mobile), userItems.getUser_mobile(), VarifyOTP.this);
                                M.savePreferences(getString(R.string.country), userItems.getUser_country(), VarifyOTP.this);
                                String userstatus = userItems.getUserstatus();
                                if (userstatus.equalsIgnoreCase("olduser")) {
                                    String usernm = userItems.getUser_name();
                                    if (userItems.getUser_name() != null) {
                                        M.setUsername(usernm, VarifyOTP.this);
                                    }
                                    loginNow();
                                } else {
                                    Intent intent = new Intent(VarifyOTP.this, UserRegistration.class);
                                    finish();
                                    startActivity(intent);
                                }
                            } else {

                                M.showToast(VarifyOTP.this, "Server Problem");
                            }
                        }
                    } else {

                        M.showToast(VarifyOTP.this, "Server Problem");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    M.hideLoadingDialog();
                    Log.d("VarifyOTP", "error:" + error.getMessage());
                }
            });
        }else{
            Toast.makeText(context,R.string.noint,Toast.LENGTH_SHORT).show();
        }
    }

    public void recivedSms(String message)
    {
        try
        {
            String otp = message.substring(0, Math.min(message.length(), 6));
            etotp.setText(otp);
        }
        catch (Exception e)
        {
        }
    }

    public void loginNow() {

        Intent i = new Intent(VarifyOTP.this, MainActivity.class);
        finish();
        startActivity(i);
        overridePendingTransition(0,0);

    }


    @Override
    public void onBackPressed() {
            Intent i = new Intent(this, Login.class);
            finish();
            startActivity(i);
            overridePendingTransition(0,0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, Login.class);
                finish();
                startActivity(i);
                overridePendingTransition(0, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
