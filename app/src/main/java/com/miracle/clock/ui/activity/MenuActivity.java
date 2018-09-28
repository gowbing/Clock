package com.miracle.clock.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.model.event.ToMainEvent;
import com.miracle.clock.model.normal.BannerResponse;
import com.miracle.clock.model.normal.TagResponse;
import com.miracle.clock.utils.normal.StringUtils;
import com.miracle.clock.utils.normal.Utility;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MenuActivity extends BaseActivity {

    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.et_search)
    EditText mEtSearch;
    @Bind(R.id.list_view)
    ListView mListView;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.ll_round_item)
    LinearLayout mLlRoundItem;

    List<TagResponse.Data> mList;
    Adapter mAdapter;
    List<ImageView> mIvList;
    BannerAdapter mBannerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        mList = new ArrayList<>();
        mAdapter = new Adapter(mList);
        mListView.setAdapter(mAdapter);
        Utility.setListViewHeightBasedOnChildren(mListView);

        mIvList = new ArrayList<>();
        mBannerAdapter = new BannerAdapter(mIvList);
        mViewPager.setAdapter(mBannerAdapter);

        getBannerList();
        getTabList();
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ToMainEvent event) {
        finish();
    }

    @Override
    public void initListener() {
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mEtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(MenuActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索操作的方法，在该方法中可以加入mEditSearchUser的非空判断
                    String str = mEtSearch.getText().toString();
                    if (!StringUtils.isEmpty(str)) {
                        startActivity(new Intent(MenuActivity.this, OpControlActivity.class)
                                .putExtra("search", str));
                    } else {
                        showToast("请输入搜索内容");
                    }
                }
                return false;
            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int max = mLlRoundItem.getChildCount();
                int index = position;
                mViewPager.setCurrentItem(index);
                mLlRoundItem.removeAllViews();
                for (int i = 0; i < max; i++) {
                    LinearLayout linearLayout;
                    if (i == index) {
                        linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_round, null);
                        ImageView imageView = linearLayout.findViewById(R.id.iv_item);
                        imageView.setImageResource(R.drawable.bg_circle_full);
                    } else {
                        linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_round, null);
                        ImageView imageView = linearLayout.findViewById(R.id.iv_item);
                        imageView.setImageResource(R.drawable.bg_circle_empty);
                    }
                    mLlRoundItem.addView(linearLayout);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void getTabList() {
        Map<String, String> map = new HashMap<String, String>();
        getDataFromServer(HttpUrlConstant.TAB_GETLIST, map, TagResponse.class, new Response.Listener<TagResponse>() {
            @Override
            public void onResponse(TagResponse response) {
                if (response.getStatus().equals("ok")) {
                    mList.clear();
                    mList.addAll(response.getResults());
                    mAdapter.notifyDataSetChanged();
                    Utility.setListViewHeightBasedOnChildren(mListView);
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

    public void getBannerList() {
        Map<String, String> map = new HashMap<>();
        getDataFromServer(HttpUrlConstant.BANNER_GETLIST, map, BannerResponse.class, new Response.Listener<BannerResponse>() {
            @Override
            public void onResponse(BannerResponse response) {
                if (response.getStatus().equals("ok")) {
                    mIvList.clear();
                    for (final BannerResponse.Data i : response.getResults()) {
                        Logger.d("------------------------------------------------------------");
                        SimpleDraweeView imageView = new SimpleDraweeView(mContext);
                        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
                        imageView.setImageURI(Uri.parse(i.getImgUrl()));
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(i.getLinkUrl());
                                intent.setData(content_url);
                                startActivity(intent);
                            }
                        });
                        mIvList.add(imageView);
                    }
                    mBannerAdapter.notifyDataSetChanged();
                    if (response.getResults() != null && response.getResults().size() != 0) {
                        for (int i = 0; i < response.getResults().size(); i++) {
                            LinearLayout linearLayout;
                            if (i == 0) {
                                linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_round, null);
                                ImageView imageView = linearLayout.findViewById(R.id.iv_item);
                                imageView.setImageResource(R.drawable.bg_circle_full);
                            } else {
                                linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_round, null);
                                ImageView imageView = linearLayout.findViewById(R.id.iv_item);
                                imageView.setImageResource(R.drawable.bg_circle_empty);
                            }
                            mLlRoundItem.addView(linearLayout);
                        }
                    }
                    Message message = new Message();
                    message.what = 0;
                    message.obj = 0;
                    mHandler.sendMessageDelayed(message, 5000);
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

    class Adapter extends BaseAdapter {

        List<TagResponse.Data> data;

        public Adapter(List<TagResponse.Data> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_menu_list, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (data != null && data.size() != 0) {
                holder.tvName.setText(data.get(i).getContent());
//                holder.tvTagid.setText(data.get(i).getTagid());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        showToast(i + "");
                        startActivity(new Intent(MenuActivity.this, OpControlActivity.class).
                                putExtra("tagid", data.get(i).getId()).
                                putExtra("tagname", data.get(i).getContent()));
                    }
                });
            }
            return view;
        }

        class ViewHolder {
            @Bind(R.id.tv_name)
            TextView tvName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    class BannerAdapter extends PagerAdapter {

        List<ImageView> mList;

        public BannerAdapter(List<ImageView> list) {
            mList = list;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            //对ViewPager页号求模取出View列表中要显示的项
//            position %= mList.size();
//            if (position < 0) {
//                position = mList.size() + position;
//            }
            ImageView view = mList.get(position);
            //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            ViewParent vp = view.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(view);
            }
            container.addView(view);
            //add listeners here if necessary
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                int max = mLlRoundItem.getChildCount();
                int index = (int) msg.obj;
                index++;
                if (index == max) {
                    index = 0;
                }
                mViewPager.setCurrentItem(index);
                mLlRoundItem.removeAllViews();
                for (int i = 0; i < max; i++) {
                    LinearLayout linearLayout;
                    if (i == index) {
                        linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_round, null);
                        ImageView imageView = linearLayout.findViewById(R.id.iv_item);
                        imageView.setImageResource(R.drawable.bg_circle_full);
                    } else {
                        linearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_round, null);
                        ImageView imageView = linearLayout.findViewById(R.id.iv_item);
                        imageView.setImageResource(R.drawable.bg_circle_empty);
                    }
                    mLlRoundItem.addView(linearLayout);
                }
                Message message = new Message();
                message.what = 0;
                message.obj = index;
                mHandler.sendMessageDelayed(message, 5000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
    }
}
