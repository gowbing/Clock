package com.miracle.clock.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.czsirius.clock.R;
import com.miracle.clock.constant.Constants;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.model.event.MineFinishEvent;
import com.miracle.clock.model.event.PlayDownloadEvent;
import com.miracle.clock.model.event.SettingCloseEvent;
import com.miracle.clock.model.normal.DownloadItem;
import com.miracle.clock.service.AudioPlayService;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DownloadActivity extends BaseActivity {

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
    @Bind(R.id.list_view)
    ListView mListView;

    List<DownloadItem> mList;
    Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("已下载音频");

        mList = new ArrayList<>();

        File[] files = new File(Constants.DOWNLOAD_PATH).listFiles();
        for (File file : files) {
            if (file.getName().indexOf(".mp3") >= 0) {
                String[] namelist = file.getName().split("/");
                String lastname = namelist[namelist.length - 1];
                String name = file.getPath();
                DownloadItem item = new DownloadItem(lastname, name);
                mList.add(item);
            }
        }
        mAdapter = new Adapter(mList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                EventBus.getDefault().post(new SettingCloseEvent());
                EventBus.getDefault().post(new MineFinishEvent());
            }
        });
    }

    class Adapter extends BaseAdapter {

        List<DownloadItem> mData;

        public Adapter(List<DownloadItem> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
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
                view = LayoutInflater.from(mContext).inflate(R.layout.item_download, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (mData != null && mData.size() != 0) {
                holder.mTvName.setText(mData.get(i).getLastname());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AudioPlayService.getInstance().setUrl(mData.get(i).getName());
                        Logger.d("getView:"+mData.get(i).getName());
                        AudioPlayService.getInstance().playNotProxy(false);
                        EventBus.getDefault().post(new PlayDownloadEvent(mData.get(i).getLastname()));
                    }
                });
            }
            return view;
        }

        class ViewHolder {
            @Bind(R.id.tv_name)
            TextView mTvName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
