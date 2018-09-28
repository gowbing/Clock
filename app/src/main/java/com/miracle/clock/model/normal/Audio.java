package com.miracle.clock.model.normal;

public class Audio {
        String author;
        String audioUrl;
        int tagid;
        String name;
        int id;
        long createtime;
        int iscollect;



    public Audio(String author, String audioUrl, int tagid, String name, int id, long createtime, int iscollect) {
        this.author = author;
        this.audioUrl = audioUrl;
        this.tagid = tagid;
        this.name = name;
        this.id = id;
        this.createtime = createtime;
        this.iscollect = iscollect;
    }

    public int getIscollect() {
        return iscollect;
    }

    public int getId() {
            return id;
        }

        public long getCreatetime() {
            return createtime;
        }

        public String getAuthor() {
            return author;
        }

        public String getAudioUrl() {
            return audioUrl;
        }

        public int getTagid() {
            return tagid;
        }

        public String getName() {
            return name;
        }
}
