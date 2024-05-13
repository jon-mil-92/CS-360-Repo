package com.snhu.weighttracker;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface DayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addDay(Day day);

    @Query("SELECT * FROM Day WHERE dayId = :dayId")
    Day getDay(int dayId);

    @Query("SELECT EXISTS(SELECT * FROM Day WHERE dayId = :dayId)")
    int dayExists(int dayId);

    @Query("SELECT dayId FROM Day WHERE month = :month AND buttonId = :buttonId")
    int getDayId(String month, int buttonId);

    @Update
    void updateDay(Day day);

    @Delete
    void deleteDay(Day day);
}