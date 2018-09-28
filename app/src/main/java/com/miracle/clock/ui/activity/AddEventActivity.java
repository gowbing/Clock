package com.miracle.clock.ui.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.custom.CustomTimePicker;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.event.BackEvent;
import com.miracle.clock.model.normal.AddEventResponse;
import com.miracle.clock.model.normal.DateRemindParams;
import com.miracle.clock.model.normal.RemindResponse;
import com.miracle.clock.model.normal.UserInfoResponse;
import com.miracle.clock.service.CustomAlarmReceiver;
import com.miracle.clock.utils.normal.AppUtils;
import com.miracle.clock.utils.normal.DateUtils;
import com.miracle.clock.utils.normal.StringUtils;
import com.orhanobut.logger.Logger;
import com.zcw.togglebutton.ToggleButton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.listeners.OnMoreItemPickListener;
import cn.addapp.pickers.listeners.OnMoreWheelListener;
import cn.addapp.pickers.listeners.OnSingleWheelListener;
import cn.addapp.pickers.picker.SinglePicker;

public class AddEventActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.toolbar_back)
    TextView toolbarBack;
    @Bind(R.id.toolbar_right)
    TextView toolbarRight;
    @Bind(R.id.toggle_button)
    ToggleButton toggleButton;
    @Bind(R.id.remind1)
    TextView remind1;
    @Bind(R.id.r1)
    LinearLayout r1;
    @Bind(R.id.remind2)
    TextView remind2;
    @Bind(R.id.r2)
    LinearLayout r2;
    @Bind(R.id.remind3)
    TextView remind3;
    @Bind(R.id.r3)
    LinearLayout r3;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_text)
    EditText etText;
    @Bind(R.id.et_address)
    EditText etAddress;
    @Bind(R.id.tv_clockTime)
    TextView tvClockTime;
    @Bind(R.id.tv_text_length)
    TextView tvTextLength;

    UserInfoResponse.Data info = BaseApplication.getInstance().getUserInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        int remindid = getIntent().getIntExtra("remindid", 0);
        if (remindid == 0) {
            toolbarTitle.setText("新建事件");
            tvClockTime.setText(DateUtils.dateToString(new Date(), DateUtils.DATE_FORMAT_FIFTEEN));
        } else {
            showInfo();
            toolbarTitle.setText("修改事件");
        }
        toolbarBack.setText("取消");
        toolbarBack.setTextColor(AppUtils.getColor(R.color.blue));
        toolbarBack.setVisibility(View.VISIBLE);
        toolbarRight.setTextColor(AppUtils.getColor(R.color.blue));
        toolbarRight.setText("储存");
//        toggleButton.toggle(false);
        toggleButton.toggle();
        toggleButton.setToggleOff();
        toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
            }
        });
