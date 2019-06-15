/*
 * Copyright (c) 2014-2015 Amberfog.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fortin.carpool.countrylookup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortin.carpool.R;

import java.util.ArrayList;


public class CountryAdapter extends ArrayAdapter<Country> {

    private LayoutInflater mLayoutInflater;
    ArrayList<Country> list=new ArrayList<>();
    Context context;

    public CountryAdapter(Context context,ArrayList<Country> data) {
        super(context, 0,data);
        this.context=context;
        mLayoutInflater = LayoutInflater.from(context);
        list=data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.item_country_drop,parent,false);

        Country country = list.get(position);

        TextView name = (TextView) listItem.findViewById(R.id.country_name);
        name.setText(country.getName());

        return listItem;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ImageView mImageView;
        TextView mNameView;
        TextView mCodeView;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_country_drop, parent, false);
        }
        mImageView = (ImageView) convertView.findViewById(R.id.image);
        mNameView = (TextView) convertView.findViewById(R.id.country_name);
        mCodeView = (TextView) convertView.findViewById(R.id.country_code);
        Country country = list.get(position);
        if (country != null) {
            mNameView.setText(country.getName());
            mCodeView.setText(country.getCountryCodeStr());
        }
        return convertView;
    }

}
