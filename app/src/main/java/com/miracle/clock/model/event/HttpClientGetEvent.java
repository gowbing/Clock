package com.miracle.clock.model.event;

/**
 * Created by hss on 2017/7/9.
 */

public class HttpClientGetEvent {
    String response;

    public HttpClientGetEvent(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
