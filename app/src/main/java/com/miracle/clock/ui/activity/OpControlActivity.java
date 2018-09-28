package com.miracle.clock.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.model.event.ToMainEvent;
import com.miracle.clock.model.normal.Audio;
import com.miracle.clock.model.normal.AudioListResponse;
import com.miracle.clock.utils.normal.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OpControlActivity extends BaseActivity {

    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.toolbar_back)
    TextView toolbarBack;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.ll_reminder)
    LinearLayout mLlReminder;
    @Bind(R.id.pull_to_refresh)
    SwipeRefreshLayout mPullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_control);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    int tagid;

    @Override
    public void initView() {
        toolbarBack.setVisibility(View.VISIBLE);
        tagid = getIntent().getIntExtra("tagid", 0);
        String search = getIntent().getStringExtra("search");
        if (search == null || "".equals(search)) {
            toolbarTitle.setText(getIntent().getStringExtra("tagname"));
            getAudioList(String.valueOf(tagid));
        } else {
            getAudioSearch(String.valueOf(0), search);
        }

        mPullToRefresh.setColorSchemeResources(R.color.white, R.color.black);
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum1 = 1;
                getAudioList(tagid + "");
            }
        });
    }

    @Override
    public void initListener() {
        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(OpControlActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索操作的方法，在该方法中可以加入mEditSearchUser的非空判断
                    String str = etSearch.getText().toString();
                    if (!StringUtils.isEmpty(str)) {
                        getAudioSearch(String.valueOf(tagid), str);
                    } else {
                        showToast("请输入搜索内容");
                    }
                }
                return false;
            }
        });
    }

    private int pageSize = 10;
    private int pageNum1 = 1;
    private int pageNum2 = 1;
    private List<Audio> ids = new ArrayList<>();

    public void getAudioList(String tagid) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tagid", tagid);
        map.put("pageNum", String.valueOf(pageNum1));
        map.put("pageSize", String.valueOf(pageSize));
        getDataFromServer(HttpUrlConstant.AUDIO_GRTLIST, map, AudioListResponse.class, new Response.Listener<AudioListResponse>() {
            @Override
            public void onResponse(AudioListResponse response) {
                mPullToRefresh.setRefreshing(false);
                if (response.getStatus().equals("ok")) {
                    ids.clear();
                    ids.addAll(response.getResults());
                    if (pageNum1 == 1) {
                        mLlReminder.removeAllViews();
                        for (Audio i : response.getResults()) {
                            addMessageItem(mLlReminder, i);
                        }
                        addLoadMore(mLlReminder, 1);
                    } else {
                        mLlReminder.removeView(loadMore);
                        for (Audio i : response.getResults()) {
                            addMessageItem(mLlReminder, i);
                        }
                        addLoadMore(mLlReminder, 1);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPullToRefresh.setRefreshing(false);
            }
        });
    }

    ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();

    public void getAudioSearch(String tagid, String search) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tagid", tagid);
        map.put("pageNum", String.valueOf(pageNum2));
        map.put("pageSize", String.valueOf(pageSize));
        map.put("content", search);
        getDataFromServer(HttpUrlConstant.ARTICLE_SEARCH, map, AudioListResponse.class, new Response.Listener<AudioListResponse>() {
            @Override
            public void onResponse(AudioListResponse response) {
                mPullToRefresh.setRefreshing(false);
                if (response.getStatus().equals("ok")) {
                    ids.clear();
                    ids.addAll(response.getResults());
                    mLlReminder.removeAllViews();
                    for (Audio i : response.getResults()) {
                        addMessageItem(mLlReminder, i);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mPullToRefresh.setRefreshing(false);
            }
        });
    }

    private void addMessageItem(ViewGroup parent, final Audio data) {
        LinearLayout item = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.collect_item_list, parent, false);
        TextView tvName = (TextView) item.findViewById(R.id.tv_name);
        TextView tvWriter = (TextView) item.findViewById(R.id.tv_writer);
        TextView tvListenTime = (TextView) item.findViewById(R.id.tv_listen_time);

        tvName.setText(data.getName());
        tvWriter.setText(data.getAuthor());
        tvListenTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.getCreatetime())));

        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String audioid = data.getId() + "";
                String strs = new Gson().toJson(ids);
//                startActivity(new Intent(OpControlActivity.this, AudioActivity.class)
//                        .putExtra("audioid", audioid).putExtra("ids", strs));

                EventBus.getDefault().post(new ToMainEvent(audioid, strs));
                finish();
            }
        });

        parent.addView(item);
    }

    RelativeLayout loadMore;

    private void addLoadMore(final ViewGroup parent, final int type) {
        loadMore = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.item_click_more, parent, false);
        final ProgressBar progressBar = (ProgressBar) loadMore.findViewById(R.id.progress);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (type == 1) {
                    pageNum1++;
                    getAudioList(tagid + "");
                } else if (type == 2) {
                    getAudioSearch(tagid + "", etSearch.getText().toString());
                }
            }
        });

        mLlReminder.addView(loadMore);
    }
}
