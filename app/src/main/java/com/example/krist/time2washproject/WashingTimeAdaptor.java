package com.example.krist.time2washproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WashingTimeAdaptor extends BaseAdapter {
    Context context;
    ArrayList<WashingTime> washingTimes;
    WashingTime washingTime;

    public WashingTimeAdaptor(Context c, ArrayList<WashingTime> washingTimeArrayList){
        context = c;
        washingTimes = washingTimeArrayList;
    }

    public int getCount(){
        if(washingTimes!=null) {
            return washingTimes.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        //return the item (demo object) in our demo array list at the given position
        if(washingTimes!=null) {
            return washingTimes.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater demoInflator =(LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = demoInflator.inflate(R.layout.washing_time_item, null);
        }

        washingTime = washingTimes.get(position);
        if(washingTime != null){
            TextView tvTime = convertView.findViewById(R.id.tvTime);
            TextView etDate = convertView.findViewById(R.id.tvDate);
            tvTime.setText(washingTime.getTime());
            etDate.setText(washingTime.getDate());
        }
        return convertView;
    }
}
