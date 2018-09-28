package com.miracle.clock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.normal.EmptyResponse;
import com.miracle.clock.model.normal.UserInfoResponse;
import com.miracle.clock.utils.normal.AppUtils;
import com.miracle.clock.utils.normal.StringUtils;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

public class LoginActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener {

    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.toolbar_back)
    TextView mToolbarBack;
    @Bind(R.id.toolbar_right)
    TextView mToolbarRight;
    @Bind(R.id.toolbar_right_img)
    ImageView mToolbarRightImg;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.rb_1)
    RadioButton mRb1;
    @Bind(R.id.rb_2)
    RadioButton mRb2;
    @Bind(R.id.rg_login_type)
    RadioGroup mRgLoginType;
    @Bind(R.id.et_phone)
    EditText mEtPhone;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.et_code)
    EditText mEtCode;
    @Bind(R.id.tv_send)
    TextView mTvSend;
    @Bind(R.id.ll_send)
    LinearLayout mLlSend;
    @Bind(R.id.tv_login)
    TextView mTvLogin;
    @Bind(R.id.tv_forget)
    TextView mTvForget;
    @Bind(R.id.ll_wechat)
    LinearLayout mLlWechat;
    @Bind(R.id.ll_qq)
    LinearLayout mLlQq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        mToolbarTitle.setText("登陆");
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarRight.setText("注册");
        mToolbarRight.setTextColor(AppUtils.getColor(R.color.blue));
    }

    @Override
    public void initListener() {
        mRgLoginType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (mRb1.isChecked()) {
                    mEtPassword.setVisibility(View.VISIBLE);
                    mLlSend.setVisibility(View.GONE);
                } else {
                    mEtPassword.setVisibility(View.GONE);
                    mLlSend.setVisibility(View.VISIBLE);
                }
            }
        });
        mToolbarBack.setOnClickListener(this);
        mToolbarRight.setOnClickListener(this);
        mTvForget.setOnClickListener(this);
        mTvLogin.setOnClickListener(this);
        mLlWechat.setOnClickListener(this);
        mLlQq.setOnClickListener(this);
        mTvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
                String phone = mEtPhone.getText().toString();
                if (!Pattern.matches(REGEX_MOBILE, phone)) {
                    showToast("请输入正确的手机号");
                } else {
                    sendCode(phone);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.toolbar_right:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.tv_forget:
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
                break;
            case R.id.ll_wechat:
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                wechat.removeAccount(true);
                authorize(wechat);
                break;
            case R.id.ll_qq:
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                qq.removeAccount(true);
                authorize(qq);
                break;
            case R.id.tv_login:
                String phone = mEtPhone.getText().toString();
                String password = mEtPassword.getText().toString();
                String code = mEtCode.getText().toString();
                String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
                if (mRb1.isChecked()) {
                    if (!Pattern.matches(REGEX_MOBILE, phone)) {
                        showToast("请输入正确的手机号码");
                        return;
                    }
                    if (StringUtils.isEmpty(password)) {
                        showToast("请输入密码");
                        return;
                    }
                    login(phone, password);
//                    thirdLogin("12", "12", "1", "23", "1");
                } else {
                    if (StringUtils.isEmpty(phone)) {
                        showToast("请输入手机号码");
                        return;
                    }
                    if (StringUtils.isEmpty(code)) {
                        showToast("请输入验证码");
                        return;
                    }
                    loginWithCode(phone, code);
                }
                break;
        }
    }

    public void thirdLogin(String nickname, String avatarUrl, String sex, String uid, String logintype) {
        showDialog("处理中");
        Map<String, String> map = new HashMap<String, String>();
        map.put("nickname", nickname);
        map.put("avatarUrl", avatarUrl);
        map.put("sex", sex);
        map.put("uid", uid);
        map.put("logintype", logintype);
        getDataFromServer(HttpUrlConstant.LOGIN_THIRD, map, UserInfoResponse.class, new Response.Listener<UserInfoResponse>() {
            @Override
            public void onResponse(UserInfoResponse response) {
                hideProgress();
                if (response.getStatus().equals("ok")) {
                    BaseApplication.getInstance().setUserInfo(response.getResults());
//                    EventBus.getDefault().post(new MineFinishEvent());
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                } else {
                    showToast(response.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                showToast("网络错误，请稍后再试");
            }
        });
    }

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            mTvSend.setBackgroundResource(R.drawable.bg_btn_grey);
            mTvSend.setEnabled(false);
            mTvSend.setText((millisUntilFinished / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            mTvSend.setBackgroundResource(R.drawable.bg_btn);
            mTvSend.setEnabled(true);
            mTvSend.setText("获取验证码");
        }
    };

    private void login(String phone, String password) {
        showDialog("处理中");
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("password", password);
        getDataFromServer(HttpUrlConstant.USER_LOGIN, map, UserInfoResponse.class, new Response.Listener<UserInfoResponse>() {
            @Override
            public void onResponse(UserInfoResponse response) {
                hideProgress();
                if (response.getStatus().equals("ok")) {
                    BaseApplication.getInstance().setUserInfo(response.getResults());
//                    EventBus.getDefault().post(new MineFinishEvent());
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                } else {
                    showToast(response.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                showToast("网络错误，请稍后再试");
            }
        });
    }

    private void loginWithCode(String phone, String verify) {
        showDialog("处理中");
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("verify", verify);
        getDataFromServer(HttpUrlConstant.USER_LOGINWITHCODE, map, UserInfoResponse.class, new Response.Listener<UserInfoResponse>() {
            @Override
            public void onResponse(UserInfoResponse response) {
                hideProgress();
                if (response.getStatus().equals("ok")) {
                    BaseApplication.getInstance().setUserInfo(response.getResults());
//                    EventBus.getDefault().post(new MineFinishEvent());
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                } else {
                    showToast(response.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                showToast("网络错误，请稍后再试");
            }
        });
    }

    private void sendCode(String phone) {
        showDialog("处理中");
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        getDataFromServer(HttpUrlConstant.USER_SENDCODE, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
                if (response.getStatus().equals("ok")) {
                    hideProgress();
                    timer.start();
                } else {
                    hideProgress();
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
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
        Log.i("LoginActivity", "onComplete");
//        if (action == Platform.ACTION_USER_INFOR) {
//            Log.i("LoginActivity","onComplete");
        Message msg = new Message();
        msg.what = MSG_AUTH_COMPLETE;
        msg.obj = new Object[]{(platform.getName() == null ? Wechat.NAME : platform.getName()), hashMap};
        handler.sendMessage(msg);
//        }
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Log.i("LoginActivity", "onError");
        handler.sendEmptyMessage(MSG_AUTH_ERROR);
        platform.removeAccount(true);
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Log.i("LoginActivity", "onCancel");
        handler.sendEmptyMessage(MSG_AUTH_CANCEL);
        platform.removeAccount(true);
    }

    //执行授权,获取用户信息
    private void authorize(Platform plat) {
        plat.setPlatformActionListener(this);
        //关闭SSO授权
        plat.SSOSetting(true);
        plat.authorize();
        plat.showUser(null);
//        showDialog("请稍候");
//        setProgressDialogCancelable(false);
    }

    private static final int MSG_AUTH_CANCEL = 2;
    private static final int MSG_AUTH_ERROR = 3;
    private static final int MSG_AUTH_COMPLETE = 4;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTH_CANCEL: {
                    //取消授权
                    hideProgress();
                    showToast("取消授权");
                    break;
                }
                case MSG_AUTH_ERROR: {
                    //授权失败
                    hideProgress();
                    showToast("授权失败");
                    break;
                }
                case MSG_AUTH_COMPLETE: {
                    //授权成功
                    hideProgress();
                    Object[] objs = (Object[]) msg.obj;
                    String platformName = (String) objs[0];
                    Log.i("LoginActivity", "platformName:" + platformName);
                    Platform platform = ShareSDK.getPlatform(platformName);
//                    HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
//                    Log.e("wxz", res.toString());
                    String sex = "";
                    String head = "";
                    String nickname = "";
                    String openid = "";
                    String type = "22";
                    if (platform != null && platform.getDb() != null) {
                        String gender = "";
                        gender = platform.getDb().getUserGender();
                        if (gender == null || gender.equals("m")) {
                            sex = "1";
                        } else {
                            sex = "0";
                        }
                        head = platform.getDb().getUserIcon();
                        nickname = platform.getDb().getUserName();
                        openid = platform.getDb().getUserId();
                        type = "22";
                    }
                    if (platformName.equals(QQ.NAME)) {
                        type = "24";
                    } else if (platformName.equals(Wechat.NAME)) {
                        type = "22";
                    }

                    Logger.d(sex + "\n" + head + "\n" + nickname + "\n" + openid + "\n" + type);

                    thirdLogin(nickname, head, sex, openid, type);
                    break;
                }
            }
        }
    };

}
