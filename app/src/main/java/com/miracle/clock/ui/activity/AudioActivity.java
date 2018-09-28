package com.miracle.clock.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.constant.PreferenceConstants;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.event.PlayDownloadEvent;
import com.miracle.clock.model.event.PlayerAllProcessEvent;
import com.miracle.clock.model.event.PlayerCompleteEvent;
import com.miracle.clock.model.event.PlayerPauseEvent;
import com.miracle.clock.model.event.PlayerPlayEvent;
import com.miracle.clock.model.normal.Audio;
import com.miracle.clock.model.normal.AudioResponse;
import com.miracle.clock.model.normal.EmptyResponse;
import com.miracle.clock.model.normal.UserInfoResponse;
import com.miracle.clock.service.AudioPlayService;
import com.miracle.clock.utils.normal.PreferenceUtils;
import com.miracle.clock.utils.normal.StringUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AudioActivity extends BaseActivity {

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
    @Bind(R.id.ll_pre)
    LinearLayout mLlPre;
    @Bind(R.id.iv_play)
    ImageView mIvPlay;
    @Bind(R.id.ll_next)
    LinearLayout mLlNext;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    @Bind(R.id.cb_collect)
    LinearLayout cbCollect;
    @Bind(R.id.tv_auther)
    TextView mTvAuther;
    @Bind(R.id.ll_auther)
    LinearLayout mLlAuther;
    @Bind(R.id.iv_isselect)
    ImageView ivIsselect;

    UserInfoResponse.Data info = BaseApplication.getInstance().getUserInfo();

    String url = "";
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        mToolbarBack.setVisibility(View.VISIBLE);
        audioidnow = getIntent().getStringExtra("audioid");
        getAudioInfo(audioidnow, 1);
    }


    private String next_id;
    private String last_id;

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cbCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (info.getUserid() == 0) {
                    showToast("未登录，无法收藏。");
                } else {
                    if (islike == false) {
                        showDialog("处理中...");
                        addCollect(Integer.parseInt(audioidnow));
                    } else {
                        showDialog("处理中...");
                        deleteCollect(Integer.parseInt(audioidnow));
                    }
                }
            }
        });

        mLlPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("处理中...");
                getLastAndNext(0);
            }
        });
        mLlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog("处理中...");
                getLastAndNext(2);
            }
        });
        mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AudioPlayService.getInstance().getPlayer().getUrl().equals(url)) {
                    if (AudioPlayService.getInstance().isPlaying()) {
                        AudioPlayService.getInstance().pause();
                    } else {
                        AudioPlayService.getInstance().start();
                    }
                } else {
                    if (isWifiConnected(mContext)) {
                        AudioPlayService.getInstance().setPlayUrl(url);
                        AudioPlayService.getInstance().play(false);
                        EventBus.getDefault().post(new PlayDownloadEvent(name));
                        PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_URL, audioidnow);
                        PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_LIST, getIntent().getStringExtra("ids"));
                    } else {
                        showWifiTip(url);
                    }
                }
            }
        });
    }

    Dialog mDialog;
    String audioidnow = null;

    private void showWifiTip(final String url) {
        if (mDialog == null) {
            mDialog = new Dialog(mContext, R.style.LoadingDialog);
            mDialog.setContentView(R.layout.dialog_wifi_tip);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            TextView tv_continue = mDialog.findViewById(R.id.tv_continue);
            TextView tv_pause = mDialog.findViewById(R.id.tv_pause);
            tv_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    AudioPlayService.getInstance().setPlayUrl(url);
                    AudioPlayService.getInstance().play(false);
                    EventBus.getDefault().post(new PlayDownloadEvent(name));
                    PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_URL, audioidnow);
                    PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_LIST, getIntent().getStringExtra("ids"));
                }
            });
            tv_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
        }
        mDialog.show();
    }

    public boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

