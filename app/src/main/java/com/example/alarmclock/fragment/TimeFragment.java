package com.example.alarmclock.fragment;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import com.example.alarmclock.activities.RingSelectActivity;
import com.example.alarmclock.activities.TimerOnTimeActivity;
import com.example.alarmclock.bean.event.TimerOnTimeEvent;
import com.example.alarmclock.bean.event.TimerStartEvent;
import com.example.alarmclock.bean.model.TimeModel;
import com.example.alarmclock.common.AlarmClockCommon;
import com.example.alarmclock.listener.OnVisibleListener;
import com.example.alarmclock.service.CountDownService;
import com.example.alarmclock.util.MyUtil;
import com.example.alarmclock.util.OttoBus;
import com.example.alarmclock.view.MyTimer;
import com.squareup.otto.Subscribe;
import com.strangeman.alarmclock.R;


import java.util.Calendar;

/**
 * Created by Administrator on 2018/1/22.
 */

public class TimeFragment extends LazyLoadFragment implements View.OnClickListener,
        MyTimer.OnTimeChangeListener, MyTimer.OnMinChangListener, CountDownService.TimerUpdateListener {
    /**
     * Log tag ：TimeFragment
     */
    private static final String LOG_TAG = "TimeFragment";

    /**
     * 计时器
     */
    private MyTimer mTimer;

    /**
     * 开始按钮
     */
    private TextView mStartBtn;

    /**
     * 开始按钮2
     */
    private TextView mStartBtn2;

    /**
     * 停止按钮
     */
    private TextView mStopBtn;

    /**
     * 初始计时按钮布局
     */
    private ViewGroup mStartLLyt;

    /**
     * 开启计时后按钮的布局
     */
    private ViewGroup mStartLLyt2;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean mIsPrepared;

    private OnVisibleListener mOnVisibleListener;

    /**
     * 绑定倒计时service
     */
    private boolean mIsBind;

    @Override
    protected void lazyLoad() {
        if (!mIsPrepared && mIsVisible) {
            if (mOnVisibleListener != null) {
                mOnVisibleListener.onVisible();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OttoBus.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fm_time, container, false);

        mOnVisibleListener = new OnVisibleListener() {
            @Override
            public void onVisible() {
                showTimeLayout(view);
                assignViews(view);
            }
        };

        return view;
    }

    private void assignViews(View view) {
        mStartLLyt = (ViewGroup) view.findViewById(R.id.btn_start_llyt);
        mStartLLyt2 = (ViewGroup) view.findViewById(R.id.btn_start_llyt2);

        mStartBtn = (TextView) view.findViewById(R.id.btn_start);
        mStartBtn2 = (TextView) view.findViewById(R.id.btn_start2);
        mStopBtn = (TextView) view.findViewById(R.id.btn_stop);
        // 重置按钮
        TextView resetBtn = (TextView) view.findViewById(R.id.btn_reset);

        // 铃声按钮
        TextView ringBtn = (TextView) view.findViewById(R.id.btn_ring);

        mStartBtn2.setOnClickListener(TimeFragment.this);
        mStopBtn.setOnClickListener(TimeFragment.this);
        resetBtn.setOnClickListener(TimeFragment.this);
        ringBtn.setOnClickListener(TimeFragment.this);

        mTimer = (MyTimer) view.findViewById(R.id.timer);
        mTimer.setOnTimeChangeListener(TimeFragment.this);
        mTimer.setTimeChangListener(TimeFragment.this);
        mTimer.setModel(TimeModel.Timer);
//        mTimer.setStartTime(0, 0, 0, true, false);
        setTimer();

        mIsPrepared = true;
    }

    private void showTimeLayout(View view) {
        // 计时布局
        ViewStub viewStub = (ViewStub) view.findViewById(R.id.viewstub_time);
        viewStub.inflate();

        // 加载中进度框
        ViewGroup progressBar = (ViewGroup) view.findViewById(R.id.progress_bar_llyt);
        progressBar.setVisibility(View.GONE);
    }

    private void setTimer() {
        SharedPreferences preferences = getContext().getSharedPreferences(
                AlarmClockCommon.EXTRA_AC_SHARE, Activity.MODE_PRIVATE);
        // 倒计时时间
        long countdown = preferences.getLong(AlarmClockCommon.COUNTDOWN_TIME, 0);
        if (countdown != 0) {
            // 剩余时间
            long remainTime;
            boolean isStop = preferences.getBoolean(AlarmClockCommon.IS_STOP, false);
            // 暂停状态
            if (isStop) {
                remainTime = countdown;
                setStart2Visible();
                mTimer.setIsStarted(true);
                // 正在计时状态
            } else {
                long now = SystemClock.elapsedRealtime();
                remainTime = countdown - now;
            }
            // 当剩余时间大于0
            if (remainTime > 0 && (remainTime / 1000 / 60) < 60) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(remainTime);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                mTimer.setStartTime(0, minute, second, isStop, false);
                setStratLlyt2Visible();
            } else {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putLong(AlarmClockCommon.COUNTDOWN_TIME, 0);
                editor.apply();

                initTimer();
            }
        } else {
            initTimer();
        }
    }

    /**
     * 初始化timer
     */
    private void initTimer() {
        setStartBtnNoClickable();
        mTimer.showAnimation();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 开始
            case R.id.btn_start:
                mTimer.setIsInDragButton(false);
                startCountDown();
                setStratLlyt2Visible();
                setStopVisible();
                break;
            // 开始2
            case R.id.btn_start2:
                startCountDown();
                setStopVisible();
                break;
            // 暂停
            case R.id.btn_stop:
                stopAlarmClockTimer();
                mTimer.stop();
                stopCountDown();
                setStart2Visible();
                break;
            // 重置
            case R.id.btn_reset:
                stopAlarmClockTimer();
                mTimer.reset();
                stopCountDown();
                setStratLlytVisible();
                break;
            // 铃声选择
            case R.id.btn_ring:
                processRingSelect();
                break;
        }
    }

    @Subscribe
    public void OnTimerStart(TimerStartEvent event) {
        startCountDownService();
    }


    @Subscribe
    public void onTimerOnTime(TimerOnTimeEvent event) {
        if (mTimer != null) {
            mTimer.clearRemainTime();
            mTimer.setIsStarted(false);
            mTimer.saveRemainTime(0,false);
            setStratLlytVisible();
        }
    }

    @Override
    public void OnUpdateTime() {
        try {
            if (mTimer != null && !getActivity().isFinishing()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTimer.updateDisplayTime();
                    }
                });
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "OnUpdateTime(): e.toString()");
        }
    }

    private void stopCountDown() {
        try {
            if (mIsBind) {
                getActivity().unbindService(mConnection);
                mIsBind = false;
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "stopCountDown(): " + e.toString());
        }
    }

    private void startCountDown() {
        if (mTimer.start()) {
            startCountDownService();
        }
    }

    private void startCountDownService() {
        if (!mIsBind) {
            Intent intent = new Intent(getActivity(), CountDownService.class);
            mIsBind = getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(LOG_TAG, ": onServiceConnected");
            CountDownService.TimerBinder binder = (CountDownService.TimerBinder) service;
            binder.setTimerUpdateListener(TimeFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(LOG_TAG, ": onServiceDisconnected");
        }
    };


    private void processRingSelect() {
        if (MyUtil.isFastDoubleClick()) {
            return;
        }

        SharedPreferences shares = getActivity().getSharedPreferences(
                AlarmClockCommon.EXTRA_AC_SHARE, Activity.MODE_PRIVATE);
        int ringPager = shares.getInt(AlarmClockCommon.RING_PAGER_TIMER, 0);
        String ringUrl = shares.getString(AlarmClockCommon.RING_URL_TIMER, AlarmClockCommon.DEFAULT_RING_URL);
        String ringName = shares.getString(AlarmClockCommon.RING_NAME_TIMER, getString(R.string.default_ring));

        Intent i = new Intent(getActivity(), RingSelectActivity.class);
        i.putExtra(AlarmClockCommon.RING_NAME, ringName);
        i.putExtra(AlarmClockCommon.RING_URL, ringUrl);
        i.putExtra(AlarmClockCommon.RING_PAGER, ringPager);
        i.putExtra(AlarmClockCommon.RING_REQUEST_TYPE, 1);
        startActivity(i);
    }

    /**
     * 停止倒计时广播
     */
    private void stopAlarmClockTimer() {
        Intent intent = new Intent(getContext(), TimerOnTimeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getContext(),
                1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getContext()
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
    }

    /**
     * 设置计时前开始按钮可以点击
     */
    private void setStartBtnClickable() {
        mStartBtn.setAlpha(1);
        //noinspection deprecation
//        mStartBtn.setBackground(getResources().getDrawable(R.drawable.bg_timer_button));
        mStartBtn.setOnClickListener(this);
    }

    /**
     * 设置计时前开始按钮不可点击
     */
    @SuppressWarnings("deprecation")
    private void setStartBtnNoClickable() {
        mStartBtn.setAlpha(0.2f);
        mStartBtn.setBackgroundDrawable(null);
        mStartBtn.setOnClickListener(null);
    }

    /**
     * 显示开始计时后的开始按钮
     */
    private void setStart2Visible() {
        mStartBtn2.setVisibility(View.VISIBLE);
        mStopBtn.setVisibility(View.GONE);
    }

    /**
     * 显示开始计时后的暂停按钮
     */
    private void setStopVisible() {
        mStartBtn2.setVisibility(View.GONE);
        mStopBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 显示开始计时前的布局
     */
    private void setStratLlytVisible() {
        setStartBtnNoClickable();
        mStartLLyt.setVisibility(View.VISIBLE);
        mStartLLyt2.setVisibility(View.GONE);
    }

    /**
     * 显示开始计时后的布局
     */
    private void setStratLlyt2Visible() {
        mStartLLyt.setVisibility(View.GONE);
        mStartLLyt2.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTimerStart(long timeRemain) {
        Log.d(LOG_TAG, "onTimerStart 距离计时结束：" + timeRemain);
        MyUtil.startAlarmTimer(getContext(), timeRemain);
    }

    @Override
    public void onTimeStop(long timeStart, long timeRemain) {
        stopCountDown();
        setStratLlytVisible();
    }

    @Override
    public void onMinChange(int minute) {
        Log.d(LOG_TAG, "minute change to " + minute);

        if (minute == 0) {
            setStartBtnNoClickable();
        } else {
            // 开始按钮为不可用状态
            if (mStartBtn.getAlpha() == 0.2f) {
                setStartBtnClickable();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        if (mTimer != null && mTimer.isStarted() ) {
            mTimer.setReset(false);
            setTimer();
            mTimer.setShowAnimation(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
        stopCountDown();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCountDown();
        OttoBus.getInstance().unregister(this);
    }
}