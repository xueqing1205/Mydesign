package com.example.alarmclock.util;

import com.squareup.otto.Bus;

/**
 * Created by Administrator on 2018/1/30.
 */

public class OttoBus  {
    private volatile static Bus bus = null;

    private OttoBus() {
    }

    public static Bus getInstance() {
        if (bus == null) {
            synchronized (OttoBus.class) {
                bus = new Bus();
            }
        }
        return bus;
    }
}
