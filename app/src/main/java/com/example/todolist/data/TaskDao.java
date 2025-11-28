package com.example.todolist.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY createdAt DESC")
    LiveData<List<Task>> getActiveTasks();

    @Query("SELECT strftime('%Y-%m-%d', createdAt/1000, 'unixepoch') AS date, COUNT(*) AS count FROM tasks GROUP BY date ORDER BY date ASC")
    LiveData<List<TaskDailyCount>> getDailyCounts();
}
