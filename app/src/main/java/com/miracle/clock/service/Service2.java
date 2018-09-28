package com.miracle.clock.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.orhanobut.logger.Logger;

/**
 * Created by Arthas on 2017/8/24.
 */

public class Service2 extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("Service2.onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("Service2.onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d("Service2.onStartCommand");
        return Service.START_STICKY;
    }
}
