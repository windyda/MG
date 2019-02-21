package com.maplefall.wind.mg.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maplefall.wind.mg.R;

public class MessageDialog extends Dialog {

    private Context mContext;
    private String mTitle;
    private String mMessage;
    private String mPositiveButtonText;
    private String mNegativeButtonText;
    private OnClickListener mPositiveListener;
    private OnClickListener mNegativeListener;
    private boolean mSetBackground;
    private int mPositiveBackground;
    private int mNegativeBackground;

    public MessageDialog(Context context) {
        super(context);
        mContext = context;
    }

    public MessageDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public void setPositiveButton(String positiveText, OnClickListener listener) {
        mPositiveButtonText = positiveText;
        mPositiveListener = listener;
    }

    public void setPositiveButton(String positiveText, int background, OnClickListener listener) {
        mSetBackground = true;
        mPositiveButtonText = positiveText;
        mPositiveBackground = background;
        mPositiveListener = listener;
    }

    public void setNegativeButton(String negativeText, OnClickListener listener) {
        mNegativeButtonText = negativeText;
        mNegativeListener = listener;
    }

    public void setNegativeButton(String negativeText, int background, OnClickListener listener) {
        mSetBackground = true;
        mNegativeButtonText = negativeText;
        mNegativeBackground = background;
        mNegativeListener = listener;
    }

    public MessageDialog createDialog() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final MessageDialog dialog = new MessageDialog(mContext, R.layout.message_dialog);
        View layout = inflater.inflate(R.layout.message_dialog, null);
        dialog.addContentView(layout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        //set custom attribute
        ((TextView) layout.findViewById(R.id.dialog_title)).setText(mTitle);
        ((TextView) layout.findViewById(R.id.dialog_message)).setText(mMessage);

        if (mSetBackground) {
            layout.findViewById(R.id.dialog_positive_btn).setBackgroundResource(mPositiveBackground);
            layout.findViewById(R.id.dialog_negative_btn).setBackgroundResource(mNegativeBackground);
        }

        //set positive button attributes
        if (mPositiveButtonText != null) {
            ((Button)layout.findViewById(R.id.dialog_positive_btn)).setText(mPositiveButtonText);
            if (mSetBackground) {
                layout.findViewById(R.id.dialog_positive_btn).setBackgroundResource(mPositiveBackground);
            }
            if (mPositiveListener != null) {
                layout.findViewById(R.id.dialog_positive_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPositiveListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    }
                });
            }
        } else {
            // if the positive button text does not set, set the button invisible
            layout.findViewById(R.id.dialog_positive_btn).setVisibility(View.GONE);
        }

        // set negative button attributes
        if (mNegativeButtonText != null) {
            ((Button) layout.findViewById(R.id.dialog_negative_btn)).setText(mNegativeButtonText);
            if (mSetBackground) {
                layout.findViewById(R.id.dialog_negative_btn).setBackgroundResource(mNegativeBackground);
            }
            if (mNegativeListener != null) {
                layout.findViewById(R.id.dialog_positive_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mNegativeListener.onClick(dialog, BUTTON_NEGATIVE);
                    }
                });
            }
        } else {
            // if the negative button text does not set, set the button invisible
            layout.findViewById(R.id.dialog_negative_btn).setVisibility(View.GONE);
        }

        dialog.setContentView(layout);
        dialog.setCancelable(false);
        return dialog;
    }
}
