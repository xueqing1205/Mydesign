package com.example.alarmclock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.alarmclock.bean.event.AlarmClockUpdateEvent;
import com.example.alarmclock.util.OttoBus;

public class AlarmClockProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        OttoBus.getInstance().post(new AlarmClockUpdateEvent());
    }
}