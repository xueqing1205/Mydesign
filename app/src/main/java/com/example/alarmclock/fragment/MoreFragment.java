package com.example.alarmclock.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.squareup.otto.Subscribe;
import com.strangeman.alarmclock.R;
import com.strangeman.alarmclock.activities.ThemeActivity;
import com.strangeman.alarmclock.bean.event.WallpaperEvent;
import com.strangeman.alarmclock.listener.OnVisibleListener;
import com.strangeman.alarmclock.util.MyUtil;
import com.strangeman.alarmclock.util.OttoBus;

/**
 * Created by Administrator on 2018/1/22.
 */

public class MoreFragment extends LazyLoadFragment {
    /**
     * Log tag ：MoreFragment
     */
    private static final String LOG_TAG = "MoreFragment";

    private ActivityManager mActivityManager;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean mIsPrepared;

    private OnVisibleListener mOnVisibleListener;

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
        final View view = inflater.inflate(R.layout.fm_more, container, false);

        mOnVisibleListener = new OnVisibleListener() {
            @Override
            public void onVisible() {
                mActivityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                showMoreLayout(view);
                assignViews(view);

                mIsPrepared = true;
            }
        };
        return view;
    }

    private void showMoreLayout(View view) {
        // 更多布局
        ViewStub viewStub = (ViewStub) view.findViewById(R.id.viewstub_more);
        viewStub.inflate();

        // 加载中进度框
        ViewGroup progressBar = (ViewGroup) view.findViewById(R.id.progress_bar_llyt);
        progressBar.setVisibility(View.GONE);
    }

    private void assignViews(View view) {
//        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
////        OverScrollDecoratorHelper.setUpOverScroll(scrollView);


        // 主题
        view.findViewById(R.id.theme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtil.isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(getActivity(), ThemeActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @Subscribe
    public void onWallpaperUpdate(WallpaperEvent wallpaperEvent) {
        ViewGroup vg = (ViewGroup) getActivity().findViewById(
                R.id.llyt_activity_main);
        // 更新壁纸
        MyUtil.setBackground(vg, getActivity());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        OttoBus.getInstance().unregister(this);
    }
}
