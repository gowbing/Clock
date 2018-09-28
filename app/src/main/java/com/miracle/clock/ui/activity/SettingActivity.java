package com.miracle.clock.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.facebook.drawee.view.SimpleDraweeView;
import com.czsirius.clock.R;
import com.miracle.clock.constant.Constants;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.normal.UserInfoResponse;
import com.miracle.clock.utils.normal.StringUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.iv_head_big)
    ImageView mIvHeadBig;
    @Bind(R.id.iv_back)
    TextView mIvBack;
    @Bind(R.id.rl_toolbar)
    RelativeLayout mRlToolbar;
    @Bind(R.id.iv_head)
    SimpleDraweeView mIvHead;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.ll_1)
    LinearLayout mLl1;
    @Bind(R.id.ll_2)
    LinearLayout mLl2;
    @Bind(R.id.ll_3)
    LinearLayout mLl3;
    @Bind(R.id.ll_4)
    LinearLayout mLl4;
    @Bind(R.id.ll_5)
    LinearLayout mLl5;
    @Bind(R.id.ll_6)
    LinearLayout mLl6;

    UserInfoResponse.Data info;

    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    @Override
    public void initView() {
        info = BaseApplication.getInstance().getUserInfo();
        if (info.getUserid() != 0) {
            mTvName.setText(info.getNickname());
            mIvHead.setImageURI(Uri.parse(info.getImgUrl()));
        }
        if (!StringUtils.isEmpty(info.getImgUrl())) {
            Glide.with(this).load(info.getImgUrl())
                    .bitmapTransform(new BlurTransformation(this, 8), new CenterCrop(this))
                    .into(mIvHeadBig);
        } else {
            Glide.with(this).load(R.drawable.ic_head)
                    .bitmapTransform(new BlurTransformation(this, 8), new CenterCrop(this))
                    .into(mIvHeadBig);
        }
    }

    private void getUserInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("userid", "" + BaseApplication.getInstance().getUserInfo().getUserid());
        map.put("userguid", "" + BaseApplication.getInstance().getUserInfo().getUserguid());
        getDataFromServer(HttpUrlConstant.USER_INFO, map, UserInfoResponse.class, new Response.Listener<UserInfoResponse>() {
            @Override
            public void onResponse(UserInfoResponse response) {
                if (response.getStatus().equals("ok")) {
                    BaseApplication.getInstance().setUserInfo(response.getResults());
                    info = BaseApplication.getInstance().getUserInfo();
                    if (info.getUserid() != 0) {
                        mTvName.setText(info.getNickname());
                        mIvHead.setImageURI(Uri.parse(info.getImgUrl()));
                    }
                    if (!StringUtils.isEmpty(info.getImgUrl())) {
                        Glide.with(mContext).load(info.getImgUrl())
                                .bitmapTransform(new BlurTransformation(mContext, 8), new CenterCrop(mContext))
                                .into(mIvHeadBig);
                    } else {
                        Glide.with(mContext).load(R.drawable.ic_head)
                                .bitmapTransform(new BlurTransformation(mContext, 8), new CenterCrop(mContext))
                                .into(mIvHeadBig);
                    }
                } else {
                    showToast(response.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("网络错误，请稍后再试");
            }
        });
    }

    @Override
    public void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 1;
                initPermission();
            }
        });
        mLl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, UserInfoActivity.class));
            }
        });
        mLl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, HelpActivity.class));
            }
        });
        mLl4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = 0;
                initPermission();
            }
        });
        mLl5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, AboutUsActivity.class));
            }
        });
        mLl6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, SuggestActivity.class));
            }
        });
    }

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
                mHandler.sendEmptyMessage(type);
            }
        } else {
            mHandler.sendEmptyMessage(type);
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
                } else {
                    mHandler.sendEmptyMessage(type);
                }
                break;
            default:
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                startActivity(new Intent(mContext, DownloadActivity.class));
            } else if (msg.what == 0) {
                showDialog("正在删除..");
                File file = new File(Constants.DOWNLOAD_PATH);
                File[] files = file.listFiles();
                if (files != null) {
                    for (File i : files) {
                        if (i.exists()) {
                            i.delete();
                        }
                    }
                }
                hideProgress();
                showToast("已清空缓存！");
            }
        }
    };
}
