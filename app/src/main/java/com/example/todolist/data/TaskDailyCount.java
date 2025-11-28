package com.example.todolist.data;

import androidx.room.ColumnInfo;

public class TaskDailyCount {
    private final String date;
    private final int count;

    public TaskDailyCount(@ColumnInfo(name = "date") String date, @ColumnInfo(name = "count") int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public int getCount() {
        return count;
    }
}
