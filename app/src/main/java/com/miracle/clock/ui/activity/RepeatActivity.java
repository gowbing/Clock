package com.miracle.clock.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.czsirius.clock.R;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.model.event.SetDayInWeekEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RepeatActivity extends BaseActivity {

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
    @Bind(R.id.monday)
    CheckBox mMonday;
    @Bind(R.id.tuesday)
    CheckBox mTuesday;
    @Bind(R.id.wednesday)
    CheckBox mWednesday;
    @Bind(R.id.thursday)
    CheckBox mThursday;
    @Bind(R.id.friday)
    CheckBox mFriday;
    @Bind(R.id.saturday)
    CheckBox mSaturday;
    @Bind(R.id.sunday)
    CheckBox mSunday;

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
        setContentView(R.layout.activity_repeat);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        mToolbarBack.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        mon = intent.getBooleanExtra("mon", false);
        tus = intent.getBooleanExtra("tus", false);
        wed = intent.getBooleanExtra("wed", false);
        thu = intent.getBooleanExtra("thu", false);
        fri = intent.getBooleanExtra("fri", false);
        sat = intent.getBooleanExtra("sat", false);
        sun = intent.getBooleanExtra("sun", false);

        mToolbarTitle.setText("重复");
        mToolbarBack.setText("返回");

        mMonday.setChecked(mon);
        mTuesday.setChecked(tus);
        mWednesday.setChecked(wed);
        mThursday.setChecked(thu);
        mFriday.setChecked(fri);
        mSaturday.setChecked(sat);
        mSunday.setChecked(sun);
    }

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Boolean> list = new ArrayList<>();
                if (mMonday.isChecked()) {
                    list.add(true);
                } else {
                    list.add(false);
                }
                if (mTuesday.isChecked()) {
                    list.add(true);
                } else {
                    list.add(false);
                }
                if (mWednesday.isChecked()) {
                    list.add(true);
                } else {
                    list.add(false);
                }
                if (mThursday.isChecked()) {
                    list.add(true);
                } else {
                    list.add(false);
                }
                if (mFriday.isChecked()) {
                    list.add(true);
                } else {
                    list.add(false);
                }
                if (mSaturday.isChecked()) {
                    list.add(true);
                } else {
                    list.add(false);
                }
                if (mSunday.isChecked()) {
                    list.add(true);
                } else {
                    list.add(false);
                }
                EventBus.getDefault().post(new SetDayInWeekEvent(list));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        List<Boolean> list = new ArrayList<>();
        if (mMonday.isChecked()) {
            list.add(true);
        } else {
            list.add(false);
        }
        if (mTuesday.isChecked()) {
            list.add(true);
        } else {
            list.add(false);
        }
        if (mWednesday.isChecked()) {
            list.add(true);
        } else {
            list.add(false);
        }
        if (mThursday.isChecked()) {
            list.add(true);
        } else {
            list.add(false);
        }
        if (mFriday.isChecked()) {
            list.add(true);
        } else {
            list.add(false);
        }
        if (mSaturday.isChecked()) {
            list.add(true);
        } else {
            list.add(false);
        }
        if (mSunday.isChecked()) {
            list.add(true);
        } else {
            list.add(false);
        }
        EventBus.getDefault().post(new SetDayInWeekEvent(list));
        super.onBackPressed();
    }
}
