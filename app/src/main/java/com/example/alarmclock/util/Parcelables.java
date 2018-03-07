package com.example.alarmclock.util;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/3/5.
 */

public class Parcelables {
    public static byte[] toByteArray(Parcelable parcelable) {
        Parcel parcel=Parcel.obtain();

        parcelable.writeToParcel(parcel, 0);

        byte[] result=parcel.marshall();

        parcel.recycle();

        return(result);
    }

    public static <T> T toParcelable(byte[] bytes,
                                     Parcelable.Creator<T> creator) {
        Parcel parcel=Parcel.obtain();

        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);

        T result=creator.createFromParcel(parcel);

        parcel.recycle();

        return(result);
    }
}