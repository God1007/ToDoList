package com.example.todolist.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.R;
import com.example.todolist.data.Task;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface TaskCheckedChangeListener {
        void onCheckedChange(Task task, boolean isChecked);
        void onEdit(Task task);
        void onDelete(Task task);
    }

    private final List<Task> tasks = new ArrayList<>();
    private final TaskCheckedChangeListener listener;

    public TaskAdapter(TaskCheckedChangeListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Task> newTasks) {
        tasks.clear();
        if (newTasks != null) {
            tasks.addAll(newTasks);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView dateText;
        private final CheckBox completedCheck;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.taskTitle);
            dateText = itemView.findViewById(R.id.taskDate);
            completedCheck = itemView.findViewById(R.id.checkboxCompleted);
            editButton = itemView.findViewById(R.id.buttonEdit);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }

        void bind(Task task) {
            titleText.setText(task.getTitle());
            Date date = new Date(task.getCreatedAt());
            dateText.setText(DateFormat.getDateTimeInstance().format(date));
            completedCheck.setOnCheckedChangeListener(null);
            completedCheck.setChecked(task.isCompleted());
            completedCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onCheckedChange(task, isChecked);
                }
            });

            editButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEdit(task);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(task);
                }
            });
        }
    }
}
