package com.example.alarmclock.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alarmclock.bean.model.WatchTime;
import com.example.alarmclock.listener.OnVisibleListener;
import com.example.alarmclock.service.CountDownService;
import com.example.alarmclock.view.MyStopWatch;
import com.strangeman.alarmclock.R;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018/1/22.
 */

public class StopWatchFragment extends LazyLoadFragment implements View.OnClickListener,CountDownService.TimerUpdateListener{

    /**
     * Log tag ：TimeFragment
     */
    private static final String LOG_TAG = "StopWatchFragment";

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean mIsPrepared;

    /**
     *秒表
     */
    private MyStopWatch myStopWatch;

    /**
     * 开始按钮
     */
    private TextView mStartBtn;

    /**
     * 停止按钮
     */
    private TextView mStopBtn;

    /**
     * 重置按钮
     */
    private TextView resetBtn;

    /**
     * 计次按钮
     */
    private TextView countBtn;

    /**
     * 绑定倒计时service
     */
    private boolean mIsBind;

    /**
     * 计次信息
     */
    private List<WatchTime> watchTimeList;

    private int count;

    private TimeAdapter adapter;

    private OnVisibleListener mOnVisibleListener;

    private ViewGroup mStartLLyt;

    private ViewGroup mStartLLyt2;

    @Override
    protected void lazyLoad() {
        if (!mIsPrepared && mIsVisible) {
            if (mOnVisibleListener != null) {
                mOnVisibleListener.onVisible();
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fm_stop_watch, container, false);
        watchTimeList=new ArrayList<>();
        count=0;
        mOnVisibleListener = new OnVisibleListener() {
            @Override
            public void onVisible() {
                showLayout(view);
                initViews(view);
            }
        };
        return view;
    }

    private void showLayout(View view) {
        // 秒表布局
        ViewStub viewStub = (ViewStub) view.findViewById(R.id.viewstub_stop_watch);
        viewStub.inflate();

        // 加载中进度框
        ViewGroup progressBar = (ViewGroup) view.findViewById(R.id.progress_bar_llyt);
        progressBar.setVisibility(View.GONE);
    }

    private void initViews(View view) {

        mStartLLyt = (ViewGroup) view.findViewById(R.id.btn_start_llyt);
        mStartLLyt2 = (ViewGroup) view.findViewById(R.id.btn_start_llyt2);

        mStartBtn = (TextView) view.findViewById(R.id.btn_start);
        mStopBtn = (TextView) view.findViewById(R.id.btn_stop);
        resetBtn = (TextView) view.findViewById(R.id.btn_reset);
        countBtn=(TextView) view.findViewById(R.id.btn_time);

        mStartBtn.setOnClickListener(StopWatchFragment.this);
        mStopBtn.setOnClickListener(StopWatchFragment.this);
        resetBtn.setOnClickListener(StopWatchFragment.this);
        countBtn.setOnClickListener(StopWatchFragment.this);

        myStopWatch=(MyStopWatch)view.findViewById(R.id.watch);

        mIsPrepared = true;

        adapter=new TimeAdapter(getActivity(),R.layout.time_item,watchTimeList);
        ListView listView=(ListView)view.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

//    private void setTime() {
//        SharedPreferences preferences = getContext().getSharedPreferences(
//                AlarmClockCommon.EXTRA_AC_SHARE, Activity.MODE_PRIVATE);
//        long time = preferences.getLong(AlarmClockCommon.COUNTDOWN_TIME, 0);
//        myStopWatch.setTime(time);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 开始
            case R.id.btn_start:
                startWatch();
                setStratLlyt2Visible();
                break;
            // 暂停
            case R.id.btn_stop:
                myStopWatch.stop();
                stopCountDown();
                setStratLlytVisible();
                break;
            // 重置
            case R.id.btn_reset:
                myStopWatch.reset();
                count=0;
                adapter.clear();
                adapter.notifyDataSetChanged();
                stopCountDown();
                setStratLlytVisible();
                break;
            case R.id.btn_time:
                count++;
                WatchTime watchTime=new WatchTime();
                watchTime.setTag("计次"+count);
                watchTime.setTime(myStopWatch.getmDisplayWatchTime());
                adapter.add(watchTime);
                adapter.notifyDataSetChanged();
        }
    }

    private void startWatch() {
        if( myStopWatch.start()){
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
            CountDownService.TimerBinder binder = (CountDownService.TimerBinder) service;
            binder.setTimerUpdateListener(StopWatchFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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

    @Override
    public void OnUpdateTime() {
        try {
            if (myStopWatch != null && !getActivity().isFinishing()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myStopWatch.updateDisplayTime();
                    }
                });
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "OnUpdateTime(): e.toString()");
        }
    }

//    /**
//     * 显示开始计时后的暂停按钮
//     */
//    private void setStopVisible() {
//        mStartBtn.setVisibility(View.GONE);
//        mStopBtn.setVisibility(View.VISIBLE);
//    }

//    /**
//     * 显示开始计时后的开始按钮
//     */
//    private void setStartVisible() {
//        mStartBtn.setVisibility(View.VISIBLE);
//        mStopBtn.setVisibility(View.GONE);
//    }

    /**
     * 显示开始计时前的布局
     */
    private void setStratLlytVisible() {
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

}
