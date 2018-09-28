package com.miracle.clock.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.czsirius.clock.R;
import com.miracle.clock.custom.CustomTimePicker;
import com.miracle.clock.greendao.gen.TimeTagDao;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.event.BackEvent;
import com.miracle.clock.model.normal.TimeTag;
import com.miracle.clock.model.normal.UserInfoResponse;
import com.miracle.clock.utils.normal.AppUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.addapp.pickers.listeners.OnMoreItemPickListener;
import cn.addapp.pickers.listeners.OnMoreWheelListener;

public class EventRemindActivity extends BaseActivity {


    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.toolbar_back)
    TextView mToolbarBack;
    @Bind(R.id.toolbar_right)
    TextView mToolbarRight;
    @Bind(R.id.rg_eventremind)
    RadioGroup rgEventremind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_remind);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    List<TimeTag> list = BaseApplication.getInstance().getDaoSession().getTimeTagDao().loadAll();

    long pre;
    String remind;

    @Override
    public void initView() {
        pre = getIntent().getLongExtra("pre", -1);
        remind = getIntent().getStringExtra("remind");

        mToolbarTitle.setText("标签");
        mToolbarRight.setText("新建事件");
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarRight.setTextSize(18);
        mToolbarRight.setTextColor(AppUtils.getColor(R.color.blue));
        loadBaseList();

        rgEventremind.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == 0) {
                    finish();
                    return;
                }
                long time = list.get(i - 1).getTime();
                Logger.d("time:" + time);
                if (remind.equals("remind1")) {
                    EventBus.getDefault().post(new BackEvent(list.get(i - 1).getContent(), remind, list.get(i - 1).getTime()));
                    finish();
                } else {
                    if (time >= pre) {
                        long min = time / 60000;
                        long hour = min / 60;
                        min = min - hour * 60;
                        String msg = "";
                        if (hour == 0) {
                            msg = "请选择第一次提醒之后的时间\n第一次提醒为" + min + "分钟前";
                        } else {
                            msg = "请选择第一次提醒之后的时间\n第一次提醒为" + hour + "小时" + min + "分钟前";
                        }
                        showToast(msg);
                    } else {
                        EventBus.getDefault().post(new BackEvent(list.get(i - 1).getContent(), remind, list.get(i - 1).getTime()));
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditTextDialog();
            }
        });
    }

    public void loadBaseList() {
        rgEventremind.removeAllViewsInLayout();
        list = BaseApplication.getInstance().getDaoSession().getTimeTagDao().loadAll();
        View v = new View(mContext);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, 50);
        v.setBackgroundColor(AppUtils.getColor(R.color.grey));
        v.setLayoutParams(params);
        rgEventremind.addView(v);
        RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.redio_btn_type, rgEventremind, false);
        rb.setId(0);
        rb.setText("无");
        rgEventremind.addView(rb);
        View v1 = new View(mContext);
        RadioGroup.LayoutParams params1 = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, 50);
        v1.setBackgroundColor(AppUtils.getColor(R.color.grey));
        v1.setLayoutParams(params1);
        rgEventremind.addView(v1);
        UserInfoResponse.Data info = BaseApplication.getInstance().getUserInfo();
        if (info.getUserid() != 0) {
            for (int i = 0; i < list.size(); i++) {
                RadioButton r = (RadioButton) getLayoutInflater().inflate(R.layout.redio_btn_type, rgEventremind, false);
                r.setId(i + 1);
                r.setText(list.get(i).getContent());
                rgEventremind.addView(r);
                View view = new View(mContext);
                RadioGroup.LayoutParams p = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, 1);
                view.setBackgroundColor(AppUtils.getColor(R.color.grey));
//              p.setMargins(55,0,0,0);
                view.setLayoutParams(p);
                rgEventremind.addView(view);
            }
        }
    }

    CustomTimePicker picker;
    String time;

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

    public void showEditTextDialog() {
        if (dialog == null) {
            dialog = new Dialog(mContext, R.style.LoadingDialog);
            dialog.setContentView(R.layout.dialog_time_text);
            LinearLayout date = dialog.findViewById(R.id.ll_birth);
            date.setVisibility(View.GONE);
            TextView tv_title = dialog.findViewById(R.id.tv_title);
            TextView tv_commit = dialog.findViewById(R.id.tv_commit);
            TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);
            timeInit(dialog);
            tv_title.setText("提前时间");
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            tv_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] times;
                    if (time == null) {
                        times = new String[2];
                        times[0] = "00";
                        times[1] = "00";
                    } else {
                        times = time.split(":");
                    }
                    String timename = "";
                    long time;
                    if (times[0].equals("00")) {
                        if (times[1].equals("00")) {
                            time = 0;
                        } else {
                            time = 60 * 1000 * Integer.parseInt(times[1]);
                            if (times[1].substring(0, 1).equals("0")) {
                                times[1] = times[1].replace("0", "");
                            }
                            timename = times[1] + "分钟前";
                        }
                    } else {
                        if (times[1].equals("00")) {
                            time = 60 * 1000 * Integer.parseInt(times[0]) * 60;
                            if (times[0].substring(0, 1).equals("0")) {
                                times[0] = times[0].replace("0", "");
                            }
                            timename = times[0] + "小时前";
                        } else {
                            time = 60 * 1000 * Integer.parseInt(times[1]) + 60 * 1000 * Integer.parseInt(times[0]) * 60;
                            if (times[0].substring(0, 1).equals("0")) {
                                times[0] = times[0].replace("0", "");
                            }
                            if (times[1].substring(0, 1).equals("0")) {
                                times[1] = times[1].replace("0", "");
                            }
                            timename = times[0] + "小时前" + times[1] + "分钟前";
                        }
                    }
                    String result = isTimeOK(timename);
                    if (result == null) {
                        TimeTag tt = new TimeTag();
                        tt.setContent(timename);
                        tt.setTime(time);
                        tt.setId(new Date().getTime());
                        TimeTagDao ttd = BaseApplication.getInstance().getDaoSession().getTimeTagDao();
                        ttd.insert(tt);
                        loadBaseList();
                        dialog.dismiss();
                    } else {
                        showToast(result);
                    }
                }
            });
        }
        dialog.show();
    }

    public String isTimeOK(String timename) {
        List<TimeTag> list = BaseApplication.getInstance().getDaoSession().getTimeTagDao().loadAll();
        if (timename.equals("")) {
            return "您未选择时间";
        }
        for (TimeTag t : list) {
            if (t.getContent().equals(timename)) {
                return "该时间已存在";
            }
        }
        return null;
    }
}
