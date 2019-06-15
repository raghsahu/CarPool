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
import com.fortin.carpool.Model.SendPojo;
import com.fortin.carpool.Model.userRegPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RequestedRideAPI;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SendHistoryAdapter extends BaseAdapter{

    private ArrayList<SendPojo> listData;
    private LayoutInflater layoutInflater;
    Context context;


    public SendHistoryAdapter(Context aContext, ArrayList<SendPojo> listData) {
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
            convertView = layoutInflater.inflate(R.layout.send_history_row, null);
            holder = new ViewHolder();
            holder.linear=(LinearLayout)convertView.findViewById(R.id.pickuplinear);
            holder.ivcancel=(TextView)convertView.findViewById(R.id.cancel);
            holder.tvdept=(TextView)convertView.findViewById(R.id.deptime);
            holder.tvsendtime=(TextView)convertView.findViewById(R.id.senttime);
            holder.tvseat=(TextView)convertView.findViewById(R.id.seat);
            holder.tvcmt=(TextView)convertView.findViewById(R.id.cmt);
            holder.tvpick=(TextView)convertView.findViewById(R.id.pickup);
            holder.tvexpected=(TextView)convertView.findViewById(R.id.expectedtime);
            holder.tvstatus=(TextView)convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
       final String tripdetailid=listData.get(position).getTripdetailid();
        final String triproute= listData.get(position).getPickuppoint()+" to "+listData.get(position).getDroppoint();
        final String seat=listData.get(position).getRequestseat();

        holder.tvpick.setText(listData.get(position).getPickuppoint()+" - "+listData.get(position).getDroppoint());
        if(listData.get(position).getComment()!="")
            holder.tvcmt.setText("Comment: "+listData.get(position).getComment());
        else
            holder.tvcmt.setVisibility(View.GONE);
        holder.tvsendtime.setText(listData.get(position).getSendtime());

        holder.tvseat.setText(seat);
        final String st=listData.get(position).getStatus();
        if(st.equalsIgnoreCase("reject") || st.equalsIgnoreCase("cancel"))
            holder.ivcancel.setVisibility(View.GONE);
        else
            holder.ivcancel.setVisibility(View.VISIBLE);
        holder.tvstatus.setText(st);
        String dtime=listData.get(position).getTripdepature();
        holder.tvdept.setText(dtime);
        if(st.equalsIgnoreCase("accept")){
            holder.tvexpected.setText(listData.get(position).getPickuptime());
        }else {
           holder.linear.setVisibility(View.GONE);
        }
        holder.ivcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure for Cancel Request?")
                        .setContentText("Won't be able to recover this request!")
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
                                M.showLoadingDialog(context);
                                RequestedRideAPI mCommentsAPI = APIService.createService(RequestedRideAPI.class);
                                mCommentsAPI.cancelSendRequest(tripdetailid,M.getUsername(context), triproute, st, new Callback<userRegPojo>() {
                                    @Override
                                    public void success(userRegPojo user, Response response) {
                                        Toast.makeText(context, user.getMessage(), Toast.LENGTH_SHORT).show();
                                        if (user.getStatus().equalsIgnoreCase("success")) {
                                            listData.remove(position);
                                            notifyDataSetChanged();
                                            sDialog.setTitleText("Cancelled!")
                                                    .setContentText("You cancelled Request!")
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
        TextView tvdept,tvsendtime,tvseat,tvcmt,tvpick,tvexpected,tvstatus;
        TextView ivcancel;
        LinearLayout linear;
    }

}


