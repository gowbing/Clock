package com.miracle.clock.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.normal.EmptyResponse;
import com.miracle.clock.utils.normal.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity {

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
    @Bind(R.id.tv_phone)
    EditText mTvPhone;
    @Bind(R.id.tv_next)
    TextView mTvNext;
    @Bind(R.id.ll_first)
    LinearLayout mLlFirst;
    @Bind(R.id.tv_code)
    EditText mTvCode;
    @Bind(R.id.tv_second)
    TextView mTvSecond;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.et_password_again)
    EditText mEtPasswordAgain;
    @Bind(R.id.tv_success)
    TextView mTvSuccess;
    @Bind(R.id.ll_second)
    LinearLayout mLlSecond;
    @Bind(R.id.cb_read)
    CheckBox mCbRead;
    @Bind(R.id.textView)
    TextView mTextView;
    @Bind(R.id.tv_paper)
    TextView mTvPaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("注册账号");
    }

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mTvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
                String phone = mTvPhone.getText().toString();
                if (StringUtils.isEmpty(phone)) {
                    showToast("请输入手机号");
                    return;
                } else {
                    if (!Pattern.matches(REGEX_MOBILE, phone)) {
                        showToast("请输入正确的手机号");
                        return;
                    }
                    showDialog("处理中");
                    sendCode(phone);
                    mLlFirst.setVisibility(View.GONE);
                    mLlSecond.setVisibility(View.VISIBLE);
                }
            }
        });

        mTvSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mTvPhone.getText().toString();
                String code = mTvCode.getText().toString();
                String pwd = mEtPassword.getText().toString();
                String pwdagain = mEtPasswordAgain.getText().toString();
                if (StringUtils.isEmpty(code)) {
                    showToast("请输入验证码");
                    return;
                }
                if (StringUtils.isEmpty(pwd)) {
                    showToast("请输入密码");
                    return;
                }
                if (!pwd.equals(pwdagain)) {
                    showToast("两次密码输入不一致");
                    return;
                }
                regist(phone, pwd, code);
            }
        });

        mTvSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("处理中");
                String phone = mTvPhone.getText().toString();
                sendCode(phone);
            }
        });

    }

    private void regist(String phone, String password, String code) {
        showDialog("处理中");
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("password", password);
        map.put("verify", code);
        getDataFromServer(HttpUrlConstant.USER_REGIST, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
                hideProgress();
                if (response.getStatus().equals("ok")) {
                    showToast("注册成功");
                    BaseApplication.getInstance().finishActivity(RegisterActivity.this);
                    BaseApplication.getInstance().finishActivityByClass(LoginActivity.class);
                    BaseApplication.getInstance().finishActivityByClass(MineActivity.class);
                    finish();
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

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mTvSecond.setText((millisUntilFinished / 1000) + "s");
        }

        @Override
        public void onFinish() {
            mTvSecond.setEnabled(true);
            mTvSecond.setText("获取验证码");
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
                    mTvSecond.setEnabled(false);
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

}
