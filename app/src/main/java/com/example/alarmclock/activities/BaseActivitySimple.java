package com.example.alarmclock.activities;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Created by Administrator on 2018/1/30.
 */

public class BaseActivitySimple extends FragmentActivity {
    private static final String LOG_TAG = "BaseActivitySimple";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
