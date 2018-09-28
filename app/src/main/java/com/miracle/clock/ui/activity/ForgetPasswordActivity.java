package com.miracle.clock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.model.normal.EmptyResponse;
import com.miracle.clock.utils.normal.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ForgetPasswordActivity extends BaseActivity {

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
    @Bind(R.id.et_phone)
    EditText mEtPhone;
    @Bind(R.id.et_code)
    EditText mEtCode;
    @Bind(R.id.tv_send)
    TextView mTvSend;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.et_password_again)
    EditText mEtPasswordAgain;
    @Bind(R.id.tv_login)
    TextView mTvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("找回密码");
    }

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("处理中");
                String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
                String phone = mEtPhone.getText().toString();
                if (StringUtils.isEmpty(phone)) {
                    showToast("请输入手机号");
                    return;
                } else {
                    if (!Pattern.matches(REGEX_MOBILE, phone)) {
                        showToast("请输入正确的手机号");
                        return;
                    }
                }
                sendCode(phone);
            }
        });
        mTvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mEtPhone.getText().toString();
                String code = mEtCode.getText().toString();
                String pwd = mEtPassword.getText().toString();
                String pwdagain = mEtPasswordAgain.getText().toString();
                if (StringUtils.isEmpty(phone)) {
                    showToast("请输入手机号");
                    return;
                }
                if (StringUtils.isEmpty(code)) {
                    showToast("请输入验证码");
                    return;
                }
                if (StringUtils.isEmpty(pwd)) {
                    showToast("请输入密码");
                    return;
                }
                if (!pwd.equals(pwdagain)) {
                    showToast("两次密码不一致");
                    return;
                }
                forgetPwd(phone, pwd, code);
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

    private void sendCode(String phone) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        getDataFromServer(HttpUrlConstant.USER_SENDCODE, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
                if (response.getStatus().equals("ok")) {
                    hideProgress();
                    timer.start();
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

    private void forgetPwd(String phone, String pwd, String verify) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("password", pwd);
        map.put("verify", verify);
        getDataFromServer(HttpUrlConstant.USER_CHANGEPWD, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
                if (response.getStatus().equals("ok")) {
                    startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
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
}
