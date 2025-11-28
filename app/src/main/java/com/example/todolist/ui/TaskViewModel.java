package com.example.todolist.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.todolist.data.Task;
import com.example.todolist.data.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final MutableLiveData<Boolean> showCompleted = new MutableLiveData<>(true);
    private final LiveData<List<Task>> tasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        tasks = Transformations.switchMap(showCompleted, repository::getTasks);
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
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
}
