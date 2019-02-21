package com.maplefall.wind.mg.bean;

import java.io.Serializable;

public class Note implements Serializable {
    private String mTitle;
    private String mContent;
    private String mTime;
    private String mDictum;
    private String mIcon;

    public Note() {
        mTitle = "";
        mContent = "";
        mTime = "";
        mDictum = "";
        mIcon = "";
    }

    public Note(String title, String content, String time, String dictum, String icon) {
        mTitle = title;
        mContent = content;
        mTime = time;
        mDictum = dictum;
        mIcon = icon;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public void setDictum(String dictum) {
        mDictum = dictum;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public String getTime() {
        return mTime;
    }

    public String getDictum() {
        return mDictum;
    }

    public String getIcon() {
        return mIcon;
    }
}
