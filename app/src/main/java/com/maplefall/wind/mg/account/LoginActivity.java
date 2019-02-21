package com.maplefall.wind.mg.account;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.maplefall.wind.mg.bean.User;
import com.maplefall.wind.mg.main.MainActivity;
import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.base.BaseActivity;
import com.maplefall.wind.mg.base.PermissionResultListener;
import com.maplefall.wind.mg.utils.EmptyUtil;

public class LoginActivity extends BaseActivity {

    private ImageView mPortraitImage;
    private EditText mAccount;
    private EditText mPassword;
//    private CheckBox mKeepCodeChk;

//    private boolean mKeepCode = true;

    private SharedPreferences mSharedPreferences;

    private boolean mPermissionResult = true;

    @Override
    public int getLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    public void findViewAndBinding() {
        mPortraitImage = findViewById(R.id.login_portrait_image);
        mAccount = findViewById(R.id.login_account_txt);
        mPassword = findViewById(R.id.login_password_txt);
//        mKeepCodeChk = findViewById(R.id.login_keepCode_chk);

        findViewById(R.id.login_login_btn).setOnClickListener(this);
        findViewById(R.id.login_forget_psw_btn).setOnClickListener(this);
        findViewById(R.id.login_register_btn).setOnClickListener(this);
    }

    @Override
    public void init() {
        // 检查是否有读写权限
        checkPermissions();

        // 添加头像图片
        //设置图片圆角角度
        RequestOptions requestOptions = RequestOptions.circleCropTransform();
        Glide.with(this)
                .load(R.drawable.portrait)
                .apply(requestOptions)
                .into(mPortraitImage);

        mSharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
//        mKeepCode = mSharedPreferences.getBoolean("keepCode", true);
//        mKeepCodeChk.setChecked(mKeepCode);
//        mKeepCodeChk.setOnClickListener(this);

        String phone = mSharedPreferences.getString("phone", "");
        if (!EmptyUtil.isEmpty(phone)) {
            mAccount.setText(phone);
            mPassword.requestFocus();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_register_btn:
                toRegister();
                break;
            case R.id.login_login_btn:
                login();
                break;
            case R.id.login_forget_psw_btn:
                forgetPassword();
                break;
//            case R.id.login_keepCode_chk:
//                checkKeepCode();
//                break;
        }
    }

    private void checkPermissions() {
        //权限申请
        //需要外存的读写权限，没有该权限则不给予使用
        // -> 在登录及跳转注册界面时会被拦截
        permissionRequest(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionResultListener() {
            @Override
            public void getResult(boolean result) {
                mPermissionResult = result;
            }
        });
    }

    private void forgetPassword() {
        showToast("程序员太懒了，现在还没有开发这个功能");
    }

    private void login() {
        if (!mPermissionResult) {
            showToast("没有相关权限，无法使用该权限");
            finish();
        }
        if (checkEditNull(mAccount, "用户名或手机号") && checkEditNull(mPassword, "密码")) {
            String shareName = mSharedPreferences.getString("userName", "");
            String sharePsw = mSharedPreferences.getString("password", "");
            String sharePhone = mSharedPreferences.getString("phone", "");

            String account = mAccount.getText().toString().trim();
            if (account.equals(shareName) || account.equals(sharePhone)) {
                if (sharePsw.equals(mPassword.getText().toString().trim())) {
                    User.mgUser = new User(shareName, "", sharePhone, "");

                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showToast("账号或密码输入不正确");
                }
            } else {
                showToast("账号输入不正确或账号不存在");
            }
        }
    }

    private void checkKeepCode() {
//        mKeepCode = mKeepCodeChk.isChecked();
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        editor.putBoolean("keepCode", mKeepCode);
//        editor.apply();
    }

    private void toRegister() {
        if (mPermissionResult) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        } else {
            showToast("没有相关权限，无法使用该APP");
        }
    }
}
