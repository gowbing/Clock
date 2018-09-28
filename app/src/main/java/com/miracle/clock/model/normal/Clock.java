package com.miracle.clock.model.normal;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Arthas on 2017/8/22.
 */

@Entity
public class Clock {
    @Id
    private long id;
    private long time;
    private long daytime;
    private long repeattime;
    private String content;
    private int whichday;
    private boolean isstart;

    @Generated(hash = 505653469)
    public Clock(long id, long time, long daytime, long repeattime, String content, int whichday, boolean isstart) {
        this.id = id;
        this.time = time;
        this.daytime = daytime;
        this.repeattime = repeattime;
        this.content = content;
        this.whichday = whichday;
        this.isstart = isstart;
    }

    @Generated(hash = 1588708936)
    public Clock() {
    }

    public boolean getIsstart() {
        return this.isstart;
    }

    public void setIsstart(boolean isstart) {
        this.isstart = isstart;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getRepeattime() {
        return this.repeattime;
    }

    public void setRepeattime(long repeattime) {
        this.repeattime = repeattime;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDaytime() {
        return this.daytime;
    }

    public void setDaytime(long daytime) {
        this.daytime = daytime;
    }

    public int getWhichday() {
        return this.whichday;
    }

    public void setWhichday(int whichday) {
        this.whichday = whichday;
    }
}
