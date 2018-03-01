package com.example.alarmclock.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/2/10.
 */

public class CountDownService extends Service {
    private TimerTask mTimerTask;
    private TimerUpdateListener mTimerUpdateListener;

    private CountDownService.TimerBinder mTimerBinder = new TimerBinder();

    public class TimerBinder extends Binder {
        public void cancel() {
            if (mTimerTask != null) {
                mTimerTask.cancel();
                stopSelf();
            }
        }

        public void setTimerUpdateListener(TimerUpdateListener timerUpdateListener) {
            mTimerUpdateListener = timerUpdateListener;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mTimerBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startCountDown();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public interface TimerUpdateListener {
        void OnUpdateTime();
    }

    public void startCountDown() {
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                if (mTimerUpdateListener != null) {
                    mTimerUpdateListener.OnUpdateTime();
                }
            }
        };

        new Timer(true).schedule(mTimerTask, 1000, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
    }
}

