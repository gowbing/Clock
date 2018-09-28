package com.miracle.clock.ui.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.greendao.gen.ClockDao;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.normal.AudioListResponse;
import com.miracle.clock.model.normal.Clock;
import com.miracle.clock.model.normal.DateRemindParams;
import com.miracle.clock.model.normal.TagResponse;
import com.miracle.clock.service.CustomAlarmReceiver;
import com.miracle.clock.utils.normal.StringUtils;
import com.miracle.clock.utils.normal.VibratorUtil;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RingActivity extends BaseActivity {

    @Bind(R.id.iv_bg)
    ImageView mIvBg;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    @Bind(R.id.tv_close)
    TextView mTvClose;
    @Bind(R.id.tv_location)
    TextView mTvLocation;
    @Bind(R.id.tv_not_ring)
    TextView mTvNotRing;

    DateRemindParams mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        String params = intent.getStringExtra("params");
        mParams = new Gson().fromJson(params, DateRemindParams.class);

        Log.e("RingActivity", "----------------------------------------------------------------------");
        Log.e("params", intent.getStringExtra("params"));
        Log.e("RingActivity", "----------------------------------------------------------------------");

        String content = mParams.getContent();
        String location = mParams.getLocation();
        long time = mParams.getTime();
        long first = mParams.getFirst();
        long second = mParams.getSecond();
        int type = mParams.getType();
        int id = mParams.getId();


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(mContext, CustomAlarmReceiver.class);
        if (type == 1) {
            ClockDao clockDao = BaseApplication.getInstance().getDaoSession().getClockDao();
            List<Clock> list = clockDao.queryBuilder().where(ClockDao.Properties.Time.eq(time)).list();
            for (Clock i : list) {
                if (i.getRepeattime() == 0) {
                    i.setIsstart(false);
                    clockDao.update(i);
                    break;
                }
                if (i.getDaytime() < new Date().getTime()) {
                    i.setDaytime(i.getDaytime() + i.getRepeattime());
                }

                DateRemindParams p = new DateRemindParams();
                p.setContent(i.getContent());
                p.setType(1);
                p.setTime(i.getTime());

                alarmIntent.putExtra("params", new Gson().toJson(params));
                PendingIntent pi = PendingIntent.getBroadcast(mContext, (int) i.getId(), alarmIntent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, i.getDaytime(), pi);

                clockDao.update(i);
            }
        } else if (type == 2) {
            DateRemindParams p = new DateRemindParams();
            p.setContent(content);
            p.setLocation(location);
            p.setType(2);
            p.setId(id);

            long now = new Date().getTime();
            if (first > now) {
                p.setFirst(second);
                alarmIntent.putExtra("params", new Gson().toJson(params));
                PendingIntent pi = PendingIntent.getBroadcast(mContext, id, alarmIntent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, first, pi);
            } else if (second > now) {
                alarmIntent.putExtra("params", new Gson().toJson(params));
                PendingIntent pi = PendingIntent.getBroadcast(mContext, id, alarmIntent, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, second, pi);
            }
            mTvNotRing.setVisibility(View.VISIBLE);
        }
        if (!StringUtils.isEmpty(content)) {
            mTvContent.setText(content);
        }
        if (!StringUtils.isEmpty(location)) {
            mTvLocation.setText("位置：" + location);
            mTvLocation.setVisibility(View.VISIBLE);
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
    }

    @Override
    public void initListener() {
        mTvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlarm();
                finish();
            }
        });
        mTvNotRing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(mContext, CustomAlarmReceiver.class);
                alarmIntent.putExtra("params", new Gson().toJson(mParams));
                PendingIntent pi = PendingIntent.getBroadcast(mContext, mParams.getId(), alarmIntent, 0);
                alarmManager.cancel(pi);
                stopAlarm();
                finish();
            }
        });
    }

    MediaPlayer mMediaPlayer;

    //获取系统默认铃声的Uri
    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this,
                RingtoneManager.TYPE_RINGTONE);
    }

    private void startAlarm(String url) {
        mMediaPlayer = MediaPlayer.create(this, Uri.parse(url));
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        long[] ring = new long[]{500, 1500, 500, 1500};
        VibratorUtil.Vibrate(BaseApplication.getInstance(), ring, true);
    }

    private void stopAlarm() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            VibratorUtil.cancel();
        }
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
                    startAlarm(response.getResults().get(0).getAudioUrl());
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
}
