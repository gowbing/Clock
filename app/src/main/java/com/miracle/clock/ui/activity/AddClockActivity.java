package com.miracle.clock.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.custom.CustomTimePicker;
import com.miracle.clock.greendao.gen.ClockDao;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.event.SetDayInWeekEvent;
import com.miracle.clock.model.normal.Clock;
import com.miracle.clock.model.normal.TagResponse;
import com.miracle.clock.utils.normal.AppUtils;
import com.miracle.clock.utils.normal.DateUtils;
import com.miracle.clock.utils.normal.StringUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.listeners.OnMoreItemPickListener;
import cn.addapp.pickers.listeners.OnMoreWheelListener;
import cn.addapp.pickers.listeners.OnSingleWheelListener;
import cn.addapp.pickers.picker.SinglePicker;

public class AddClockActivity extends BaseActivity {

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
    @Bind(R.id.ll_time_picker)
    LinearLayout mLlTimePicker;
    @Bind(R.id.tv_retype)
    TextView mTvRetype;
    @Bind(R.id.ll_re)
    LinearLayout mLlRe;
    @Bind(R.id.tv_tag)
    TextView mTvTag;
    @Bind(R.id.ll_tag)
    LinearLayout mLlTag;

    private CustomTimePicker picker;

    long clockTime = 0;

    final long oneDay = 1000 * 60 * 60 * 24;

