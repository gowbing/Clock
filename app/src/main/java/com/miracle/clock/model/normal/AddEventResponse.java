package com.miracle.clock.model.normal;

/**
 * Created by Arthas on 2017/9/1.
 */

public class AddEventResponse extends EmptyResponse {

    Data results;

    public Data getResults() {
        return results;
    }

    public static class Data {
        int id;

        public int getId() {
            return id;
        }
    }

}
