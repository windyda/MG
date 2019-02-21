package com.maplefall.wind.mg.note;

import android.text.Editable;
import android.widget.EditText;

public class EditOperation {

    //原始内容
    private String mSrc;
    private int mSrcStart;
    private int mSrcEnd;

    //目标内容
    private String mDest;
    private int mDestStart;
    private int mDestEnd;

    private EditText mEdit;

    public EditOperation(EditText editText) {
        mEdit = editText;
    }

    public void setSrc(CharSequence src, int srcStart, int srcEnd) {
        mSrc = src == null ? "" : src.toString();
        mSrcStart = srcStart;
        mSrcEnd = srcEnd;
    }

    public void setDest(CharSequence dest, int destStart, int destEnd) {
        mDest = dest == null ? "" : dest.toString();
        mDestStart = destStart;
        mDestEnd = destEnd;
    }

    public void undo() {
        Editable editable = mEdit.getText();

        int index = -1;
        if(mDestEnd > 0) {  //删除目标内容
            editable.delete(mDestStart, mDestEnd);

            if(mSrc == null) {
                index = mDestStart;
            }
        }

        if(mSrc != null) {  //插入原始内容
            editable.insert(mSrcStart, mSrc);
            index = mSrcStart + mSrc.length();
        }
        if(index >= 0) {  //设置光标位置
            mEdit.setSelection(index);
        }
    }

    public void redo() {
        Editable editable = mEdit.getText();

        int index = -1;
        if(mSrcEnd > 0) {  //删除原始内容
            editable.delete(mSrcStart, mSrcEnd);
            if(mDest == null) {
                index = mSrcStart;
            }
        }
        if(mDest != null) {  //插入目标内容
            editable.insert(mDestStart, mDest);
            index = mDestStart + mDest.length();
        }
        if(index >= 0) {
            mEdit.setSelection(index);
        }
    }

}
