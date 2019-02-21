package com.maplefall.wind.mg.note;

import android.view.View;

public interface OnItemClickListener {
    void onItemClick(View view, int position);
    void onItemLeftSlide(View view, int position);
    void onItemClick(String content);
}
