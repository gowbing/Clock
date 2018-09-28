package com.miracle.clock.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.os.Bundle;

import com.czsirius.clock.R;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.orhanobut.logger.Logger;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        initPermission();
    }

    @Override
    public void initListener() {
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (BaseApplication.getInstance().getUserInfo().getUserid() == 0) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
            } else {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }
    };

    private static final int BAIDU_PERMISSION = 100;

    private void initPermission() {
        Logger.d("Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    this.checkSelfPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED) {
                // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义)
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, BAIDU_PERMISSION);
            } else {
                mHandler.sendEmptyMessageDelayed(1, 3000);
            }
        } else {
            mHandler.sendEmptyMessageDelayed(1, 3000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.d("onRequestPermissionsResult");
        switch (requestCode) {
            case BAIDU_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Logger.d("BAIDU_PERMISSION");
                    showToast("获取权限失败！");
                    finish();
                } else {
                    mHandler.sendEmptyMessageDelayed(1, 3000);
                }
                break;
            default:
                break;
        }
    }

}
