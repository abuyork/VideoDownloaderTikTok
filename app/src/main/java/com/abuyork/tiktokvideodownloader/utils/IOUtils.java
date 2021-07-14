package com.abuyork.tiktokvideodownloader.utils;

import android.os.Build;


public class IOUtils {
    private final static String TAG = IOUtils.class.getSimpleName();


    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }
    public static boolean hasLolipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
    public static boolean hasMarsallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
