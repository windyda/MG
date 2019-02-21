package com.maplefall.wind.mg.utils;

import android.content.Context;

/**
 * Created by Wind on 2018/8/14.
 */

public class ResourceUtil {
    private static final String RES_ID = "id";
    private static final String RES_STRING = "string";
    private static final String RES_DRAWABLE = "drawable";
    private static final String RES_LAYOUT = "layout";
    private static final String RES_STYLE = "style";
    private static final String RES_COLOR = "color";
    private static final String RES_DIMEN = "dimen";
    private static final String RES_ANIM = "anim";
    private static final String RES_MENU = "menu";

    /**
     * 获取资源文件的id
     * @param context
     * @param resName
     * @return
     */
    public static int getId(Context context, String resName) {
        return getResId(context, resName, RES_ID);
    }

    /**
     * 获取资源文件string中的资源id
     * @param context
     * @param resName
     * @return
     */
    public static int getStringId(Context context, String resName) {
        return getResId(context, resName, RES_STRING);
    }

    /**
     * 获取资源文件夹drawable的资源id
     * @param context
     * @param resName
     * @return
     */
    public static int getDrawableId(Context context, String resName) {
        return getResId(context, resName, RES_DRAWABLE);
    }

    /**
     * 获取资源文件夹layout的资源id
     * @param context
     * @param resName
     * @return
     */
    public static int getLayoutId(Context context, String resName) {
        return getResId(context, resName, RES_LAYOUT);
    }

    /**
     * 获取资源文件style的资源id
     * @param context
     * @param resName
     * @return
     */
    public static int getStyleId(Context context, String resName) {
        return getResId(context, resName, RES_STYLE);
    }

    /**
     * 获取资源文件color的资源id
     * @param context
     * @param resName
     * @return
     */
    public static int getColorId(Context context, String resName) {
        return getResId(context, resName, RES_COLOR);
    }

    /**
     * 获取资源文件dimen的资源id
     * @param context
     * @param resName
     * @return
     */
    public static int getDimenId(Context context, String resName) {
        return getResId(context, resName, RES_DIMEN);
    }

    /**
     * 获取资源文件anim的资源id
     * @param context
     * @param resName
     * @return
     */
    public static int getAnimId(Context context, String resName) {
        return getResId(context, resName, RES_DRAWABLE);
    }

    /**
     * 获取资源文件menu的资源id
     * @param context
     * @param resName
     * @return
     */
    public static int getMenuId(Context context, String resName) {
        return getResId(context, resName, RES_DRAWABLE);
    }

    public static int getResId(Context context, String resName, String resType) {
        return context.getResources().getIdentifier(resName, resType, context.getPackageName());
    }
}
