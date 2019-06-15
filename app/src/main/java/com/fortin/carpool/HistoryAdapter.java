package com.fortin.carpool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.RidePojo;
import com.fortin.carpool.Model.userRegPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RequestedRideAPI;
import com.fortin.carpool.WebServices.RideAPI;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class HistoryAdapter extends BaseAdapter{

    private ArrayList<RidePojo> listData;
    private LayoutInflater layoutInflater;
    Context context;
    String tripid;

    public HistoryAdapter(Context aContext, ArrayList<RidePojo> listData) {
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
            convertView = layoutInflater.inflate(R.layout.history_row, null);
            holder = new ViewHolder();
            holder.linearLayout=(LinearLayout)convertView.findViewById(R.id.linearret);
            holder.linearstatus=(LinearLayout)convertView.findViewById(R.id.linearstatus);
            holder.tvsource = (TextView) convertView.findViewById(R.id.tvsource);
            holder.tvdestination=(TextView)convertView.findViewById(R.id.tvdestination);
            holder.tvdestime=(TextView)convertView.findViewById(R.id.tvdestime);
            holder.tvrate=(TextView)convertView.findViewById(R.id.tvrate);
            holder.tvcar=(TextView)convertView.findViewById(R.id.tvcarnm);
            holder.tvstatus=(TextView)convertView.findViewById(R.id.txtstatus);
            holder.tvroutes=(TextView)convertView.findViewById(R.id.routes);
            holder.tvretime=(TextView)convertView.findViewById(R.id.tvreturntime);
            holder.tvyes=(TextView)convertView.findViewById(R.id.yes);
            holder.tvno=(TextView)convertView.findViewById(R.id.no);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String tripid=listData.get(position).getTrip_id();
        String rtime=listData.get(position).getRet_time();
        String dtime=listData.get(position).getDep_time();
        holder.tvdestime.setText(dtime);
        if(listData.get(position).getTrip_type().equalsIgnoreCase("Round trip")) {
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.tvretime.setText(rtime);
        }else
            holder.linearLayout.setVisibility(View.GONE);
        String route=listData.get(position).getRoute();
        holder.tvroutes.setText(route);
        holder.tvsource.setText(listData.get(position).getSource());
        holder.tvdestination.setText(listData.get(position).getDestination());


        if(listData.get(position).getRate()=="")
            holder.tvrate.setVisibility(View.GONE);
        else
            holder.tvrate.setText(listData.get(position).getRate()+"\u20B9"+"(per km)");
        holder.tvcar.setText(listData.get(position).getVehicle());
        String status=listData.get(position).getTrip_status();

        if(status.equalsIgnoreCase("CANCEL") || status.equalsIgnoreCase("completed")) {
            holder.linearstatus.setVisibility(View.GONE);
            holder.tvstatus.setVisibility(View.VISIBLE);
            holder.tvstatus.setText(status);
        }
        else {
            holder.tvstatus.setVisibility(View.GONE);
            holder.linearstatus.setVisibility(View.VISIBLE);
        }
        holder.tvyes.setTag(position);
        holder.tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changestatus("COMPLETED",tripid,position);
            }
        });
        holder.tvno.setTag(position);
        holder.tvno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure for Cancel Ride?")
                        .setContentText("")
                        .setCancelText("No,cancel plx!")
                        .setConfirmText("Yes,cancel it!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sDialog) {
                               changestatus("CANCEL",tripid,position);
                                sDialog.cancel();

                            }
                        })
                        .show();
            }
        });
        return convertView;
    }

    public void changestatus(final String status,String tripid, final int pos){
        M.showLoadingDialog(context);
        RideAPI mCommentsAPI = APIService.createService(RideAPI.class);
        mCommentsAPI.changestatus(tripid, status, new Callback<userRegPojo>() {
            @Override
            public void success(userRegPojo user, Response response) {
                Toast.makeText(context, user.getMessage(), Toast.LENGTH_SHORT).show();
                if (user.getStatus().equalsIgnoreCase("success")) {
                    listData.get(pos).setTrip_status(status);
                    notifyDataSetChanged();
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

    class ViewHolder {
        TextView tvsource,tvdestination,tvdestime,tvretime,tvrate,tvcar,tvstatus,tvroutes,tvyes,tvno;
        LinearLayout linearLayout,linearstatus;
    }

}


