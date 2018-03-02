package com.example.alarmclock.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.alarmclock.util.ActivityCollector;
import com.strangeman.alarmclock.R;


/**
 * Created by Administrator on 2018/2/3.
 */

public abstract class SingleFragmentOrdinaryActivity extends BaseActivityOrdinary {

    /**
     * 抽象方法：创建Fragment
     *
     * @return Fragment
     */
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fm_activity);
        ActivityCollector.addActivity(this);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_containers);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_containers, fragment)
                    .commit();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}