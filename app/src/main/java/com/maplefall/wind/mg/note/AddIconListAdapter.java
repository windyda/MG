package com.maplefall.wind.mg.note;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.utils.ResourceUtil;

public class AddIconListAdapter extends RecyclerView.Adapter {

    private String[] mIconList;
    private Context mContext;
    private OnItemClickListener mListener;

    public AddIconListAdapter(Context context, String[] names) {
        mContext = context;
        mIconList = names;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.add_icon_item, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder)holder;  // 绑定
        myHolder.mIcon.setImageResource(ResourceUtil.getDrawableId(mContext, mIconList[position]));

        //单击事件
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.itemView, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mIconList.length;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    class MyHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;

        public MyHolder(View view) {
            super(view);
            mIcon = view.findViewById(R.id.add_icon_image);
        }
    }
}
