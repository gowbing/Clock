package com.miracle.clock.model.event;

/**
 * Created by Arthas on 2017/9/1.
 */

public class PlayerNowProcessEvent {
    String now;

    public String getNow() {
        return now;
    }

    public PlayerNowProcessEvent(String now) {
        this.now = now;
    }
}
