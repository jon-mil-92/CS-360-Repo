package com.snhu.weighttracker;

import android.content.Context;
import androidx.room.Room;

import java.util.List;

public class DayRepository {
    private static DayRepository dayRepo;
    private final DayDao dayDao;

    // Instantiate or get the repository singleton
    public static DayRepository getInstance(Context context) {
        if (dayRepo == null) {
            dayRepo = new DayRepository(context);
        }

        return dayRepo;
    }

    private DayRepository(Context context) {
        DayDatabase dayDatabase = Room.databaseBuilder(context, DayDatabase.class,
                "day.db").allowMainThreadQueries().build();

        dayDao = dayDatabase.dayDao();
    }

    public void addDay(Day day) {
        dayDao.addDay(day);
    }

    public Day getDay(int dayId) {
        return dayDao.getDay(dayId);
    }

    public int getDayId(String month, int buttonId) {
        return dayDao.getDayId(month, buttonId);
    }

    public int dayExists(int dayId) {
        return dayDao.dayExists(dayId);
    }

    public void updateDay(Day day) {
        dayDao.updateDay(day);
    }

    public void deleteDay(Day day) {
        dayDao.deleteDay(day);
    }
}