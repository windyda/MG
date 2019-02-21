package com.maplefall.wind.mg.base;

import android.content.Context;
import android.view.LayoutInflater;

import com.maplefall.wind.mg.utils.EmptyUtil;
import com.maplefall.wind.mg.utils.ResourceUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Wind on 2018/8/12.
 */

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    protected List<T> mList;
    protected LayoutInflater mInflater;
    protected Context mContext;

    public BaseAdapter(Context context) {
        mList = new ArrayList<>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    public void clearData() {
        mList.clear();
    }

    public void addAllData(List<T> list) {
        if(!EmptyUtil.isEmpty(list)) {
            mList.addAll(list);
        }
    }

    public void setList(List<T> list) {
        if (!EmptyUtil.isEmpty(list)) {
            mList = list;
            notifyDataSetChanged();
        }
    }

    public void addAddDataAndNotify(List<T> list) {
        addAllData(list);
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mList;
    }

    /**
     * 清空list原有数据
     * 替换为新传入的list数据
     * @param list
     */
    public void refreshList(List<T> list) {
        mList.clear();
        if(!EmptyUtil.isEmpty(list)) {
            mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (EmptyUtil.isEmpty(mList)) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
