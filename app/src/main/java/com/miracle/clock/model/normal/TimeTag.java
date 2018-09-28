package com.miracle.clock.model.normal;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Arthas on 2017/8/22.
 */

@Entity
public class TimeTag {
    @Id
    private long id;
    private long time;
    private String content;

    @Generated(hash = 425714554)
    public TimeTag(long id, long time, String content) {
        this.id = id;
        this.time = time;
        this.content = content;
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

    @Generated(hash = 974937371)
    public TimeTag() {
    }

}
