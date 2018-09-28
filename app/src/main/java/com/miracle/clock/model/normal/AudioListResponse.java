package com.miracle.clock.model.normal;

import java.util.List;

/**
 * Created by hss on 2017/6/24.
 */

public class AudioListResponse extends EmptyResponse {
    List<Audio> results;

    public void setResults(List<Audio> results) {
        this.results = results;
    }

    public List<Audio> getResults() {
        return results;
    }


}
