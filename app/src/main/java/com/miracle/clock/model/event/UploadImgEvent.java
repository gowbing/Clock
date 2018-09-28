package com.miracle.clock.model.event;

/**
 * Created by hss on 2017/6/24.
 */

public  class UploadImgEvent {
    String isSuccess;

    public UploadImgEvent(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getIsSuccess() {
        return isSuccess;
    }
}
