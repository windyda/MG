package com.maplefall.wind.mg.utils;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;

/**
 * Created by Wind on 2018/7/18.
 */

public class ActivityManager {
    private static Stack<Activity> activityStack;
    private static ActivityManager instance;

    public ActivityManager() {

    }

    /**
     * 实例化instance
     * @return ActivityManager
     */
    public static ActivityManager getActivityManager() {
        if(null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 将activity添加到堆栈
     */
    public boolean addActivity(Activity activity) {
        if(activityStack == null) {
            activityStack = new Stack<>();
        }
        return activityStack.add(activity);
    }

    /**
     * 将activity移除出堆栈
     */
    public boolean removeActivity(Activity activity) {
        if(null == activityStack) {
            activityStack = new Stack<>();
        }
        return activityStack.remove(activity);
    }

    /**
     * 获取当前activity
     */
    public Activity getActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束当前activity
     */
    public void finishCurrentActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束其他activity,只保留当前activity
     */
    public void finishOtherActivity() {
        for (int i = activityStack.size(); i>1; i--) {
            finishActivity(activityStack.get(i - 1));
        }
    }

    /**
     * 结束制定activity
     */
    public void finishActivity(Activity activity) {
        if(activity != null && !activity.isFinishing()) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity: activityStack) {
            if(activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 结束所有activity
     */
    public void finishAllActivity() {
        for(Activity activity: activityStack) {
            if(null != activity) {
                finishActivity(activity);
                break;
            }
        }
        activityStack.clear();
    }

    /**
     * 获取指定的Activity
     */
    public Activity getActivity(Class<?> cls) {
        if(activityStack != null) {
            for(Activity activity: activityStack) {
                if(activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        }
        return null;
    }

    /**
     * 退出应用
     */
    public void appExit(Context context) {
        try{
            finishAllActivity();
//            Process.killProcess(Progress.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
