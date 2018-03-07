package com.example.alarmclock.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.alarmclock.activities.AlarmClockEditActivity;
import com.example.alarmclock.activities.AlarmClockNewActivity;
import com.example.alarmclock.adapter.AlarmClockAdapter;
import com.example.alarmclock.bean.event.AlarmClockDeleteEvent;
import com.example.alarmclock.bean.event.AlarmClockUpdateEvent;
import com.example.alarmclock.bean.model.AlarmClock;
import com.example.alarmclock.common.AlarmClockCommon;
import com.example.alarmclock.db.AlarmClockOperate;
import com.example.alarmclock.listener.OnItemClickListener;
import com.example.alarmclock.util.MyUtil;
import com.example.alarmclock.util.OttoBus;
import com.example.alarmclock.util.ToastUtil;
import com.squareup.otto.Subscribe;
import com.strangeman.alarmclock.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/22.
 */

public class AlarmClockFragment extends BaseFragment implements View.OnClickListener {

    /**
     * 新建闹钟的requestCode
     */
    private static final int REQUEST_ALARM_CLOCK_NEW = 1;

    /**
     * 修改闹钟的requestCode
     */
    private static final int REQUEST_ALARM_CLOCK_EDIT = 2;

    /**
     * 闹钟列表
     */
    private RecyclerView mRecyclerView;

    /**
     * 保存闹钟信息的list
     */
    private List<AlarmClock> mAlarmClockList;

    /**
     * 保存闹钟信息的adapter
     */
    private AlarmClockAdapter mAdapter;

    /**
     * 操作栏删除按钮
     */
    private TextView mEditAction;

    /**
     * 操作栏删除完成按钮
     */
    private TextView mAcceptAction;

    /**
     * List内容为空时的视图
     */
    private LinearLayout mEmptyView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OttoBus.getInstance().register(this);
        mAlarmClockList = new ArrayList<>();
        mAdapter = new AlarmClockAdapter(getActivity(), mAlarmClockList);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_alarm_clock, container, false);

        mEmptyView = (LinearLayout) view
                .findViewById(R.id.alarm_clock_empty);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_view);
        mRecyclerView.setHasFixedSize(true);
        //设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

