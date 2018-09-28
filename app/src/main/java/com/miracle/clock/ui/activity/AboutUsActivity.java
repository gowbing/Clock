package com.miracle.clock.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.internal.LinkedTreeMap;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.manager.BaseActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutUsActivity extends BaseActivity {

    @Bind(R.id.wv_aboutus)
    WebView wvAboutus;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar_back)
    TextView toolbarBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initView();
        initListener();
    }


    @Override
    public void initView() {
        toolbarTitle.setText("关于我们");
        toolbarBack.setVisibility(View.VISIBLE);
        toolbarBack.setText("返回");
        getData();
    }

    @Override
    public void initListener() {
        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("type", "3");
        getDataFromServer(HttpUrlConstant.ARTICLE_GETINFO, map, LinkedTreeMap.class, new Response.Listener<LinkedTreeMap>() {
            @Override
            public void onResponse(LinkedTreeMap response) {
                if (response.get("status").equals("ok")) {
                    LinkedTreeMap s = (LinkedTreeMap) response.get("results");
                    String data = (String) s.get("content");
                    wvAboutus.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
                } else {
                    showToast(response.get("message").toString());
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
