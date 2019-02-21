package com.maplefall.wind.mg.note;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.base.BaseActivity;
import com.maplefall.wind.mg.bean.Note;
import com.maplefall.wind.mg.main.MainActivity;
import com.maplefall.wind.mg.span.SpanParser;
import com.maplefall.wind.mg.span.SpanText;
import com.maplefall.wind.mg.storage.DBHelper;
import com.maplefall.wind.mg.utils.AnimationUtil;
import com.maplefall.wind.mg.utils.EmptyUtil;
import com.maplefall.wind.mg.utils.GeneralUtil;

/**
 * 添加日记类
 * 完成编辑、添加样式、保存等功能
 */

public class AddNoteActivity extends BaseActivity {

    private SpanText mEdit;
    private EditText mNoteTitle;
    private RecyclerView mIconList;
    private String[] ICON_NAMES = {"bold", "italic", "bg", "underline", "cross", "link", "bullet"};
    private LinearLayout mIconLayout;

    private Note mCurrentNote;

    private FloatingActionButton mEnableEditBtn;
    private ImageView mDelBtn;

    private boolean isEdit = false;
    private boolean mHasEdited = false;

    @Override
    public int getLayoutID() {
        return R.layout.activity_add_note;
    }

    @Override
    public void findViewAndBinding() {
        findViewById(R.id.add_undo_icon).setOnClickListener(this);
        findViewById(R.id.add_redo_icon).setOnClickListener(this);
        findViewById(R.id.title_left_linear).setOnClickListener(this);

        // 软键盘上方的功能栏
        mIconList = findViewById(R.id.add_icons_list);
        mIconLayout = findViewById(R.id.icon_linear);
        mEdit = findViewById(R.id.add_note_edit);
        mNoteTitle = findViewById(R.id.add_note_title);
        mEnableEditBtn = findViewById(R.id.add_edit_btn);
        mDelBtn = findViewById(R.id.title_right_image);

        mEnableEditBtn.setOnClickListener(this);
        mDelBtn.setOnClickListener(this);

        mDelBtn.setImageResource(R.drawable.delete);
    }

    @Override
    public void init() {

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        ((LinearLayoutManager) manager).setOrientation(LinearLayoutManager.HORIZONTAL);  // 水平滑动
        mIconList.setLayoutManager(manager);

        AddIconListAdapter adapter = new AddIconListAdapter(this, ICON_NAMES);
        adapter.setOnItemClickListener(mItemClickListener);
        mIconList.setAdapter(adapter);

        // 初始化笔记显示
        Intent intent = getIntent();
        if (intent.getStringExtra("intent").equals("edit_note")) {
            mCurrentNote = (Note) intent.getSerializableExtra("editNote");
            if (mCurrentNote != null) {
                mNoteTitle.setText(mCurrentNote.getTitle());
                mEdit.fromHtml(mCurrentNote.getContent());
            }

            isEdit = true;
            mEdit.setSelection(mEdit.getEditableText().length());  // 让焦点在文本最后
            mEdit.setOnFocusChangeListener(mEditFocusListener);
        } else {
            // 点击添加按钮、以及其余情况按照添加笔记进行操作
            isEdit = false;
            mCurrentNote = new Note();
        }

        initEditable();
    }

    private void initEditable() {
        if (isEdit) {
            // 显示 “编辑” 按钮，使得编辑不可用
            mEnableEditBtn.setVisibility(View.VISIBLE);
            mEdit.setFocusable(false);
            mEdit.setFocusableInTouchMode(false);
            mNoteTitle.setFocusable(false);
            mNoteTitle.setFocusableInTouchMode(false);

            mDelBtn.setVisibility(View.VISIBLE);
        } else {
            mEnableEditBtn.setVisibility(View.GONE);
        }
    }

    private View.OnFocusChangeListener mEditFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                mIconLayout.startAnimation(new AnimationUtil().fadeInOrOut(true));
                mIconLayout.setVisibility(View.VISIBLE);
            } else {
                mIconLayout.startAnimation(new AnimationUtil().fadeInOrOut(false));
                mIconLayout.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left_linear:
                back();
            case R.id.add_undo_icon:
                noteUndo();
                return;
            case R.id.add_redo_icon:
                noteRedo();
                return;
            case R.id.add_edit_btn:
                enableEdit();
                return;
            case R.id.title_right_image:
                removeNote();
                return;
        }
    }

    private void removeNote() {
        // 做删除操作
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.deleteNote(mCurrentNote.getTime());
    }

    private void enableEdit() {
        mHasEdited = true;  // 点击了编辑按钮就当做已经编辑了笔记
        mEnableEditBtn.setVisibility(View.GONE);
        mEdit.setFocusable(true);
        mEdit.setFocusableInTouchMode(true);
        mNoteTitle.setFocusable(true);
        mNoteTitle.setFocusableInTouchMode(true);
    }

    private void noteUndo() {
        mEdit.undo();
    }

    private void noteRedo() {
        mEdit.redo();
    }

    private void back() {
        if (isEdit) {
            if (mHasEdited) {
                // 先删除，再做添加
                DBHelper dbHelper = new DBHelper(this);
                dbHelper.deleteNote(mCurrentNote.getTime());

                saveNote();
            }
        } else {
            saveNote();
        }

        // 考虑从 splash 页面近来的情况，不能简单调用 finish() 函数
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveNote() {
        String title = mNoteTitle.getText().toString().trim();
        String content = new SpanParser().toHtml(mEdit.getText());

        if (EmptyUtil.isEmpty(title) && EmptyUtil.isEmpty(content)) {
            return;  // 如果标题和内容都为空，则不进行写文件操作
        }

        mCurrentNote.setTitle(title);
        mCurrentNote.setContent(content);
        mCurrentNote.setTime(new GeneralUtil().getTime("yyyyMMddHHmmss"));
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.addNote(mCurrentNote);
    }

    private void setLink() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText inputText = new EditText(this);
        builder.setTitle("请输入链接地址");
        builder.setView(inputText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mEdit.setLink(inputText.getText().toString());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            iconClick(position);
        }

        @Override
        public void onItemLeftSlide(View view, int position) {

        }

        @Override
        public void onItemClick(String content) {

        }
    };

    private void iconClick(int position) {
        switch (ICON_NAMES[position]) {
            case "bold":
                mEdit.setBold();
                return;
            case "italic":
                mEdit.setItalic();
                return;
            case "underline":
                mEdit.setUnderLine();
                return;
            case "cross":
                mEdit.setStrikeThrough();
                return;
            case "bullet":
                mEdit.setBullet();
                return;
            case "bg":
                mEdit.setBackground();
                return;
            case "link":
                setLink();
                return;
        }
    }
}
