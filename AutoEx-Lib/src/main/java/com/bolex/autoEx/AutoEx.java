package com.bolex.autoEx;

import android.content.Context;
import android.content.Intent;


/**
 * Created by Bolex on 2018/4/30.
 */

public class AutoEx implements AutoExConstant {

    private Context mApp;
    private Thread.UncaughtExceptionHandler mUEH;
    public static int maxSize = 4;
    public static String tag = LOG_TAG;
    private boolean isDebug;
    private static volatile AutoEx autoEx;

    /**
     * @param mApp Applicatin
     */
    public static void apply(Context mApp) {
        apply(mApp, AutoEx.maxSize);
    }

    /**
     * @param mApp    Applicatin
     * @param maxSize 最大提示答案数目
     */
    public static void apply(Context mApp, int maxSize) {
        apply(mApp, maxSize, LOG_TAG, true);
    }

    /**
     * @param mApp    Applicatin
     * @param maxSize 最大提示答案数目
     * @param tag     自定义日志 默认 AutoEx
     */
    public static void apply(Context mApp, int maxSize, String tag) {
        apply(mApp, maxSize, tag, true);
    }

    /**
     * @param mApp    Applicatin
     * @param maxSize 最大提示答案数目
     * @param tag     自定义日志 默认 AutoEx
     * @param isDebug 是否开启调试 true为开启 false为关闭 默认开启
     */
    public static void apply(Context mApp, int maxSize, String tag, boolean isDebug) {
        if (!isDebug) {
            return;
        }
        if (autoEx == null) {
            autoEx = new AutoEx();
            autoEx.init(mApp, maxSize, tag, isDebug);
        }
    }

    private void init(Context mApp, int maxSize, String tag, boolean isDebug) {
        this.mApp = mApp;
        this.maxSize = maxSize;
        this.tag = tag;
        this.isDebug = isDebug;
        mUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(final Thread thread, final Throwable ex) {
            String errorMsg = getErrorMsg(ex);
            Intent intent = new Intent(mApp, DoHandleService.class);
            intent.putExtra(ERROR_MSG, errorMsg);
            intent.putExtra(MAX_SIZE, maxSize);
            mApp.startService(intent);
            mUEH.uncaughtException(thread, ex);
        }
    };


    private String getErrorMsg(Throwable ex) {
        String message = ex.getMessage();
        return message.substring(message.indexOf(":") + 2);
    }

}
