package com.example.alarmclock.activities;

import android.os.Bundle;
import android.util.Log;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


/**
 * Created by Administrator on 2018/1/22.
 */

public class BaseActivity extends SwipeBackActivity {

    private static final String LOG_TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.i(LOG_TAG, getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
