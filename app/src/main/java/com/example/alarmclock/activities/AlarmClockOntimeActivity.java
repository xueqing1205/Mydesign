package com.example.alarmclock.activities;

import android.support.v4.app.Fragment;

import com.example.alarmclock.fragment.AlarmClockOntimeFragment;


/**
 * Created by Administrator on 2018/1/30.
 */

public class AlarmClockOntimeActivity extends SingleFragmentDialogActivity {


    @Override
    protected Fragment createFragment() {
        return new AlarmClockOntimeFragment();
    }

    @Override
    public void onBackPressed() {
        // 禁用back键
    }


}


