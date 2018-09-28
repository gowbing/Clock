package com.miracle.clock.ui.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.greendao.gen.ClockDao;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.normal.Clock;
import com.miracle.clock.model.normal.DateRemindParams;
import com.miracle.clock.model.normal.EmptyResponse;
import com.miracle.clock.model.normal.Remind;
import com.miracle.clock.model.normal.RemindListResponse;
import com.miracle.clock.model.normal.UserInfoResponse;
import com.miracle.clock.service.CustomAlarmReceiver;
import com.orhanobut.logger.Logger;
import com.zcw.togglebutton.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ClockReminderActivity extends BaseActivity {
    @Bind(R.id.tv_update)
    TextView tvUpdate;
    @Bind(R.id.rb1)
    RadioButton rb1;
    @Bind(R.id.rb2)
    RadioButton rb2;
    @Bind(R.id.rg_btn_type)
    RadioGroup rgBtnType;
    @Bind(R.id.tv_add)
    TextView tvAdd;
    @Bind(R.id.lv_title_clock)
    ListView lvTitleClock;
    @Bind(R.id.ll_reminder)
    LinearLayout mLlReminder;
    @Bind(R.id.pull_to_refresh)
    SwipeRefreshLayout mPullToRefresh;
    @Bind(R.id.toolbar_back)
    TextView toolbarBack;

    UserInfoResponse.Data info;

    Adapter mClockAdapter;
    List<List<Clock>> mLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_reminder);
        ButterKnife.bind(this);
        initView();
        initListener();

    }

    @Override
    public void initView() {
        toolbarBack.setVisibility(View.VISIBLE);
        mLists = new ArrayList<>();
        mClockAdapter = new Adapter(mLists);
        lvTitleClock.setAdapter(mClockAdapter);
        info = BaseApplication.getInstance().getUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDao();
        if (info.getUserid() != 0) {
            getReminds();
        } else {
            mLists = new ArrayList<>();
        }
    }

    @Override
    public void initListener() {
        toolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rgBtnType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rb1.isChecked()) {
                    lvTitleClock.setVisibility(View.VISIBLE);
                    mPullToRefresh.setVisibility(View.GONE);
                } else {
                    lvTitleClock.setVisibility(View.GONE);
                    mPullToRefresh.setVisibility(View.VISIBLE);
                }
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info = BaseApplication.getInstance().getUserInfo();
                if (info.getUserid() == 0) {
                    showToast("您未登录，无法操作");
                    return;
                } else {
                    if (rb1.isChecked()) {
                        startActivity(new Intent(ClockReminderActivity.this, AddClockActivity.class));
                    } else {
                        startActivity(new Intent(ClockReminderActivity.this, AddEventActivity.class));
                    }
                }
            }
        });

        mPullToRefresh.setColorSchemeResources(R.color.white, R.color.black);
        mPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getReminds();
            }
        });

    }

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
        mLists.clear();
        mLists.addAll(list);
        mClockAdapter.notifyDataSetChanged();
    }

    int pageSize = 10;
    int pageNum = 1;

    public void getReminds() {
        info = BaseApplication.getInstance().getUserInfo();
        Map<String, String> map = new HashMap<>();
        map.put("userid", String.valueOf(info.getUserid()));
        map.put("userguid", String.valueOf(info.getUserguid()));
        map.put("pageSize", String.valueOf(pageSize));
        map.put("pageNum", String.valueOf(pageNum));
        getDataFromServer(HttpUrlConstant.MEMORANDUM_GRTLIST, map, RemindListResponse.class, new Response.Listener<RemindListResponse>() {
            @Override
            public void onResponse(RemindListResponse response) {
                mPullToRefresh.setRefreshing(false);
                if (response.getStatus().equals("ok")) {
                    if (pageNum == 1) {
                        mLlReminder.removeAllViews();
                        for (Remind i : response.getResults()) {
                            addMessageItem(mLlReminder, i);
                        }
                        addLoadMore(mLlReminder);
                    } else {
                        mLlReminder.removeView(loadMore);
                        for (Remind i : response.getResults()) {
                            addMessageItem(mLlReminder, i);
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

    private void addMessageItem(ViewGroup parent, final Remind data) {
        LinearLayout item = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.remind_item_list, parent, false);
        TextView tvTime = (TextView) item.findViewById(R.id.tv_time);
        TextView tvText = (TextView) item.findViewById(R.id.tv_text);

        DateRemindParams params = new DateRemindParams();
        params.setContent(data.getContent());
        params.setLocation(data.getLocation());
        params.setType(2);
        params.setId(data.getId() + 1000000);

        long nowDate = new Date().getTime();
        long first = -1;
        long second = -1;
        long third = -1;
        if (data.getNotifytime() != -1) {
            first = nowDate - data.getNotifytime() * 1000;
        }
        if (data.getSecondnotifytime() != -1) {
            second = nowDate - data.getSecondnotifytime() * 1000;
        }
        if (data.getThirdnotifytime() != -1) {
            third = nowDate - data.getThirdnotifytime() * 1000;
        }

        long now = new Date().getTime();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(mContext, CustomAlarmReceiver.class);

        if (first > now) {
            params.setFirst(second);
            params.setSecond(third);

            alarmIntent.putExtra("params", new Gson().toJson(params));
            PendingIntent pi = PendingIntent.getBroadcast(mContext, data.getId() + 1000000, alarmIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, first, pi);
        } else if (second > now) {
            params.setFirst(third);

            alarmIntent.putExtra("params", new Gson().toJson(params));
            PendingIntent pi = PendingIntent.getBroadcast(mContext, data.getId() + 1000000, alarmIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, second, pi);
        } else if (third > now) {
            alarmIntent.putExtra("params", new Gson().toJson(params));
            PendingIntent pi = PendingIntent.getBroadcast(mContext, data.getId() + 1000000, alarmIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, third, pi);
        }

        tvTime.setText(new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date(data.getStarttime() * 1000)));
        tvText.setText(data.getContent());
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClockReminderActivity.this, AddEventActivity.class)
                        .putExtra("remindid", data.getId()));
            }
        });
        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                heedImageClick2(data);
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
                getReminds();
            }
        });

        mLlReminder.addView(loadMore);
    }

    class Adapter extends BaseAdapter {

        List<List<Clock>> data;

        public Adapter(List<List<Clock>> data) {
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
                view = LayoutInflater.from(mContext).inflate(R.layout.clock_item_list, viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (data != null && data.size() != 0) {
                holder.tvTimeList.setText(getTime(data.get(i).get(0).getTime()));
                holder.tvTypeList.setText(data.get(i).get(0).getContent());
                holder.toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                    @Override
                    public void onToggle(boolean on) {
                        ClockDao clockDao = BaseApplication.getInstance().getDaoSession().getClockDao();
                        final long oneDay = 1000 * 60 * 60 * 24;
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent alarmIntent = new Intent(mContext, CustomAlarmReceiver.class);
                        if (on) {
                            for (Clock j : data.get(i)) {
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
                            for (Clock j : data.get(i)) {
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
                });
                if (data.get(i).get(0).getIsstart()) {
                    holder.toggleButton.setToggleOn();
                    ClockDao clockDao = BaseApplication.getInstance().getDaoSession().getClockDao();
                    final long oneDay = 1000 * 60 * 60 * 24;
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent alarmIntent = new Intent(mContext, CustomAlarmReceiver.class);
                    for (Clock j : data.get(i)) {
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
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean mon = false;
                        boolean tus = false;
                        boolean wed = false;
                        boolean thu = false;
                        boolean fri = false;
                        boolean sat = false;
                        boolean sun = false;
                        for (Clock j : data.get(i)) {
                            switch (j.getWhichday()) {
                                case 1:
                                    mon = true;
                                    break;
                                case 2:
                                    tus = true;
                                    break;
                                case 3:
                                    wed = true;
                                    break;
                                case 4:
                                    thu = true;
                                    break;
                                case 5:
                                    fri = true;
                                    break;
                                case 6:
                                    sat = true;
                                    break;
                                case 7:
                                    sun = true;
                                    break;
                            }
                        }
                        startActivity(new Intent(ClockReminderActivity.this, AddClockActivity.class)
                                .putExtra("time", data.get(i).get(0).getTime())
                                .putExtra("tag", data.get(i).get(0).getContent())
                                .putExtra("mon", mon)
                                .putExtra("tus", tus)
                                .putExtra("wed", wed)
                                .putExtra("thu", thu)
                                .putExtra("fri", fri)
                                .putExtra("sat", sat)
                                .putExtra("sun", sun));
                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        heedImageClick(data.get(i));
                        return true;
                    }
                });
            }
            return view;
        }

        class ViewHolder {
            @Bind(R.id.tv_time_list)
            TextView tvTimeList;
            @Bind(R.id.tv_type_list)
            TextView tvTypeList;
            @Bind(R.id.toggle_button)
            ToggleButton toggleButton;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

    }

    Dialog mDialogSelectImage;

    public void heedImageClick(final List<Clock> mList) {
        if (mDialogSelectImage == null) {
            mDialogSelectImage = new Dialog(this, R.style.ExitAppDialogStyle);
            LinearLayout localObject = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_delete_clock_dialog, null);
            localObject.setMinimumWidth(10000);
            TextView tvDelete = (TextView) localObject.findViewById(R.id.tv_delete);
            tvDelete.setText("删除闹钟");
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialogSelectImage.dismiss();
                    showToast("删除成功");
                    ClockDao clockDao = BaseApplication.getInstance().getDaoSession().getClockDao();
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent alarmIntent = new Intent(mContext, CustomAlarmReceiver.class);
                    for (Clock j : mList) {
                        DateRemindParams params = new DateRemindParams();
                        params.setContent(j.getContent());
                        params.setType(1);
                        params.setTime(j.getTime());

                        alarmIntent.putExtra("params", new Gson().toJson(params));
                        PendingIntent pi = PendingIntent.getBroadcast(mContext, (int) j.getId(), alarmIntent, 0);
                        alarmManager.cancel(pi);
                        clockDao.delete(j);
                        Logger.d(new Gson().toJson(j));
                    }
                    loadDao();
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

    Dialog mDialogSelectImage2;

    public void heedImageClick2(final Remind item) {
        if (mDialogSelectImage2 == null) {
            mDialogSelectImage2 = new Dialog(this, R.style.ExitAppDialogStyle);
            LinearLayout localObject = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_delete_clock_dialog, null);
            localObject.setMinimumWidth(10000);
            TextView tvDelete = (TextView) localObject.findViewById(R.id.tv_delete);
            tvDelete.setText("删除备忘录");
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialogSelectImage2.dismiss();
                    showToast("删除成功");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("userid", String.valueOf(info.getUserid()));
                    map.put("userguid", String.valueOf(info.getUserguid()));
                    map.put("id", String.valueOf(item.getId()));
                    getDataFromServer(HttpUrlConstant.MEMORANDUM_DELETE, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
                        @Override
                        public void onResponse(EmptyResponse response) {
                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent alarmIntent = new Intent(mContext, CustomAlarmReceiver.class);

                            PendingIntent pi = PendingIntent.getBroadcast(mContext, item.getId() + 1000000, alarmIntent, 0);
                            alarmManager.cancel(pi);

                            pageNum = 1;
                            getReminds();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }
            });

            WindowManager.LayoutParams localLayoutParams = mDialogSelectImage2.getWindow().getAttributes();
            localLayoutParams.x = 0;
            localLayoutParams.y = -1000;
            localLayoutParams.gravity = 80;
            mDialogSelectImage2.onWindowAttributesChanged(localLayoutParams);
            mDialogSelectImage2.setCanceledOnTouchOutside(true);
            mDialogSelectImage2.setContentView(localObject);
        }
        mDialogSelectImage2.show();
    }

    private String getTime(long time) {
        int sec = (int) (time / 1000);
        int min = sec / 60;
        int hour = min / 60;
        min = min - hour * 60;

        String stringTime = "";
        if (hour < 10) {
            stringTime += "0" + hour;
        } else {
            stringTime += hour;
        }
        stringTime += ":";
        if (min < 10) {
            stringTime += "0" + min;
        } else {
            stringTime += min;
        }
        return stringTime;
    }
}



