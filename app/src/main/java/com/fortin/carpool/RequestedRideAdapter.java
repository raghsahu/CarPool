package com.fortin.carpool;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fortin.carpool.Model.M;
import com.fortin.carpool.Model.RequestedRidePojo;
import com.fortin.carpool.Model.userRegPojo;
import com.fortin.carpool.WebServices.APIService;
import com.fortin.carpool.WebServices.RideAPI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestedRideAdapter extends BaseAdapter implements View.OnClickListener {

    private ArrayList<RequestedRidePojo> listData;
    private LayoutInflater layoutInflater;
    Context context;
    String tripdetailid,tripid,seat,status,TAG="adapter",exdt,expectedtime="";
    String triproute, ridername;
    int pos;
    Dialog dialog;

    //DatePicker
    final Calendar dateAndTime=Calendar.getInstance();
    SimpleDateFormat fmtDateAndTime=new SimpleDateFormat("dd MMM yyyy");
    SimpleDateFormat dtfmt=new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timefmt24=new SimpleDateFormat("hh:mm");
    SimpleDateFormat timefmt12=new SimpleDateFormat("hh:mm a");
    DatePickerDialog.OnDateSetListener d;


    //TimePicker
    int hour ;//= dateAndTime.get(Calendar.HOUR_OF_DAY);
    int minute;// = dateAndTime.get(Calendar.MINUTE);
    TimePickerDialog dtimepicker;

    public RequestedRideAdapter(Context aContext, ArrayList<RequestedRidePojo> listData) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.notification_row, null);
            holder = new ViewHolder();
            holder.tvusername = (TextView) convertView.findViewById(R.id.txtusernm);
            holder.tvpickup=(TextView)convertView.findViewById(R.id.txtpickup);
            holder.tvseats=(TextView)convertView.findViewById(R.id.txtselseats);
            holder.tvcmt=(TextView)convertView.findViewById(R.id.txtcmt);
            holder.tvdepsch=(TextView)convertView.findViewById(R.id.txtdeptschedule);
            holder.tvdetail=(TextView)convertView.findViewById(R.id.userlink);
            holder.accept=(TextView)convertView.findViewById(R.id.btnaccept);
            holder.reject=(TextView)convertView.findViewById(R.id.btnreject);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvdepsch.setText(listData.get(position).getDeptdate()+" "+listData.get(position).getDepttime());
        holder.tvusername.setText(listData.get(position).getUsername());
        holder.tvpickup.setText(listData.get(position).getPickup_point()+" - "+listData.get(position).getDrop_point());
        holder.tvseats.setText( listData.get(position).getSeats());
        String cmt=listData.get(position).getComment();
        if(cmt!="")
            holder.tvcmt.setText("Comment: "+cmt);
        else
            holder.tvcmt.setVisibility(View.GONE);
        holder.tvdetail.setOnClickListener(this);
        holder.tvdetail.setTag(position);
        holder.reject.setOnClickListener(this);
        holder.reject.setTag(position);
        holder.accept.setOnClickListener(this);
        holder.accept.setTag(position);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        pos=(Integer)v.getTag();
        tripdetailid=listData.get(pos).getTripdetail_id();
        tripid=listData.get(pos).getTrip_id();
        seat=listData.get(pos).getSeats();
        if(v.getId()==R.id.userlink){
            Intent it=new Intent(context,UserProfile.class);
            it.putExtra("memberid",listData.get(pos).getUserid());
            context.startActivity(it);
        }
        else if(v.getId()==R.id.btnreject){

            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure for Reject Request?")
                    .setContentText("Won't be able to recover this request!")
                    .setCancelText("No,Reject plx!")
                    .setConfirmText("Yes,Reject it!")
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

                            status="Reject";
                            ridername = M.getUsername(context);
                            triproute = listData.get(pos).getPickup_point() + " to " + listData.get(pos).getDrop_point();
                            riderresponse(status, pos,expectedtime);
                            sDialog.setTitleText("Cancelled!")
                                    .setContentText("Your Posted Ride has been cancelled!")
                                    .setConfirmText("OK")
                                    .showCancelButton(false)
                                    .setCancelClickListener(null)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                        }
                    })
                    .show();

        }
        if(v.getId()==R.id.btnaccept){
            dialog = new Dialog(context);
            dialog.setTitle("Select Expected DateTime for Reach to Pickup Point: ");
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            dialog.setContentView(R.layout.accept_dialog);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.WHITE));
            final EditText date=(EditText)dialog.findViewById(R.id.etdate);
            final EditText time=(EditText)dialog.findViewById(R.id.ettime);

            date.setText(listData.get(pos).getDeptdate());
            exdt=listData.get(pos).getDeptdate();
            time.setText(listData.get(pos).getDepttime());
            try {
                Date t=timefmt12.parse(listData.get(pos).getDepttime());
                hour=t.getHours();
                minute=t.getMinutes();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Button btnsend=(Button)dialog.findViewById(R.id.btnsendresponse);

            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog d1 = new DatePickerDialog(context, d, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH));
                    d1.show();
                }
            });
            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dtimepicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            try {
                                Date etime=timefmt24.parse(selectedHour + ":" + selectedMinute);
                                time.setText(timefmt12.format(etime));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }, hour, minute, false);//Yes 24 hour time
                    dtimepicker.setTitle("Select Expected Depture Time");
                    dtimepicker.show();
                }
            });
            btnsend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dt=date.getText().toString();
                    String t=time.getText().toString();

                    if(dt=="" && t==""){
                        Toast.makeText(context,"Select Expected DateTime for Reach to pickup point...",Toast.LENGTH_SHORT).show();
                    }else {
                        expectedtime=exdt+" "+t;
                        status="Accept";
                        ridername = M.getUsername(context);
                        riderresponse(status,pos,expectedtime);

                        dialog.cancel();
                    }
                }
            });

            d=new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker arg0, int arg1, int arg2,int arg3)
                {

                    dateAndTime.set(Calendar.YEAR, arg1);
                    dateAndTime.set(Calendar.MONTH,arg2);
                    dateAndTime.set(Calendar.DAY_OF_MONTH, arg3);

                    date.setText(fmtDateAndTime.format(dateAndTime.getTime()));
                    exdt=dtfmt.format(dateAndTime.getTime());

                }
            };
            dialog.show();

        }


    }

    public void riderresponse(String st, final int pos,String expectedtime){

        M.showLoadingDialog(context);
        RideAPI mCommentsAPI = APIService.createService(RideAPI.class);
        mCommentsAPI.riderResponse(tripdetailid, ridername, triproute, seat, tripid, st,expectedtime, new Callback<userRegPojo>() {
            @Override
            public void success(userRegPojo user, Response response) {

                Toast.makeText(context, user.getMessage(), Toast.LENGTH_SHORT).show();
                if(user.getStatus().equalsIgnoreCase("success")) {
                    listData.remove(pos);
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
        TextView tvusername,tvpickup,tvcmt,tvseats,tvdepsch,tvdetail;
        TextView accept,reject;
    }
}
