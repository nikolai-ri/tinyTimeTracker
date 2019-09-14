package com.example.timertracker.Persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.timertracker.Model.Workday;

import java.util.List;

@Dao
public interface WorkdayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert (Workday workday);

    @Delete
    void delete (Workday workday);

    @Update
    void update (Workday workday);

    @Query("SELECT * from workday_table ORDER BY id ASC")
    LiveData<List<Workday>> getAllWorkdays();

    @Query("SELECT * from workday_table WHERE id = :id")
    LiveData<Workday> getWorkdayById(long id);

}
