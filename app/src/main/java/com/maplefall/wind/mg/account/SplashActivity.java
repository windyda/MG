package com.maplefall.wind.mg.account;

import android.content.Intent;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.base.BaseActivity;
import com.maplefall.wind.mg.note.AddNoteActivity;
import com.maplefall.wind.mg.note.OnItemClickListener;

public class SplashActivity extends BaseActivity {

    private GridView mKeyGrid;
    private TextView mPswText;

    private StringBuilder mPsw = new StringBuilder();

    private final String[] KEYS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "确定"};

    @Override
    public int getLayoutID() {
        return R.layout.activity_splash;
    }

    @Override
    public void findViewAndBinding() {
        mKeyGrid = findViewById(R.id.splash_keyboard_grid);
        mPswText = findViewById(R.id.splash_psw_text);

        findViewById(R.id.splash_note).setOnClickListener(this);
        findViewById(R.id.splash_cancel).setOnClickListener(this);
        findViewById(R.id.splash_clear).setOnClickListener(this);
    }

    @Override
    public void init() {
        KeyboardAdapter adapter = new KeyboardAdapter(this, KEYS);
        adapter.setOnClickListener(mListener);
        mKeyGrid.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.splash_note:
                addNewNote();
                break;
            case R.id.splash_clear:
                clearPsw();
                break;
            case R.id.splash_cancel:
                finish();
        }
    }

    private void clearPsw() {
        mPswText.setText("");
        mPsw.delete(0, mPsw.length());
    }

    private OnItemClickListener mListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onItemLeftSlide(View view, int position) {

        }

        @Override
        public void onItemClick(String content) {
            if (content.equals(getString(R.string.sure))) {
                // 验证密码


            } else {
                mPsw.append(content);
                mPswText.setText(mPswText.getText() + "*");
            }
        }
    };

    private void addNewNote() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra("intent", "add_note");
        startActivity(intent);
        finish();
    }
}
