package com.maplefall.wind.mg.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.note.OnItemClickListener;

public class KeyboardAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mKeys;
    private OnItemClickListener mListener;

    public KeyboardAdapter(Context context, String[] nameList) {
        mContext = context;
        mKeys = nameList;
    }

    @Override
    public int getCount() {
        return mKeys.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position / 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.keyboard_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mKey = convertView.findViewById(R.id.keyboard_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mKey.setText(mKeys[position]);
        viewHolder.mKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(((Button) view).getText().toString());
            }
        });

        return convertView;
    }

    public void setOnClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    class ViewHolder {
        private Button mKey;
    }
}
