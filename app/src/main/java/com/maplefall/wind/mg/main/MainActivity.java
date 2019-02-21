package com.maplefall.wind.mg.main;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.base.BaseActivity;
import com.maplefall.wind.mg.note.AddNoteActivity;
import com.maplefall.wind.mg.slide.SettingActivity;
import com.maplefall.wind.mg.storage.DBHelper;
import com.maplefall.wind.mg.bean.Note;
import com.maplefall.wind.mg.note.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawerLayout;

    private RecyclerView mMainList;

    @Override
    public int getLayoutID() {
        return R.layout.activity_main_slice;
    }

    @Override
    public void findViewAndBinding() {
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        mToolbar = findViewById(R.id.main_toolbar);
        mMainList = findViewById(R.id.main_list);

        findViewById(R.id.main_add_btn).setOnClickListener(this);
        findViewById(R.id.main_slide_portrait_layout).setOnClickListener(this);
        findViewById(R.id.main_slide_setting_option).setOnClickListener(this);
    }

    @Override
    public void init() {
        initToolbar();
        initList();
        initSlideLayout();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_add_btn:
                Intent intent = new Intent(this, AddNoteActivity.class);
                intent.putExtra("intent", "add_note");
                startActivity(intent);
                break;
            case R.id.main_slide_portrait_layout:
                setUserInfo();
                break;
            case R.id.main_slide_setting_option:
                toSetting();
                break;
        }
    }

    private void setUserInfo() {

    }

    private void toSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void initList() {
        mMainList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mMainList.addItemDecoration(new ItemDecoration(Integer.parseInt(getString(R.string.item_space))));

        final ArrayList<Note> noteList = new DBHelper(this).getAllNote();
        Collections.reverse(noteList);  // noteList 中的数据本来是新的数据在底部，反转之后使得最新的数据显示在最前面
        MainListAdapter adapter = new MainListAdapter(this, noteList);
//        Intent intent = getIntent();
//        if(intent.getBooleanExtra("dataChanged", false)) {
//            // RecyclerView 在 noteList 发生变化时不会刷新，此处主动调用方法使其刷新
//            adapter.notifyDataSetChanged();
//        }
        mMainList.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent toEdit = new Intent(MainActivity.this, AddNoteActivity.class);
                toEdit.putExtra("intent", "edit_note");
                toEdit.putExtra("editNote", noteList.get(position));
                startActivity(toEdit);
            }

            @Override
            public void onItemLeftSlide(View view, int position) {

            }

            @Override
            public void onItemClick(String content) {

            }
        });
    }

    private void initToolbar() {
        mToolbar.setTitle("MyGarden");
        //实现了监听的开关 ，最后2个参数可以写0
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mToggle.syncState();//同步drawerLayout

        //给drawerLayout添加监听
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                mToggle.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                mToggle.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                mToggle.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                mToggle.onDrawerStateChanged(newState);
            }
        });
    }

    //drawerLayout实现侧拉
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }


    private void initSlideLayout() {
        // 初始化头像
        RequestOptions requestOptions = RequestOptions.circleCropTransform();
        Glide.with(this)
                .load(R.drawable.portrait)
                .apply(requestOptions)
                .into((ImageView) findViewById(R.id.main_slide_portrait));


        // 初始化账户名及座右铭

    }

    private class ItemDecoration extends RecyclerView.ItemDecoration {
        private int mSpace;

        private ItemDecoration(int space) {
            mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = mSpace;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = mSpace;
            }
        }
    }
}