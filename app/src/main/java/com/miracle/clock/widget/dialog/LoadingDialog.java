package com.miracle.clock.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.czsirius.clock.R;


/**
 * 自定义Dialog
 */
public class LoadingDialog {
    private Context context;
    private Dialog ad;
    private TextView tvContent;

    public LoadingDialog(Context context) {
        this.context = context;
        ad = new Dialog(context, R.style.LoadingDialog);
        ad.setCancelable(false);
        ad.setCanceledOnTouchOutside(false);
        ad.setContentView(R.layout.dialog_loading);

        tvContent = (TextView) ad.findViewById(R.id.tv_content);
    }

    /**
     * 设置内容
     */
    public void setContext(String s) {
        tvContent.setText(s);
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        ad.dismiss();
    }

    public boolean isShowing() {
        return ad.isShowing();
    }

    /**
     * 展示对话框
     */
    public void show() {
        ad.show();
    }

    /**
     * 设置是否可以关闭
     *
     * @param cancelable true或false
     */
    public void setCancelable(boolean cancelable) {
        ad.setCancelable(cancelable);
    }

    /**
     * 设置是否可以点击dialog外面关闭dialog
     *
     * @param canceledOnTouchOutside true或false
     */
    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        ad.setCanceledOnTouchOutside(canceledOnTouchOutside);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置dialog消失事件
     *
     * @param onDismissListener dialog消失事件
     */
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        ad.setOnDismissListener(onDismissListener);
    }

    /**
     * 播放进度条动画
     */
    public void playAnimation(View view) {
        Animation an = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        an.setInterpolator(new LinearInterpolator());//不停顿
        an.setRepeatCount(-1);//重复次数 -1 表示无限循环
        an.setDuration(1000);//旋转停留时间
        view.setAnimation(an);
    }

    /**
     * 停止播放进度条动画
     */
    public void stopAnimation(View view) {
        view.clearAnimation();
    }

}