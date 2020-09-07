package com.java.jiangbaisheng;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Newsdata.class},version = 1, exportSchema = false)
public abstract class Newsdatabase extends RoomDatabase {
    public abstract DataDao getNewsDao();

    private static final String DB_NAME = "NewsDatabase.db";
    private static volatile Newsdatabase instance;

    //注意单例？
    public static synchronized Newsdatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static Newsdatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                Newsdatabase.class,
                DB_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }
}
