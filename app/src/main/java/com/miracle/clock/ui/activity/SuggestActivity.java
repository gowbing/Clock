package com.miracle.clock.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.normal.EmptyResponse;
import com.miracle.clock.utils.normal.AppUtils;
import com.miracle.clock.utils.normal.StringUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SuggestActivity extends BaseActivity {

    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.toolbar_back)
    TextView mToolbarBack;
    @Bind(R.id.toolbar_right)
    TextView mToolbarRight;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.et_text)
    EditText mEtText;
    @Bind(R.id.tv_text_length)
    TextView mTvTextLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        mToolbarTitle.setText("修改建议");
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarRight.setText("保存");
        mToolbarRight.setTextColor(AppUtils.getColor(R.color.blue));
        mToolbarRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.isEmpty(mEtText.getText().toString())) {
                    addSuggest();
                }
            }
        });
        mEtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEtText.getText().toString().length() > 100) {
                    String s1 = mEtText.getText().toString().substring(0, 100);
                    mEtText.setText(s1);
                }
                mTvTextLength.setText(String.valueOf(100 - mEtText.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mEtText.getText().toString().length() > 100) {
                    String s1 = mEtText.getText().toString().substring(0, 60);
                    mEtText.setText(s1);
                }
                mTvTextLength.setText(String.valueOf(100 - mEtText.getText().toString().length()));
            }
        });
    }

    private void addSuggest() {
        showDialog("提交中");
        Map<String, String> params = new HashMap<>();
        params.put("content", mEtText.getText().toString());
        params.put("userid", BaseApplication.getInstance().getUserInfo().getUserid() + "");
        params.put("userguid", BaseApplication.getInstance().getUserInfo().getUserguid() + "");
        getDataFromServer(HttpUrlConstant.SUGGEST_ADD, params, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
                hideProgress();
                if (response.getStatus().equals("ok")) {
                    showToast("提交成功");
                    finish();
                } else {
                    showToast(response.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                showToast("网络错误");
            }
        });
    }
}
