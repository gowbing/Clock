package com.miracle.clock.model.event;

/**
 * Created by Arthas on 2017/11/7.
 */

public class ToMainEvent {

    private String audioId;
    private String ids;

    public ToMainEvent(String audioId, String ids) {
        this.audioId = audioId;
        this.ids = ids;
    }

    public String getAudioId() {
        return audioId;
    }

    public String getIds() {
        return ids;
    }
}
