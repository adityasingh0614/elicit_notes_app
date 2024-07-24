package com.example.todaybuddy;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface Notesdao {
    @Insert
    void insert(notes notes);

    @Update
    void update(notes notes);

    @Delete
    void delete(notes notes);


    @Query("SELECT * FROM  mynotes  ")
    LiveData<List<notes>> fetchAll();
}
