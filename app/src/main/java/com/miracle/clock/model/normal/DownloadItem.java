package com.miracle.clock.model.normal;

/**
 * Created by Arthas on 2017/9/15.
 */

public class DownloadItem {
    String lastname;
    String name;

    public DownloadItem(String lastname, String name) {
        this.lastname = lastname;
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getName() {
        return name;
    }
}
