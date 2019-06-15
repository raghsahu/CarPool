package com.fortin.carpool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.MemberPojo;
import com.fortin.carpool.Model.userRegPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RideAPI;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MemberAdapter extends BaseAdapter {

    private ArrayList<MemberPojo> listData;
    String triproute, ridername;
    private LayoutInflater layoutInflater;
    Context context;
    String tripid;

    public MemberAdapter(Context aContext, ArrayList<MemberPojo> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
        context=aContext;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.member_row, null);
            holder = new ViewHolder();
           // holder.tvdrop = (TextView) convertView.findViewById(R.id.tvdrop);
            holder.tvmob=(TextView)convertView.findViewById(R.id.mobile);
            holder.tvnm=(TextView)convertView.findViewById(R.id.name);
            holder.tvpick=(TextView)convertView.findViewById(R.id.tvpick);
            holder.tvpicktime=(TextView)convertView.findViewById(R.id.picktime);
            holder.ivreject=(TextView)convertView.findViewById(R.id.reject);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String tripdetailid=listData.get(position).getTripdetailid();
        final String tripid=listData.get(position).getTripid();
        final String nm=listData.get(position).getMembernm();
        final String seat=listData.get(position).getSeat();
        holder.tvnm.setText(nm);
        holder.tvmob.setText(listData.get(position).getMembermob());
      //  holder.tvdrop.setText(listData.get(position).getDrop());
        holder.tvpick.setText(listData.get(position).getPickup()+" - "+listData.get(position).getDrop());
        holder.tvpicktime.setText(listData.get(position).getPickuptime());
        holder.ivreject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure for Reject "+nm+" Request?")
                        .setContentText("Won't be able to recover this request!")
                        .setCancelText("No,reject plx!")
                        .setConfirmText("Yes,reject it!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.setTitleText("Cancelled!")
                                        .setContentText("You have not reject "+ nm+" Request.)")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);


                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog) {

                                ridername = M.getUsername(context);
                                triproute = listData.get(position).getPickup()+" - "+listData.get(position).getDrop();
                                M.showLoadingDialog(context);
                                RideAPI mCommentsAPI = APIService.createService(RideAPI.class);
                                mCommentsAPI.riderResponse(tripdetailid,  ridername, triproute, seat, tripid, "Rereject","", new Callback<userRegPojo>() {
                                    @Override
                                    public void success(userRegPojo user, Response response) {

                                        Toast.makeText(context, user.getMessage(), Toast.LENGTH_SHORT).show();
                                        if(user.getStatus().equalsIgnoreCase("success")) {
                                            listData.remove(position);
                                            notifyDataSetChanged();
                                            sDialog.setTitleText("Rejected!")
                                                .setContentText("You rejected "+nm+" Request!")
                                                .setConfirmText("OK")
                                                .showCancelButton(false)
                                                .setCancelClickListener(null)
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                        }

                                        M.hideLoadingDialog();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        M.hideLoadingDialog();
                                        System.out.println("error" + error.getMessage());
                                    }
                                });

                            }
                        })
                        .show();
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tvnm,tvmob,tvpick,tvpicktime;
        TextView ivreject;
    }

}


