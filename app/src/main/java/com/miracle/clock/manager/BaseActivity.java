package com.miracle.clock.manager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.miracle.clock.custom.ViewInit;
import com.miracle.clock.utils.http.MyMapRequest;
import com.miracle.clock.widget.dialog.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hss on 2017/6/21.
 */

public abstract class BaseActivity extends AppCompatActivity implements ViewInit {

    protected final String TAG = getClass().getSimpleName();

    public String getTAG() {
        return TAG;
    }

    protected Context mContext = null;
    protected LoadingDialog mProgressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        if (registerEventBus()) {
            //在事件总线中注册
            EventBus.getDefault().register(this);
        }
        Thread.setDefaultUncaughtExceptionHandler(BaseApplication.getInstance());
        BaseApplication.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (registerEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        BaseApplication.getInstance().getRequestQueue().cancelAll(TAG);
        BaseApplication.getInstance().finishActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Thread.setDefaultUncaughtExceptionHandler(BaseApplication.getInstance());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 是否在事件总线中注册
     *
     * @return 返回true则要在相应的activity中定义public void onEvent(SomeEvent event)方法,默认返回false
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
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    /**
     * 弹出消息
     *
     * @param s      消息内容
     * @param length 持续时间
     */
    protected void showToast(String s, int length) {
        Toast.makeText(this, s, length).show();
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
