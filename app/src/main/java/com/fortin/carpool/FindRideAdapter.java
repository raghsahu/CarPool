package com.fortin.carpool;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortin.carpool.Model.RidePojo;
import com.fortin.carpool.helper.CropSquareTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FindRideAdapter extends BaseAdapter{

    private ArrayList<RidePojo> listData;
    private LayoutInflater layoutInflater;
    Context context;


    public FindRideAdapter(Context aContext, ArrayList<RidePojo> listData) {
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
            convertView = layoutInflater.inflate(R.layout.findride_row, null);
            holder = new ViewHolder();
            holder.tvvehicle = (TextView) convertView.findViewById(R.id.vehicle);
            holder.tvroute=(TextView)convertView.findViewById(R.id.route);
            holder.tvseats=(TextView)convertView.findViewById(R.id.seats);
            holder.tvrate=(TextView)convertView.findViewById(R.id.rate);
            holder.tvtime=(TextView)convertView.findViewById(R.id.datetime);
            holder.ivrider=(ImageView)convertView.findViewById(R.id.riderimg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvvehicle.setText(listData.get(position).getVehicle());
        holder.tvtime.setText(listData.get(position).getDep_time());
        holder.tvroute.setText(listData.get(position).getSource()+"-"+listData.get(position).getDestination());
        holder.tvrate.setText(Html.fromHtml(listData.get(position).getRate()));
        holder.tvseats.setText(listData.get(position).getSeats()+" Seats Avaliable");
        String img=AppConst.imgurl+"profile/"+listData.get(position).getUserimg();
        Picasso.with(context)
                .load(img)
                .transform(new CropSquareTransformation())
                .placeholder(R.drawable.user_icon_dark)
                .error(R.drawable.user_icon_dark)
                .into(holder.ivrider);
        return convertView;
    }

    class ViewHolder {
        TextView tvroute,tvrate,tvvehicle,tvtime,tvseats;
        ImageView ivrider;
    }

}


