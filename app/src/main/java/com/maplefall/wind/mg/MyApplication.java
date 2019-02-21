package com.maplefall.wind.mg;

import android.app.Application;

import org.xutils.x;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initXUtils();
    }

    private void initXUtils() {
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
    }
}
