package com.example.todolist.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.todolist.data.Task;
import com.example.todolist.data.TaskRepository;
import com.example.todolist.data.TaskDailyCount;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final MutableLiveData<Boolean> showCompleted = new MutableLiveData<>(true);
    private final LiveData<List<Task>> tasks;
    private final LiveData<List<TaskDailyCount>> dailyCounts;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        tasks = Transformations.switchMap(showCompleted, repository::getTasks);
        dailyCounts = repository.getDailyCounts();
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public LiveData<List<TaskDailyCount>> getDailyCounts() {
        return dailyCounts;
    }

    public void setShowCompleted(boolean show) {
        showCompleted.setValue(show);
    }

    public void addTask(String title) {
        repository.addTask(title);
    }

    public void toggleTask(Task task, boolean completed) {
        task.setCompleted(completed);
        repository.updateTask(task);
    }

    public void updateTaskTitle(Task task, String newTitle) {
        task.setTitle(newTitle);
        repository.updateTask(task);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }
}
