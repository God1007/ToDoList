package com.example.todolist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.ui.TaskAdapter;
import com.example.todolist.ui.TaskViewModel;
import com.example.todolist.data.Task;
import com.example.todolist.data.TaskDailyCount;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "todo_prefs";
    private static final String KEY_SHOW_COMPLETED = "show_completed";

    private TaskViewModel viewModel;
    private TaskAdapter adapter;
    private SharedPreferences preferences;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        EditText taskInput = findViewById(R.id.editTaskTitle);
        Button addButton = findViewById(R.id.buttonAddTask);
        SwitchMaterial showCompletedSwitch = findViewById(R.id.switchShowCompleted);
        RecyclerView recyclerView = findViewById(R.id.recyclerTasks);
        chart = findViewById(R.id.chartTasks);

        configureChart();

        adapter = new TaskAdapter(new TaskAdapter.TaskCheckedChangeListener() {
            @Override
            public void onCheckedChange(Task task, boolean isChecked) {
                viewModel.toggleTask(task, isChecked);
            }

            @Override
            public void onEdit(Task task) {
                showEditDialog(task);
            }

            @Override
            public void onDelete(Task task) {
                confirmDelete(task);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        viewModel.getTasks().observe(this, adapter::submitList);
        viewModel.getDailyCounts().observe(this, this::updateChart);

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

    private void showEditDialog(Task task) {
        final EditText input = new EditText(this);
        input.setText(task.getTitle());
        input.setSelection(input.getText().length());

        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_task_title)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String newTitle = input.getText().toString().trim();
                    if (TextUtils.isEmpty(newTitle)) {
                        Toast.makeText(this, R.string.empty_task_warning, Toast.LENGTH_SHORT).show();
                    } else {
                        viewModel.updateTaskTitle(task, newTitle);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void confirmDelete(Task task) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deleteTask(task))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void configureChart() {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setNoDataText(getString(R.string.no_data));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);

        chart.getAxisRight().setEnabled(false);
    }

    private void updateChart(List<TaskDailyCount> counts) {
        if (counts == null || counts.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());

        for (TaskDailyCount count : counts) {
            try {
                Date date = parser.parse(count.getDate());
                if (date != null) {
                    entries.add(new Entry(date.getTime(), count.getCount()));
                }
            } catch (ParseException ignored) {
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setCircleRadius(4f);
        dataSet.setLineWidth(2f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(getColor(android.R.color.holo_blue_dark));
        dataSet.setCircleColor(getColor(android.R.color.holo_blue_dark));
        dataSet.setValueTextSize(10f);

        chart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return displayFormat.format(new Date((long) value));
            }
        });
        chart.getXAxis().setGranularity(24 * 60 * 60 * 1000f);
        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }
}
