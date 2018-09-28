package com.miracle.clock.model.normal;

import java.util.List;

/**
 * Created by Arthas on 2017/8/16.
 */

public class RemindListResponse extends EmptyResponse {

    List<Remind> results;

    public List<Remind> getResults() {
        return results;
    }


}
