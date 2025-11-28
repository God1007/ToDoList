package com.example.todolist.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao taskDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public TaskRepository(Application application) {
        ToDoDatabase db = ToDoDatabase.getDatabase(application);
        this.taskDao = db.taskDao();
    }

    public LiveData<List<Task>> getTasks(boolean showCompleted) {
        if (showCompleted) {
            return taskDao.getAllTasks();
        } else {
            return taskDao.getActiveTasks();
        }
    }

    public void addTask(String title) {
        executor.execute(() -> {
            Task task = new Task(title, false, System.currentTimeMillis());
            taskDao.insert(task);
        });
    }

    public void updateTask(Task task) {
        executor.execute(() -> taskDao.update(task));
    }
}
