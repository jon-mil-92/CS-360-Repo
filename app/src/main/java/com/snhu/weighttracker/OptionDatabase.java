package com.snhu.weighttracker;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Option.class}, version = 1)
public abstract class OptionDatabase extends RoomDatabase {
    public abstract OptionDao optionDao();
}