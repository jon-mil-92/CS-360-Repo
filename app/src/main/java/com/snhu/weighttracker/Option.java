package com.snhu.weighttracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Option {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "option")
    private String option;

    @NonNull
    @ColumnInfo(name = "value")
    private String value;

    public Option(@NonNull String option, @NonNull String value) {
        this.option = option;
        this.value = value;
    }

    @NonNull
    public String getOption() {
        return option;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    @NonNull
    public void setValue(String value) {
        this.value = value;
    }
}