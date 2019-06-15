package com.fortin.carpool;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.PushNotificationHistory;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.UserAPI;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class Notification extends Fragment {

    public Notification() {
        // Required empty public constructor
    }


    ListView lv;
    List<PushNotificationHistory> list;
    Image_adapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);



        lv = (ListView)rootView.findViewById(R.id.lv);



        getdata();
 return rootView;
    }


    public void getdata() {
        M.showLoadingDialog(getActivity());
        UserAPI mUsersAPI = APIService.createService(UserAPI.class, M.getToken(getActivity()));
        mUsersAPI.getPushHistory(M.getID(getActivity()), new Callback<List<PushNotificationHistory>>() {
            @Override
            public void success(List<PushNotificationHistory> resp, Response response) {
                M.L(response.getBody() + "");

                if(resp != null)
                {
                    list = resp;
                    adapter = new Image_adapter(getActivity());
                    adapter.notifyDataSetChanged();
                    lv.setAdapter(adapter);
                }else
                {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("No Notifications")
                            .setConfirmText("Alright")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                    getActivity().finish();
                                    startActivity(i);
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }



                M.hideLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                M.hideLoadingDialog();
                M.L(getString(R.string.ServerError));

                System.out.println("url--" + error.getUrl());
            }
        });
    }








    public class Image_adapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public Image_adapter(Context c) {
            mInflater = LayoutInflater.from(c);

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.message_row,
                        null);

                holder = new ViewHolder();
                holder.txtnotification = (TextView) convertView
                        .findViewById(R.id.txtnotification);


                holder.txtdate = (TextView) convertView
                        .findViewById(R.id.txtdate);




                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtnotification.setText(list.get(position).getMessage());

            holder.txtdate.setText(list.get(position).getCreated_at());




            return convertView;
        }

        class ViewHolder {
            TextView txtnotification, txtdate;
            Button btndelete;

        }

    }



}
