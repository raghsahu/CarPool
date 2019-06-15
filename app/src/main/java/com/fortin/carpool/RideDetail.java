package com.fortin.carpool;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.RidePojo;
import com.fortin.carpool.Model.UserPojo;
import com.fortin.carpool.Model.userRegPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.UserAPI;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.fortin.carpool.helper.CropSquareTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RideDetail extends AppCompatActivity {

    RidePojo ride;
    String userid;
    String TAG="RideDetail",vehicleid;
    String trip_userid="",name,depaturetime,route="",seat,profileimg,passengertype="",rate="",source,destination;
    String smoking,ac,extra;
    ImageView ivprofile,ivwoman,ivvehicle;
    TextView tvname,tvdepature,tvroute,tvseats,tvplace,tvcontact,tvmaplink,tvpasstype,tvrate,tvsource,tvextra,tvfbcnt,tvverify,tvvehicle;
    TextView ivsmoking,ivac;
    Button btnsend,btnfollow;
    Spinner spndrop,spnpick;
    Button btnext;
    Dialog dialog;
    ArrayList<String> picklist=new ArrayList<>();
    ArrayList<String> droplist=new ArrayList<>();

    String pickpoint,droppoint;

    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(RideDetail.this);
        setContentView(R.layout.activity_ride_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.backicon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivprofile=(ImageView)findViewById(R.id.profileimg);
        tvname=(TextView)findViewById(R.id.tvname);
        tvfbcnt=(TextView)findViewById(R.id.tvfbcnt);
        tvverify=(TextView)findViewById(R.id.tvverify);
        tvfbcnt.setVisibility(View.GONE);
        tvdepature=(TextView)findViewById(R.id.tvdtime);
        tvroute=(TextView)findViewById(R.id.tvroute);
        tvseats=(TextView)findViewById(R.id.tvseats);
        tvplace=(TextView)findViewById(R.id.tvplace);
        tvcontact=(TextView)findViewById(R.id.tvcontactno);
        tvmaplink=(TextView)findViewById(R.id.tvmaplink);
        tvpasstype = (TextView)findViewById(R.id.tvpasstype);
        tvrate=(TextView)findViewById(R.id.tvtriprate);
        tvsource=(TextView)findViewById(R.id.source);
        tvextra=(TextView)findViewById(R.id.tvextra);
        tvvehicle=(TextView)findViewById(R.id.vehicledata);
        ivsmoking=(TextView)findViewById(R.id.smoking);
        ivvehicle=(ImageView)findViewById(R.id.ivvehicle);
        ivac=(TextView)findViewById(R.id.ac);
        ivwoman=(ImageView)findViewById(R.id.woman);
        btnfollow=(Button)findViewById(R.id.btnfollow);
        btnsend=(Button)findViewById(R.id.btnsend);
        userid=M.getID(RideDetail.this);

        if(AppConst.selride!=null) {

            ride = AppConst.selride;
            trip_userid=ride.getUserid();
            getTripUserDetail();
            if(userid.length()==0 || userid.equals(trip_userid)){
                btnsend.setVisibility(View.GONE);
                btnfollow.setVisibility(View.GONE);
            }
            vehicleid=ride.getVehicle_id();

            depaturetime="Depature Time: " + ride.getDep_time();

            if(!ride.getRet_time().equals("00:00:00") && !ride.getRet_time().equalsIgnoreCase(""))
                depaturetime=depaturetime+"\n"+"Return Time: "+ride.getRet_time();
            tvdepature.setText(depaturetime);

            seat=ride.getSeats();
            if(seat=="1")
                seat="1 Seat";
            else
                seat=seat+getString(R.string.seats);
            seat="<font color='#f04e25'>"+seat+"</font>";
            tvseats.setText(Html.fromHtml(seat+" Avaliable"));

            source=ride.getSource();
            destination=ride.getDestination();

            tvsource.setText(source+" - "+destination);

            route=getString(R.string.route)+ride.getRoute();

            if(ride.getRoute().length()==0) {
                tvroute.setVisibility(View.GONE);
            }
            else
                tvroute.setText(route);

            String type=ride.getPassenger_type();
            if (type.equalsIgnoreCase(getString(R.string.both))) {
                passengertype = getString(R.string.bothmsg);
                ivwoman.setVisibility(View.GONE);
            }
            else if(type.equalsIgnoreCase(getString(R.string.male))) {
                passengertype = getString(R.string.malemsg);
                ivwoman.setImageResource(R.drawable.male);
            }
            else if(type.equalsIgnoreCase(getString(R.string.female))) {
                passengertype = getString(R.string.femalemsg);
                ivwoman.setImageResource(R.drawable.female);
            }

            tvpasstype.setText(passengertype);

            rate=ride.getRate();
            if(rate=="")
                tvrate.setVisibility(View.GONE);
            else {
                tvrate.setText(Html.fromHtml(rate));
            }

            smoking=ride.getSmoking();
            ac=ride.getAc();
            extra=ride.getExtra();

            if(ac.equals("1")) {
                ivac.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.ac,0,0);
                ivac.setText(getString(R.string.ac));
            }
            else {
                ivac.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.noac,0,0);
                ivac.setText(getString(R.string.noac));
            }
            if(smoking=="1") {
                ivsmoking.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.smoking,0,0);
                ivsmoking.setText(getString(R.string.smoking));
            }
            else {
                ivsmoking.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.nosmoking, 0, 0);
                ivsmoking.setText(getString(R.string.nosmoking));
            }
            if(extra=="")
                tvextra.setText("");
            else
                tvextra.setText(extra);

            Picasso.with(RideDetail.this)
                    .load(AppConst.imgurl+"vehicle/"+ride.getVehicleimg())
                    .placeholder(R.drawable.offerride)
                    .error(R.drawable.offerride)
                    .into(ivvehicle);

            tvvehicle.setText(ride.getVehicle().toString());

            btnsend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(AppConst.pickupoint==null || AppConst.droppoint==null){
                        String route[]=ride.getRoute().split(" > ");
                        for (int i=0;i<route.length;i++){
                            if(i!=route.length-1)
                                picklist.add(route[i]);
                            if(i!=0)
                                droplist.add(route[i]);
                        }
                        dialog = new Dialog(RideDetail.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        dialog.setContentView(R.layout.custom_dialog);
                        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
                        spndrop=(Spinner)dialog.findViewById(R.id.spndrop);
                        spnpick=(Spinner)dialog.findViewById(R.id.spnpick);
                        btnext=(Button)dialog.findViewById(R.id.btnsel);
                        ArrayAdapter<String> pickadapter = new ArrayAdapter<String>(RideDetail.this, android.R.layout.simple_list_item_1, picklist);
                        spnpick.setAdapter(pickadapter);
                        spnpick.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                pickpoint = parent.getSelectedItem().toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        ArrayAdapter<String> dropadapter = new ArrayAdapter<String>(RideDetail.this, android.R.layout.simple_list_item_1, droplist);
                        spndrop.setAdapter(dropadapter);
                        spndrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                droppoint = parent.getSelectedItem().toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        btnext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppConst.pickupoint=pickpoint;
                                AppConst.droppoint=droppoint;
                                dialog.cancel();
                                Intent it = new Intent(RideDetail.this, SendRequest.class);
                                it.putExtra("tripid", ride.getTrip_id());
                                it.putExtra("depaturetime", ride.getDep_time());
                                it.putExtra("riderid", ride.getUserid());
                                it.putExtra("seats", ride.getSeats());
                                startActivity(it);
                            }
                        });
                        dialog.show();
                    }else {
                        Intent it = new Intent(RideDetail.this, SendRequest.class);
                        it.putExtra("tripid", ride.getTrip_id());
                        it.putExtra("depaturetime", ride.getDep_time());
                        it.putExtra("riderid", ride.getUserid());
                        it.putExtra("seats", ride.getSeats());
                        startActivity(it);
                    }
                }
            });

            tvmaplink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent it = new Intent(RideDetail.this, MapsActivity.class);
                    it.putExtra("place", ride.getRoute());
                    it.putExtra("lat", ride.getRoutelat());
                    it.putExtra("long", ride.getRoutelong());
                    startActivity(it);
                }
            });

            checkFollowingRider();

            btnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    followrider();
                }
            });

        }

    }

    public void getTripUserDetail(){
        M.showLoadingDialog(RideDetail.this);

            UserAPI mUsersAPI = APIService.createService(UserAPI.class, M.getToken(RideDetail.this));
            mUsersAPI.getuser(trip_userid, new Callback<UserPojo>() {
                @Override
                public void success(UserPojo userItems, Response response) {
                    String city="",country="";
                    name = userItems.getUser_name();
                    profileimg = AppConst.imgurl + "profile/" + userItems.getUser_profile_img();

                    Picasso.with(RideDetail.this)
                            .load(profileimg)
                            .transform(new CropSquareTransformation())
                            .placeholder(R.drawable.user_icon_dark)
                            .error(R.drawable.user_icon_dark)
                            .into(ivprofile);
                    tvname.setText(name);
                    if(userItems.getUser_city()!=null && !userItems.getUser_city().isEmpty()) {
                       city=userItems.getUser_city();
                    }
                    country=userItems.getUser_country();

                    if(city.length()>0) {
                        tvplace.setText(city + "," + country);
                    }else{
                        tvplace.setText(country);
                    }
                    tvcontact.setText(" " + userItems.getUser_mobile());
                    String status ="";
                    status = userItems.getIsverified();
                    if (status.length() == 0 || status=="")
                        status = "No";
                    tvverify.setText(Html.fromHtml("Verified: " + "<font color='#24a699'>" + status.toUpperCase() + "</font>"));


                    String fbid = userItems.getUser_fb_id();
                    if (fbid != "0" && fbid.length() != 0) {

                        if(isLoggedIn()) {
                            callbackManager = CallbackManager.Factory.create();

                            accessTokenTracker = new AccessTokenTracker() {
                                @Override
                                protected void onCurrentAccessTokenChanged(
                                        AccessToken oldAccessToken,
                                        AccessToken currentAccessToken) {
                                }
                            };
                            accessToken = AccessToken.getCurrentAccessToken();

                            LoginManager.getInstance().logInWithReadPermissions(RideDetail.this, Arrays.asList("user_friends"));
                            GraphRequest request = GraphRequest.newGraphPathRequest(
                                    accessToken,
                                    "/" + fbid + "/friends",
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            JSONObject json = response.getJSONObject();
                                            if (json != null) {
                                                try {
                                                    JSONObject jsonobj = json.getJSONObject("summary");
                                                    if (jsonobj.length() != 0) {
                                                        String totfriend = jsonobj.getString("total_count");
                                                        tvfbcnt.setVisibility(View.VISIBLE);
                                                        tvfbcnt.setText(getString(R.string.fbfriends) + totfriend);
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                            request.executeAsync();
                        }
                    }
                    M.hideLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    M.hideLoadingDialog();
                    Log.d(TAG, "error:" + error.getMessage());
                }
            });

    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void checkFollowingRider(){
        String followerid=ride.getUserid();
        M.hideLoadingDialog();
        M.showLoadingDialog(RideDetail.this);
        UserAPI mCommentsAPI = APIService.createService(UserAPI.class);
        mCommentsAPI.checkFollowing(userid, followerid, new Callback<userRegPojo>() {
            @Override
            public void success(userRegPojo pojo, Response response) {
                if (pojo.getStatus().equalsIgnoreCase("success")) {
                    btnfollow.setBackgroundColor(getResources().getColor(R.color.radioselected));
                    btnfollow.setText("Following");
                    btnfollow.setClickable(false);
                }
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                System.out.print("error:" + error.getMessage());
            }

        });
    }

    public  void followrider(){

        String followerid=ride.getUserid();
        M.showLoadingDialog(RideDetail.this);
        UserAPI mCommentsAPI = APIService.createService(UserAPI.class);
        mCommentsAPI.addFollowers(userid, followerid, new Callback<userRegPojo>() {
            @Override
            public void success(userRegPojo pojo, Response response) {
                if(pojo.getStatus().equalsIgnoreCase("success")){
                    btnfollow.setBackgroundColor(getResources().getColor(R.color.radioselected));
                    btnfollow.setText("Following");
                    btnfollow.setClickable(false);
                }
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                System.out.print("error:" + error.getMessage());
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

