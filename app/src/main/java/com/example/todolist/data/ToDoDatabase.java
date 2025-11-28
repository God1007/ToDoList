package com.example.todolist.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class ToDoDatabase extends RoomDatabase {

    private static volatile ToDoDatabase INSTANCE;

    public abstract TaskDao taskDao();

    public static ToDoDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (ToDoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ToDoDatabase.class,
                            "todo_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
