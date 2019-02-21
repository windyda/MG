package com.maplefall.wind.mg.base;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.view.TitleView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wind on 2018/8/15.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    protected View mView;
    protected Context mContext;
    protected LayoutInflater mInflater;
    public TitleView mTitle;
    public ACache mACache;
    private Toast mToast;
    private Dialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = super.onCreateView(inflater, container, savedInstanceState);
        mInflater = inflater;
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTitle = mView.findViewById(R.id.title);
        mACache = ACache.get(getActivity());
        mContext = getActivity();
        findViewAndBinding();
        init();
    }

    public abstract void findViewAndBinding();

    public abstract void init();

    @Override
    public void onClick(View view) {

    }

    /**
     * 简化Toast.show()
     */
    public void showToast(String msg) {
        if (null == mToast) {
            mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    public void showToast(int msg) {
        showToast(getString(msg));
    }


    /**
     * 获取当前系统时间
     *
     * @return
     */
    protected String getTime(String key) {
        String time = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(key);
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        time = simpleDateFormat.format(date);
        return time;
    }

    /**
     * 展示加载图框
     *
     * @param cancelable
     */
    protected void showLoadingView(boolean cancelable) {
        if (null == mProgressDialog) {
            mProgressDialog = new Dialog(mContext, R.style.theme_loading_dialog);
            mProgressDialog.setContentView(R.layout.loading_dialog);
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }

    /**
     * 使加载图框消失
     */
    protected void hideLoadingView() {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
    }
}
