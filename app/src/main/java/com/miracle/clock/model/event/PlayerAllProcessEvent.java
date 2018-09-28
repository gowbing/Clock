package com.miracle.clock.model.event;

/**
 * Created by Arthas on 2017/9/1.
 */

public class PlayerAllProcessEvent {
    String all;
    int allInt;

    public int getAllInt() {
        return allInt;
    }

    public String getAll() {
        return all;
    }

    public PlayerAllProcessEvent(String all, int allInt) {
        this.all = all;
        this.allInt = allInt;
    }
}
