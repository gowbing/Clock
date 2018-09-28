package com.miracle.clock.model.normal;

public class Remind {
    long createtime;
    long notifytime;
    long secondnotifytime;
    long thirdnotifytime;
    long starttime;
    String location;
    String content;
    String bodytext;
    int userid;
    int id;

    public String getBodytext() {
        return bodytext;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getCreatetime() {
        return createtime;
    }

    public long getNotifytime() {
        return notifytime;
    }

    public String getContent() {
        return content;
    }

    public int getUserid() {
        return userid;
    }

    public int getId() {
        return id;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public void setNotifytime(long notifytime) {
        this.notifytime = notifytime;
    }

    public long getSecondnotifytime() {
        return secondnotifytime;
    }

    public void setSecondnotifytime(long secondnotifytime) {
        this.secondnotifytime = secondnotifytime;
    }

    public long getThirdnotifytime() {
        return thirdnotifytime;
    }

    public void setThirdnotifytime(long thirdnotifytime) {
        this.thirdnotifytime = thirdnotifytime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public void setId(int id) {
        this.id = id;
    }
}
