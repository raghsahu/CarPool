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

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.RequestedRidePojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RequestedRideAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestNotification extends Fragment {

    ListView lvnotification;

    String TAG="GetRequests",userid;
    List<RequestedRidePojo> list=new ArrayList<>();
    RequestedRideAdapter adapter;

    public RequestNotification() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_request_notification, container, false);
   
        userid=M.getID(getActivity());

        if(userid.length()==0 || userid.equals('0')){
            Intent it=new Intent(getActivity(),Login.class);
            getActivity().  finish();
            startActivity(it);
        }
        lvnotification=(ListView)rootView.findViewById(R.id.lvnotification);
        getRequestNotification();

        return rootView;
    }

    public void getRequestNotification(){
        M.showLoadingDialog(getActivity());
        RequestedRideAPI mCommentsAPI = APIService.createService(RequestedRideAPI.class);
        mCommentsAPI.getRequest(userid, new Callback<List<RequestedRidePojo>>() {
            @Override
            public void success(List<RequestedRidePojo> pojo, Response response) {
                list = pojo;
                Log.d(TAG, response.toString());
                adapter = new RequestedRideAdapter(getActivity(), (ArrayList<RequestedRidePojo>) list);
                lvnotification.setAdapter(adapter);
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
