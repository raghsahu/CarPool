package com.fortin.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.SendPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RequestedRideAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SendRequestHistory extends Fragment {

    ListView lvsendhistory;
    TextView txt;
    String userid,TAG="History";
    SendHistoryAdapter adapter;
    ArrayList<SendPojo> list=new ArrayList<>();
    public SendRequestHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_send_request_history, container, false);


        lvsendhistory = (ListView)rootView. findViewById(R.id.lvsendhistory);
        txt=(TextView)rootView.findViewById(R.id.nohistory);
        getHistory();
        
        return rootView;
    }

    public void getHistory() {
    userid=M.getID(getActivity());

    M.showLoadingDialog(getActivity());

    RequestedRideAPI mUsersAPI = APIService.createService(RequestedRideAPI.class, M.getToken(getActivity()));
    mUsersAPI.getHistory(userid, new Callback<List<SendPojo>>() {
                @Override
                public void success(List<SendPojo> sendPojos, Response response) {
                    if(sendPojos!=null){
                        lvsendhistory.setVisibility(View.VISIBLE);
                        txt.setVisibility(View.GONE);
                        list= (ArrayList<SendPojo>) sendPojos;
                        adapter = new SendHistoryAdapter(getActivity(), list);
                        lvsendhistory.setAdapter(adapter);
                    }else {
                        lvsendhistory.setVisibility(View.GONE);
                        txt.setVisibility(View.VISIBLE);
                    }
                    M.hideLoadingDialog();
                }

                @Override
                public void failure(RetrofitError error) {
                    M.hideLoadingDialog();
                    Log.d(TAG, "error:" + error.getMessage());
                }
            }

    );
}

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent it=new Intent(getActivity(),MainActivity.class);
//        finish();
//        startActivity(it);
//    }
}
