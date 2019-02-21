package com.maplefall.wind.mg.utils;

import android.app.Activity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Wind on 2018/8/1.
 */

public class PermissionUtil {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PermissionHelper {
        boolean permissionResult();
        int requestCode();
    }

    public static void permissionRequestActivity(Activity activity, boolean permissionResult, int requestCode) {
        Class currentClass = activity.getClass();
        Method[] methods = currentClass.getDeclaredMethods();
        for (Method method : methods) {
            PermissionHelper annotation = method.getAnnotation(PermissionHelper.class);
            if (permissionResult == annotation.permissionResult() && annotation.requestCode() == requestCode){
                try {
                    method.setAccessible(true);
                    method.invoke(activity);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
