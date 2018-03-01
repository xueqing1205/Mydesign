package com.example.alarmclock.bean.event;

/**
 * Created by Administrator on 2018/2/14.
 */

public class WallpaperEvent {
    private boolean mIsAppWallpaper = false;

    public WallpaperEvent() {
    }

    public WallpaperEvent(boolean isAppWallpaper) {
        mIsAppWallpaper = isAppWallpaper;
    }

    /**
     * 变更是否为应用自带壁纸
     */
    public boolean isAppWallpaper() {
        return mIsAppWallpaper;
    }
}