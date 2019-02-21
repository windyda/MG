package com.maplefall.wind.mg.account;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.maplefall.wind.mg.bean.User;
import com.maplefall.wind.mg.main.MainActivity;
import com.maplefall.wind.mg.R;
import com.maplefall.wind.mg.base.BaseActivity;
import com.maplefall.wind.mg.utils.EmptyUtil;
import com.maplefall.wind.mg.utils.HttpRequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterActivity extends BaseActivity {

    private EditText mName;
    private EditText mPassword;
    private EditText mConfirmPsw;
    private EditText mPhone;

    @Override
    public int getLayoutID() {
        return R.layout.activity_register;
    }

    @Override
    public void findViewAndBinding() {
        mName = findViewById(R.id.register_userName);
        mPassword = findViewById(R.id.register_password);
        mConfirmPsw = findViewById(R.id.register_confirm_pwd);
        mPhone = findViewById(R.id.register_phone);

        mConfirmPsw.setOnFocusChangeListener(confirmPswFocusListener);

        findViewById(R.id.title_left_linear).setOnClickListener(this);
        findViewById(R.id.register_register_btn).setOnClickListener(this);
    }

    @Override
    public void init() {
        TextView titleName = findViewById(R.id.title_text_behind_back);
        titleName.setVisibility(View.VISIBLE);
        titleName.setText("注册");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left_linear:
                backToLogin();
                break;
            case R.id.register_register_btn:
                registerAccount();
                break;
        }
    }

    private void backToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerAccount() {
        if (checkEditNull(mName, "用户名") && checkEditNull(mPassword, "密码")
                && checkEditNull(mConfirmPsw, "确认密码") && checkEditNull(mPhone, "手机号码")) {

            final String name = mName.getText().toString().trim();
            final String psw = mPassword.getText().toString().trim();
            final String phone = mPhone.getText().toString().trim();

            // 请求接口
//            String url = "register";
//            final HashMap<String, String> paramMap = new HashMap<>();
//            paramMap.put("userName", name);
//            paramMap.put("password", psw);
//            paramMap.put("phone", phone);
//            new HttpRequestUtil(this, url).get(paramMap, new HttpRequestUtil.HttpRequest() {
//                @Override
//                public void requestResult(String result) {
//                    if (!EmptyUtil.isEmpty(result)) {
//                        try {
//                            JSONObject jsonResult = new JSONObject(result);
//                            boolean isSuccess = jsonResult.getBoolean("successful");
//                            if (isSuccess) {
                                registerSuccess(name, psw, phone);
//                            } else {
//                                showToast(jsonResult.getString("message"));
//                            }
//                        } catch (JSONException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//            });
        }
    }

    private void registerSuccess(String name, String password, String phone) {
        showToast("您已注册成功");

        // 初始化本地
        User.mgUser = new User(name, "", phone, "");
        SharedPreferences preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", name);
        editor.putString("password", password);
        editor.putString("phone", phone);
        editor.apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private View.OnFocusChangeListener confirmPswFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean bln) {
            if (!bln) {
                String psw = mPassword.getText().toString().trim();
                String confirmPsw = mConfirmPsw.getText().toString().trim();
                if (confirmPsw.equals("")) {
                    showToast("请确认登录密码");
                } else if (!psw.equals(confirmPsw)) {
                    mConfirmPsw.setText("");
                    showToast("两次输入的密码不一致，请重新输入");
                }
            }
        }
    };
}
