package com.example.todolist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.ui.TaskAdapter;
import com.example.todolist.ui.TaskViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "todo_prefs";
    private static final String KEY_SHOW_COMPLETED = "show_completed";

    private TaskViewModel viewModel;
    private TaskAdapter adapter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        EditText taskInput = findViewById(R.id.editTaskTitle);
        Button addButton = findViewById(R.id.buttonAddTask);
        SwitchMaterial showCompletedSwitch = findViewById(R.id.switchShowCompleted);
        RecyclerView recyclerView = findViewById(R.id.recyclerTasks);

        adapter = new TaskAdapter((task, isChecked) -> viewModel.toggleTask(task, isChecked));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        viewModel.getTasks().observe(this, adapter::submitList);

        boolean showCompleted = preferences.getBoolean(KEY_SHOW_COMPLETED, true);
        showCompletedSwitch.setChecked(showCompleted);
        viewModel.setShowCompleted(showCompleted);

        showCompletedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(KEY_SHOW_COMPLETED, isChecked).apply();
            viewModel.setShowCompleted(isChecked);
        });

        addButton.setOnClickListener(v -> {
            String title = taskInput.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, R.string.empty_task_warning, Toast.LENGTH_SHORT).show();
            } else {
                viewModel.addTask(title);
                taskInput.setText("");
            }
        });
    }
}
