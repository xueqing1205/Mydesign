package com.example.alarmclock.bean.event;


import com.example.alarmclock.bean.model.AlarmClock;

/**
 * Created by Administrator on 2018/2/1.
 */

public class AlarmClockDeleteEvent {

    private int mPosition;
    private AlarmClock mAlarmClock;

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public AlarmClockDeleteEvent(int position, AlarmClock alarmClock) {
        this.mPosition = position;
        mAlarmClock = alarmClock;
    }

    public AlarmClock getAlarmClock() {
        return mAlarmClock;
    }
}
