package com.java.jiangbaisheng;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DataDao { // DAO: Data Access Object
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertdata(Newsdata... news);

    @Query("DELETE FROM Newsdata")
    void deleteAll();
    @Query("SELECT * FROM Newsdata ORDER BY ID")
    List<Newsdata> getall();

    @Query("SELECT * FROM Newsdata WHERE type LIKE :type")
    List<Newsdata> getwithtype(String type);

}