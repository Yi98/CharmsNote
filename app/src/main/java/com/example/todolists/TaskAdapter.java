package com.example.todolists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public TaskViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.task_title);
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);

        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task player = tasks.get(position);
        holder.title.setText(player.getTitle());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public Task getItem(int position) {
        return tasks.get(position);
    }


}
