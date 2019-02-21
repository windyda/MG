package com.maplefall.wind.mg.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.bean.Note;
import com.maplefall.wind.mg.note.OnItemClickListener;
import com.maplefall.wind.mg.span.SpanParser;
import com.maplefall.wind.mg.utils.GeneralUtil;

import java.util.ArrayList;

public class MainListAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<Note> mList;
    private OnItemClickListener mListener;

    public MainListAdapter(Context context, ArrayList<Note> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.mTitle.setText(mList.get(position).getTitle());
        myHolder.mContent.setText((new SpanParser().fromHtml(mList.get(position).getContent())));
        myHolder.mItemTime.setText(parserTime(mList.get(position).getTime()));

        // 暂不设置 icon 的值

        // 单击事件
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.itemView, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_list_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    private String parserTime(String time) {
        return new GeneralUtil().switchDateFormat(time, "yyyyMMddHHmmss", "yy-MM-dd HH:mm");
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mContent;
        private TextView mItemTime;
        private ImageView mIcon;

        private MyHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.main_item_title);
            mContent = view.findViewById(R.id.main_item_content);
            mItemTime = view.findViewById(R.id.main_item_time);
            mIcon = view.findViewById(R.id.main_item_icon);
        }
    }
}
