package com.example.alarmclock.activities;

import android.support.v4.app.Fragment;

import com.example.alarmclock.fragment.ThemeFragment;


/**
 * Created by Administrator on 2018/2/14.
 */

public class ThemeActivity extends SingleFragmentOrdinaryActivity {
    @Override
    protected Fragment createFragment() {
        return new ThemeFragment();
    }
}




