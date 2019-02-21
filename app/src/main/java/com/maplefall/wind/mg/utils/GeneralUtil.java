package com.maplefall.wind.mg.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralUtil {

    /**
     * 获取当前系统时间
     *
     * @return
     */
    public String getTime(String key) {
        String time = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(key);
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        time = simpleDateFormat.format(date);
        return time;
    }

    /**
     * 时间格式转化
     *
     * @param date      要转换的时间字符串
     * @param oldFormat 当前的时间字符串格式
     * @param newFormat 目标时间字符串格式
     * @return
     */
    public String switchDateFormat(String date, String oldFormat, String newFormat) {
        String time = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(oldFormat);
            formatter.setLenient(false);
            Date newDate = formatter.parse(date);
            formatter = new SimpleDateFormat(newFormat);
            time = formatter.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