    long time;
    String tag;
    boolean mon;
    boolean tus;
    boolean wed;
    boolean thu;
    boolean fri;
    boolean sat;
    boolean sun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clock);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        mToolbarBack.setVisibility(View.VISIBLE);
        getTagList();
        Intent intent = getIntent();
        time = intent.getLongExtra("time", 0);
        tag = intent.getStringExtra("tag");
        mon = intent.getBooleanExtra("mon", false);
        tus = intent.getBooleanExtra("tus", false);
        wed = intent.getBooleanExtra("wed", false);
        thu = intent.getBooleanExtra("thu", false);
        fri = intent.getBooleanExtra("fri", false);
        sat = intent.getBooleanExtra("sat", false);
        sun = intent.getBooleanExtra("sun", false);

        mToolbarTitle.setText("添加闹钟");
        mToolbarBack.setText("取消");
        mToolbarBack.setTextColor(AppUtils.getColor(R.color.blue));
        mToolbarRight.setText("储存");
        mToolbarRight.setTextColor(AppUtils.getColor(R.color.blue));

        int[] clock = getTime(time);

        picker = new CustomTimePicker(this);
        picker.setWeightEnable(true);
        picker.setColumnWeight(0.5f, 0.5f, 1);
        picker.setWheelModeEnable(true);
        picker.setTextSize(18);
        picker.setSelectedTextColor(0xFF5d5d5d);//前四位值是透明度
        picker.setUnSelectedTextColor(0xFFc4c4c4);
        picker.setCanLoop(true);
        picker.setOffset(3);
        picker.setSelectedIndex(clock[0], clock[1]);
        picker.setOnMoreItemPickListener(new OnMoreItemPickListener<String>() {
            @Override
            public void onItemPicked(String s1, String s2, String s3) {
                s3 = !TextUtils.isEmpty(s3) ? ",item3: " + s3 : "";
                Toast.makeText(AddClockActivity.this, "item1: " + s1 + ",item2: " + s2 + s3, Toast.LENGTH_SHORT).show();
            }
        });
        picker.setOnMoreWheelListener(new OnMoreWheelListener() {
            @Override
            public void onFirstWheeled(int index, String item) {
                int hour = Integer.valueOf(item);
                int min = Integer.valueOf(picker.getSelectedSecondItem());
                clockTime = hour * 1000 * 60 * 60 + min * 1000 * 60;
                Logger.d(DateUtils.dateToString(new Date(0), DateUtils.DATE_FORMAT_NINETEEN));
                Logger.d(DateUtils.dateToString(new Date(clockTime), DateUtils.DATE_FORMAT_NINETEEN));
            }

            @Override
            public void onSecondWheeled(int index, String item) {
                int hour = Integer.valueOf(picker.getSelectedFirstItem());
                int min = Integer.valueOf(item);
                clockTime = hour * 1000 * 60 * 60 + min * 1000 * 60;
                Logger.d(DateUtils.dateToString(new Date(0), DateUtils.DATE_FORMAT_NINETEEN));
                Logger.d(DateUtils.dateToString(new Date(clockTime), DateUtils.DATE_FORMAT_NINETEEN));
            }

            @Override
            public void onThirdWheeled(int index, String item) {

            }
        });
        mLlTimePicker.addView(picker.getContentView());

        String repeat = "星期";

        if (mon) {
            repeat += "一、";
        }
        if (tus) {
            repeat += "二、";
        }
        if (wed) {
            repeat += "三、";
        }
        if (thu) {
            repeat += "四、";
        }
        if (fri) {
            repeat += "五、";
        }
        if (sat) {
            repeat += "六、";
        }
        if (sun) {
            repeat += "天、";
        }
        if (repeat.equals("星期")) {
            repeat = "只响一次";
        } else if (repeat.equals("星期一、二、三、四、五、六、天、")) {
            repeat = "每天";
        } else {
            repeat = repeat.substring(0, repeat.length() - 1);
        }
        mTvRetype.setText(repeat);

        mTvTag.setText(tag);
    }

    private int[] getTime(long time) {
        int sec = (int) (time / 1000);
        int min = sec / 60;
        int hour = min / 60;
        min = min - hour * 60;

        int[] clock = new int[2];
        clock[0] = hour;
        clock[1] = min;
        return clock;
    }

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLlRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddClockActivity.this, RepeatActivity.class)
                        .putExtra("mon", mon)
                        .putExtra("tus", tus)
                        .putExtra("wed", wed)
                        .putExtra("thu", thu)
                        .putExtra("fri", fri)
                        .putExtra("sat", sat)
                        .putExtra("sun", sun));
            }
        });
        mLlTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionPicker(list);
            }
        });
        mToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String repeat = mTvRetype.getText().toString();
                String content = mTvTag.getText().toString();

                ClockDao clockDao = BaseApplication.getInstance().getDaoSession().getClockDao();

                if (StringUtils.isEmpty(content)) {
                    showToast("请填写闹钟提醒标签");
                    return;
                }

                //删除数据库之前的数据
                List<Clock> clocks = new ArrayList<>();
                try {
                    clocks.addAll(clockDao.queryBuilder().where(ClockDao.Properties.Time.eq(time)).list());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (clocks != null && clocks.size() != 0) {
                    for (Clock i : clocks) {
                        clockDao.deleteByKey(i.getId());
                    }
                }

                //获取数据库最后一条数据id
                List<Clock> list = new ArrayList<>();
                try {
                    list.addAll(clockDao.loadAll());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                long id = 0;
                if (list.size() == 0) {
                    id = 1;
                } else {
                    id = list.get(list.size() - 1).getId();
                    id++;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                //今天周几
                int whichDay = calendar.get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7 : calendar.get(Calendar.DAY_OF_WEEK) - 1;

                long today = 0;
                try {
                    //今天日期
                    today = DateUtils.stringToDate(DateUtils.getNowDate(), DateUtils.DATE_FORMAT_ONE).getTime();

                    Logger.d(DateUtils.dateToString(new Date(today), DateUtils.DATE_FORMAT_FIFTEEN));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //现在时间
                long now = new Date().getTime();

                //今天闹钟时间
                today += clockTime;

                if (repeat.equals("只响一次")) {
                    if (today > now) {
                        clockDao.insert(new Clock(id, clockTime, today, 0, content, 1, true));
                    } else {
                        clockDao.insert(new Clock(id, clockTime, today + oneDay, 0, content, 1, true));
                    }
                    showClockTip();
                    return;
                }

                long monday = 0;
                long tuesday = 0;
                long wedesday = 0;
                long thurday = 0;
                long friday = 0;
                long saturday = 0;
                long sunday = 0;

                switch (whichDay) {
                    case 1:
                        if (today > now) {
                            monday = today;
                            tuesday = today + oneDay;
                            wedesday = today + oneDay * 2;
                            thurday = today + oneDay * 3;
                            friday = today + oneDay * 4;
                            saturday = today + oneDay * 5;
                            sunday = today + oneDay * 6;
                        } else {
                            monday = today + oneDay * 7;
                            tuesday = today + oneDay;
                            wedesday = today + oneDay * 2;
                            thurday = today + oneDay * 3;
                            friday = today + oneDay * 4;
                            saturday = today + oneDay * 5;
                            sunday = today + oneDay * 6;
                        }
                        break;
                    case 2:
                        if (today > now) {
                            monday = today + oneDay * 6;
                            tuesday = today;
                            wedesday = today + oneDay;
                            thurday = today + oneDay * 2;
                            friday = today + oneDay * 3;
                            saturday = today + oneDay * 4;
                            sunday = today + oneDay * 5;
                        } else {
                            monday = today + oneDay * 6;
                            tuesday = today + oneDay * 7;
                            wedesday = today + oneDay;
                            thurday = today + oneDay * 2;
                            friday = today + oneDay * 3;
                            saturday = today + oneDay * 4;
                            sunday = today + oneDay * 5;
                        }
                        break;
                    case 3:
                        if (today > now) {
                            monday = today + oneDay * 5;
                            tuesday = today + oneDay * 6;
                            wedesday = today;
                            thurday = today + oneDay;
                            friday = today + oneDay * 2;
                            saturday = today + oneDay * 3;
                            sunday = today + oneDay * 4;
                        } else {
                            monday = today + oneDay * 5;
                            tuesday = today + oneDay * 6;
                            wedesday = today + oneDay * 7;
                            thurday = today + oneDay;
                            friday = today + oneDay * 2;
                            saturday = today + oneDay * 3;
                            sunday = today + oneDay * 4;
                        }
                        break;
                    case 4:
                        if (today > now) {
                            monday = today + oneDay * 4;
                            tuesday = today + oneDay * 5;
                            wedesday = today + oneDay * 6;
                            thurday = today;
                            friday = today + oneDay;
                            saturday = today + oneDay * 2;
                            sunday = today + oneDay * 3;
                        } else {
                            monday = today + oneDay * 4;
                            tuesday = today + oneDay * 5;
                            wedesday = today + oneDay * 6;
                            thurday = today + oneDay * 7;
                            friday = today + oneDay;
                            saturday = today + oneDay * 2;
                            sunday = today + oneDay * 3;
                        }
                        break;
                    case 5:
                        if (today > now) {
                            monday = today + oneDay * 3;
                            tuesday = today + oneDay * 4;
                            wedesday = today + oneDay * 5;
                            thurday = today + oneDay * 6;
                            friday = today;
                            saturday = today + oneDay;
                            sunday = today + oneDay * 2;
                        } else {
                            monday = today + oneDay * 3;
                            tuesday = today + oneDay * 4;
                            wedesday = today + oneDay * 5;
                            thurday = today + oneDay * 6;
                            friday = today + oneDay * 7;
                            saturday = today + oneDay;
                            sunday = today + oneDay * 2;
                        }
                        break;
                    case 6:
                        if (today > now) {
                            monday = today + oneDay * 2;
                            tuesday = today + oneDay * 3;
                            wedesday = today + oneDay * 4;
                            thurday = today + oneDay * 5;
                            friday = today + oneDay * 6;
                            saturday = today;
                            sunday = today + oneDay;
                        } else {
                            monday = today + oneDay * 2;
                            tuesday = today + oneDay * 3;
                            wedesday = today + oneDay * 4;
                            thurday = today + oneDay * 5;
                            friday = today + oneDay * 6;
                            saturday = today + oneDay * 7;
                            sunday = today + oneDay;
                        }
                        break;
                    case 7:
                        if (today > now) {
                            monday = today + oneDay * 1;
                            tuesday = today + oneDay * 2;
                            wedesday = today + oneDay * 3;
                            thurday = today + oneDay * 4;
                            friday = today + oneDay * 5;
                            saturday = today + oneDay * 6;
                            sunday = today;
                        } else {
                            monday = today + oneDay * 1;
                            tuesday = today + oneDay * 2;
                            wedesday = today + oneDay * 3;
                            thurday = today + oneDay * 4;
                            friday = today + oneDay * 5;
                            saturday = today + oneDay * 6;
                            sunday = today + oneDay * 7;
                        }
                        break;
                }

                if (mon) {
                    clockDao.insert(new Clock(id, clockTime, monday, oneDay * 7, content, 1, true));
                    id++;
                    Logger.d("1:" + new Gson().toJson(clockDao.loadAll()));
                }
                if (tus) {
                    clockDao.insert(new Clock(id, clockTime, tuesday, oneDay * 7, content, 2, true));
                    id++;
                    Logger.d("2:" + new Gson().toJson(clockDao.loadAll()));
                }
                if (wed) {
                    clockDao.insert(new Clock(id, clockTime, wedesday, oneDay * 7, content, 3, true));
                    id++;
                    Logger.d("3:" + new Gson().toJson(clockDao.loadAll()));
                }
                if (thu) {
                    clockDao.insert(new Clock(id, clockTime, thurday, oneDay * 7, content, 4, true));
                    id++;
                    Logger.d("4:" + new Gson().toJson(clockDao.loadAll()));
                }
                if (fri) {
                    clockDao.insert(new Clock(id, clockTime, friday, oneDay * 7, content, 5, true));
                    id++;
                    Logger.d("5:" + new Gson().toJson(clockDao.loadAll()));
                }
                if (sat) {
                    clockDao.insert(new Clock(id, clockTime, saturday, oneDay * 7, content, 6, true));
                    id++;
                    Logger.d("6:" + new Gson().toJson(clockDao.loadAll()));
                }
                if (sun) {
                    clockDao.insert(new Clock(id, clockTime, sunday, oneDay * 7, content, 7, true));
                    Logger.d("7:" + new Gson().toJson(clockDao.loadAll()));
                }
                showClockTip();
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

    Dialog dialog;
    ArrayList<String> list;

    private void getTagList() {
        Map<String, String> map = new HashMap<>();
        getDataFromServer(HttpUrlConstant.TAB_GETLIST, map, TagResponse.class, new Response.Listener<TagResponse>() {
            @Override
            public void onResponse(TagResponse response) {
                List<TagResponse.Data> tagList = response.getResults();
                list = new ArrayList<>();
                for (TagResponse.Data i : tagList) {
                    list.add(i.getContent());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    public void onOptionPicker(final ArrayList<String> data) {
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
                mTvTag.setText(item);
            }
        });
        picker.show();
    }

    private void showEditTextDialog() {
        if (dialog == null) {
            dialog = new Dialog(mContext, R.style.LoadingDialog);
            dialog.setContentView(R.layout.dialog_edit_text);

            TextView tv_title = dialog.findViewById(R.id.tv_title);
            final EditText et_text = dialog.findViewById(R.id.et_text);
            TextView tv_commit = dialog.findViewById(R.id.tv_commit);
            TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);

            tv_title.setText("标签");
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            tv_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTvTag.setText(et_text.getText().toString());
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SetDayInWeekEvent event) {
        Logger.d("5:" + fri);
        String repeat = "";
        List<Boolean> list = event.getWhichDay();
        repeat = "星期";
        for (int i = 0; i < list.size(); i++) {
            switch (i) {
                case 0:
                    if (list.get(i)) {
                        repeat += "一、";
                        mon = true;
                    } else {
                        mon = false;
                    }
                    break;
                case 1:
                    if (list.get(i)) {
                        repeat += "二、";
                        tus = true;
                    } else {
                        tus = false;
                    }
                    break;
                case 2:
                    if (list.get(i)) {
                        repeat += "三、";
                        wed = true;
                    } else {
                        wed = false;
                    }
                    break;
                case 3:
                    if (list.get(i)) {
                        repeat += "四、";
                        thu = true;
                    } else {
                        thu = false;
                    }
                    break;
                case 4:
                    if (list.get(i)) {
                        repeat += "五、";
                        fri = true;
                    } else {
                        fri = false;
                    }
                    break;
                case 5:
                    if (list.get(i)) {
                        repeat += "六、";
                        sat = true;
                    } else {
                        sat = false;
                    }
                    break;
                case 6:
                    if (list.get(i)) {
                        repeat += "天、";
                        sun = true;
                    } else {
                        sun = false;
                    }
                    break;
            }
        }
        if (repeat.equals("星期")) {
            repeat = "只响一次";
        } else if (repeat.equals("星期一、二、三、四、五、六、天、")) {
            repeat = "每天";
        } else {
            repeat = repeat.substring(0, repeat.length() - 1);
        }

        mTvRetype.setText(repeat);
        Logger.d("5:" + fri);
    }
}
