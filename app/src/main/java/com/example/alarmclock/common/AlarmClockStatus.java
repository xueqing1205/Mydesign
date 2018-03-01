package com.example.alarmclock.common;

/**
 * Created by Administrator on 2018/1/30.
 */

public class AlarmClockStatus {

    /**
     * 启动的AlarmClockOnTimeActivity个数
     */
    public static int sActivityNumber = 0;

    /**
     * 上一次闹钟响起时间
     */
    public static long sLastStartTime = 0;

    /**
     * 上一次响起级别（1：闹钟，2：小睡，0：无）
     */
    public static int sStrikerLevel = 0;

}
