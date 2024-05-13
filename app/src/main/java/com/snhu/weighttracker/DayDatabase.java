package com.snhu.weighttracker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Day.class}, version = 1)
public abstract class DayDatabase extends RoomDatabase {
    public abstract DayDao dayDao();
}