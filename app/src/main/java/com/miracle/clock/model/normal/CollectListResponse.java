package com.miracle.clock.model.normal;

import java.util.List;

/**
 * Created by Arthas on 2017/8/16.
 */

public class CollectListResponse extends EmptyResponse {

    List<Data> results;

    public List<Data> getResults() {
        return results;
    }

    public static class Data {
        long createtime;
        int audioid;
        int id;
        Audio audio;
        int userid;

        public int getUserid() {
            return userid;
        }

        public long getCreatetime() {
            return createtime;
        }

        public int getId() {
            return id;
        }

        public int getAudioid() {
            return audioid;
        }

        public Audio getAudio() {
            return audio;
        }

        public Data(long createtime, int id, int userid, int audioid, Audio audio) {
            this.createtime = createtime;
            this.id = id;
            this.userid = userid;
            this.audioid = audioid;
            this.audio = audio;
        }


    }


}
