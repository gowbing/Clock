package com.miracle.clock.custom;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;

import com.miracle.clock.constant.PreferenceConstants;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.event.PlayerAllProcessEvent;
import com.miracle.clock.model.event.PlayerCompleteEvent;
import com.miracle.clock.model.event.PlayerNowProcessEvent;
import com.miracle.clock.model.event.PlayerPauseEvent;
import com.miracle.clock.model.event.PlayerPlayEvent;
import com.miracle.clock.utils.normal.PreferenceUtils;
import com.miracle.clock.utils.player.MediaPlayerProxy;

import org.greenrobot.eventbus.EventBus;


public class Player implements OnBufferingUpdateListener, OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
    public MediaPlayer mediaPlayer;
    private SeekBar skbProgress;

    private Timer mTimer = new Timer();

    MediaPlayerProxy proxy;

    private boolean USE_PROXY = true;

    private String now = "00:00";
    private String all = "00:00";
    private boolean isMain = true;
    private String url = "";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Player(SeekBar skbProgress) {
        this.skbProgress = skbProgress;

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }

        mTimer.schedule(mTimerTask, 0, 1000);

        proxy = new MediaPlayerProxy();
        proxy.init();
        proxy.start();
    }

    /*******************************************************
     * 通过定时器和Handler来更新进度条
     ******************************************************/
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying() && skbProgress.isPressed() == false) {
                handleProgress.sendEmptyMessage(0);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            now = getStringTime(mediaPlayer.getCurrentPosition());
            PreferenceUtils.setPrefInt(BaseApplication.getInstance(), PreferenceConstants.KEY_PROGRESS_NOW, mediaPlayer.getCurrentPosition());
            EventBus.getDefault().post(new PlayerNowProcessEvent(now));
            if (duration > 0 && !skbProgress.isPressed()) {
                long pos = skbProgress.getMax() * position / duration;
                skbProgress.setProgress((int) pos);
            }
        }

        ;
    };


    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    public int allProgress() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void play() {
        mediaPlayer.start();
        EventBus.getDefault().post(new PlayerPlayEvent());
    }

    public void playUrl(String url, boolean isMain) {
        setUrl(url);
        this.isMain = isMain;
        if (USE_PROXY) {
            startProxy();
            url = proxy.getProxyURL(url);
        }

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            long p = System.currentTimeMillis();
            Log.e("P", String.valueOf(p));
            mediaPlayer.prepare();
            long s = System.currentTimeMillis();
            Log.e("S", String.valueOf(s) + " " + (p - s));
            mediaPlayer.start();
            long x = System.currentTimeMillis();
            Log.e("X", String.valueOf(x) + " " + (x - s));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playUrlNotProxy(String url, boolean isMain) {
        setUrl(url);
        this.isMain = isMain;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            long p = System.currentTimeMillis();
            Log.e("P", String.valueOf(p));
            mediaPlayer.prepare();
            long s = System.currentTimeMillis();
            Log.e("S", String.valueOf(s) + " " + (p - s));
            mediaPlayer.start();
            long x = System.currentTimeMillis();
            Log.e("X", String.valueOf(x) + " " + (x - s));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        mediaPlayer.pause();
        EventBus.getDefault().post(new PlayerPauseEvent());
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    /**
     * 通过onPrepared播放
     */
    public void onPrepared(MediaPlayer arg0) {
        Log.e("mediaPlayer", "onPrepared");
        all = getStringTime(arg0.getDuration());
        EventBus.getDefault().post(new PlayerAllProcessEvent(all, arg0.getDuration()));
        if (!isMain) {
            arg0.start();
            EventBus.getDefault().post(new PlayerPlayEvent());
        }
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        Log.e("mediaPlayer", "onCompletion");
        skbProgress.setProgress(100);
        now = all;
        EventBus.getDefault().post(new PlayerCompleteEvent());
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        skbProgress.setSecondaryProgress(bufferingProgress);
        int currentProgress = skbProgress.getMax() * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        Log.e(currentProgress + "% play", bufferingProgress + "% buffer");
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        EventBus.getDefault().post(new PlayerPlayEvent());
    }

    private void startProxy() {
        if (proxy == null) {
            proxy = new MediaPlayerProxy();
            proxy.init();
            proxy.start();
        }
    }

    private String getStringTime(int intTime) {
        int time = intTime / 1000;
        int min = time / 60;
        int sec = time - min * 60;
        StringBuffer stringTime = new StringBuffer();
        stringTime.append(min < 10 ? "0" + min : min);
        stringTime.append(":");
        stringTime.append(sec < 10 ? "0" + sec : sec);
        return stringTime.toString();
    }
}
