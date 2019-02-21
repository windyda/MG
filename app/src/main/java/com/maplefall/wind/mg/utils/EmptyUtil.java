package com.maplefall.wind.mg.utils;

/**
 * Created by Wind on 2018/8/12.
 */

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 此类主要是对String、List、Set、Map的是否为空或长度是否为零进行判断
 */

public class EmptyUtil {

    /**
     * 判断String是否为空 或 长度为 0
     * @param str
     * @return 当字符串长度为空或长度为0时，返回true
     */
    public static boolean isEmpty(String str){
        if("".equals(str) || str==null){
            return true;
        }
        return false;
    }

    /**
     * 判断List是否为空 或 长度为 0
     */
    public static boolean isEmpty(List<?> list) {
        return (null == list || list.size() == 0);
    }

    /**
     * 判断Set是否为空 或 长度为 0
     */
    public static boolean isEmpty(Set<?> set) {
        return (null == set || set.size() == 0);
    }

    /**
     * 判断Map是否为空 或 长度为 0
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (null == map || map.size() == 0);
    }

    /**
     * 判断Map是否为空 或 长度为 0
     */
    public static boolean isEmpty(Object[] obj) {
        return (null == obj || obj.length == 0);
    }

}
