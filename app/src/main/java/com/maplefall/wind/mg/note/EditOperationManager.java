package com.maplefall.wind.mg.note;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.LinkedList;

public class EditOperationManager implements TextWatcher {

    private EditOperation mEditOpt;
    private boolean mEnable = true;  //应用开关，用于过滤撤销/重做时的编辑操作
    private EditText mEdit;

    //使用LinkedList代替栈
    private final LinkedList<EditOperation> mUndoOpts = new LinkedList<>();
    private final LinkedList<EditOperation> mRedoOpts = new LinkedList<>();

    private final int MAX_RECORD_SIZE = 50;

    public EditOperationManager(EditText editText) {
        mEdit = editText;
    }

    private void disabled() {
        mEnable = false;
    }

    private void enable() {
        mEnable = true;
    }

    @Override
    public void beforeTextChanged(CharSequence chars, int start, int count, int after) {
        if(count > 0) {
            int end = start + count;
            if(mEnable) {
                if(mEditOpt == null) {
                    mEditOpt = new EditOperation(mEdit);
                }
                //记录原始内容
                mEditOpt.setSrc(chars.subSequence(start, end), start, end);
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence chars, int start, int before, int count) {
        if(count > 0) {
            int end = start + count;
            if(mEnable) {
                if(mEditOpt == null) {
                    mEditOpt = new EditOperation(mEdit);
                }
                mEditOpt.setDest(chars.subSequence(start, end), start, end);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if(mEnable && mEditOpt != null) {
            if(!mRedoOpts.isEmpty()) {
                //重做栈不为空时用户又编辑了文本，视为重做栈
                mRedoOpts.clear();
            }
            mUndoOpts.push(mEditOpt);
        }
        mEditOpt = null;
    }

    public boolean undo() {
        if(!mUndoOpts.isEmpty()) {
            EditOperation undoOpt = mUndoOpts.pop();

            //屏蔽产生的事件
            disabled();
            undoOpt.undo();
            enable();

            //填入重做栈
            mRedoOpts.push(undoOpt);
            if(mRedoOpts.size() > MAX_RECORD_SIZE) {  // 只保留50条记录
                mRedoOpts.removeLast();
            }

            return true;
        }
        return false;
    }

    public boolean redo() {
        if(!mRedoOpts.isEmpty()) {
            EditOperation redoOpt = mRedoOpts.pop();

            //屏蔽重做的事件
            disabled();
            redoOpt.redo();
            enable();

            //填入撤销
            mUndoOpts.push(redoOpt);
            if(mUndoOpts.size() > MAX_RECORD_SIZE) {  // 只保留50条记录
                mUndoOpts.removeLast();
            }
            return true;
        }
        return false;
    }
}