//        toggleButton.setToggleOn();
//        toggleButton.setToggleOff();
//        toggleButton.setToggleOn(false);
//        toggleButton.setToggleOff(false);
//        toggleButton.setAnimate(false);
    }

    @Override
    public void initListener() {
        toolbarBack.setOnClickListener(this);
        r1.setOnClickListener(this);
        r2.setOnClickListener(this);
        r3.setOnClickListener(this);
        toolbarRight.setOnClickListener(this);
        tvClockTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditTextDialog(tvClockTime);
            }
        });

        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etText.getText().toString().length() > 100) {
                    String s1 = etText.getText().toString().substring(0, 100);
                    etText.setText(s1);
                }
                tvTextLength.setText(String.valueOf(100 - etText.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etText.getText().toString().length() > 100) {
                    String s1 = etText.getText().toString().substring(0, 60);
                    etText.setText(s1);
                }
                tvTextLength.setText(String.valueOf(100 - etText.getText().toString().length()));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.r1:
                startActivity(new Intent(AddEventActivity.this, EventRemindActivity.class)
                        .putExtra("remind", "remind1"));
                break;
            case R.id.r2:
                if (notifytime != -1) {
                    startActivity(new Intent(AddEventActivity.this, EventRemindActivity.class)
                            .putExtra("remind", "remind2").putExtra("pre", notifytime));
                } else {
                    showToast("请先选择第一次提醒");
                }
                break;
            case R.id.r3:
                if (secondnotifytime != -1) {
                    startActivity(new Intent(AddEventActivity.this, EventRemindActivity.class)
                            .putExtra("remind", "remind3").putExtra("pre", secondnotifytime));
                } else {
                    showToast("请先选择第二次提醒");
                }
                break;
            case R.id.toolbar_right:
                doAddEvent();
        }
    }

    long before1 = 0;
    long before2 = 0;
    long before3 = 0;

    public void showInfo() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", String.valueOf(info.getUserid()));
        map.put("userguid", String.valueOf(info.getUserguid()));
        map.put("id", "" + getIntent().getIntExtra("remindid", 0));
        getDataFromServer(HttpUrlConstant.MEMORANDUM_GETINFO, map, RemindResponse.class, new Response.Listener<RemindResponse>() {
            @Override
            public void onResponse(RemindResponse response) {
                if (response.getStatus().equals("ok")) {
                    before1 = response.getResults().getNotifytime();
                    before2 = response.getResults().getSecondnotifytime();
                    before3 = response.getResults().getThirdnotifytime();
                    etTitle.setText(response.getResults().getContent());
                    etAddress.setText(response.getResults().getLocation());
                    etText.setText(response.getResults().getBodytext());
                    tvClockTime.setText(DateUtils.dateToString(new Date(response.getResults().getCreatetime()), DateUtils.DATE_FORMAT_FIFTEEN));
                    String first = "无";
                    String second = "无";
                    String third = "无";
                    if (response.getResults().getNotifytime() != -1) {
                        first = getTimeConent(response.getResults().getNotifytime()*1000);
                    }
                    if (response.getResults().getSecondnotifytime() != -1) {
                        second = getTimeConent(response.getResults().getSecondnotifytime()*1000);
                    }
                    if (response.getResults().getThirdnotifytime() != -1) {
                        third = getTimeConent(response.getResults().getThirdnotifytime()*1000);
                    }
                    remind1.setText(first);
                    remind2.setText(second);
                    remind3.setText(third);
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

    public String getTimeConent(long time) {
        String str = "";
        long min = time / 1000 / 60;
        if (min > 60) {
            long h = min / 60;
            str = h + "小时" + min + "分钟前";
        } else {
            str = min + "分钟前";
        }
        return str;
    }

    public void doAddEvent() {
        String content = etTitle.getText().toString();
        String bodytext = etText.getText().toString();
        long nowDate = 0;
        String t = tvClockTime.getText().toString();
        try {
            nowDate = DateUtils.getFormatDate(t, DateUtils.DATE_FORMAT_FIFTY).getTime();

            Logger.d(DateUtils.dateToString(new Date(nowDate), DateUtils.DATE_FORMAT_FIFTEEN));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String address = etAddress.getText().toString();
        int remindid = getIntent().getIntExtra("remindid", 0);
        if (remindid == 0) {
            long first = -1;
            long second = -1;
            long third = -1;
            if (notifytime != -1) {
                first = nowDate - notifytime;
            }
            if (secondnotifytime != -1) {
                second = nowDate - secondnotifytime;
            }
            if (thirdnotifytime != -1) {
                third = nowDate - thirdnotifytime;
            }
            if (StringUtils.isEmpty(content)) {
                showToast("请输入事件的标题");
                return;
            }
            addEvent(content,bodytext, address, null, nowDate, notifytime, secondnotifytime, thirdnotifytime);
        } else {
            long first = -1;
            long second = -1;
            long third = -1;
            if (notifytime != -1) {
                first = notifytime;
            } else {
                first = before1;
            }
            if (secondnotifytime != -1) {
                second = secondnotifytime;
            } else {
                second = before2;
            }
            if (thirdnotifytime != -1) {
                third = thirdnotifytime;
            } else {
                third = before3;
            }
            if (StringUtils.isEmpty(content)) {
                showToast("请输入事件的标题");
                return;
            }
            addEvent(content,bodytext, address, remindid + "", nowDate, first, second, third);
        }
    }

    public void addEvent(final String content,final String bodytext, final String address, final String id, final long nowDate, final long notifytime, final long secondnotifytime, final long thirdnotifytime) {
        showDialog("处理中");
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid", String.valueOf(info.getUserid()));
        map.put("userguid", String.valueOf(info.getUserguid()));
        map.put("content", content);
        map.put("bodytext", bodytext);
        map.put("location", address);
        map.put("starttime", String.valueOf(nowDate / 1000));
        map.put("notifytime", String.valueOf(notifytime / 1000));
        map.put("secondnotifytime", String.valueOf(secondnotifytime / 1000));
        map.put("thirdnotifytime", String.valueOf(thirdnotifytime / 1000));
        String url = "";
        Logger.d("notifytime:" + DateUtils.dateToString(new Date(notifytime), DateUtils.DATE_FORMAT_FIVE) + "\n" +
                "secondnotifytime:" + DateUtils.dateToString(new Date(secondnotifytime), DateUtils.DATE_FORMAT_FIVE) + "\n" +
                "thirdnotifytime:" + DateUtils.dateToString(new Date(thirdnotifytime), DateUtils.DATE_FORMAT_FIVE));
        if (id == null) {
            url = HttpUrlConstant.MEMORANDUM_ADD;
        } else {
            map.put("id", String.valueOf(id));
            url = HttpUrlConstant.MEMORANDUM_UPDATE;
        }
        final String finalUrl = url;
        getDataFromServer(url, map, AddEventResponse.class, new Response.Listener<AddEventResponse>() {
            @Override
            public void onResponse(AddEventResponse response) {
                hideProgress();
                if (!response.getStatus().equals("ok")) {
                    showToast(response.getMessage());
                    return;
                }
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(mContext, CustomAlarmReceiver.class);

                DateRemindParams params = new DateRemindParams();
                params.setContent(content);
                params.setLocation(address);
                params.setType(2);
                params.setId(response.getResults().getId() + 1000000);

                long first = -1;
                long second = -1;
                long third = -1;
                if (notifytime != -1) {
                    first = nowDate - notifytime;
                }
                if (secondnotifytime != -1) {
                    second = nowDate - secondnotifytime;
                }
                if (thirdnotifytime != -1) {
                    third = nowDate - thirdnotifytime;
                }

                long now = new Date().getTime();
                if (first > now) {
                    params.setFirst(second);
                    params.setSecond(third);

                    alarmIntent.putExtra("params", new Gson().toJson(params));
                    PendingIntent pi = PendingIntent.getBroadcast(mContext, response.getResults().getId() + 1000000, alarmIntent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, first, pi);
                } else if (second > now) {
                    params.setFirst(third);

                    alarmIntent.putExtra("params", new Gson().toJson(params));
                    PendingIntent pi = PendingIntent.getBroadcast(mContext, response.getResults().getId() + 1000000, alarmIntent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, second, pi);
                } else if (third > now) {
                    alarmIntent.putExtra("params", new Gson().toJson(params));
                    PendingIntent pi = PendingIntent.getBroadcast(mContext, response.getResults().getId() + 1000000, alarmIntent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, third, pi);
                }
                Log.e("ClockReminderActivity", "----------------------------------------------------------------------");
                Log.e("params", new Gson().toJson(params));
                Log.e("ClockReminderActivity", "----------------------------------------------------------------------");
                showClockTip();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                showToast("网络错误，请稍后再试。");
            }
        });
    }

    Dialog mDialog;

    private void showClockTip() {
        if (mDialog == null) {
            mDialog = new Dialog(mContext, R.style.LoadingDialog);
            mDialog.setContentView(R.layout.dialog_clock_tip);
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            TextView tv_ok = mDialog.findViewById(R.id.tv_ok);
            tv_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    finish();
                }
            });
        }
        mDialog.show();
    }

    public void dateClick(final Dialog dialog) {
        final ArrayList<String> list = new ArrayList<>();
        LinearLayout tv_year = (LinearLayout) dialog.findViewById(R.id.ll_year);
        tv_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                for (int i = 1970; i <= 2100; i++) {
                    list.add("" + i);
                }
                onOptionPicker(11, list, dialog);
            }
        });
        LinearLayout tv_month = (LinearLayout) dialog.findViewById(R.id.ll_month);
        tv_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                for (int i = 1; i <= 12; i++) {
                    if (i < 10) {
                        list.add("0" + i);
                    } else {
                        list.add("" + i);
                    }
                }
                onOptionPicker(12, list, dialog);
            }
        });
        LinearLayout tv_day = (LinearLayout) dialog.findViewById(R.id.ll_day);
        tv_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView mTvYear = (TextView) dialog.findViewById(R.id.tv_year);
                TextView mTvMonth = (TextView) dialog.findViewById(R.id.tv_month);
                TextView mTvDay = (TextView) dialog.findViewById(R.id.tv_day);
                if (StringUtils.isEmpty(mTvYear.getText().toString())) {
                    showToast("请您先选择年份");
                    return;
                }
                if (StringUtils.isEmpty(mTvMonth.getText().toString())) {
                    showToast("请您先选择月份");
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.valueOf(mTvYear.getText().toString()));//先指定年份
                calendar.set(Calendar.MONTH, Integer.valueOf(mTvMonth.getText().toString()) - 1);//再指定月份 Java月份从0开始算
                int daysCountOfMonth = calendar.getActualMaximum(Calendar.DATE);//获取指定年份中指定月份有几天
                list.clear();
                for (int i = 1; i <= daysCountOfMonth; i++) {
                    if (i < 10) {
                        list.add("0" + i);
                    } else {
                        list.add("" + i);
                    }
                }
                onOptionPicker(13, list, dialog);
            }
        });
    }

    CustomTimePicker picker;
    String time = "";

    public void timeInit(Dialog dialog) {
        picker = new CustomTimePicker(this);
        picker.setWeightEnable(true);
        picker.setColumnWeight(0.5f, 0.5f, 1);
        picker.setWheelModeEnable(true);
        picker.setTextSize(18);
        picker.setSelectedTextColor(0xFF5d5d5d);//前四位值是透明度
        picker.setUnSelectedTextColor(0xFFc4c4c4);
        picker.setCanLoop(true);
        picker.setOffset(3);
        picker.setOnMoreItemPickListener(new OnMoreItemPickListener<String>() {
            @Override
            public void onItemPicked(String s1, String s2, String s3) {
                s3 = !TextUtils.isEmpty(s3) ? ",item3: " + s3 : "";
                Toast.makeText(AddEventActivity.this, "item1: " + s1 + ",item2: " + s2 + s3, Toast.LENGTH_SHORT).show();
            }
        });
        picker.setOnMoreWheelListener(new OnMoreWheelListener() {
            @Override
            public void onFirstWheeled(int index, String item) {
                time = item + ":" + picker.getSelectedSecondItem();
            }

            @Override
            public void onSecondWheeled(int index, String item) {
                time = picker.getSelectedFirstItem() + ":" + item;
            }

            @Override
            public void onThirdWheeled(int index, String item) {

            }
        });
        LinearLayout mLlTimePicker = (LinearLayout) dialog.findViewById(R.id.ll_time_picker);
        mLlTimePicker.addView(picker.getContentView());
    }

    Dialog dialog;

    public void showEditTextDialog(final TextView tvShow) {
        if (dialog == null) {
            dialog = new Dialog(mContext, R.style.LoadingDialog);
            dialog.setContentView(R.layout.dialog_time_text);
            TextView tv_title = dialog.findViewById(R.id.tv_title);
            final TextView mTvYear = dialog.findViewById(R.id.tv_year);
            final TextView mTvMonth = dialog.findViewById(R.id.tv_month);
            final TextView mTvDay = dialog.findViewById(R.id.tv_day);
            Calendar date = Calendar.getInstance();
            String yyyy = String.valueOf(date.get(Calendar.YEAR));
            String mm = String.valueOf(date.get(Calendar.MONTH) + 1);
            String dd = String.valueOf(date.get(Calendar.DATE));
            mTvYear.setText(yyyy);
            mTvMonth.setText(mm);
            mTvDay.setText(dd);
            TextView tv_commit = dialog.findViewById(R.id.tv_commit);
            TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);
            timeInit(dialog);
            dateClick(dialog);
            tv_title.setText("选择日期");
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            tv_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String date = mTvYear.getText().toString() + "-"
                            + mTvMonth.getText().toString() + "-" +
                            mTvDay.getText().toString() + " " + time;
                    long nowdate = 0;
                    try {
                        nowdate = DateUtils.getFormatDate(date, DateUtils.DATE_FORMAT_FIFTY).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (nowdate < new Date().getTime()) {
                        showToast("请选择正确的时间");
                    } else {
                        tvShow.setText(date);
                        dialog.dismiss();
                    }
                }
            });
        }
        dialog.show();
    }

    public void onOptionPicker(final int type, final ArrayList<String> data, Dialog dialog) {
        final TextView mTvYear = (TextView) dialog.findViewById(R.id.tv_year);
        final TextView mTvMonth = (TextView) dialog.findViewById(R.id.tv_month);
        final TextView mTvDay = (TextView) dialog.findViewById(R.id.tv_day);
        SinglePicker<String> picker = new SinglePicker<>(this, data);
        picker.setCanLoop(false);//不禁用循环
        picker.setLineVisible(true);
        picker.setShadowVisible(true);
        picker.setTextSize(18);
        picker.setSelectedIndex(0);
        picker.setWheelModeEnable(true);
        //启用权重 setWeightWidth 才起作用
//        picker.setLabel("分");
        picker.setWeightEnable(true);
        picker.setWeightWidth(1);
        picker.setSelectedTextColor(0xFF279BAA);//前四位值是透明度
        picker.setUnSelectedTextColor(0xFF999999);
        picker.setOnSingleWheelListener(new OnSingleWheelListener() {
            @Override
            public void onWheeled(int index, String item) {
            }
        });
        picker.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                switch (type) {
                    case 11:
                        mTvYear.setText(item);
                        break;
                    case 12:
                        mTvMonth.setText(item);
                        break;
                    case 13:
                        mTvDay.setText(item);
                        break;
                }
            }
        });
        picker.show();
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    long notifytime = -1;
    long secondnotifytime = -1;
    long thirdnotifytime = -1;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BackEvent event) {
        if (event.getTypename().equals("remind1")) {
            remind1.setText(event.getName());
            notifytime = event.getTime();
            remind2.setText("无");
            secondnotifytime = -1;
            remind3.setText("无");
            thirdnotifytime = -1;
        } else if (event.getTypename().equals("remind2")) {
            remind2.setText(event.getName());
            secondnotifytime = event.getTime();
            remind3.setText("无");
            thirdnotifytime = -1;
        } else {
            remind3.setText(event.getName());
            thirdnotifytime = event.getTime();
        }
        Logger.d(notifytime + "\n" + secondnotifytime + "\n" + thirdnotifytime);
    }
}
