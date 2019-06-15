package com.fortin.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.RidePojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RideAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RidesHistory extends Fragment implements View.OnClickListener {

    TextView recently,upcoming,norides;
    ListView lvrecently,lvupcoming;
    List<RidePojo> recentlylist=new ArrayList<>();
    List<RidePojo> upcominglist=new ArrayList<>();
    HistoryAdapter adapter;
    MyRideAdapter myadapter;
    String userid,TAG="myrides";
    public RidesHistory() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_my_posted_rides, container, false);

        recently=(TextView)rootView.findViewById(R.id.recently);
        upcoming=(TextView)rootView.findViewById(R.id.upcoming);
        norides=(TextView)rootView.findViewById(R.id.norides);
        norides.setVisibility(View.GONE);
        lvrecently=(ListView)rootView.findViewById(R.id.lvrecentlyride);
        lvrecently.setVisibility(View.GONE);
        lvupcoming=(ListView)rootView.findViewById(R.id.lvupcomingride);
        lvupcoming.setVisibility(View.GONE);
        
        userid=M.getID(getActivity());
        if(userid.length()>0 && userid!="0"){
           recently.setOnClickListener(this);
           upcoming.setOnClickListener(this);
        }

        upcoming.setBackgroundResource(R.color.dividercolor);
        recently.setBackgroundResource(R.color.colorAccent);
        getUpcomingRide();
        return rootView;
    }

    public void getRide(){
        M.showLoadingDialog(getActivity());
        RideAPI mCommentsAPI = APIService.createService(RideAPI.class);
        mCommentsAPI.getMyRides(userid, new Callback<List<RidePojo>>() {
            @Override
            public void success(List<RidePojo> pojo, Response response) {
                recentlylist = pojo;
                if(recentlylist.size()==0) {
                    lvupcoming.setVisibility(View.GONE);
                    lvrecently.setVisibility(View.GONE);
                    norides.setVisibility(View.VISIBLE);
                    norides.setText("Ride not Posted....");
                }
                else {
                    lvrecently.setVisibility(View.VISIBLE);
                    lvupcoming.setVisibility(View.GONE);
                    norides.setVisibility(View.GONE);
                    adapter = new HistoryAdapter(getActivity(), (ArrayList<RidePojo>) recentlylist);
                    lvrecently.setAdapter(adapter);
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

    public void getUpcomingRide(){
        M.showLoadingDialog(getActivity());
        RideAPI mCommentsAPI = APIService.createService(RideAPI.class);
        mCommentsAPI.getUpcomingRides(userid, new Callback<List<RidePojo>>() {
            @Override
            public void success(List<RidePojo> upojo, Response response) {
                upcominglist = upojo;

                if (upcominglist.size() == 0) {
                    lvupcoming.setVisibility(View.GONE);
                    lvrecently.setVisibility(View.GONE);
                    norides.setVisibility(View.VISIBLE);
                    norides.setText("Ride not Posted....");
                }
                else {
                    lvupcoming.setVisibility(View.VISIBLE);
                    lvrecently.setVisibility(View.GONE);
                    norides.setVisibility(View.GONE);
                    myadapter = new MyRideAdapter(getActivity(), (ArrayList<RidePojo>) upcominglist);
                    lvupcoming.setAdapter(myadapter);
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
    public void onClick(View v) {
        if(v.getId()==R.id.recently){
            recently.setBackgroundResource(R.color.dividercolor);
            upcoming.setBackgroundResource(R.color.colorAccent);
            getRide();
        }else if(v.getId()==R.id.upcoming){
            upcoming.setBackgroundResource(R.color.dividercolor);
            recently.setBackgroundResource(R.color.colorAccent);
            getUpcomingRide();
        }
    }


}
