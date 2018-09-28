package com.miracle.clock.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.czsirius.clock.R;


/**
 * 自定义Dialog
 */
public class CustomDialog {
    private Context context;
    private AlertDialog ad;
    private TextView title;
    private LinearLayout titleLayout;
    private LinearLayout contentLayout;
    private LinearLayout buttonLayout;

    public CustomDialog(Context context) {
        this.context = context;
        ad = new AlertDialog.Builder(context).create();
        ad.setCancelable(false);
        ad.show();

        Window window = ad.getWindow();
        window.setContentView(R.layout.custom_dialog);
        //设置dialog的宽度
        WindowManager.LayoutParams lp = window.getAttributes();
        Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); // 获取屏幕宽、高用
        lp.width = (int) (d.getWidth() * 0.85);
        window.setAttributes(lp);
        // 解决AlertDialog弹不出输入法的问题
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        titleLayout = (LinearLayout) window.findViewById(R.id.dialog_title);
        title = (TextView) window.findViewById(R.id.reminder_title);
        contentLayout = (LinearLayout) window.findViewById(R.id.lin_dialog_content);
        buttonLayout = (LinearLayout) window.findViewById(R.id.lin_dialog_btn);
    }

    public void setTitleName(int resId) {
        title.setText(resId);
    }

    /**
     * 设置title 内容
     *
     * @param titleName
     */
    public void setTitleName(String titleName) {
        title.setText(titleName);
    }

    public TextView getTitleView() {
        return title;
    }

    /**
     * 显示或隐藏头部
     *
     * @param isShow 是否显示头部
     */
    public void showTitle(boolean isShow) {
        if (isShow) {
            titleLayout.setVisibility(View.VISIBLE);
        } else {
            titleLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 设置内容
     *
     * @param layoutId 布局id
     */
    public void setContentView(int layoutId, LayoutParams params) {
        LayoutInflater inflaterDl = LayoutInflater.from(context);
        View view = inflaterDl.inflate(layoutId, null);
        contentLayout.addView(view, params);
        contentLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 设置内容
     *
     * @param view   视图
     * @param params 布局参数
     */
    public void setContentView(View view, LayoutParams params) {
        contentLayout.removeAllViews();
        contentLayout.addView(view, params);
        contentLayout.setVisibility(View.VISIBLE);
    }

    public View getContentView() {
        return contentLayout;
    }

    /**
     * 设置按钮
     *
     * @param text     按钮显示文字
     * @param resId    按钮背景图片布局
     * @param listener 按钮点击事件
     */
    public void setButton(String text, int resId, final View.OnClickListener listener) {
        TextView button = new TextView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
        button.setLayoutParams(params);
        button.setGravity(Gravity.CENTER);
        button.setBackgroundResource(resId);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        button.setOnClickListener(listener);
        params.setMargins(dip2px(context, 15), 0, dip2px(context, 15), 0);
        button.setLayoutParams(params);
        buttonLayout.addView(button);
        buttonLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        ad.dismiss();
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