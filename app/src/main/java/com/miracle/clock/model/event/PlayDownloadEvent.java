package com.miracle.clock.model.event;

/**
 * Created by Arthas on 2017/9/15.
 */

public class PlayDownloadEvent {
    String name;

    public PlayDownloadEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
