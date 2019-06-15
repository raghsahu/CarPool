package com.fortin.carpool;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.UserPojo;
import com.fortin.carpool.Model.userRegPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.UserAPI;
import com.fortin.carpool.WebServices.userRegAPI;
import com.fortin.carpool.helper.CropSquareTransformation;
import com.fortin.carpool.helper.FilePath;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class UserRegistration extends AppCompatActivity implements View.OnClickListener {

    EditText etusernm, etemail, etmob, etcity, etcountry, etpostal, etbdate;
    RadioGroup rgender;
    RadioButton rbgender,rbmale,rbfemale;
    Button btnsave;
    ImageView ivprofile, ividentity;

    LoginButton btnfblogin;
    CallbackManager callbackManager;
    private Intent mIntent;

    String TAG = "UserRegistration", userid="", usernm="", email, mob = "", city = "", country = "", postal, gender, bdate, userfbid = "";
    private String profile = null, identity = null;
    private static int RESULT_LOAD_IMAGE1 = 1;
    private static int RESULT_LOAD_IMAGE2 = 2;

    Calendar dateAndTime = Calendar.getInstance();
    SimpleDateFormat fmtDateAndTime = new SimpleDateFormat("dd MMM yyyy");
    DatePickerDialog.OnDateSetListener d;

    boolean firsttime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(UserRegistration.this);
        setContentView(R.layout.activity_user_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.backicon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        callbackManager = CallbackManager.Factory.create();

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.fortin.carpool", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
     //   }

        etusernm = (EditText) findViewById(R.id.etusernm);
        etemail = (EditText) findViewById(R.id.etemail);
        etmob = (EditText) findViewById(R.id.etmob);
        etcity = (EditText) findViewById(R.id.etcity);
        etcountry = (EditText) findViewById(R.id.etcountry);
        etpostal = (EditText) findViewById(R.id.etpostal);
        etbdate = (EditText) findViewById(R.id.etbdate);
        rgender = (RadioGroup) findViewById(R.id.rg);
        rbmale=(RadioButton)findViewById(R.id.rbmale);
        rbmale.setBackgroundColor(getResources().getColor(R.color.radioselected));
        rbfemale=(RadioButton)findViewById(R.id.rbfemale);
        ivprofile = (ImageView) findViewById(R.id.ivprofile);
        ividentity = (ImageView) findViewById(R.id.ividentity);
        btnsave = (Button) findViewById(R.id.btnsave);
        btnfblogin = (LoginButton) findViewById(R.id.login_facebook);

        userid = M.getID(UserRegistration.this);
        usernm=M.getUsername(UserRegistration.this);
        if(userid.length()==0 || userid.equals('0') || userid==null){
            Intent it=new Intent(UserRegistration.this,Login.class);
            finish();
            startActivity(it);
        }
        getUserProfile();

        rgender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                radioButton.setBackgroundColor(getResources().getColor(R.color.radioselected));
                if(!rbmale.isChecked())
                    rbmale.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                if(!rbfemale.isChecked())
                    rbfemale.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        });

        mob = M.loadSavedPreferences(getString(R.string.mobile), UserRegistration.this);
        etmob.setText(mob);
        etmob.setFocusable(false);
        country=M.loadSavedPreferences(getString(R.string.country),UserRegistration.this);
        if(country.length()>0) {
            etcountry.setText(country);
            etcountry.setFocusable(false);
        }
        etbdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog d1 = new DatePickerDialog(UserRegistration.this, d, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH));
                d1.show();
            }
        });

        d = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                dateAndTime.set(Calendar.YEAR, arg1);
                dateAndTime.set(Calendar.MONTH, arg2);
                dateAndTime.set(Calendar.DAY_OF_MONTH, arg3);
                etbdate.setText(fmtDateAndTime.format(dateAndTime.getTime()));
            }

        };

        ivprofile.setOnClickListener(this);
        ividentity.setOnClickListener(this);
        btnsave.setOnClickListener(this);
        //btnfblogin.setOnClickListener(this);
        btnfblogin.setReadPermissions("public_profile", "email");
        btnfblogin.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        // App code
                        System.out.println("Success---" + loginResult.getAccessToken());

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            Log.d(TAG, response.getError().toString());
                                            Toast.makeText(getApplicationContext(), response.getError().toString(), Toast.LENGTH_SHORT);
                                        } else {
                                            json = response.getJSONObject();
                                            try {
                                                  usernm = json.getString("name");
                                                  userfbid = json.getString("id");
                                                  email = json.getString("email");
                                                  gender = json.getString("gender");
                                                  etusernm.setText(usernm);
                                                  etemail.setText(email);
                                                  if (gender.equalsIgnoreCase("Female")) {
                                                      rbfemale.setChecked(true);
                                                      rbmale.setChecked(false);
                                                      rbfemale.setBackgroundColor(getResources().getColor(R.color.radioselected));
                                                      rbmale.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                                                  }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }

                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        System.out.println("exception--" + exception.toString());
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//    uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ivprofile) {

            mIntent = new Intent();
            mIntent.setType("image/*");
            mIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(mIntent, getString(R.string.selpicture)),
                    RESULT_LOAD_IMAGE1);
        }
        else if (v.getId() == R.id.ividentity) {
            mIntent = new Intent();
            mIntent.setType("image/*");
            mIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(mIntent,  getString(R.string.selpicture)),
                    RESULT_LOAD_IMAGE2);
        }

        else if (v.getId() == R.id.btnsave) {
            if(etusernm.getText().toString().trim().equals("")){
                etusernm.setError("Required Username");
            }else if(etemail.getText().toString().trim().equals("")){
                etemail.setError("Required Email");
            }else {
                usernm = etusernm.getText().toString();
                email = etemail.getText().toString();
                mob = etmob.getText().toString();
                city = etcity.getText().toString();
                country = etcountry.getText().toString();
                postal = etpostal.getText().toString();
                bdate = etbdate.getText().toString();

                int selectedId = rgender.getCheckedRadioButtonId();
                rbgender = (RadioButton) findViewById(selectedId);
                gender = rbgender.getText().toString();

                datainsert();
            }
        }
    }

    public void datainsert() {
        M.showLoadingDialog(this);
        userRegAPI mCommentsAPI = APIService.createService(userRegAPI.class);
        TypedFile profileimg =null,identityimg=null;
        if(profile!=null) {
            profileimg = new TypedFile("image/jpg", new File(profile));
        }
        if(identity!=null) {
            identityimg = new TypedFile("image/jpg", new File(identity));
        }
        mCommentsAPI.userRegistration(userid,usernm, email, mob, city, postal, country, gender, bdate, profileimg,identityimg,userfbid, new Callback<userRegPojo>()
            {
                @Override
                public void success(userRegPojo user, Response response) {
                    M.hideLoadingDialog();
                    if (user.getStatus().equals("success")) {
                        firsttime = false;
                       getUserProfile();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                     M.hideLoadingDialog();
                    System.out.print("error:userregistration"+error.getMessage());
                }
            }
        );
    }

    public void getUserProfile(){
        M.showLoadingDialog(this);

        UserAPI mCommentsAPI = APIService.createService(UserAPI.class);
        mCommentsAPI.getuser(userid, new Callback<UserPojo>() {
                    @Override
                    public void success(UserPojo user, Response response) {
                        M.hideLoadingDialog();
                        String unm=user.getUser_name();
                        String umail=user.getUser_email();
                        String ucity=user.getUser_city();
                        String upostal=user.getPostal_code();
                        String usergender=user.getUser_gender();
                        String ubdate=user.getUser_dob();
                        String pimg=AppConst.imgurl+"profile/"+user.getUser_profile_img();
                        String imgi=AppConst.imgurl+"identity/"+user.getUser_identity_img();
                        etusernm.setText(unm);
                        etemail.setText(umail);
                        etcity.setText(ucity);
                        etpostal.setText(upostal);
                        if(usergender==null)
                        {
                            usergender = "Male";
                        }
                        if (usergender.equalsIgnoreCase("Female")) {
                                rbfemale.setChecked(true);
                                rbmale.setChecked(false);
                                rbfemale.setBackgroundColor(getResources().getColor(R.color.radioselected));
                                rbmale.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                            }

                        etbdate.setText(ubdate);
                        Picasso.with(UserRegistration.this)
                                .load(pimg)
                                .transform(new CropSquareTransformation())
                                .placeholder(R.drawable.user_icon_dark)
                                .error(R.drawable.user_icon_dark)
                                .into(ivprofile);
                        Picasso.with(UserRegistration.this)
                                .load(imgi)
                                .placeholder(R.drawable.identity)
                                .error(R.drawable.identity)
                                .into(ividentity);

                        if(!firsttime)
                        {
                            Intent i = new Intent(UserRegistration.this, MainActivity.class);
                            startActivity(i);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        M.hideLoadingDialog();
                        System.out.println("error" + error.getMessage());
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == UserRegistration.this.RESULT_OK && requestCode == RESULT_LOAD_IMAGE1) {
             profile = FilePath.getPath(UserRegistration.this, data.getData());
             Picasso.with(UserRegistration.this)
                     .load(data.getData())
                     .placeholder(R.drawable.user_icon)
                     .error(R.drawable.user_icon)
                     .into(ivprofile);
             if (ivprofile.getVisibility() != View.VISIBLE) {
                    ivprofile.setVisibility(View.VISIBLE);
             }
        }
        else if (resultCode == UserRegistration.this.RESULT_OK && requestCode == RESULT_LOAD_IMAGE2) {
            identity = FilePath.getPath(UserRegistration.this, data.getData());
            Picasso.with(UserRegistration.this)
                     .load(data.getData())
                     .placeholder(R.drawable.identity)
                     .error(R.drawable.identity)
                     .into(ividentity);
            if (ividentity.getVisibility() != View.VISIBLE) {
                    ividentity.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onBackPressed() {
        Intent it=new Intent(UserRegistration.this,MainActivity.class);
        finish();
        startActivity(it);
    }
}
