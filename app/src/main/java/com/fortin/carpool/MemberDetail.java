package com.fortin.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.MemberPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RequestedRideAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MemberDetail extends AppCompatActivity {

    ListView lvmember;
    String TAG="MemberDetail",tripid="";
    ArrayList<MemberPojo> memberlist=new ArrayList<>();
    MemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.backicon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lvmember=(ListView)findViewById(R.id.lvmember);

        Intent it=getIntent();
        tripid=it.getStringExtra("tripid");
        if(tripid.length()!=0){
            getmember();
        }

    }

    public void getmember(){

        M.showLoadingDialog(MemberDetail.this);
        RequestedRideAPI mCommentsAPI = APIService.createService(RequestedRideAPI.class);
        mCommentsAPI.getMember(tripid, new Callback<List<MemberPojo>>() {
            @Override
            public void success(List<MemberPojo> pojo, Response response) {
                memberlist = (ArrayList) pojo;
                if (memberlist != null && !memberlist.isEmpty()) {
                    adapter=new MemberAdapter(MemberDetail.this,memberlist);
                    lvmember.setAdapter(adapter);
                }else {
                    Toast.makeText(MemberDetail.this,"No Member for this trip...",Toast.LENGTH_SHORT).show();
                    finish();
                    onBackPressed();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.print("error:" + error.getMessage());
            }
        });
        M.hideLoadingDialog();

    }

}
