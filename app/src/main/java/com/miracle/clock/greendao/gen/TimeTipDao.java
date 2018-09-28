package com.miracle.clock.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.miracle.clock.model.normal.TimeTip;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TIME_TIP".
*/
public class TimeTipDao extends AbstractDao<TimeTip, Long> {

    public static final String TABLENAME = "TIME_TIP";

    /**
     * Properties of entity TimeTip.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Time = new Property(1, long.class, "time", false, "TIME");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property Address = new Property(3, String.class, "address", false, "ADDRESS");
        public final static Property Repeat1 = new Property(4, long.class, "repeat1", false, "REPEAT1");
        public final static Property Repeat2 = new Property(5, long.class, "repeat2", false, "REPEAT2");
        public final static Property Repeat3 = new Property(6, long.class, "repeat3", false, "REPEAT3");
    };


    public TimeTipDao(DaoConfig config) {
        super(config);
    }
    
    public TimeTipDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TIME_TIP\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"TIME\" INTEGER NOT NULL ," + // 1: time
                "\"CONTENT\" TEXT," + // 2: content
                "\"ADDRESS\" TEXT," + // 3: address
                "\"REPEAT1\" INTEGER NOT NULL ," + // 4: repeat1
                "\"REPEAT2\" INTEGER NOT NULL ," + // 5: repeat2
                "\"REPEAT3\" INTEGER NOT NULL );"); // 6: repeat3
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TIME_TIP\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TimeTip entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getTime());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(4, address);
        }
        stmt.bindLong(5, entity.getRepeat1());
        stmt.bindLong(6, entity.getRepeat2());
        stmt.bindLong(7, entity.getRepeat3());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TimeTip entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getTime());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(4, address);
        }
        stmt.bindLong(5, entity.getRepeat1());
        stmt.bindLong(6, entity.getRepeat2());
        stmt.bindLong(7, entity.getRepeat3());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public TimeTip readEntity(Cursor cursor, int offset) {
        TimeTip entity = new TimeTip( //
            cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // time
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // address
            cursor.getLong(offset + 4), // repeat1
            cursor.getLong(offset + 5), // repeat2
            cursor.getLong(offset + 6) // repeat3
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TimeTip entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setTime(cursor.getLong(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAddress(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRepeat1(cursor.getLong(offset + 4));
        entity.setRepeat2(cursor.getLong(offset + 5));
        entity.setRepeat3(cursor.getLong(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TimeTip entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TimeTip entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}