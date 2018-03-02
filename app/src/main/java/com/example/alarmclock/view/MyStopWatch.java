package com.example.alarmclock.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.alarmclock.common.AlarmClockCommon;
import com.example.alarmclock.util.MyUtil;
import com.strangeman.alarmclock.R;


import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Administrator on 2018/2/13.
 */

public class MyStopWatch extends View {

    /**
     * 是否已经初始化
     */
    private boolean mIsInitialized = false;

    /**
     * 是否秒表已经开始
     */
    private boolean mIsStarted = false;

    /**
     * 秒表时间
     */
    private Calendar mTimeStart;

    /**
     * 显示的秒表时间
     */
    private String mDisplayWatchTime;

    /**
     * 控件宽
     */
    private float mViewWidth;

    /**
     * 控件高
     */
    private float mViewHeight;

    /**
     * 表盘半径
     */
    private float mCircleRadiusWatcher;

    /**
     * 当前角度
     */
    private float mCurrentDegree;

    /**
     * 表盘中心x坐标
     */
    private float mCenterX;

    /**
     * 表盘中心y坐标
     */
    private float mCenterY;

    /**
     * 表盘外圈宽度
     */
    private float mStrokeWidth;

    /**
     * 表盘背景画笔
     */
    private Paint mPaintCircleBackground;

    /**
     * 弧形画笔
     */
    private Paint mPaintArc;

    /**
     * 显示秒表时间画笔
     */
    private Paint mPaintRemainTime;


    /**
     * 辉光效果画笔
     */
    private Paint mPaintGlowEffect;

    /**
     * 秒表时间变化回调
     */
    private MyTimer.OnTimeChangeListener mRemainTimeChangeListener;

    /**
     * 初始化完成回调
     */
    private MyTimer.OnInitialFinishListener mInitialFinishListener;

    /**
     * 矩形范围
     */
    private Rect mRect;

    /**
     * 当前设置的分钟数
     */
    private int mRemainMinute = 0;

    /**
     * 弧形的参考矩形
     */
    private RectF mRectF;

    /**
     * 演示动画
     */
    private boolean isShowingAnimation;

    public MyStopWatch(Context context) {
        super(context);
    }

    public MyStopWatch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyStopWatch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 初始化
        if (!mIsInitialized) {
            initialize(canvas);
            mIsInitialized = true;
        }
        updateDegree();
        // 画表盘背景的圆圈
        canvas.drawCircle(mCenterX, mCenterY, mCircleRadiusWatcher, mPaintCircleBackground);
        // 画弧形
        canvas.drawArc(mRectF, -90, mCurrentDegree, false, mPaintArc);

