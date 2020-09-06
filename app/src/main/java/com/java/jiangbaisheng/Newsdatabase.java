package com.java.jiangbaisheng;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Newsdata.class},version = 1, exportSchema = false)
public abstract class Newsdatabase extends RoomDatabase {
    public abstract DataDao getNewsDao();

}
