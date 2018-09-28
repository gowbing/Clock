package com.miracle.clock.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.czsirius.clock.R;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.utils.normal.DateUtils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;

public class DatePickerActivity extends BaseActivity {

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
    @Bind(R.id.date_picker)
    DatePicker mDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("日期选择");

        mDatePicker.setMode(DPMode.SINGLE);
        Date date = new Date();
        mDatePicker.setDate(DateUtils.getYear(date), DateUtils.getMonth(date));
        mDatePicker.setDeferredDisplay(false);
        mDatePicker.setOnDatePickedListener(new DatePicker.OnDatePickedListener() {
            @Override
            public void onDatePicked(String date) {
                showToast(date);
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
    }
}
