package com.fortin.carpool;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fortin.carpool.Model.VehiclePojo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class VehicleAdapter extends BaseAdapter{

    private ArrayList<VehiclePojo> listData;
    private LayoutInflater layoutInflater;
    Context context;


    public VehicleAdapter(Context aContext, ArrayList<VehiclePojo> listData) {
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
            convertView = layoutInflater.inflate(R.layout.custom_vehicle_row, null);
            holder = new ViewHolder();
            holder.linear=(LinearLayout)convertView.findViewById(R.id.vrowlinear);
            holder.tvcompany = (TextView) convertView.findViewById(R.id.company);
            holder.tvmodel=(TextView)convertView.findViewById(R.id.model);
            holder.tvnum=(TextView)convertView.findViewById(R.id.number);
            holder.tvseat=(TextView)convertView.findViewById(R.id.seats);
            holder.ivcar=(ImageView)convertView.findViewById(R.id.car);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(position%2==0)
            holder.linear.setBackgroundResource(R.drawable.card_background_dark);
        else
            holder.linear.setBackgroundResource(R.drawable.card_background);

        String vehtype="("+listData.get(position).getType()+")";
        holder.tvcompany.setText(listData.get(position).getCompany());
        holder.tvnum.setText(listData.get(position).getNumber());
        holder.tvmodel.setText(listData.get(position).getModel()+" "+vehtype);
        holder.tvseat.setText(listData.get(position).getSeats()+" Seats");
        String img=AppConst.imgurl+"vehicle/"+listData.get(position).getImage();

        Picasso.with(context)
                .load(img)
                .placeholder(R.drawable.offerride1)
                .error(R.drawable.offerride1)
                .into(holder.ivcar);
        return convertView;
    }

    class ViewHolder {
        TextView tvcompany,tvmodel,tvnum,tvseat;
        ImageView ivcar;
        LinearLayout linear;
    }

}


