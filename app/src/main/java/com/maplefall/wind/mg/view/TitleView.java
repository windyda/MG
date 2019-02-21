package com.maplefall.wind.mg.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.base.TitleStyle;

/**
 * Created by Wind on 2018/7/27.
 */

public class TitleView extends LinearLayout {

    private Context mContext;
    private ImageView left_image;  //左边的ImageView
    private ImageView right_image, right_image_mid, right_image_right;  //右边的三个ImageView
    private TextView left_text;  //左边的TextView
    private TextView mid_text;  //中间的TextView
    private TextView right_text, right_text_mid, right_text_right;  //右边的三个TextView

    private RelativeLayout title_relative;  //底层的RelativeLayout
    private LinearLayout left_linear, mid_linear, right_linear;  //三个子控件的父LinearLayout

    private OnClickListener leftBackListener;

    public TitleView(Context context) {
        super(context);
    }

    public TitleView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.title_view, this, true);

        findView();

        leftBackListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) mContext).finish();
            }
        };
    }

    public void initTitle(String title) {
        //默认title
        initTitle(TitleStyle.TITLE_DEFAULT, title);
    }

    public void initTitle(int style) {
        initTitle(style, "");
    }

    public void initTitle(int style, String title) {
        switch (style) {
            case TitleStyle.DEFAULT: {
                left_image.setVisibility(VISIBLE);
                left_linear.setOnClickListener(leftBackListener);
                break;
            }
            case TitleStyle.TITLE_DEFAULT:{
                //只有左边的返回按钮和中间的标题
                left_image.setVisibility(VISIBLE);
                mid_text.setVisibility(VISIBLE);
                left_linear.setOnClickListener(leftBackListener);
                mid_text.setText(title);
                break;
            }
            case TitleStyle.TITLE_ONLY_TITLE:{
                //只显示title中间的标题
                mid_text.setVisibility(VISIBLE);
                mid_text.setText(title);
                break;
            }
            case TitleStyle.TITLE_RIGHT_IMG:{
                //显示title左边的返回按钮，中间的标题和右边的图片
                left_image.setVisibility(VISIBLE);
                mid_text.setVisibility(VISIBLE);
                right_image.setVisibility(VISIBLE);
                mid_text.setText(title);
                left_linear.setOnClickListener(leftBackListener);
                break;
            }
            case TitleStyle.TITLE_RIGHT_TEXT:{
                //显示title左边的返回按钮，中间的标题和右边的字
                left_image.setVisibility(VISIBLE);
                mid_text.setVisibility(VISIBLE);
                right_text.setVisibility(VISIBLE);
                mid_text.setText(title);
                left_linear.setOnClickListener(leftBackListener);
                break;
            }
        }
    }

    private void findView() {
        left_image = findViewById(R.id.title_left_image);
        right_image = findViewById(R.id.title_right_image);
        right_image_mid = findViewById(R.id.title_right_image_mid);
        right_image = findViewById(R.id.title_right_image_right);
        left_text = findViewById(R.id.title_left_text);
        mid_text = findViewById(R.id.title_mid_text);
        right_text = findViewById(R.id.title_right_text);
        right_text_mid = findViewById(R.id.title_right_text_mid);
        right_text_right = findViewById(R.id.title_right_text_right);
        title_relative = findViewById(R.id.title_base_relative);
        left_linear = findViewById(R.id.title_left_linear);
        mid_linear = findViewById(R.id.title_mid_linear);
        right_linear = findViewById(R.id.title_right_linear);
    }

    protected TextView getLeft_text() {
        return left_text;
    }

    protected TextView getMid_text() {
        return mid_text;
    }

    protected TextView getRight_text() {
        return right_text;
    }

    protected TextView getRight_text_mid() {
        return right_text_mid;
    }

    protected TextView getRight_text_right() {
        return right_text_right;
    }

    protected ImageView getLeft_image() {
        return left_image;
    }

    protected ImageView getRight_image() {
        return right_image;
    }

    protected ImageView getRight_image_mid() {
        return right_image_mid;
    }

    protected ImageView getRight_image_right() {
        return right_image_right;
    }

    protected RelativeLayout getTitle_base_relative() {
        return title_relative;
    }

    protected LinearLayout getLeft_linear() {
        return left_linear;
    }

    protected LinearLayout getMid_linear() {
        return mid_linear;
    }

    protected LinearLayout getRight_linear() {
        return right_linear;
    }



    protected void setTitleBackgroundColor(int color) {
        title_relative.setBackgroundResource(color);
    }
}
