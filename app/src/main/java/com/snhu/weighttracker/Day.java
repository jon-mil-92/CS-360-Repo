package com.snhu.weighttracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Day {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "dayId")
    private int dayId;

    @NonNull
    @ColumnInfo(name = "month")
    private String month;

    @NonNull
    @ColumnInfo(name = "buttonId")
    private int buttonId;

    @NonNull
    @ColumnInfo(name = "weight")
    private String weight;

    public Day(@NonNull String month, @NonNull int buttonId, @NonNull String weight) {
        this.month = month;
        this.buttonId = buttonId;
        this.weight = weight;
    }

    @NonNull
    public int getDayId() {
        return dayId;
    }

    @NonNull
    public String getMonth() {
        return month;
    }

    @NonNull
    public int getButtonId() {
        return buttonId;
    }

    @NonNull
    public String getWeight() {
        return weight;
    }

    @NonNull
    public void setWeight(String weight) {
        this.weight = weight;
    }

    @NonNull
    public void setDayId(int dayId) {
        this.dayId = dayId;
    }
}
