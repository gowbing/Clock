package com.miracle.clock.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.czsirius.clock.R;
import com.miracle.clock.constant.PreferenceConstants;
import com.miracle.clock.greendao.gen.ClockDao;
import com.miracle.clock.greendao.gen.TimeTagDao;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.event.MineFinishEvent;
import com.miracle.clock.model.event.MusicNameEvent;
import com.miracle.clock.model.event.ToMainEvent;
import com.miracle.clock.model.normal.Clock;
import com.miracle.clock.model.normal.TimeTag;
import com.miracle.clock.model.normal.UserInfoResponse;
import com.miracle.clock.service.AudioPlayService;
import com.miracle.clock.utils.normal.AppUtils;
import com.miracle.clock.utils.normal.PreferenceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MineActivity extends BaseActivity implements View.OnClickListener {

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
    @Bind(R.id.iv_head)
    SimpleDraweeView mIvHead;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.ll_1)
    LinearLayout mLl1;
    @Bind(R.id.ll_2)
    LinearLayout mLl2;
    @Bind(R.id.ll_3)
    LinearLayout mLl3;
    @Bind(R.id.ll_4)
    LinearLayout mLl4;
    UserInfoResponse.Data data = null;
    @Bind(R.id.tv_new)
    TextView tvNew;
    @Bind(R.id.ll_loginout)
    LinearLayout llLoginout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void initView() {
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarTitle.setText("我的");
        mToolbarBack.setText("返回");
        data = BaseApplication.getInstance().getUserInfo();
//        showToast();
        if (data.getUserid() != 0) {
            mTvName.setText(data.getNickname());
            mIvHead.setImageURI(Uri.parse(data.getImgUrl()));
        } else {
            llLoginout.setVisibility(View.GONE);
        }

    }

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(this);
        mTvName.setOnClickListener(this);
        mLl1.setOnClickListener(this);
        mLl2.setOnClickListener(this);
        mLl3.setOnClickListener(this);
        mLl4.setOnClickListener(this);
        tvNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MineActivity.this, NewHelpActivity.class));
            }
        });
        llLoginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog_Exit();
            }
        });
    }

    NormalDialog normalDialog;

    private void showDialog_Exit() {
        if (normalDialog == null) {
            BaseAnimatorSet mBasIn = new BounceTopEnter();
            normalDialog = new NormalDialog(mContext);
            normalDialog.isTitleShow(false)
                    .bgColor(AppUtils.getColor(R.color.dialog_bg))
                    .cornerRadius(5)
                    .content("确定要退出登录吗？")
                    .contentGravity(Gravity.CENTER)
                    .contentTextColor(AppUtils.getColor(R.color.white))
                    .dividerColor(AppUtils.getColor(R.color.dialog_divider))
                    .btnTextSize(16f, 16f)
                    .btnTextColor(AppUtils.getColor(R.color.white), AppUtils.getColor(R.color.dialog_exit))
                    .btnPressColor(AppUtils.getColor(R.color.dialog_press))
                    .widthScale(0.85f)
                    .showAnim(mBasIn);
            normalDialog.setOnBtnClickL(new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    normalDialog.setOnDismissListener(null);
                    normalDialog.dismiss();
                }

            }, new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    normalDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            ClockDao clockDao = BaseApplication.getInstance().getDaoSession().getClockDao();
                            List<Clock> list = clockDao.loadAll();
                            for (Clock c : list) {
                                clockDao.delete(c);
                            }
                            TimeTagDao timeTagDao = BaseApplication.getInstance().getDaoSession().getTimeTagDao();
                            List<TimeTag> listtag = timeTagDao.loadAll();
                            for (TimeTag c : listtag) {
                                timeTagDao.delete(c);
                            }
                            PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_URL, "");
                            PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_LAST_PLAY_LIST, "");
                            PreferenceUtils.setPrefString(mContext, PreferenceConstants.KEY_USER_INFO, "");
                            AudioPlayService.getInstance().setPlayUrl("");
                            BaseApplication.getInstance().setUserInfo(new UserInfoResponse.Data());
                            EventBus.getDefault().post(new MusicNameEvent());
                            startActivity(new Intent(mContext, LoginActivity.class));
                            finish();
                        }
                    });
                    normalDialog.dismiss();
                }

            });
        }
        normalDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.tv_name:
                if (data.getUserid() == 0) {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
            case R.id.ll_1:
                if (data.getUserid() == 0) {
                    startActivity(new Intent(this, LoginActivity.class));
                    break;
                }
                startActivity(new Intent(this, CollectsActivity.class));
                break;
            case R.id.ll_2:
                if (data.getUserid() == 0) {
                    startActivity(new Intent(this, LoginActivity.class));
                    break;
                }
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.ll_3:
                break;
            case R.id.ll_4:
                if (data.getUserid() == 0) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    showShare("http://42.51.40.195/clockAPP/share/pages/share.html");
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
        oks.setText(txt);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl("");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(txt);
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(txt);
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("金水大叔");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(txt);
        // 启动分享GUI
        oks.show(this);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ToMainEvent event) {
        finish();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MineFinishEvent event) {
        finish();
    }

}
