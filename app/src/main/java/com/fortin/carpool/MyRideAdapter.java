package com.fortin.carpool;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fortin.carpool.Model.RidePojo;
import com.fortin.carpool.Model.userRegPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RequestedRideAPI;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MyRideAdapter extends BaseAdapter implements View.OnClickListener {

    private ArrayList<RidePojo> listData;

    private LayoutInflater layoutInflater;
    Context context;
    String tripid;

    public MyRideAdapter(Context aContext, ArrayList<RidePojo> listData) {
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
            convertView = layoutInflater.inflate(R.layout.myride_row, null);
            holder = new ViewHolder();
            holder.retlinear=(LinearLayout)convertView.findViewById(R.id.retlinear);
            holder.tvsource = (TextView) convertView.findViewById(R.id.tvsource);
            holder.tvdestination=(TextView)convertView.findViewById(R.id.tvdest);
            holder.tvdestime=(TextView)convertView.findViewById(R.id.tvdestime);
            holder.tvrettime=(TextView)convertView.findViewById(R.id.tvrettime);
            holder.tvseats=(TextView)convertView.findViewById(R.id.tvseat);
            holder.tvrate=(TextView)convertView.findViewById(R.id.tvrate);
            holder.tvcar=(TextView)convertView.findViewById(R.id.tvcarnm);
            holder.tvstatus=(TextView)convertView.findViewById(R.id.txtstatus);
            holder.tvlink=(TextView)convertView.findViewById(R.id.txtlink);
            holder.tvroutes=(TextView)convertView.findViewById(R.id.tvroutes);
            holder.ivcanel=(TextView)convertView.findViewById(R.id.btncancel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String tid=listData.get(position).getTrip_id();
            holder.tvlink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it=new Intent(context,MemberDetail.class);
                    it.putExtra("tripid",tid);
                    context.startActivity(it);
                   }
            });

        String rtime=listData.get(position).getRet_time();
        String dtime=listData.get(position).getDep_time();
        if(listData.get(position).getTrip_type().equalsIgnoreCase("Round Trip")) {
            holder.retlinear.setVisibility(View.VISIBLE);
            holder.tvrettime.setText(rtime);
        }else {
            holder.retlinear.setVisibility(View.GONE);
        }
        String route=listData.get(position).getRoute();
        holder.tvroutes.setText(route);

        holder.tvsource.setText(listData.get(position).getSource());
        holder.tvdestination.setText(listData.get(position).getDestination());
        holder.tvdestime.setText(dtime);
        holder.tvseats.setText(listData.get(position).getSeats()+" Avaliable");
        if(listData.get(position).getRate()=="")
            holder.tvrate.setVisibility(View.GONE);
        else
            holder.tvrate.setText(listData.get(position).getRate()+"\u20B9"+" (Per km)");
        holder.tvcar.setText(listData.get(position).getVehicle());
        String status=listData.get(position).getTrip_status();
        if(status.equalsIgnoreCase("CANCEL")) {
            holder.ivcanel.setVisibility(View.GONE);
            holder.tvstatus.setVisibility(View.VISIBLE);
            holder.tvstatus.setText(status);
        }
        else {
            holder.tvstatus.setVisibility(View.GONE);
            holder.ivcanel.setOnClickListener(this);
            holder.ivcanel.setTag(position);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btncancel) {
            tripid = listData.get((Integer) v.getTag()).getTrip_id();
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure for Cancel Ride?")
                    .setContentText("Won't be able to recover this ride!")
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

                             RequestedRideAPI mCommentsAPI = APIService.createService(RequestedRideAPI.class);
                             mCommentsAPI.cancelRide(tripid, new Callback<userRegPojo>() {
                             @Override
                             public void success(userRegPojo pojo, Response response) {
                               sDialog.setTitleText("Cancelled!")
                                    .setContentText("Your Posted Ride has been cancelled!")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setCancelClickListener(null)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                             }

                             @Override
                             public void failure(RetrofitError error) {
                                System.out.print("error:" + error.getMessage());
                             }
                            });
                        }
                    })
                    .show();
            }
        }

    class ViewHolder {
        TextView tvsource,tvdestination,tvdestime,tvrettime,tvseats,tvrate,tvcar,tvstatus,tvlink,tvroutes;
        TextView ivcanel;
        LinearLayout retlinear;
    }

}


