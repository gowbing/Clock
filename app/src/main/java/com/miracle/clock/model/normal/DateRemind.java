package com.miracle.clock.model.normal;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Arthas on 2017/8/22.
 */

@Entity
public class DateRemind {
    @Id
    private long id;
    private long time;
    private long repeat_1;
    private long repeat_2;
    private long repeat_3;
    private String content;
    private boolean isstart;

    @Generated(hash = 850681639)
    public DateRemind(long id, long time, long repeat_1, long repeat_2, long repeat_3, String content, boolean isstart) {
        this.id = id;
        this.time = time;
        this.repeat_1 = repeat_1;
        this.repeat_2 = repeat_2;
        this.repeat_3 = repeat_3;
        this.content = content;
        this.isstart = isstart;
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

    public long getRepeat_3() {
        return this.repeat_3;
    }

    public void setRepeat_3(long repeat_3) {
        this.repeat_3 = repeat_3;
    }

    public long getRepeat_2() {
        return this.repeat_2;
    }

    public void setRepeat_2(long repeat_2) {
        this.repeat_2 = repeat_2;
    }

    public long getRepeat_1() {
        return this.repeat_1;
    }

    public void setRepeat_1(long repeat_1) {
        this.repeat_1 = repeat_1;
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

    @Generated(hash = 275483324)
    public DateRemind() {
    }

}
