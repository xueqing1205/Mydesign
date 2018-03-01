package com.example.alarmclock.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alarmclock.fragment.AlarmClockFragment;
import com.example.alarmclock.fragment.StopWatchFragment;
import com.example.alarmclock.fragment.ThemeFragment;
import com.example.alarmclock.fragment.TimeFragment;
import com.example.alarmclock.util.ActivityCollector;
import com.example.alarmclock.util.MyUtil;
import com.strangeman.alarmclock.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/3.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String LOG_TAG = "MainActivity";

    /**
     * 闹钟Tab控件
     */
    private TextView tv_alarm_clock;

    /**
     * 计时Tab控件
     */
    private TextView tv_time;

    /**
     * 秒表Tab控件
     */
    private TextView tv_stop_watch;

    /**
     * 更多Tab控件
     */
    private TextView tv_more;

    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager mFm;

    /**
     * Tab未选中文字颜色
     */
    private int mUnSelectColor;

    /**
     * Tab选中时文字颜色
     */
    private int mSelectColor;

    /**
     * 滑动菜单视图
     */
    private ViewPager mViewPager;

    /**
     * Tab页面集合
     */
    private List<Fragment> mFragmentList;

    /**
     * 当前Tab的Index
     */
    private int mCurrentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setSwipeBackEnable(true);
        setContentView(R.layout.activity_main);
        setThemeWallpaper();
//      获取fragmentManager
        mFm=getSupportFragmentManager();
        //设置选中和未选中tab颜色
        mSelectColor=getResources().getColor(android.R.color.holo_blue_light);
        mUnSelectColor=getResources().getColor(android.R.color.darker_gray);

        // 初始化布局元素
        initViews();
        // 启动程序后选中Tab为闹钟
        setTabSelection(0);
    }

    private void initViews() {
        // 取得Tab布局
        // 闹钟Tab界面布局
        ViewGroup tab_alarm_clock = (ViewGroup) findViewById(R.id.tab_alarm_clock);
        // 计时Tab界面布局
        ViewGroup tab_time = (ViewGroup) findViewById(R.id.tab_time);
        // 秒表Tab界面布局
        ViewGroup tab_stop_watch = (ViewGroup) findViewById(R.id.tab_stop_watch);
        // 更多Tab界面布局
        ViewGroup tab_more = (ViewGroup) findViewById(R.id.tab_more);

        // 取得Tab控件
        tv_alarm_clock = (TextView) findViewById(R.id.tv_alarm_clock);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_stop_watch = (TextView) findViewById(R.id.tv_stop_watch);
        tv_more = (TextView) findViewById(R.id.tv_more);

        // 设置Tab点击事件
        tab_alarm_clock.setOnClickListener(this);
        tab_stop_watch.setOnClickListener(this);
        tab_time.setOnClickListener(this);
        tab_more.setOnClickListener(this);

        // 设置Tab页面集合
        mFragmentList = new ArrayList<>();
        // 展示闹钟的Fragment
        AlarmClockFragment mAlarmClockFragment = new AlarmClockFragment();
        // 展示计时的Fragment
        TimeFragment mTimeFragment = new TimeFragment();
        // 展示秒表的Fragment
        StopWatchFragment mStopWatchFragment= new StopWatchFragment();
        // 展示更多的Fragment
        ThemeFragment mMoreFragment = new ThemeFragment();

        mFragmentList.add(mAlarmClockFragment);
        mFragmentList.add(mTimeFragment);
        mFragmentList.add(mStopWatchFragment);
        mFragmentList.add(mMoreFragment);

        // 设置ViewPager
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(mFm));
        // 设置一边加载的page数
        mViewPager.setOffscreenPageLimit(3);
        // TODO：切换渐变
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {
                setTabSelection(index);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    private void setThemeWallpaper() {
        ViewGroup vg = (ViewGroup) findViewById(R.id.llyt_activity_main);
        MyUtil.setBackground(vg, this);
    }


    public void setTabSelection(int index) {
        if(mCurrentIndex==index){
            return ;
        }

        mCurrentIndex = index;
        // 改变ViewPager视图
        mViewPager.setCurrentItem(index, false);
        // 清除掉上次的选中状态
        clearSelection();
        // 判断传入的Index
        switch (index) {
            // 闹钟
            case 0:
                // 改变闹钟控件的图片和文字颜色
                setTextView( tv_alarm_clock,
                        mSelectColor);
                break;
            // 计时
            case 1:
                // 改变计时控件的图片和文字颜色
                setTextView( tv_time, mSelectColor);
                break;
            // 秒表
            case 2:
                // 改变秒表控件的图片和文字颜色
                setTextView( tv_stop_watch, mSelectColor);
                break;
            // 更多
            case 3:
                // 改变更多控件的图片和文字颜色
                setTextView(tv_more, mSelectColor);
                break;
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            // 当选中闹钟Tab时
            case R.id.tab_alarm_clock:
                // 切换闹钟视图
                setTabSelection(0);
                break;
            // 当选中计时Tab时
            case R.id.tab_time:
                // 切换计时视图
                setTabSelection(1);
                break;
            // 当选中秒表Tab时
            case R.id.tab_stop_watch:
                // 切换秒表视图
                setTabSelection(2);
                break;
            // 当选中更多Tab时
            case R.id.tab_more:
                // 切换更多视图
                setTabSelection(3);
                break;
            default:
                break;
        }
    }

    /**
     * ViewPager适配器
     */
    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

    }

    /**
     * 设置Tab布局
     * @param textView Tab文字
     * @param color    Tab文字颜色
     */
    private void setTextView(TextView textView, int color) {
//        @SuppressWarnings("deprecation") Drawable drawable = getResources().getDrawable(iconId);
//        if (drawable != null) {
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(),
//                    drawable.getMinimumHeight());
//            // 设置图标
//            textView.setCompoundDrawables(null, drawable, null, null);
//        }
        // 设置文字颜色
        textView.setTextColor(color);
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        // 设置闹钟Tab为未选中状态
        setTextView( tv_alarm_clock,
                mUnSelectColor);
        // 设置秒表Tab为未选中状态
        setTextView( tv_stop_watch, mUnSelectColor);
        // 设置计时Tab为未选中状态
        setTextView(tv_time, mUnSelectColor);
        // 设置更多Tab为未选中状态
        setTextView(tv_more, mUnSelectColor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
