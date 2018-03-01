package com.example.alarmclock.activities;


import android.support.v4.app.Fragment;

import com.example.alarmclock.fragment.AlarmClockEditFragment;


/**
 * Created by Administrator on 2018/2/3.
 */

public class AlarmClockEditActivity extends SingleFragmentOrdinaryActivity{


    @Override
    protected Fragment createFragment() {
        return new AlarmClockEditFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 按下返回键开启移动退出动画
        overridePendingTransition(0, R.anim.move_out_bottom);
    }



}
