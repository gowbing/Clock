package com.miracle.clock.model.event;

/**
 * Created by Administrator on 2017/8/24.
 */

public class BackEvent {
     String name ;

     String typename;

     long time;

    public BackEvent(String name, String typename, long time) {
        this.name = name;
        this.typename = typename;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getTypename() {
        return typename;
    }

    public long getTime() {
        return time;
    }
}
