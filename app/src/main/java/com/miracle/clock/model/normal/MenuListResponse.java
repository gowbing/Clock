package com.miracle.clock.model.normal;

import java.util.List;

/**
 * Created by Arthas on 2017/8/16.
 */

public class MenuListResponse extends EmptyResponse {

    List<Data> results;

    public List<Data> getResults() {
        return results;
    }

    public static class Data {
        String name;

        int tagid;

        public int getTagid() {
            return tagid;
        }

        public Data(String name, int tagid) {
            this.name = name;
            this.tagid = tagid;
        }

        public String getName() {
            return name;
        }
    }
}
