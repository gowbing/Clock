package com.miracle.clock.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.SeekBar;

import com.miracle.clock.custom.Player;
import com.miracle.clock.custom.PreLoad;
import com.miracle.clock.model.event.AudioCompleteEvent;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;


/**
 * Created by Arthas on 2017/9/1.
 */

public class AudioPlayService extends Service {

    private static AudioPlayService mInstance;

    private Player player;

    private String url = "";

    public static AudioPlayService getInstance() {
        return mInstance;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void setSeekBar(SeekBar seekBar) {
        player = new Player(seekBar);
    }

    public void setPlayUrl(final String url) {
        setUrl(url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                PreLoad load = new PreLoad(url);
                load.download(300 * 1000);
            }
        }).start();
    }

    public void stop() {
        player.stop();
    }

    public void start() {
        player.play();
    }

    public void pause() {
        player.pause();
    }

    public void play(boolean isMain) {
        String url = getUrl();
        Logger.d(url);
        player.playUrl(url, isMain);
    }

    public void playNotProxy(boolean isMain) {
        String url = getUrl();
        Logger.d(url);
        player.playUrlNotProxy(url, isMain);
    }

    public void seekTo(int i) {
        player.seekTo(i);
    }

    public int allProgress() {
        return player.allProgress();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * URL编码
     *
     * @param url
     * @return
     */
    public static String urlEncode(String url) {
        try {
            url = java.net.URLEncoder.encode(url, "UTF-8");
            url = url.replaceAll("%2F", "/");
            url = url.replaceAll("%3A", ":");
            url = url.replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
