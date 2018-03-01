package com.example.alarmclock.activities;

import android.support.v4.app.Fragment;

import com.example.alarmclock.fragment.RingSelectFragment;


/**
 * Created by Administrator on 2018/2/3.
 */

public class RingSelectActivity extends SingleFragmentDialogActivity {

    @Override
    protected Fragment createFragment() {
        return new RingSelectFragment();
    }

}