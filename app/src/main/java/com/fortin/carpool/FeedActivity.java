package com.fortin.carpool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class FeedActivity extends Fragment {

    String TAG="FeedActiviy";
    FindRideAdapter adapter;
    ListView lvfeed;
    TextView tvmsg;

    ArrayList<RidePojo> list=new ArrayList<>();
    public FeedActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_feed, container, false);

        lvfeed=(ListView)rootView.findViewById(R.id.lvfeed);
        tvmsg=(TextView)rootView.findViewById(R.id.tvnofeed);
        tvmsg.setVisibility(View.GONE);
        getFeed();
        AppConst.pickupoint=null;
        AppConst.droppoint=null;
        lvfeed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppConst.selride = list.get(position);
                Intent intent = new Intent(getActivity(), RideDetail.class);
              
                startActivity(intent);
            }
        });
        
        return rootView;
    }

    public void getFeed(){
        M.showLoadingDialog(getActivity());
        RideAPI mCommentsAPI = APIService.createService(RideAPI.class);
        mCommentsAPI.getFeed(new Callback<List<RidePojo>>() {
            @Override
            public void success(List<RidePojo> pojo, Response response) {
                if (pojo.size() != 0) {
                   list= (ArrayList<RidePojo>) pojo;
                        adapter = new FindRideAdapter(getActivity(), list);
                        lvfeed.setAdapter(adapter);
                        M.hideLoadingDialog();

                } else {
                    tvmsg.setVisibility(View.VISIBLE);
                    lvfeed.setVisibility(View.GONE);
                    tvmsg.setText("Sorry! Feeds not avaliable... Please Try Later... ");
                    M.hideLoadingDialog();
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


}
