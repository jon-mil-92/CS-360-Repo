package com.snhu.weighttracker;

import android.content.Context;
import androidx.room.Room;

public class OptionRepository {
    private static OptionRepository optionRepo;
    private final OptionDao optionDao;

    // Instantiate or get the repository singleton
    public static OptionRepository getInstance(Context context) {
        if (optionRepo == null) {
            optionRepo = new OptionRepository(context);
        }

        return optionRepo;
    }

    private OptionRepository(Context context) {
        OptionDatabase optionDatabase = Room.databaseBuilder(context, OptionDatabase.class,
                "option.db").allowMainThreadQueries().build();

        optionDao = optionDatabase.optionDao();

        if (optionDao.getOptions().isEmpty()) {
            addOptions();
        }
    }

    public Option getOption(String option) {
        return optionDao.getOption(option);
    }

    public void updateOption(Option option) {
        optionDao.updateOption(option);
    }

    private void addOptions() {
        // Create the default options
        Option goalType = new Option("goal_type", "loose_weight");
        Option goalWeight = new Option("goal_weight", "150");

        // Insert the options into the database
        optionDao.addOption(goalType);
        optionDao.addOption(goalWeight);
    }
}