package com.java.jiangbaisheng;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DataDao {
    @Insert
    void insertWords(Newsdata... news);
    @Update
    int updateWords(Newsdata... news);

    @Query("SELECT * FROM Newsdata ORDER BY ID DESC")
    List<Newsdata> getall();

}