//

        // 监听闹铃item点击事件Listener
        OnItemClickListener onItemClickListener = new OnItemClickListenerImpl();
        mAdapter.setOnItemClickListener(onItemClickListener);

        // 操作栏新建按钮
        TextView newAction = (TextView) view.findViewById(R.id.action_new);
        newAction.setOnClickListener(this);

        // 删除闹钟
        mEditAction = (TextView) view.findViewById(R.id.action_edit);
        mEditAction.setOnClickListener(this);

        // 完成按钮
        mAcceptAction = (TextView) view.findViewById(R.id.action_accept);
        mAcceptAction.setOnClickListener(this);
        updateList();
        return view;
    }

    class OnItemClickListenerImpl implements OnItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            // 不响应重复点击
            if (MyUtil.isFastDoubleClick()) {
                return;
            }
            AlarmClock alarmClock = mAlarmClockList.get(position);
            Intent intent = new Intent(getActivity(),
                    AlarmClockEditActivity.class);
            intent.putExtra(AlarmClockCommon.ALARM_CLOCK, alarmClock);
            // 开启编辑闹钟界面
            startActivityForResult(intent, REQUEST_ALARM_CLOCK_EDIT);
            // 启动移动进入效果动画
            getActivity().overridePendingTransition(R.anim.move_in_bottom,
                    0);
        }

        @Override
        public void onItemLongClick(View view, int position) {
            // 显示删除，完成按钮，隐藏修改按钮
            displayDeleteAccept();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_new:
                // 不响应重复点击
                if (MyUtil.isFastDoubleClick()) {
                    return;
                }
                Intent intent = new Intent(getActivity(),
                        AlarmClockNewActivity.class);
                // 开启新建闹钟界面
                startActivityForResult(intent, REQUEST_ALARM_CLOCK_NEW);
                // 启动渐变放大效果动画
                getActivity().overridePendingTransition(R.anim.zoomin, 0);
                break;
            case R.id.action_edit:
                // 当列表内容为空时禁止响应编辑事件
                if (mAlarmClockList.size() == 0) {
                    return;
                }
                // 显示删除，完成按钮，隐藏修改按钮
                displayDeleteAccept();
                break;
            case R.id.action_accept:
                // 隐藏删除，完成按钮,显示修改按钮
                hideDeleteAccept();
                break;
        }

    }

    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;
    private AlarmClock mDeletedAlarmClock;

    /**
     * 显示删除，完成按钮，隐藏修改按钮
     */
    private void displayDeleteAccept() {
        mAdapter.setIsCanClick(false);
        mAdapter.displayDeleteButton(true);
        mAdapter.notifyDataSetChanged();
        mEditAction.setVisibility(View.GONE);
        mAcceptAction.setVisibility(View.VISIBLE);

        if (mSensorManager == null) {
            mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            mSensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float xValue = Math.abs(event.values[0]);
                    float yValue = Math.abs(event.values[1]);
                    float zValue = Math.abs(event.values[2]);
                    // 认为用户摇动了手机，找回被删除的闹钟
                    if (xValue > 15 || yValue > 15 || zValue > 15) {
                        //  ToastUtil.showShortToast(getActivity(),"晃动手机");
                        if (mDeletedAlarmClock != null) {
                            MyUtil.vibrate(getActivity());
                            AlarmClockOperate.getInstance().saveAlarmClock(mDeletedAlarmClock);
                            addList(mDeletedAlarmClock);
                            mDeletedAlarmClock = null;
                            ToastUtil.showLongToast(getActivity(), getString(R.string.retrieve_alarm_clock_success));
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
        }
        mSensorManager.registerListener(mSensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }


    /**
     * 隐藏单个闹钟删除图标，完成按钮,显示删除按钮
     */
    private void hideDeleteAccept() {
        mAdapter.setIsCanClick(true);
        mAdapter.displayDeleteButton(false);
        mAdapter.notifyDataSetChanged();
        mAcceptAction.setVisibility(View.GONE);
        mEditAction.setVisibility(View.VISIBLE);

        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        AlarmClock ac = data
                .getParcelableExtra(AlarmClockCommon.ALARM_CLOCK);
        switch (requestCode) {
            // 新建闹钟
            case REQUEST_ALARM_CLOCK_NEW:
                // 插入新闹钟数据
                AlarmClockOperate.getInstance().saveAlarmClock(ac);
                addList(ac);

                break;
            // 修改闹钟
            case REQUEST_ALARM_CLOCK_EDIT:
                // 更新闹钟数据
//                TabAlarmClockOperate.getInstance(getActivity()).update(ac);
                AlarmClockOperate.getInstance().updateAlarmClock(ac);
                updateList();
                break;

        }
    }

    @Subscribe
    public void onAlarmClockUpdate(AlarmClockUpdateEvent event) {
        updateList();
    }


    @Subscribe
    public void OnAlarmClockDelete(AlarmClockDeleteEvent event) {
        deleteList(event);

        mDeletedAlarmClock = event.getAlarmClock();

   /*     SharedPreferences share = getActivity().getSharedPreferences(
                AlarmClockCommon.EXTRA_AC_SHARE, Activity.MODE_PRIVATE);   */
    }



    private void addList(AlarmClock ac) {
        mAlarmClockList.clear();
        int id = ac.getId();
        int count = 0;
        int position = 0;
        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);

            if (id == alarmClock.getId()) {
                position = count;
                if (alarmClock.isOnOff()) {
                    MyUtil.startAlarmClock(getActivity(), alarmClock);
                }
            }
            count++;
        }

        checkIsEmpty(list);

        mAdapter.notifyItemInserted(position);
        mRecyclerView.scrollToPosition(position);
    }

    private void deleteList(AlarmClockDeleteEvent event) {
        mAlarmClockList.clear();

        int position = event.getPosition();
        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);
        }
        // 列表为空时不显示删除，完成按钮
        if (mAlarmClockList.size() == 0) {
            mAcceptAction.setVisibility(View.GONE);
            mEditAction.setVisibility(View.VISIBLE);
            mAdapter.displayDeleteButton(false);
        }
        checkIsEmpty(list);
        mAdapter.notifyItemRemoved(position);
    }
    private void updateList() {
        mAlarmClockList.clear();
        List<AlarmClock> list = AlarmClockOperate.getInstance().loadAlarmClocks();
        for (AlarmClock alarmClock : list) {
            mAlarmClockList.add(alarmClock);
            // 当闹钟为开时刷新开启闹钟
            if (alarmClock.isOnOff()) {
                MyUtil.startAlarmClock(getActivity(), alarmClock);
            }
        }
        checkIsEmpty(list);
        mAdapter.notifyDataSetChanged();
    }

    private void checkIsEmpty(List<AlarmClock> list) {
        if (list.size() != 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            if (mSensorManager != null) {
                mSensorManager.unregisterListener(mSensorEventListener);
            }
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        OttoBus.getInstance().unregister(this);
    }
}
