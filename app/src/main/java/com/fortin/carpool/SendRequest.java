package com.fortin.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.userRegPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RideAPI;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SendRequest extends AppCompatActivity implements View.OnClickListener {

    TextView tvpick,tvdrop;
    EditText etcomment;
    ImageView ivinc,ivdec;
    TextView tvseat;
    Button btnrequest;

    String comment="",trip_id,userid,pickpoint,droppoint,riderid,avaliableseats,seats,deptime;
    String TAG="SendRequest";
    int s=1,seat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.backicon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userid= M.getID(SendRequest.this);

        if(userid.length()==0 || userid.equals('0')){
            Intent it=new Intent(SendRequest.this,Login.class);
            finish();
            startActivity(it);
        }
        tvpick=(TextView)findViewById(R.id.pick);
        tvdrop=(TextView)findViewById(R.id.drop);
        etcomment=(EditText)findViewById(R.id.etcmt);
        tvseat=(TextView)findViewById(R.id.txtseat);
        ivdec=(ImageView)findViewById(R.id.ivdec);
        ivinc=(ImageView)findViewById(R.id.ivinc);
        btnrequest=(Button)findViewById(R.id.btnrequest);

        btnrequest.setOnClickListener(this);
        ivdec.setOnClickListener(this);
        ivinc.setOnClickListener(this);
        droppoint=AppConst.droppoint;
        pickpoint=AppConst.pickupoint;
        tvpick.setText(Html.fromHtml("PickUp Point: "+ "<b>"+pickpoint+"</b>"));
        tvdrop.setText(Html.fromHtml("Drop Point: "+"<b>"+droppoint+"</b>"));

        Intent it=getIntent();
        trip_id=it.getStringExtra("tripid");
        deptime=it.getStringExtra("depaturetime");
        riderid=it.getStringExtra("riderid");
        avaliableseats=it.getStringExtra("seats");
        tvseat.setText(s+"");
        seat=Integer.parseInt(avaliableseats);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ivinc){
            s=Integer.parseInt(tvseat.getText().toString());
            if(seat>s) {
                s++;
                tvseat.setText(s+"");
            }
            else
            Toast.makeText(SendRequest.this,"Only "+avaliableseats+" seats Avaliable",Toast.LENGTH_SHORT).show();
        }
        if(v.getId()==R.id.ivdec){
            s=Integer.parseInt(tvseat.getText().toString());
            if(s>1) {
                s--;
                tvseat.setText(s+"");
            }
        }
        if(v.getId()==R.id.btnrequest){

            comment=etcomment.getText().toString();
            seats=tvseat.getText().toString();

            M.showLoadingDialog(this);
            RideAPI mCommentsAPI = APIService.createService(RideAPI.class);
            mCommentsAPI.sendRequest(userid, M.getUsername(SendRequest.this),trip_id,deptime, riderid, pickpoint, droppoint,seats, comment, new Callback<userRegPojo>() {
                @Override
                public void success(userRegPojo user, Response response) {
                    M.hideLoadingDialog();

                    if (user.getStatus().equals("success")) {
                        Intent i = new Intent(SendRequest.this, MainActivity.class);
                        finish();
                        startActivity(i);
                        overridePendingTransition(0, 0);
                    }
                }
                @Override
                public void failure(RetrofitError error) {
                    M.hideLoadingDialog();
                    System.out.println("error" + error.getMessage());
                }
            });

        }
    }
}
