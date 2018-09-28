package com.miracle.clock.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.coolerfall.daemon.Daemon;
import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Arthas on 2017/8/24.
 */

public class DaemonService extends Service {

    private final long INTERVAL_MILLIS = 1000 * 60 * 60;

    Timer timer;

    @Override
    public void onCreate() {
        Logger.d("DaemonService.onCreate");
        super.onCreate();
        Daemon.run(DaemonService.this,
                DaemonService.class, Daemon.INTERVAL_ONE_MINUTE);
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                Logger.d(DateUtils.dateToString(new Date(), DateUtils.DATE_FORMAT_FIVE));
//                Toast.makeText(DaemonService.this, "wocalei", Toast.LENGTH_SHORT).show();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 5000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Logger.d("DaemonService.onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("DaemonService.onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

//    private void grayGuard() {
//        if (Build.VERSION.SDK_INT < 18) {
//            //API < 18 ，此方法能有效隐藏Notification上的图标
//            startForeground(GRAY_SERVICE_ID, new Notification());
//        } else {
//            Intent innerIntent = new Intent(this, DaemonInnerService.class);
//            startService(innerIntent);
//            startForeground(GRAY_SERVICE_ID, new Notification());
//        }
//
//        //发送唤醒广播来促使挂掉的UI进程重新启动起来
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent alarmIntent = new Intent();
//        alarmIntent.setAction(WakeReceiver.GRAY_WAKE_ACTION);
//        PendingIntent operation = PendingIntent.getBroadcast(this,
//                WAKE_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmManager.setWindow(AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis(), ALARM_INTERVAL, operation);
//        }else {
//            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis(), ALARM_INTERVAL, operation);
//        }
//    }
//
//    public static class DaemonInnerService extends Service {
//
//        @Override
//        public void onCreate() {
//            Log.i(LOG_TAG, "InnerService -> onCreate");
//            super.onCreate();
//        }
//
//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//            Log.i(LOG_TAG, "InnerService -> onStartCommand");
//            startForeground(GRAY_SERVICE_ID, new Notification());
//            //stopForeground(true);
//            stopSelf();
//            return super.onStartCommand(intent, flags, startId);
//        }
//
//        @Override
//        public IBinder onBind(Intent intent) {
//            throw new UnsupportedOperationException("Not yet implemented");
//        }
//
//        @Override
//        public void onDestroy() {
//            Log.i(LOG_TAG, "InnerService -> onDestroy");
//            super.onDestroy();
//        }
//    }
}
