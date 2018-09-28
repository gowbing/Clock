package com.miracle.clock.ui.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.czsirius.clock.R;
import com.miracle.clock.constant.Constants;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.constant.PreferenceConstants;
import com.miracle.clock.greendao.gen.ClockDao;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.event.AudioCompleteEvent;
import com.miracle.clock.model.event.MusicNameEvent;
import com.miracle.clock.model.event.PlayDownloadEvent;
import com.miracle.clock.model.event.PlayerAllProcessEvent;
import com.miracle.clock.model.event.PlayerCompleteEvent;
import com.miracle.clock.model.event.PlayerNowProcessEvent;
import com.miracle.clock.model.event.PlayerPauseEvent;
import com.miracle.clock.model.event.PlayerPlayEvent;
import com.miracle.clock.model.event.ToMainEvent;
import com.miracle.clock.model.normal.Audio;
import com.miracle.clock.model.normal.AudioListResponse;
import com.miracle.clock.model.normal.AudioResponse;
import com.miracle.clock.model.normal.Clock;
import com.miracle.clock.model.normal.DateRemindParams;
import com.miracle.clock.model.normal.EmptyResponse;
import com.miracle.clock.model.normal.Remind;
import com.miracle.clock.model.normal.RemindListResponse;
import com.miracle.clock.model.normal.TagResponse;
import com.miracle.clock.model.normal.UserInfoResponse;
import com.miracle.clock.service.AudioPlayService;
import com.miracle.clock.service.CustomAlarmReceiver;
import com.miracle.clock.service.GrayService;
import com.miracle.clock.utils.normal.PreferenceUtils;
import com.miracle.clock.utils.normal.StringUtils;
import com.miracle.clock.utils.normal.ValidationUtil;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.toolbar_right)
    TextView mToolbarRight;
    @Bind(R.id.toolbar_right_img)
    ImageView mToolbarRightImg;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_back)
    TextView mToolbarBack;
    @Bind(R.id.ll_pre)
    LinearLayout mLlPre;
    @Bind(R.id.iv_play)
    ImageView mIvPlay;
    @Bind(R.id.ll_next)
    LinearLayout mLlNext;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_now)
    TextView mTvNow;
    @Bind(R.id.seek_bar)
    SeekBar mSeekBar;
    @Bind(R.id.tv_all)
    TextView mTvAll;
    @Bind(R.id.ll_1)
    LinearLayout mLl1;
    @Bind(R.id.ll_2)
    LinearLayout mLl2;
    @Bind(R.id.ll_3)
    LinearLayout mLl3;
    @Bind(R.id.ll_4)
    LinearLayout mLl4;
    @Bind(R.id.iv_isselect)
    ImageView ivIsselect;
    @Bind(R.id.cb_collect)
    LinearLayout cbCollect;

    UserInfoResponse.Data info;

    String audioidnow;

    //    String url = "";
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        File file = new File(Constants.DOWNLOAD_PATH);
        File[] files = file.listFiles();
        if (files != null) {
            for (File i : files) {
                if (i.exists()) {
                    i.delete();
                }
            }
        }

        startService(new Intent(getApplicationContext(), GrayService.class));
        startService(new Intent(mContext, AudioPlayService.class));

        initView();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(mContext, AudioPlayService.class));

        File file = new File(Constants.DOWNLOAD_PATH);
        File[] files = file.listFiles();
        if (files != null) {
            for (File i : files) {
                if (i.exists()) {
                    i.delete();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        audioidnow = PreferenceUtils.getPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_URL, "");
    }

    @Override
    public void initView() {
        mToolbarTitle.setText(R.string.app_name);
        mTvNow.setText("00:00");
        mTvAll.setText("00:00");
        mTvTitle.setText("");

        getUserInfo();
    }

    private void getUserInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("userid", "" + BaseApplication.getInstance().getUserInfo().getUserid());
        map.put("userguid", "" + BaseApplication.getInstance().getUserInfo().getUserguid());
        getDataFromServer(HttpUrlConstant.USER_INFO, map, UserInfoResponse.class, new Response.Listener<UserInfoResponse>() {
            @Override
            public void onResponse(UserInfoResponse response) {
                if (response.getStatus().equals("ok")) {
                    BaseApplication.getInstance().setUserInfo(response.getResults());
                    if (StringUtils.isEmpty(BaseApplication.getInstance().getUserInfo().getPhone())) {
                        showUpdateDialog();
                    }

                    loadDao();
                    getReminds();

                    String list = PreferenceUtils.getPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_LIST, "");
                    String audio = PreferenceUtils.getPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_URL, "");

                    Logger.d("list:" + list + "\n" + "audio:" + audio);

                    JSONArray listArray = null;
                    try {
                        if (!StringUtils.isEmpty(list) && !StringUtils.isEmpty(audio)) {
                            listArray = new JSONArray(list);
                            for (int i = 0; i < listArray.length(); i++) {
                                Audio a = new Gson().fromJson(listArray.optString(i), Audio.class);
                                if (a.getId() == Integer.valueOf(audio)) {
                                    audioidnow = a.getId() + "";
                                    getAudioInfo(audioidnow, 1);
                                    return;
                                }
                            }
                        } else if (!StringUtils.isEmpty(list) && StringUtils.isEmpty(audio)) {
                            Audio a = new Gson().fromJson(listArray.optString(0), Audio.class);
                            audioidnow = a.getId() + "";
                            getAudioInfo(audioidnow, 1);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    JSONArray array = null;
                    try {
                        array = new JSONArray(BaseApplication.getInstance().getUserInfo().getFavorite());
                        Logger.d(BaseApplication.getInstance().getUserInfo().getFavorite());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (array != null && array.length() != 0) {
                        for (int i = 0; i < array.length(); i++) {
                            TagResponse.Data data = new Gson().fromJson(array.opt(i).toString(), TagResponse.Data.class);
                            if (data.getId() != 0) {
                                getAudioList(data.getId() + "");
                                break;
                            }
                        }
                    } else {
                        getAudioList("1");
                    }
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

    public void getAudioList(String tagid) {
        Map<String, String> map = new HashMap<>();
        map.put("tagid", tagid);
        map.put("pageNum", "1");
        map.put("pageSize", "10");
        getDataFromServer(HttpUrlConstant.AUDIO_GRTLIST, map, AudioListResponse.class, new Response.Listener<AudioListResponse>() {
            @Override
            public void onResponse(AudioListResponse response) {
                if (response.getStatus().equals("ok")) {
                    PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_LIST, new Gson().toJson(response.getResults()));
                    if (response.getResults() != null && response.getResults().size() != 0) {
                        audioidnow = "" + response.getResults().get(0).getId();
                        getAudioInfo(audioidnow, 1);
                    }
                } else {
                    showToast(response.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public void initListener() {
        mLl1.setOnClickListener(this);
        mLl2.setOnClickListener(this);
        mLl3.setOnClickListener(this);
        mLl4.setOnClickListener(this);
        mLlPre.setOnClickListener(this);
        mIvPlay.setOnClickListener(this);
        mLlNext.setOnClickListener(this);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() < seekBar.getSecondaryProgress()) {
                    double progress = seekBar.getProgress() / 100.0;
                    int sec = (int) (AudioPlayService.getInstance().allProgress() * progress);
                    AudioPlayService.getInstance().seekTo(sec);
                }
            }
        });

        cbCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(audioidnow)) {
                    showToast("暂无音频，无法收藏");
                    return;
                }
                if (islike == false) {
                    addCollect(Integer.parseInt(audioidnow));
                } else {
                    deleteCollect(Integer.parseInt(audioidnow));
                }
            }
        });
    }

    boolean islike;

    private void loadDao() {
        ClockDao clockDao = BaseApplication.getInstance().getDaoSession().getClockDao();
        List<Clock> clocks = new ArrayList<>();
        try {
            clocks.addAll(clockDao.loadAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<List<Clock>> list = new ArrayList<>();
        if (clocks.size() != 0) {
            List<Clock> item = new ArrayList<>();
            long pre = clocks.get(0).getTime();
            for (Clock i : clocks) {
                if (i.getTime() == pre) {
                    item.add(i);
                } else {
                    list.add(item);
                    item = new ArrayList<>();
                    pre = i.getTime();
                    item.add(i);
                }
            }
            if (item.size() != 0) {
                list.add(item);
            }
        }
        Logger.d(new Gson().toJson(list));
        final long oneDay = 1000 * 60 * 60 * 24;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(mContext, CustomAlarmReceiver.class);
        for (List<Clock> i : list) {
            if (i.get(0).getIsstart()) {
                for (Clock j : i) {
                    while (j.getDaytime() < new Date().getTime()) {
                        j.setDaytime(j.getDaytime() + oneDay * 7);
                    }
                    clockDao.update(j);

                    DateRemindParams params = new DateRemindParams();
                    params.setContent(j.getContent());
                    params.setType(1);
                    params.setTime(j.getTime());

                    alarmIntent.putExtra("params", new Gson().toJson(params));
                    PendingIntent pi = PendingIntent.getBroadcast(mContext, (int) j.getId(), alarmIntent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, j.getDaytime(), pi);
                    j.setIsstart(true);
                    clockDao.update(j);
                    Logger.d(new Gson().toJson(j));
                }
            } else {
                for (Clock j : i) {
                    DateRemindParams params = new DateRemindParams();
                    params.setContent(j.getContent());
                    params.setType(1);
                    params.setTime(j.getTime());

                    alarmIntent.putExtra("params", new Gson().toJson(params));
                    PendingIntent pi = PendingIntent.getBroadcast(mContext, (int) j.getId(), alarmIntent, 0);
                    alarmManager.cancel(pi);
                    j.setIsstart(false);
                    clockDao.update(j);
                    Logger.d(new Gson().toJson(j));
                }
            }
        }
    }

    public void getReminds() {
        info = BaseApplication.getInstance().getUserInfo();
        Map<String, String> map = new HashMap<>();
        map.put("userid", String.valueOf(info.getUserid()));
        map.put("userguid", String.valueOf(info.getUserguid()));
        map.put("pageSize", "20");
        map.put("pageNum", "1");
        getDataFromServer(HttpUrlConstant.MEMORANDUM_GRTLIST, map, RemindListResponse.class, new Response.Listener<RemindListResponse>() {
            @Override
            public void onResponse(RemindListResponse response) {
                if (response.getStatus().equals("ok")) {
                    for (Remind i : response.getResults()) {
                        DateRemindParams params = new DateRemindParams();
                        params.setContent(i.getContent());
                        params.setLocation(i.getLocation());
                        params.setType(2);
                        params.setId(i.getId() + 1000000);

                        long nowDate = new Date().getTime();
                        long first = -1;
                        long second = -1;
                        long third = -1;
                        if (i.getNotifytime() != -1) {
                            first = nowDate - i.getNotifytime() * 1000;
                        }
                        if (i.getSecondnotifytime() != -1) {
                            second = nowDate - i.getSecondnotifytime() * 1000;
                        }
                        if (i.getThirdnotifytime() != -1) {
                            third = nowDate - i.getThirdnotifytime() * 1000;
                        }

                        long now = new Date().getTime();

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent alarmIntent = new Intent(mContext, CustomAlarmReceiver.class);

                        if (first > now) {
                            params.setFirst(second);
                            params.setSecond(third);

                            alarmIntent.putExtra("params", new Gson().toJson(params));
                            PendingIntent pi = PendingIntent.getBroadcast(mContext, i.getId() + 1000000, alarmIntent, 0);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, first, pi);
                        } else if (second > now) {
                            params.setFirst(third);

                            alarmIntent.putExtra("params", new Gson().toJson(params));
                            PendingIntent pi = PendingIntent.getBroadcast(mContext, i.getId() + 1000000, alarmIntent, 0);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, second, pi);
                        } else if (third > now) {
                            alarmIntent.putExtra("params", new Gson().toJson(params));
                            PendingIntent pi = PendingIntent.getBroadcast(mContext, i.getId() + 1000000, alarmIntent, 0);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, third, pi);
                        }
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    Dialog mUpdateDialog;

    private void showUpdateDialog() {
        if (mUpdateDialog == null) {
            mUpdateDialog = new Dialog(mContext, R.style.LoadingDialog);
            mUpdateDialog.setContentView(R.layout.dialog_update_phone);
            mUpdateDialog.setCanceledOnTouchOutside(false);
            mUpdateDialog.setCancelable(true);
            final EditText et_phone = mUpdateDialog.findViewById(R.id.et_phone);
            final EditText et_code = mUpdateDialog.findViewById(R.id.et_code);
            final TextView tv_send = mUpdateDialog.findViewById(R.id.tv_send);
            TextView tv_commit = mUpdateDialog.findViewById(R.id.tv_commit);

            tv_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = et_phone.getText().toString();
                    if (ValidationUtil.isMobileNumber(phone)) {
                        sendCode(phone, tv_send);
                    }
                }
            });
            tv_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = et_phone.getText().toString();
                    String code = et_code.getText().toString();
                    if (ValidationUtil.isMobileNumber(phone)) {
                        updatePhone(phone, code);
                    }
                }
            });
        }
        mUpdateDialog.show();
    }

    public void updatePhone(String phone, String code) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", BaseApplication.getInstance().getUserInfo().getUserid() + "");
        map.put("phone", phone);
        map.put("verify", code);
        getDataFromServer(HttpUrlConstant.USER_UPDATEPHONE, map, UserInfoResponse.class, new Response.Listener<UserInfoResponse>() {
            @Override
            public void onResponse(UserInfoResponse response) {
                if (response.getStatus().equals("ok")) {
                    BaseApplication.getInstance().setUserInfo(response.getResults());
                    mUpdateDialog.dismiss();
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

    private void sendCode(String phone, final TextView tv_send) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        getDataFromServer(HttpUrlConstant.USER_SENDCODE, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
                if (response.getStatus().equals("ok")) {
                    new CountDownTimer(60000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            tv_send.setBackgroundResource(R.drawable.bg_btn_grey);
                            tv_send.setEnabled(false);
                            tv_send.setText((millisUntilFinished / 1000) + "秒后重发");
                        }

                        @Override
                        public void onFinish() {
                            tv_send.setBackgroundResource(R.drawable.bg_btn);
                            tv_send.setEnabled(true);
                            tv_send.setText("获取验证码");
                        }
                    }.start();
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

    public void getAudioInfo(String audioid, final int type) {
        info = BaseApplication.getInstance().getUserInfo();
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", audioid);
        map.put("userid", String.valueOf(info.getUserid()));
        getDataFromServer(HttpUrlConstant.AUDIO_GRT, map, AudioResponse.class, new Response.Listener<AudioResponse>() {
            @Override
            public void onResponse(AudioResponse response) {
                hideProgress();
                mTvTitle.setText(response.getResults().getName());
                mToolbarBack.setVisibility(View.INVISIBLE);
                audioidnow = response.getResults().getId() + "";
                name = response.getResults().getName();
                if (response.getResults().getIscollect() == 1) {
                    islike = true;
                    ivIsselect.setBackgroundResource(R.drawable.ic_collected);
                } else {
                    islike = false;
                    ivIsselect.setBackgroundResource(R.drawable.ic_collect_not);
                }

                if (type == 1) {
                    String str = PreferenceUtils.getPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_LIST, "");
                    JSONArray array = null;
                    try {
                        array = new JSONArray(str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String audioUrl = "";
                    for (int i = 0; i < array.length(); i++) {
                        Audio audio = new Gson().fromJson(array.opt(i).toString(), Audio.class);
                        if (audioidnow.equals(audio.getId() + "")) {
                            audioUrl = audio.getAudioUrl();
                        }
                    }

                    if (AudioPlayService.getInstance().getPlayer().getUrl().equals(audioUrl)) {
                        if (AudioPlayService.getInstance().isPlaying()) {
                            AudioPlayService.getInstance().pause();
                        } else {
                            AudioPlayService.getInstance().start();
                        }
                    } else {
                        if (isWifiConnected(mContext)) {
                            AudioPlayService.getInstance().setPlayUrl(audioUrl);
                            AudioPlayService.getInstance().play(false);
                            EventBus.getDefault().post(new PlayDownloadEvent(name));
                            PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_URL, audioidnow);
                        } else {
                            showWifiTip(audioUrl);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
            }
        });
    }

    public void addCollect(int audioid) {
        info = BaseApplication.getInstance().getUserInfo();
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", String.valueOf(info.getUserid()));
        map.put("userguid", String.valueOf(info.getUserguid()));
        map.put("audioid", String.valueOf(audioid));
        getDataFromServer(HttpUrlConstant.COLLECTION_ADD, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
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
                showToast("网络错误，请稍后再试");
            }
        });
    }

    public void deleteCollect(int id) {
        info = BaseApplication.getInstance().getUserInfo();
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", String.valueOf(info.getUserid()));
        map.put("userguid", String.valueOf(info.getUserguid()));
        map.put("id", String.valueOf(id));
        getDataFromServer(HttpUrlConstant.COLLECTION_DELETE, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
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
                showToast("网络错误，请稍后再试");
            }
        });
    }

    @Override
    public void onBackPressed() {
        showExitTip();
    }

    Dialog mExitDialog;

    private void showExitTip() {
        if (mExitDialog == null) {
            mExitDialog = new Dialog(this, R.style.LoadingDialog);
            mExitDialog.setContentView(R.layout.dialog_exit_tip);
            mExitDialog.setCanceledOnTouchOutside(false);
            mExitDialog.setCancelable(false);
            TextView tv_continue = (TextView) mExitDialog.findViewById(R.id.tv_continue);
            TextView tv_pause = (TextView) mExitDialog.findViewById(R.id.tv_pause);
            tv_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mExitDialog.dismiss();
                }
            });
            tv_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mExitDialog.dismiss();
                    BaseApplication.getInstance().finishAllActivity();
                }
            });
        }
        mExitDialog.show();
    }

    Dialog mDialog;

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

    private String next_id;
    private String last_id;

    public void getLastAndNext(int type) {
        next_id = audioidnow;
        last_id = audioidnow;

        String ids = PreferenceUtils.getPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_LIST, "");
        Logger.d(ids);
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
            if (last_id.equals(audioidnow)) {
                hideProgress();
                showToast("上一条为空");
            } else {
                audioidnow = last_id;
                getAudioInfo(audioidnow, 1);
            }
        } else if (type == 2) {
            if (next_id.equals(audioidnow)) {
                hideProgress();
                showToast("下一条为空");
            } else {
                audioidnow = next_id;
                getAudioInfo(audioidnow, 1);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_1:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.ll_2:
                startActivity(new Intent(this, MineActivity.class));
                break;
            case R.id.ll_3:
                startActivity(new Intent(this, ClockReminderActivity.class));
                break;
            case R.id.ll_4:
                String shareUrl = "http://www.jsds-glx.com/clockAPP/share/pages/share.html?id=" + audioidnow;
                showShare(shareUrl);
                break;
            case R.id.ll_pre:
                showDialog("处理中...");
                getLastAndNext(0);
                break;
            case R.id.ll_next:
                showDialog("处理中...");
                getLastAndNext(2);
                break;
            case R.id.iv_play:
                if (StringUtils.isEmpty(audioidnow)) {
                    showToast("您当前未选择音频");
                    return;
                }
                String ids = PreferenceUtils.getPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_LIST, "");
                String url = "";
                String name = "";
                try {
                    JSONArray jsondata = new JSONArray(ids);
                    for (int i = 0; i < jsondata.length(); i++) {
                        Audio audio = new Gson().fromJson(jsondata.get(i).toString(), Audio.class);
                        String id = audio.getId() + "";
                        if (id.equals(audioidnow)) {
                            url = audio.getAudioUrl();
                            name = audio.getName();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (StringUtils.isEmpty(AudioPlayService.getInstance().getPlayer().getUrl())) {
                    if (isWifiConnected(mContext)) {
                        mTvTitle.setText(name);
                        AudioPlayService.getInstance().setPlayUrl(url);
                        AudioPlayService.getInstance().play(false);
                    } else {
                        showWifiTip(url);
                    }
                } else {
                    if (AudioPlayService.getInstance().isPlaying()) {
                        AudioPlayService.getInstance().pause();
                    } else {
                        AudioPlayService.getInstance().start();
                    }
                }
                break;

        }
    }

    private void showShare(String txt) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
        oks.setTitle("金水大叔");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
        oks.setTitleUrl(txt);
        // text是分享文本，所有平台都需要这个字段
        oks.setText("金水大叔管理学陪伴你终身学习，学无止境。");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("http://www.jsds-glx.com/clockAPP/share/pages/ic_launcher.png");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(txt);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("金水大叔管理学陪伴你终身学习，学无止境。");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("金水大叔");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(txt);
        // 启动分享GUI
        oks.show(this);
    }

    /*
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MusicNameEvent event) {
        audioidnow = "";
        mSeekBar.setProgress(0);
        PreferenceUtils.setPrefInt(BaseApplication.getInstance(), PreferenceConstants.KEY_PROGRESS_NOW, 0);
        mSeekBar.setSecondaryProgress(0);
        mTvNow.setText("00:00");
        mTvAll.setText("00:00");
        mTvTitle.setText("");
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ToMainEvent event) {
        audioidnow = event.getAudioId();
        getAudioInfo(audioidnow, 1);
        PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_LIST, event.getIds());
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerCompleteEvent event) {
//        Logger.d("PlayerCompleteEvent");
        mIvPlay.setImageResource(R.drawable.ic_play);
        PreferenceUtils.setPrefInt(mContext, PreferenceConstants.KEY_PROGRESS_NOW, 0);
        getLastAndNext(2);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerPlayEvent event) {
//        Logger.d("PlayerPlayEvent");
        mIvPlay.setImageResource(R.drawable.ic_pause);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerPauseEvent event) {
//        Logger.d("PlayerPauseEvent");
        mIvPlay.setImageResource(R.drawable.ic_play);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerNowProcessEvent event) {
//        Logger.d("PlayerNowProcessEvent");
        mTvNow.setText(event.getNow());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerAllProcessEvent event) {
//        Logger.d("PlayerAllProcessEvent");
        mTvNow.setText("00:00");
        mSeekBar.setProgress(0);
        mTvAll.setText(event.getAll());
        mIvPlay.setImageResource(R.drawable.ic_play);
        int now = PreferenceUtils.getPrefInt(mContext, PreferenceConstants.KEY_PROGRESS_NOW, 0);
        if (now != 0) {
            AudioPlayService.getInstance().seekTo(now);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AudioCompleteEvent event) {
        AudioPlayService.getInstance().setSeekBar(mSeekBar);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayDownloadEvent event) {
        mTvTitle.setText(event.getName());
    }
}
