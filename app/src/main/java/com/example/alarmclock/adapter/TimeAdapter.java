package com.example.alarmclock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alarmclock.bean.model.WatchTime;
import com.strangeman.alarmclock.R;


import java.util.List;

/**
 * Created by Administrator on 2018/2/23.
 */

public class TimeAdapter extends ArrayAdapter<WatchTime> {
    private int resourceId;

    public TimeAdapter(Context context, int resource, List<WatchTime> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        WatchTime watchTime=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView timeTag=(TextView)view.findViewById(R.id.time_tag);
        TextView time=(TextView)view.findViewById(R.id.time);
        timeTag.setText(watchTime.getTag());
        time.setText(watchTime.getTime());
        return view;
    }
}
