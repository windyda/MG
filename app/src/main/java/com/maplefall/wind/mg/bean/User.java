package com.maplefall.wind.mg.bean;

public class User {
    private String mUserName;
    private String mPassword;
    private String mPhone;
    private String mMotto;

    private User() {
        mUserName = "";
        mPassword = "";
        mPhone = "";
        mMotto = "";
    }

    public User(String name, String password, String phone, String motto) {
        mUserName = name;
        mPassword = password;
        mPhone = phone;
        mMotto = motto;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getMotto() {
        return mMotto;
    }

    public void setUserName(String name) {
        mUserName = name;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public void setMotto(String motto) {
        mMotto = motto;
    }

    public static User mgUser = new User();
}