        // 设置显示的秒表时间
        setDisplayNumber();
        // 画剩余时间
        canvas.drawText(mDisplayWatchTime, mCenterX - mRect.width() / 2,
                mCenterY + mRect.height() / 2, mPaintRemainTime);

//        LogUtil.d(LOG_TAG, "绘制中");
    }


    /**
     * 初始化
     *
     * @param canvas canvas
     */
    @SuppressWarnings("deprecation")
    private void initialize(Canvas canvas) {
        mTimeStart = Calendar.getInstance();
        mTimeStart.clear();
        // GMT（格林尼治标准时间）一般指世界时.中国时间（GST）与之相差-8小时
        TimeZone tz = TimeZone.getTimeZone("GMT");
        mTimeStart.setTimeZone(tz);
//获取手机相关信息
        float density = getResources().getDisplayMetrics().density;
        mViewWidth = canvas.getWidth();
        mViewHeight = canvas.getHeight();

        mStrokeWidth = 10 * density;

        mCircleRadiusWatcher = mViewWidth / 3;
        mCurrentDegree = 0;
        mCenterX = mViewWidth / 2;
        mCenterY = mViewHeight / 2;


        mPaintCircleBackground = new Paint();
        mPaintArc = new Paint();
        mPaintRemainTime = new Paint();
        mPaintGlowEffect = new Paint();


        // 表盘外圈颜色
        int colorWatcher = getResources().getColor(R.color.white_trans10);
        mPaintCircleBackground.setColor(colorWatcher);
        mPaintCircleBackground.setStrokeWidth(mStrokeWidth);
        mPaintCircleBackground.setStyle(Paint.Style.STROKE);
        mPaintCircleBackground.setAntiAlias(true);

        // 秒表时间颜色
        int colorRemainTime = Color.WHITE;


        mPaintArc.setColor(colorRemainTime);
        mPaintArc.setStrokeWidth(mStrokeWidth);
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setAntiAlias(true);

        mPaintRemainTime.setStyle(Paint.Style.FILL);
        mPaintRemainTime.setAntiAlias(true);
        mRect = new Rect();
        float densityText = getResources().getDisplayMetrics().scaledDensity;
        mPaintRemainTime.setTextSize(60 * densityText);
        mPaintRemainTime.setColor(colorRemainTime);
        mPaintRemainTime.setAntiAlias(true);
        mPaintRemainTime.getTextBounds("00:00", 0, "00:00".length(), mRect);


        mPaintGlowEffect.setMaskFilter(new BlurMaskFilter(2 * mStrokeWidth / 3, BlurMaskFilter.Blur.NORMAL));
        mPaintGlowEffect.setAntiAlias(true);
        mPaintGlowEffect.setColor(colorRemainTime);
        mPaintGlowEffect.setStyle(Paint.Style.FILL);


        mRectF = new RectF(mCenterX - mCircleRadiusWatcher, mCenterY - mCircleRadiusWatcher
                , mCenterX + mCircleRadiusWatcher, mCenterY + mCircleRadiusWatcher);
        //完成初始化回调
        if (mInitialFinishListener != null) {
            mInitialFinishListener.onInitialFinish();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 控件默认宽度（屏幕宽度）
        int defaultViewWidth = (int) (360 * getResources().getDisplayMetrics().density);
        int width = getDimension(defaultViewWidth, widthMeasureSpec);
        int height = getDimension(width, heightMeasureSpec);

        mViewWidth = width;
        mViewHeight = height;

        setMeasuredDimension(width, height);
    }


    /**
     * 设置显示的剩余时间
     */
    private void setDisplayNumber() {
        mDisplayWatchTime = MyUtil.formatTime(mTimeStart.get(Calendar.MINUTE),
                mTimeStart.get(Calendar.SECOND));
    }

    /**
     * 取得尺寸
     *
     * @param defaultDimension 默认尺寸
     * @param measureSpec      measureSpec
     * @return 尺寸
     */
    private int getDimension(int defaultDimension, int measureSpec) {
        int result;
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY:
                result = MeasureSpec.getSize(measureSpec);
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(defaultDimension, MeasureSpec.getSize(measureSpec));
                break;
            default:
                result = defaultDimension;
                break;
        }
        return result;
    }

    /**
     * 更新角度，角度由剩余时间决定
     * 度数 = （（分钟数 * 60 + 秒数） / 60分 * 60秒） * 360度 = （分钟数 * 60 + 秒数） * /10.0
     */
    private void updateDegree() {
        mCurrentDegree = (float) ((mTimeStart.get(Calendar.MINUTE) * 60 + mTimeStart.get(Calendar.SECOND)) / 10.0);
    }

    public void updateDisplayTime() {
        if (mIsStarted) {
            mTimeStart.add(Calendar.MILLISECOND, 1000);
            invalidate();
        }
    }

//    public void setTime(long time){
//        mIsStarted=true;
//        mTimeStart.setTimeInMillis(time);
//        invalidate();
//    }

    public boolean start(){
        mIsStarted=true;
        return mIsStarted;
    }

    public void stop() {
        mIsStarted=false;
        saveTime(mTimeStart.getTimeInMillis(),false);
    }

    /**
     * 重置
     */
    public void reset() {
        mIsStarted = false;
        saveTime(0, false);
        mIsInitialized = false;
        invalidate();
    }

    private void saveTime(long watchTime,boolean IsStart) {
        SharedPreferences preferences = getContext().getSharedPreferences(
                AlarmClockCommon.EXTRA_AC_SHARE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(AlarmClockCommon.COUNTDOWN_TIME,watchTime );
        editor.putBoolean("IsStart", IsStart);
        editor.apply();
    }

    public String getmDisplayWatchTime(){
        return mDisplayWatchTime;
    }

}
