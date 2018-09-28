package com.miracle.clock.model.normal;

import java.util.List;

/**
 * Created by Arthas on 2017/9/28.
 */

public class BannerResponse extends EmptyResponse {

    List<Data> results;

    public List<Data> getResults() {
        return results;
    }

    public static class Data {
        String imgUrl;
        long createtime;
        String linkUrl;
        int id;
        int type;

        public String getImgUrl() {
            return imgUrl;
        }

        public long getCreatetime() {
            return createtime;
        }

        public String getLinkUrl() {
            return linkUrl;
        }

        public int getId() {
            return id;
        }

        public int getType() {
            return type;
        }
    }
}
