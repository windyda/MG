package com.maplefall.wind.mg.utils;

import android.content.Context;

import com.maplefall.wind.mg.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;

public class HttpRequestUtil {

    private Context mContext;
    private String mUrl;

    public HttpRequestUtil(Context context, String url) {
        mContext = context;
        mUrl = mContext.getString(R.string.app_server) + url;
    }

    public void get(HashMap<String, String> paramMap, final HttpRequest request) {
        RequestParams params = new RequestParams(mUrl);
        for(String key : paramMap.keySet()) {
            params.addQueryStringParameter(key, paramMap.get(key));
        }

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                request.requestResult(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                request.requestResult(null);
                ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException ex) {
                request.requestResult(null);
                ex.printStackTrace();
            }

            @Override
            public void onFinished() {
                request.requestResult(null);
            }
        });
    }

    public interface HttpRequest {
        void requestResult(String result);
    }
}
