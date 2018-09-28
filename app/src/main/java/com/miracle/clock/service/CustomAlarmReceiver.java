package com.miracle.clock.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.miracle.clock.ui.activity.RingActivity;

/**
 * Created by Arthas on 2017/8/23.
 */

public class CustomAlarmReceiver extends BroadcastReceiver {

    // 电源管理器
    private PowerManager mPowerManager;
    // 唤醒锁
    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("onReceive", "----------------------------------------------------------------------");
        Log.e("params", intent.getStringExtra("params"));
        Log.e("onReceive", "----------------------------------------------------------------------");

        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock
                (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Tag");
        mWakeLock.acquire();

        Intent i = new Intent(context, RingActivity.class)
                .putExtra("params", intent.getStringExtra("params"));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }


}
