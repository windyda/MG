package com.maplefall.wind.mg.base;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.bean.User;
import com.maplefall.wind.mg.utils.ActivityManager;
import com.maplefall.wind.mg.utils.EmptyUtil;
import com.maplefall.wind.mg.utils.PermissionUtil;
import com.maplefall.wind.mg.utils.ResourceUtil;
import com.maplefall.wind.mg.view.MessageDialog;
import com.maplefall.wind.mg.view.TitleView;

import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wind on 2018/7/18.
 */

public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    private Toast mToast;
    private Dialog mProgressDialog;
    protected TitleView mTitle;
    private int mPermissionRequestCode = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将当前activity加入堆栈
//        ActivityManager.getActivityManager().addActivity(this);
        setContentView(getLayoutID());

        // 设置沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(getResources().getColor(R.color.statusBarColor, null));
        }
        findViewAndBinding();
        mTitle = findViewById(R.id.title);
        init();
    }

    /**
     * 返回layout的id
     *
     * @return
     */
    public abstract int getLayoutID();

    /**
     * 控件绑定
     * 以及注册监听器
     */
    public abstract void findViewAndBinding();

    /**
     * 逻辑操作
     */
    public abstract void init();

    /**
     * 简化Toast.show()
     */
    public void showToast(String msg) {
        if (null == mToast) {
            mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(msg);
        mToast.show();
    }

    public void showToast(int msg) {
        showToast(getString(msg));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //可以在此处设置返回app时的动画显示或者输入密码
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        ActivityManager.getActivityManager().removeActivity(this);
        //在调用分类的onDestroy（）方法前需要的操作

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 展示加载图框
     *
     * @param cancelable
     */
    protected void showLoadingView(boolean cancelable) {
        if (null == mProgressDialog) {
            mProgressDialog = new Dialog(this, R.style.theme_loading_dialog);
            mProgressDialog.setContentView(R.layout.loading_dialog);
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.show();
        } else {
            mProgressDialog.show();
        }
    }

    /**
     * 使加载图框消失
     */
    protected void hideLoadingView() {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
    }

    public boolean checkEditNull(EditText editText, String viewName) {
        if(EmptyUtil.isEmpty(editText.getText().toString().trim())) {
            showToast(viewName + "不能为空");
            return false;
        }
        return true;
    }

    /**
     * 申请权限的Activity所要重载函数
     * toDoSomething()方法写获得权限之后做的操作逻辑
     * 然后调用toDoSomething()方法
     * 例如：
     * if(checkPermission(Manifest.permission.CAMERA, PERMISSION_CAMERA)) {
     *     toDoSomething();
     * }
     */
    /**
     * private final int PERMISSION_CAMERA =100;
     *
     * @PermissionHandle.PermissionHelper(permissionResult = true, requestCode = PERMISSION_CAMERA)
     * private void toDoSomething() {
     * Toast.makeText(PermissionTestActivity.this, "I am alive!", Toast.LENGTH_SHORT).show();
     * }
     */


//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantedResults) {
//        if (grantedResults.length > 0) {
//            PermissionUtil.permissionRequestActivity(this, grantedResults[0] == PackageManager.PERMISSION_GRANTED, mPermissionRequestCode);
//        }
//    }
//
//    public boolean checkPermission(String permission, int requestCode) {
//        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, permission)) {
//            return true;
//        } else {
//            mPermissionRequestCode = requestCode;
//            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
//            return false;
//        }
//    }

    /**
     * 检查网络连接状态
     * 有网络连接返回true，否则返回false
     *
     * @return
     */
    public boolean checkNetStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            return true;
        }
        return false;
    }

    /**
     * 使app字体不受系统字体大小变化影响
     * sp为单位的字体会受到系统字体大小变化的影响
     * 此函数即为避免App字体受系统字体影响而设置
     * 放在BaseActivity等base类
     */
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration newConfig = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        if (newConfig.fontScale != 1) {
            newConfig.fontScale = 1;
            Context configurationContext = createConfigurationContext(newConfig);
            resources = configurationContext.getResources();
            displayMetrics.scaledDensity = displayMetrics.density * newConfig.fontScale;
        }
        return resources;
    }

    private PermissionResultListener onPermissionListener;

    public void permissionRequest(String permission, PermissionResultListener onBooleanListener) {
        onPermissionListener = onBooleanListener;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, mPermissionRequestCode);
        } else {
            onPermissionListener.getResult(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mPermissionRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限通过
                if (onPermissionListener != null) {
                    onPermissionListener.getResult(true);
                }
            } else {
                //权限拒绝
                if (onPermissionListener != null) {
                    onPermissionListener.getResult(false);
                }
            }
        }
    }

    protected void saveUserToSP() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", User.mgUser.getUserName());
        editor.putString("userMotto", User.mgUser.getMotto());
        editor.putString("userPhone", User.mgUser.getPhone());
        editor.apply();
    }

}