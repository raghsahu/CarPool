package com.fortin.carpool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.ResponseModel;
import com.fortin.carpool.Model.UserPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.UserAPI;
import com.fortin.carpool.helper.CropSquareTransformation;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean homescreen = false;
    String userid;
    ImageView imgprofile;
    TextView txtname;
    FloatingActionButton fab;
    String TAG="MainActivity";
    FirebaseAuth mAuth;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=MainActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            FirebaseApp.getInstance();
        } catch (IllegalStateException ex) {
            FirebaseApp.initializeApp(context);
        }
        mAuth=FirebaseAuth.getInstance();
        userid   = M.getID(MainActivity.this);
        if (userid.length() != 0) {
            updateRegID();
        }
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userid.length() == 0) {
                      showalert();
                }
                else {
                  Intent  i = new Intent(MainActivity.this, FindRide.class);
                    startActivity(i);
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        if(userid==null && userid.isEmpty()) {
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        }
        View header = navigationView.getHeaderView(0);


        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, UserRegistration.class);
                startActivity(i);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        if(getIntent().getExtras() == null)
        {
            navigationView.getMenu().getItem(0).setChecked(true);
        }else if(getIntent().getStringExtra("newcar") != null)
        {
           navigationView.getMenu().getItem(1).setChecked(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, new FeedActivity()).commit();
        }
        imgprofile = (ImageView) header.findViewById(R.id.imgprofile);
        txtname = (TextView)header.findViewById(R.id.txtname);
        getUserProfile();
        isStoragePermissionGranted();
    }

    @Override
    public void onBackPressed() {
        // your code.
        if(homescreen) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            Intent i = new Intent(this, MainActivity.class);
            finish();
            startActivity(i);
            overridePendingTransition(0, 0);
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("here","Permission is granted");
                return true;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},2);

                Log.v("Here","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Here","Permission is granted");
            return true;
        }


    }
    public void getUserProfile(){

        UserAPI mCommentsAPI = APIService.createService(UserAPI.class);
        mCommentsAPI.getuser(userid, new Callback<UserPojo>() {
                    @Override
                    public void success(UserPojo user, Response response) {

                        String unm=user.getUser_name();
                        String umail=user.getUser_email();
                        String ucity=user.getUser_city();
                        String upostal=user.getPostal_code();
                        String usergender=user.getUser_gender();
                        String ubdate=user.getUser_dob();
                        String pimg=AppConst.imgurl+"profile/"+user.getUser_profile_img();
                        String imgi=AppConst.imgurl+"identity/"+user.getUser_identity_img();

                    if(unm!=null && !unm.isEmpty()) {
                        txtname.setText("" + unm);
                    }
                        Picasso.with(MainActivity.this)
                                .load(pimg)
                                .transform(new CropSquareTransformation())
                                .placeholder(R.drawable.user_icon)
                                .error(R.drawable.user_icon)
                                .into(imgprofile);

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        System.out.println("error" + error.getMessage());
                    }
                }
        );
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Intent i = null;
        String title = getString(R.string.app_name);
        fab.setVisibility(View.VISIBLE);
        if (id == R.id.nav_feed) {
            fragment = new FeedActivity();
            homescreen = true;
            title = "Latest Rides";
        } else if (id == R.id.nav_vehicle) {

            if (userid.length() == 0) {
                fragment = new FeedActivity();
                homescreen = true;
                title = "Latest Rides";
                showalert();
            }
            else {
                fab.setVisibility(View.GONE);
                fragment = new MyVehicles();
                homescreen = false;
                title = "My Vehichles";
            }
        } else if (id == R.id.nav_offerride) {
            if (userid.length() == 0) {
                fragment = new FeedActivity();
                homescreen = false;
                title = "Latest Rides";
                showalert();
            }
            else {
                i = new Intent(MainActivity.this, Offerride.class);
                startActivity(i);
            }
        } else if (id == R.id.nav_myrides) {
            if (userid.length() == 0) {
                fragment = new FeedActivity();
                homescreen = false;
                title = "Latest Rides";
                showalert();
            }
            else {
                fragment = new RidesHistory();
                homescreen = false;
                title = "My Rides";
            }
        } else if (id == R.id.nav_myrequests) {
            if (userid.length() == 0) {
                fragment = new FeedActivity();
                homescreen = true;
                title = "Latest Rides";
                showalert();
            }
            else {
                fragment = new SendRequestHistory();
                homescreen = false;
                title = "Requests Sent";
            }
        } else if (id == R.id.nav_requestreceived) {
            if (userid.length() == 0) {
                fragment = new FeedActivity();
                homescreen = true;
                title = "Latest Rides";
                showalert();
            }
            else {
                fragment = new RequestNotification();
                homescreen = false;
                title = "Requests Received";
            }
        } else if (id == R.id.nav_mymessages) {
            if (userid.length() == 0) {
                fragment = new FeedActivity();
                homescreen = true;
                title = "Latest Rides";
                showalert();
            }
            else {
                fragment = new Notification();
                homescreen = true;
                title = "My Messages";
            }
        }else if (id == R.id.nav_share) {
           share();
        }else if (id == R.id.nav_logout) {
            if(mAuth.getCurrentUser()!=null)
                FirebaseAuth.getInstance().signOut();
            logout();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainFrame, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


        public void showalert()
        {

                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Not Logged in!")
                        .setContentText("You will need to log in to access this!")
                        .setConfirmText("Login")
                        .setCancelText("Not Now")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                Intent i = new Intent(MainActivity.this, Login.class);
                                finish();
                                startActivity(i);
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();



        }


    public void share()
    {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                String packageName = resolveInfo.activityInfo.packageName;
                Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
                targetedShareIntent.setType("text/plain");
                if (TextUtils.equals(packageName, "com.facebook.katana")) {
                    targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://thecarpool.in");
                } else {
                    targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, AppConst.sharetext);
                }
                targetedShareIntent.setPackage(packageName);
                targetedShareIntents.add(targetedShareIntent);
            }
            Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[targetedShareIntents.size()]));
            startActivity(chooserIntent);
        }
    }

    public void logout()
    {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("You want to logout form this App?")
                .setConfirmText(getString(R.string.alright))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        M.logOut(MainActivity.this);
                        Intent i = new Intent(MainActivity.this, Login.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.this.finish();
                        startActivity(i);

                        sDialog.dismissWithAnimation();
                    }
                }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sDialog) {

                sDialog.dismissWithAnimation();
            }
        })
                .show();
    }

    public void updateRegID() {
        String token=null;
        if(AppConst.fcm_id!=null && AppConst.fcm_id.trim().length()>0)
            token=AppConst.fcm_id;
        else{
            token = FirebaseInstanceId.getInstance().getToken();
        }
        if (token != null) {
            UserAPI mGlobalAPI = APIService.createService(UserAPI.class);
            mGlobalAPI.updateRegID(M.getID(MainActivity.this), token, "android" , new Callback<ResponseModel>() {
                @Override
                public void success(ResponseModel response, Response response2) {
                    Log.d(TAG,"fcm res:"+response.getSuccess());
                }

                @Override
                public void failure(RetrofitError error) {
                    System.out.println("error---" + error.getMessage());
                }
            });
        }
    }

}
