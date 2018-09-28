package com.miracle.clock.model.normal;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Arthas on 2017/8/22.
 */

@Entity
public class TimeTip {
    @Id
    private long id;
    private long time;
    private String content;
    private String address;
    private long repeat1;
    private long repeat2;
    private long repeat3;

    @Generated(hash = 1521486346)
    public TimeTip(long id, long time, String content, String address, long repeat1, long repeat2, long repeat3) {
        this.id = id;
        this.time = time;
        this.content = content;
        this.address = address;
        this.repeat1 = repeat1;
        this.repeat2 = repeat2;
        this.repeat3 = repeat3;
    }


    public long getRepeat3() {
        return this.repeat3;
    }


    public void setRepeat3(long repeat3) {
        this.repeat3 = repeat3;
    }


    public long getRepeat2() {
        return this.repeat2;
    }


    public void setRepeat2(long repeat2) {
        this.repeat2 = repeat2;
    }


    public long getRepeat1() {
        return this.repeat1;
    }


    public void setRepeat1(long repeat1) {
        this.repeat1 = repeat1;
    }


    public String getContent() {
        return this.content;
    }


    public void setContent(String content) {
        this.content = content;
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


    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    @Generated(hash = 415319671)
    public TimeTip() {
    }

}
