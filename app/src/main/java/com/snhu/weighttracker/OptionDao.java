package com.snhu.weighttracker;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface OptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOption(Option option);

    @Query("SELECT * FROM Option WHERE option = :option")
    Option getOption(String option);

    @Query("SELECT * FROM Option")
    List<Option> getOptions();

    @Update
    void updateOption(Option option);
}