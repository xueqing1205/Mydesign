package com.example.alarmclock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.example.alarmclock.activities.AlarmClockOntimeActivity;
import com.example.alarmclock.bean.model.AlarmClock;
import com.example.alarmclock.common.AlarmClockCommon;
import com.example.alarmclock.common.AlarmClockStatus;
import com.example.alarmclock.db.AlarmClockOperate;
import com.example.alarmclock.util.MyUtil;


/**
 * Created by Administrator on 2018/1/30.
 */

public class AlarmClockBroadcast extends BroadcastReceiver {

    private static final String LOG_TAG = "AlarmClockBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmClock alarmClock = intent
                .getParcelableExtra(AlarmClockCommon.ALARM_CLOCK);
        AlarmClock alarmClock1 = intent
                .getParcelableExtra("test1");
        String test=intent.getStringExtra("test");

        if (alarmClock != null) {
            // 单次响铃
            if (alarmClock.getWeeks() == null) {
                AlarmClockOperate.getInstance().updateAlarmClock(false,
                        alarmClock.getId());
                Intent i = new Intent("com.example.alarmclock.AlarmClockOff");
                context.sendBroadcast(i);
            } else {
                // 重复周期闹钟
                MyUtil.startAlarmClock(context, alarmClock);
            }
        }
        // 小睡已执行次数
        int napTimesRan = intent.getIntExtra(AlarmClockCommon.NAP_RAN_TIMES, 0);
        // 当前时间
        long now = SystemClock.elapsedRealtime();
        // 当上一次闹钟响起时间等于0
        if (AlarmClockStatus.sLastStartTime == 0) {
            // 上一次闹钟响起时间等于当前时间
            AlarmClockStatus.sLastStartTime = now;
            // 当上一次响起任务距离现在小于3秒时
        } else if ((now - AlarmClockStatus.sLastStartTime) <= 3000) {

            Log.d(LOG_TAG, "进入3秒以内再次响铃 小睡次数：" + napTimesRan + "距离时间毫秒数："
                    + (now - AlarmClockStatus.sLastStartTime));
            Log.d(LOG_TAG, "WeacStatus.strikerLevel："
                    + AlarmClockStatus.sStrikerLevel);
//            LogUtil.d(LOG_TAG, "闹钟名：" + alarmClock.getTag());

            // 当是新闹钟任务并且上一次响起也为新闹钟任务时，开启了时间相同的多次闹钟，只保留一个其他关闭
            if ((napTimesRan == 0) & (AlarmClockStatus.sStrikerLevel == 1)) {
                return;
            }
        } else {
            // 上一次闹钟响起时间等于当前时间
            AlarmClockStatus.sLastStartTime = now;
        }

        Intent it = new Intent(context, AlarmClockOntimeActivity.class);

        // 新闹钟任务
        if (napTimesRan == 0) {
            // 设置响起级别为闹钟
            AlarmClockStatus.sStrikerLevel = 1;
            // 小睡任务
        } else {
            // 设置响起级别为小睡
            AlarmClockStatus.sStrikerLevel = 2;
            // 小睡已执行次数
            it.putExtra(AlarmClockCommon.NAP_RAN_TIMES, napTimesRan);
        }
        it.putExtra(AlarmClockCommon.ALARM_CLOCK, alarmClock);
        // 清除栈顶的Activity
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(it);
    }
}
