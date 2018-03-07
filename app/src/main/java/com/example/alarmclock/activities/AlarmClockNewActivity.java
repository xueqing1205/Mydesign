package com.example.alarmclock.activities;

import android.support.v4.app.Fragment;

import com.example.alarmclock.fragment.AlarmClockNewFragment;
import com.strangeman.alarmclock.R;


/**
 * Created by Administrator on 2018/2/3.
 */

public class AlarmClockNewActivity extends SingleFragmentDialogActivity {

    @Override
    protected Fragment createFragment() {
        return new AlarmClockNewFragment();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 按下返回键开启渐变缩小动画
        overridePendingTransition(0, R.anim.zoomout);
    }


}