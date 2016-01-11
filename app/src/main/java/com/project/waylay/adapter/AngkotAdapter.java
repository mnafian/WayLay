package com.project.waylay.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.waylay.R;

import java.util.ArrayList;

/**
 * Created on : January 08, 2016
 * Author     : mnafian
 * Name       : M. Nafian
 * Email      : mnafian@icloud.com
 * GitHub     : https://github.com/mnafian
 * LinkedIn   : https://id.linkedin.com/in/mnafian
 */
public class AngkotAdapter extends BaseAdapter {

    Context ctx = null;
    ArrayList<String> listarray = null;
    private LayoutInflater mInflater = null;


    public AngkotAdapter(Activity activty, ArrayList<String> list) {
        this.ctx = activty;
        mInflater = activty.getLayoutInflater();
        this.listarray = list;
    }

    @Override
    public int getCount() {
        return listarray.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.way_list_item_angkot, null);

            holder.titlename = (TextView) convertView.findViewById(R.id.tittle_text);
            holder.tittleAlamat = (TextView) convertView.findViewById(R.id.tittle_near);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String datavalue = listarray.get(position);
        holder.titlename.setText(datavalue);
        holder.tittleAlamat.setText("200 M Near you");
        return convertView;
    }

    private static class ViewHolder {
        TextView titlename, tittleAlamat;
    }
}
