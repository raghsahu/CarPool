package com.fortin.carpool;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.VehiclePojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RideAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyVehicles extends Fragment {

    ListView lvvehicle;
    String userid="",TAG="Vehicle";
    List<VehiclePojo> list=new ArrayList<>();
    VehicleAdapter adapter;

    public MyVehicles() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_my_vehicles, container, false);

        FloatingActionButton fab = (FloatingActionButton)rootView. findViewById(R.id.fab);
        lvvehicle=(ListView)rootView.findViewById(R.id.lvvehicle);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppConst.selvehicle=null;
               Intent intent=new Intent(getActivity(),VehicleRegistration.class);
               startActivity(intent);
            }
        });

        fillvehicledetail();
        lvvehicle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(isStoragePermissionGranted()) {
                    Intent it = new Intent(getActivity(), VehicleRegistration.class);
                    AppConst.selvehicle = list.get(position);
                    startActivity(it);
                }


            }
        });

        return rootView;
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("here","Permission is granted");
                return true;
            } else {

                Log.v("Here","Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Here","Permission is granted");
            return true;
        }


    }
    public void fillvehicledetail(){

        userid= M.getID(getActivity());
        Log.d(TAG,"userid:"+userid);
        if(userid.length()==0)
            getActivity().finish();
        else {
            M.showLoadingDialog(getActivity());
            RideAPI mCommentsAPI = APIService.createService(RideAPI.class);
            mCommentsAPI.getVehicles(userid, new Callback<List<VehiclePojo>>() {
                @Override
                public void success(List<VehiclePojo> pojo, Response response) {
                    list = pojo;

                    if (list!=null && !list.isEmpty()) {
                        adapter = new VehicleAdapter(getActivity(), (ArrayList<VehiclePojo>) list);
                        lvvehicle.setAdapter(adapter);
                    } else {
                        Toast.makeText(getActivity(), "Your Vehicle is not register.First Register your vehicle for Post Ride", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), VehicleRegistration.class);
                        getActivity(). finish();
                        startActivity(intent);
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


}
