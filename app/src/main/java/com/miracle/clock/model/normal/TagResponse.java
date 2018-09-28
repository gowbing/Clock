package com.miracle.clock.model.normal;

import java.util.List;

/**
 * Created by Arthas on 2017/8/22.
 */

public class TagResponse extends EmptyResponse {

    List<Data> results;


    public List<Data> getResults() {
        return results;
    }

    public static class Data {
        long createtime;
        String content;
        int id;

        public long getCreatetime() {
            return createtime;
        }

        public String getContent() {
            return content;
        }

        public int getId() {
            return id;
        }
    }
}
