package com.example.alarmclock.bean.model;

/**
 * Created by Administrator on 2018/2/23.
 */

public class WatchTime {

    private String tag;

    private String time;

    public WatchTime(){

    }
    public WatchTime(String tag, String time) {
        this.tag = tag;
        this.time = time;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
