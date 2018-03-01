package com.example.alarmclock.adapter;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.alarmclock.bean.event.AlarmClockDeleteEvent;
import com.example.alarmclock.bean.event.AlarmClockUpdateEvent;
import com.example.alarmclock.bean.model.AlarmClock;
import com.example.alarmclock.db.AlarmClockOperate;
import com.example.alarmclock.listener.OnItemClickListener;
import com.example.alarmclock.util.AudioPlayer;
import com.example.alarmclock.util.MyUtil;
import com.example.alarmclock.util.OttoBus;
import com.strangeman.alarmclock.R;

import java.util.List;

/**
 * Created by Administrator on 2018/1/29.
 */

public class AlarmClockAdapter extends RecyclerView.Adapter<AlarmClockAdapter.MyViewHolder> {

    private final Context mContext;

    /**
     * 是否显示删除按钮
     */
    private boolean mIsDisplayDeleteBtn = false;

    /**
     * 白色
     */
    private int mWhite;

    /**
     * 淡灰色
     */
    private int mWhiteTrans;

    private List<AlarmClock> mList;

    private boolean isCanClick = true;

    public void setIsCanClick(boolean isCanClick) {
        this.isCanClick = isCanClick;
    }

    /**
     * 保存闹钟信息的adapter
     *
     * @param context Activity上下文
     * @param objects 闹钟信息List
     */
    public AlarmClockAdapter(Context context, List<AlarmClock> objects) {
        mContext = context;
        mList = objects;
        mWhite = mContext.getResources().getColor(android.R.color.white);
        mWhiteTrans = mContext.getResources().getColor(R.color.white_trans30);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.lv_alarm_clock, parent, false));
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final AlarmClock alarmClock = mList.get(position);

        if (mOnItemClickListener != null) {
            viewHolder.rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isCanClick) {
                        mOnItemClickListener.onItemClick(viewHolder.itemView, viewHolder.getLayoutPosition());
                    }
                }
            });

            viewHolder.rippleView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isCanClick) {
                        mOnItemClickListener.onItemLongClick(viewHolder.itemView, viewHolder.getLayoutPosition());
                        return false;
                    }
                    return true;
                }
            });
        }

        // 当闹钟为开启状态时
        if (alarmClock.isOnOff()) {
            // 设置字体颜色为白色
            viewHolder.time.setTextColor(mWhite);
            viewHolder.repeat.setTextColor(mWhite);
            viewHolder.tag.setTextColor(mWhite);
        } else {
            // 设置字体颜色为淡灰色
            viewHolder.time.setTextColor(mWhiteTrans);
            viewHolder.repeat.setTextColor(mWhiteTrans);
            viewHolder.tag.setTextColor(mWhiteTrans);

        }

        // 显示删除按钮
        if (mIsDisplayDeleteBtn) {
            viewHolder.deleteBtn.setVisibility(View.VISIBLE);
            viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 删除闹钟数据
//                    TabAlarmClockOperate.getInstance(mContext).delete(alarmClock
//                            .getAlarmClockCode());
                    AlarmClockOperate.getInstance().deleteAlarmClock(alarmClock);
                    OttoBus.getInstance().post(new AlarmClockDeleteEvent(viewHolder.getAdapterPosition(), alarmClock));

                    // 关闭闹钟
                    MyUtil.cancelAlarmClock(mContext,
                            alarmClock.getId());
                    // 关闭小睡
                    MyUtil.cancelAlarmClock(mContext,
                            -alarmClock.getId());

                    NotificationManager notificationManager = (NotificationManager) mContext
                            .getSystemService(Activity.NOTIFICATION_SERVICE);
                    // 取消下拉列表通知消息
                    notificationManager.cancel(alarmClock.getId());

                }
            });
        } else {
            viewHolder.deleteBtn.setVisibility(View.GONE);
        }

        // 取得格式化后的时间
        String time = MyUtil.formatTime(alarmClock.getHour(),
                alarmClock.getMinute());
        // 设定闹钟时间的显示
        viewHolder.time.setText(time);
        // 设定重复的显示
        viewHolder.repeat.setText(alarmClock.getRepeat());
        // 设定标签的显示
        viewHolder.tag.setText(alarmClock.getTag());
        viewHolder.toggleBtn
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // ToggleButton默认为false
                        if (isChecked) {
                            // 闹钟状态为开的话不更新数据
                            if (!alarmClock.isOnOff()) {
                                updateTab(true);
                            }
                        } else {
                            // 闹钟状态为关的话不处理
                            if (!alarmClock.isOnOff()) {
                                return;
                            }
                            updateTab(false);
                            // 取消闹钟
                            MyUtil.cancelAlarmClock(mContext,
                                    alarmClock.getId());
                            // 取消小睡
                            MyUtil.cancelAlarmClock(mContext,
                                    -alarmClock.getId());

                            NotificationManager notificationManager = (NotificationManager) mContext.
                                    getSystemService(
                                            Activity.NOTIFICATION_SERVICE);
                            // 取消下拉列表通知消息
                            notificationManager.cancel(alarmClock.getId());

                            // 停止播放
                            AudioPlayer.getInstance(mContext).stop();
                        }

                    }

                    /**
                     * 更新闹钟列表
                     *
                     * @param onOff
                     *            开关
                     */
                    private void updateTab(boolean onOff) {
                        // 更新闹钟数据
//                        TabAlarmClockOperate.getInstance(mContext).update(onOff,
//                                alarmClock.getAlarmClockCode());
                        AlarmClockOperate.getInstance().updateAlarmClock(onOff,
                                alarmClock.getId());
                        OttoBus.getInstance().post(new AlarmClockUpdateEvent());
                    }
                });
        // 设定闹钟开关
        viewHolder.toggleBtn.setChecked(alarmClock.isOnOff());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 保存控件实例
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        MaterialRippleLayout rippleView;
        // 时间
        TextView time;
        // 重复
        TextView repeat;
        // 标签
        TextView tag;
        // 开关
        ToggleButton toggleBtn;
        // 删除
        ImageView deleteBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            rippleView = (MaterialRippleLayout) itemView.findViewById(R.id.ripple_view);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            repeat = (TextView) itemView.findViewById(R.id.tv_repeat);
            tag = (TextView) itemView.findViewById(R.id.tv_tag);
            toggleBtn = (ToggleButton) itemView.findViewById(R.id.toggle_btn);
            deleteBtn = (ImageView) itemView.findViewById(R.id.alarm_list_delete_btn);
        }
    }

    /**
     * 更新删除闹钟按钮状态
     *
     * @param isDisplayDeleteBtn 是否显示删除按钮
     */
    public void displayDeleteButton(boolean isDisplayDeleteBtn) {
        mIsDisplayDeleteBtn = isDisplayDeleteBtn;
    }
}
