package com.example.alarmclock.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.example.alarmclock.bean.model.RingSelectItem;
import com.example.alarmclock.common.AlarmClockCommon;
import com.example.alarmclock.util.AudioPlayer;
import com.example.alarmclock.util.MyUtil;
import com.strangeman.alarmclock.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/4.
 */

public class RingSelectFragment extends BaseFragment implements View.OnClickListener {

    /**
     * 铃声种类集合
     */
    private List<Fragment> mFragmentList;

    /**
     * 铃声名
     */
    public static String sRingName;

    /**
     * 铃声地址
     */
    public static String sRingUrl;

    /**
     * 铃声界面
     */
    public static int sRingPager;

    /**
     * 铃声请求类型
     */
    public static int sRingRequestType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取得新建闹钟Activity传递过来的铃声信息参数
        Intent intent = getActivity().getIntent();
        sRingName = intent.getStringExtra(AlarmClockCommon.RING_NAME);
        sRingUrl = intent.getStringExtra(AlarmClockCommon.RING_URL);
        sRingPager = intent.getIntExtra(AlarmClockCommon.RING_PAGER, -1);
        sRingRequestType = intent.getIntExtra(AlarmClockCommon.RING_REQUEST_TYPE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_ring_select, container, false);
        // 铃声选择界面
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.ring_select_llyt);
        // 设置页面背景
        MyUtil.setBackground(viewGroup, getActivity());

        // 设置显示铃声列表的Fragment
        initFragment();
        // 设置ViewPager
        initViewPager(view);

        // 返回按钮
        ImageView actionCancel = (ImageView) view.findViewById(R.id.ring_select_cancel);
        actionCancel.setOnClickListener(this);

        // 保存按钮
        TextView actionSave = (TextView) view.findViewById(R.id.ring_select_save);
        actionSave.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 当没有播放录音停止播放音时
        if (!AudioPlayer.sIsRecordStopMusic) {
            // 停止播放
            AudioPlayer.getInstance(getActivity()).stop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 当点击返回按钮
            case R.id.ring_select_cancel:
                getActivity().finish();
                break;
            // 当点击保存按钮
            case R.id.ring_select_save:
                // 取得选中的铃声信息
                String ringName = RingSelectItem.getInstance().getName();
                String ringUrl = RingSelectItem.getInstance().getUrl();
                int ringPager = RingSelectItem.getInstance().getRingPager();

                // 保存选中的铃声信息
                SharedPreferences share = getActivity().getSharedPreferences(
                        AlarmClockCommon.EXTRA_AC_SHARE, Activity.MODE_PRIVATE);
                SharedPreferences.Editor edit = share.edit();

                // 来自闹钟请求
                if (sRingRequestType == 0) {
                    edit.putString(AlarmClockCommon.RING_NAME, ringName);
                    edit.putString(AlarmClockCommon.RING_URL, ringUrl);
                    edit.putInt(AlarmClockCommon.RING_PAGER, ringPager);
                    // 计时器请求
                } else {
                    edit.putString(AlarmClockCommon.RING_NAME_TIMER, ringName);
                    edit.putString(AlarmClockCommon.RING_URL_TIMER, ringUrl);
                    edit.putInt(AlarmClockCommon.RING_PAGER_TIMER, ringPager);
                }
                edit.apply();

                // 传递选中的铃声信息
                Intent i = new Intent();
                i.putExtra(AlarmClockCommon.RING_NAME, ringName);
                i.putExtra(AlarmClockCommon.RING_URL, ringUrl);
                i.putExtra(AlarmClockCommon.RING_PAGER, ringPager);
                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
                break;

        }

    }

    /**
     * 设置显示铃声列表的Fragment
     */
    private void initFragment() {
        // 展示系统铃声的Fragment
        SystemRingFragment systemRingFragment = new SystemRingFragment();
        // 展示本地铃声的Fragment
        LocalMusicFragment localMusicFragment = new LocalMusicFragment();

        mFragmentList = new ArrayList<>();
        mFragmentList.add(systemRingFragment);
        mFragmentList.add(localMusicFragment);
    }

    /**
     * 设置ViewPager并与开源组件【PagerSlidingTabStrip】相关联
     *
     * @param view view
     */
    private void initViewPager(View view) {
//      铃声种类
        ViewPager viewPager = (ViewPager) view
                .findViewById(R.id.fragment_ring_select_sort);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getActivity()
                .getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        // 铃声界面位置
        int currentIndex;
        // 当由编辑闹钟界面跳转时
        if (sRingPager != -1) {
            // 设置闹钟界面为保存的铃声界面位置
            viewPager.setCurrentItem(sRingPager);
            currentIndex = sRingPager;
        } else {
            // 取得最近一次选择的闹钟界面位置信息
            SharedPreferences shares = getActivity().getSharedPreferences(
                    AlarmClockCommon.EXTRA_AC_SHARE, Activity.MODE_PRIVATE);
            int position = shares.getInt(AlarmClockCommon.RING_PAGER, 0);
            viewPager.setCurrentItem(position);
            currentIndex = position;
        }

        PagerSlidingTabStrip strip = (PagerSlidingTabStrip) view
                .findViewById(R.id.tabstrip);
        strip.setViewPager(viewPager);
//        // 设置当前铃声界面位置，来初始化选中文字颜色
//        strip.setCurrentIndex(currentIndex);
        // 普通字体
        strip.setTypeface(Typeface.DEFAULT, 0);
    }

    /**
     * 铃声种类ViewPager适配器
     */
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        /**
         * 铃声选择列表标题
         */
        private final String[] titles = {getString(R.string.system_ring),
                getString(R.string.local_music)};

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // 开源控件PagerSlidingTabStrip需要通过它获取标签标题
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
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
}
