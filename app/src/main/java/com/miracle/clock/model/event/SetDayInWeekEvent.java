package com.miracle.clock.model.event;

import java.util.List;

/**
 * Created by Arthas on 2017/8/31.
 */

public class SetDayInWeekEvent {
    List<Boolean> whichDay;

    public List<Boolean> getWhichDay() {
        return whichDay;
    }

    public SetDayInWeekEvent(List<Boolean> whichDay) {
        this.whichDay = whichDay;
    }
}
