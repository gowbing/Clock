package com.miracle.clock.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.event.ToMainEvent;
import com.miracle.clock.model.normal.Audio;
import com.miracle.clock.model.normal.CollectListResponse;
import com.miracle.clock.model.normal.EmptyResponse;
import com.miracle.clock.model.normal.UserInfoResponse;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CollectsActivity extends BaseActivity {

    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar_back)
    TextView toolbarBack;
    @Bind(R.id.toolbar_right)
    TextView toolbarRight;
    @Bind(R.id.pull_to_refresh)
    SwipeRefreshLayout mPullToRefresh;
    @Bind(R.id.ll_reminder)
    LinearLayout mLlReminder;

    UserInfoResponse.Data info = BaseApplication.getInstance().getUserInfo();

    ArrayList<Map<String, String>> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collects);
        ButterKnife.bind(this);
        initView();
        initListener();

    }


    @Override
    public void initView() {
        toolbarBack.setVisibility(View.VISIBLE);
        toolbarTitle.setText("个人收藏");
        toolbarBack.setText("返回");
        getCollectList();


    }

    @Override
    public void initListener() {
        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(CollectsActivity.this,ClockReminderActivity.class));
            }
        });
        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mPullToRefresh.setColorSchemeResources(R.color.white, R.color.black);
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getCollectList();
            }
        });
    }


    private int pageSize = 10;
    private int pageNum = 1;
    private List<Audio> ids = new ArrayList<>();

    private void getCollectList() {
        Map<String, String> map = new HashMap<>();
        map.put("pageSize", String.valueOf(pageSize));
        map.put("pageNum", String.valueOf(pageNum));
        map.put("userid", String.valueOf(info.getUserid()));
        map.put("userguid", String.valueOf(info.getUserguid()));
        getDataFromServer(HttpUrlConstant.COLLECTION_GETLIST, map, CollectListResponse.class, new Response.Listener<CollectListResponse>() {
            @Override
            public void onResponse(CollectListResponse response) {
                mPullToRefresh.setRefreshing(false);
                if (response.getStatus().equals("ok")) {
                    ids.clear();
                    for (CollectListResponse.Data i : response.getResults()) {
                        ids.add(i.getAudio());
                    }
                    if (pageNum == 1) {
                        mLlReminder.removeAllViews();
                        for (CollectListResponse.Data i : response.getResults()) {
                            addMessageItem(mLlReminder, i.getAudio());
                        }
                        addLoadMore(mLlReminder);
                    } else {
                        mLlReminder.removeView(loadMore);
                        for (CollectListResponse.Data i : response.getResults()) {
                            addMessageItem(mLlReminder, i.getAudio());
                        }
                        addLoadMore(mLlReminder);
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
        TextView tvName = item.findViewById(R.id.tv_name);
        TextView tvWriter = item.findViewById(R.id.tv_writer);
        TextView tvListenTime = item.findViewById(R.id.tv_listen_time);

        tvName.setText(data.getName());
        tvWriter.setText(data.getAuthor());
        tvListenTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.getCreatetime())));
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String audioid = data.getId() + "";
                String strs = new Gson().toJson(ids);
//                startActivity(new Intent(mContext, AudioActivity.class)
//                        .putExtra("audioid", audioid).putExtra("ids", strs));

                EventBus.getDefault().post(new ToMainEvent(audioid, strs));
                finish();
            }
        });
        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                heedImageClick(data);
                return false;
            }
        });
        parent.addView(item);
    }

    RelativeLayout loadMore;

    private void addLoadMore(final ViewGroup parent) {
        loadMore = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.item_click_more, parent, false);
        final ProgressBar progressBar = (ProgressBar) loadMore.findViewById(R.id.progress);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                pageNum++;
                getCollectList();
            }
        });

        mLlReminder.addView(loadMore);
    }

    Dialog mDialogSelectImage;

    public void heedImageClick(final Audio data) {
        if (mDialogSelectImage == null) {
            mDialogSelectImage = new Dialog(this, R.style.ExitAppDialogStyle);
            LinearLayout localObject = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_delete_clock_dialog, null);
            localObject.setMinimumWidth(10000);
            TextView tvDelete = (TextView) localObject.findViewById(R.id.tv_delete);
            tvDelete.setText("取消收藏");
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialogSelectImage.dismiss();

                    Map<String, String> map = new HashMap<>();
                    map.put("userid", String.valueOf(info.getUserid()));
                    map.put("userguid", String.valueOf(info.getUserguid()));
                    map.put("id", data.getId() + "");
                    getDataFromServer(HttpUrlConstant.COLLECTION_DELETE, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
                        @Override
                        public void onResponse(EmptyResponse response) {
                            pageNum = 1;
                            getCollectList();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                }
            });

            WindowManager.LayoutParams localLayoutParams = mDialogSelectImage.getWindow().getAttributes();
            localLayoutParams.x = 0;
            localLayoutParams.y = -1000;
            localLayoutParams.gravity = 80;
            mDialogSelectImage.onWindowAttributesChanged(localLayoutParams);
            mDialogSelectImage.setCanceledOnTouchOutside(true);
            mDialogSelectImage.setContentView(localObject);
        }
        mDialogSelectImage.show();
    }
}
