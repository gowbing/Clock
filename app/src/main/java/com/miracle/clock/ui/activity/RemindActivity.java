package com.miracle.clock.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.czsirius.clock.R;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.utils.normal.AppUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RemindActivity extends BaseActivity {
    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.toolbar_back)
    TextView mToolbarBack;
    @Bind(R.id.toolbar_right)
    TextView mToolbarRight;
    @Bind(R.id.rg_redio)
    RadioGroup rgRedio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    public void initView() {
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("标签");
        final String[] strs = {"经营类", "企业类", "励志类", "文学类", "笑话类"};
        for (int i = 0; i < strs.length; i++) {
            RadioButton r = (RadioButton) getLayoutInflater().inflate(R.layout.redio_btn_type, rgRedio, false);
            r.setId(i);
            r.setText(strs[i]);
            rgRedio.addView(r);
            View view = new View(mContext);
            RadioGroup.LayoutParams p = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, 1);
            view.setBackgroundColor(AppUtils.getColor(R.color.grey));
//            p.setMargins(55,0,0,0);
            view.setLayoutParams(p);
            rgRedio.addView(view);
        }
//        rgRedio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//               showToast(strs[i],100);
//            }
//        });
    }

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}
