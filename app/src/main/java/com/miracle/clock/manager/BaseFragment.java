package com.miracle.clock.manager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.miracle.clock.custom.ViewInit;
import com.miracle.clock.utils.http.MyMapRequest;
import com.miracle.clock.widget.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * Created by hss on 2017/6/21.
 */

public abstract class BaseFragment extends Fragment implements ViewInit {
    protected final String TAG = getClass().getSimpleName();

    public String getTAG() {
        return TAG;
    }

    protected LoadingDialog mProgressDialog;

    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (registerEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (registerEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        BaseApplication.getInstance().getRequestQueue().cancelAll(TAG);
    }

    /**
     * 是否在事件总线中注册
     *
     * @return 返回true则要在相应Fragment中定义public void onEvent(SomeEvent event)方法, 默认返回false
     */
    protected boolean registerEventBus() {
        return false;
    }

    /**
     * 显示进度条
     *
     * @param msg 进度条文字
     * @return
     */
    protected LoadingDialog showDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new LoadingDialog(mContext);
        }
        mProgressDialog.setContext(msg);
        mProgressDialog.show();
        return mProgressDialog;
    }

    /**
     * 隐藏进度条
     */
    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 弹出消息
     *
     * @param s 消息内容
     */
    protected void showToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
    }

    /**
     * 弹出消息
     *
     * @param s      消息内容
     * @param length 持续时间
     */
    protected void showToast(String s, int length) {
        Toast.makeText(mContext, s, length).show();
    }

    public <T> void getDataFromServer(String url, Map<String, String> params,
                                      Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        MyMapRequest<T> request = new MyMapRequest<>(Request.Method.POST, url, clazz, listener, errorListener);
        if (params != null) {
            request.addParams(params);
        }
        executeRequest(request);
    }

    public <T> void getDataFromServer(String url,
                                      Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        MyMapRequest<T> request = new MyMapRequest<>(Request.Method.GET, url, clazz, listener, errorListener);
        executeRequest(request);
    }

    protected void executeRequest(Request<?> request) {
        request.setTag(TAG);
        BaseApplication.getInstance().getRequestQueue().add(request);
    }
}
