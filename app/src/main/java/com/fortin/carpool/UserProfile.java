package com.fortin.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.ProfilePojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.userRegAPI;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserProfile extends AppCompatActivity {

    ImageView ivprofile;
    TextView tvusernm,tvoffercnt,tvtakecnt,tvverify, tvgender, tvcity, tvmembersince;
    String TAG="UserProfile";
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.backicon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent it1=getIntent();
        userid= it1.getStringExtra("memberid");
        if(userid.length()==0){
            Intent it=new Intent(UserProfile.this,RequestNotification.class);
            finish();
            startActivity(it);
        }

        ivprofile=(ImageView)findViewById(R.id.ivprofileimg);
        tvusernm=(TextView)findViewById(R.id.tvusrname);
        tvoffercnt=(TextView)findViewById(R.id.tvpostcnt);
        tvtakecnt=(TextView)findViewById(R.id.tvtakecnt);
        tvverify=(TextView)findViewById(R.id.verify);
        tvgender=(TextView)findViewById(R.id.tvgender);
        tvcity=(TextView)findViewById(R.id.tvcity);
        tvmembersince=(TextView)findViewById(R.id.tvmembersince);
        getProfileData();
    }

    public void getProfileData(){
        M.showLoadingDialog(UserProfile.this);
        userRegAPI mCommentsAPI = APIService.createService(userRegAPI.class);
        mCommentsAPI.getuserprofile(userid, new Callback<ProfilePojo>() {
            @Override
            public void success(ProfilePojo pojo, Response response) {

                String profileimg = AppConst.imgurl + "profile/" + pojo.getProfile_img();

                Picasso.with(UserProfile.this)
                        .load(profileimg)
                        .placeholder(R.drawable.user_icon)
                        .error(R.drawable.user_icon)
                        .into(ivprofile);

                tvusernm.setText(pojo.getUsername());
                String status=pojo.getVerify();
                if(status.equals("") || status.length()==0)
                    status="No";
                tvverify.setText( Html.fromHtml(""+"<font color='#24a699'>"+status.toUpperCase()+"</font>"));
                tvoffercnt.setText(""+pojo.getPostridecnt());
                tvtakecnt.setText(""+pojo.getTakeridecnt());
                tvcity.setText(""+pojo.getUser_city() + " "+ pojo.getUser_country());
                tvgender.setText(""+pojo.getUser_gender());
                if(pojo.getMembersince()!=null)
                    tvmembersince.setText(""+pojo.getMembersince());
                else
                    tvmembersince.setText("");
                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                System.out.print("error:" + error.getMessage());
            }

        });
    }
}
