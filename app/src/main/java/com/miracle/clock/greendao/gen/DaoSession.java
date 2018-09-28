package com.miracle.clock.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.miracle.clock.model.normal.Clock;
import com.miracle.clock.model.normal.DateRemind;
import com.miracle.clock.model.normal.TimeTag;
import com.miracle.clock.model.normal.TimeTip;

import com.miracle.clock.greendao.gen.ClockDao;
import com.miracle.clock.greendao.gen.DateRemindDao;
import com.miracle.clock.greendao.gen.TimeTagDao;
import com.miracle.clock.greendao.gen.TimeTipDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig clockDaoConfig;
    private final DaoConfig dateRemindDaoConfig;
    private final DaoConfig timeTagDaoConfig;
    private final DaoConfig timeTipDaoConfig;

    private final ClockDao clockDao;
    private final DateRemindDao dateRemindDao;
    private final TimeTagDao timeTagDao;
    private final TimeTipDao timeTipDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        clockDaoConfig = daoConfigMap.get(ClockDao.class).clone();
        clockDaoConfig.initIdentityScope(type);

        dateRemindDaoConfig = daoConfigMap.get(DateRemindDao.class).clone();
        dateRemindDaoConfig.initIdentityScope(type);

        timeTagDaoConfig = daoConfigMap.get(TimeTagDao.class).clone();
        timeTagDaoConfig.initIdentityScope(type);

        timeTipDaoConfig = daoConfigMap.get(TimeTipDao.class).clone();
        timeTipDaoConfig.initIdentityScope(type);

        clockDao = new ClockDao(clockDaoConfig, this);
        dateRemindDao = new DateRemindDao(dateRemindDaoConfig, this);
        timeTagDao = new TimeTagDao(timeTagDaoConfig, this);
        timeTipDao = new TimeTipDao(timeTipDaoConfig, this);

        registerDao(Clock.class, clockDao);
        registerDao(DateRemind.class, dateRemindDao);
        registerDao(TimeTag.class, timeTagDao);
        registerDao(TimeTip.class, timeTipDao);
    }
    
    public void clear() {
        clockDaoConfig.getIdentityScope().clear();
        dateRemindDaoConfig.getIdentityScope().clear();
        timeTagDaoConfig.getIdentityScope().clear();
        timeTipDaoConfig.getIdentityScope().clear();
    }

    public ClockDao getClockDao() {
        return clockDao;
    }

    public DateRemindDao getDateRemindDao() {
        return dateRemindDao;
    }

    public TimeTagDao getTimeTagDao() {
        return timeTagDao;
    }

    public TimeTipDao getTimeTipDao() {
        return timeTipDao;
    }

}