//    protected void onDestroy() {
//        super.onDestroy();
//        mPlayer.destroy();
//    }

    public void getLastAndNext(int type) {
        String ids = getIntent().getStringExtra("ids");
        if (StringUtils.isEmpty(ids)) {
            hideProgress();
            showToast("没有找到列表记录");
            return;
        }
        try {
            JSONArray jsondata = new JSONArray(ids);
            for (int i = 0; i < jsondata.length(); i++) {
                Audio audio = new Gson().fromJson(jsondata.get(i).toString(), Audio.class);
                String id = audio.getId() + "";
                if (id.equals(audioidnow)) {
                    if (i == 0) {
                        next_id = new Gson().fromJson(jsondata.get(i + 1).toString(), Audio.class).getId() + "";
                        last_id = new Gson().fromJson(jsondata.get(jsondata.length() - 1).toString(), Audio.class).getId() + "";
                    } else if (i == jsondata.length() - 1) {
                        last_id = new Gson().fromJson(jsondata.get(i - 1).toString(), Audio.class).getId() + "";
                        next_id = new Gson().fromJson(jsondata.get(0).toString(), Audio.class).getId() + "";
                    } else {
                        last_id = new Gson().fromJson(jsondata.get(i - 1).toString(), Audio.class).getId() + "";
                        next_id = new Gson().fromJson(jsondata.get(i + 1).toString(), Audio.class).getId() + "";
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (type == 0) {
            audioidnow = last_id;
            getAudioInfo(audioidnow, 1);
        } else if (type == 2) {
            audioidnow = next_id;
            getAudioInfo(audioidnow, 1);
        }
    }

    public void addCollect(int audioid) {
        showDialog("处理中");
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", String.valueOf(info.getUserid()));
        map.put("userguid", String.valueOf(info.getUserguid()));
        map.put("audioid", String.valueOf(audioid));
        getDataFromServer(HttpUrlConstant.COLLECTION_ADD, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
                hideProgress();
                if (response.getStatus().equals("ok")) {
                    islike = true;
                    ivIsselect.setBackgroundResource(R.drawable.ic_collected);
                } else {
                    showToast(response.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
            }
        });
    }

    boolean islike;

    public void deleteCollect(int id) {
        showDialog("处理中");
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", String.valueOf(info.getUserid()));
        map.put("userguid", String.valueOf(info.getUserguid()));
        map.put("id", String.valueOf(id));
        getDataFromServer(HttpUrlConstant.COLLECTION_DELETE, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
                hideProgress();
                if (response.getStatus().equals("ok")) {
                    islike = false;
                    ivIsselect.setBackgroundResource(R.drawable.ic_collect_not);
                } else {
                    showToast(response.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
            }
        });
    }

    public void getAudioInfo(String audioid, final int type) {
        showDialog("处理中");
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", audioid);
        map.put("userid", String.valueOf(info.getUserid()));
        getDataFromServer(HttpUrlConstant.AUDIO_GRT, map, AudioResponse.class, new Response.Listener<AudioResponse>() {
            @Override
            public void onResponse(AudioResponse response) {
                hideProgress();
                if (response.getStatus().equals("ok")) {

                    name = response.getResults().getName();
                    mTvTitle.setText(response.getResults().getName());
                    mTvContent.setText(response.getResults().getName());
                    mTvAuther.setText(response.getResults().getAuthor());
                    mToolbarTitle.setText(response.getResults().getName());
                    if (response.getResults().getIscollect() == 1) {
                        islike = true;
                        ivIsselect.setBackgroundResource(R.drawable.ic_collected);
                    } else {
                        islike = false;
                        ivIsselect.setBackgroundResource(R.drawable.ic_collect_not);
                    }
                    url = response.getResults().getAudioUrl();

                    if (type == 1) {
                        if (AudioPlayService.getInstance().getPlayer().getUrl().equals(url)) {
                            if (AudioPlayService.getInstance().isPlaying()) {
                                AudioPlayService.getInstance().pause();
                            } else {
                                AudioPlayService.getInstance().start();
                            }
                        } else {
                            if (isWifiConnected(mContext)) {
                                AudioPlayService.getInstance().setPlayUrl(url);
                                AudioPlayService.getInstance().play(false);
                                EventBus.getDefault().post(new PlayDownloadEvent(name));
                                PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_URL, audioidnow);
                                PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_LIST, getIntent().getStringExtra("ids"));
                            } else {
                                showWifiTip(url);
                            }
                        }
                    }
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

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerCompleteEvent event) {
        Logger.d("PlayerCompleteEvent");
        mIvPlay.setImageResource(R.drawable.ic_play);
        getLastAndNext(2);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerPlayEvent event) {
        Logger.d("PlayerPlayEvent");
        mIvPlay.setImageResource(R.drawable.ic_pause);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerPauseEvent event) {
        Logger.d("PlayerPauseEvent");
        mIvPlay.setImageResource(R.drawable.ic_play);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerAllProcessEvent event) {
        Logger.d("PlayerAllProcessEvent");
        hideProgress();
        mIvPlay.setImageResource(R.drawable.ic_pause);
    }
}
