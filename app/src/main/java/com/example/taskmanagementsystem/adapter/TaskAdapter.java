package com.example.taskmanagementsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementsystem.R;
import com.example.taskmanagementsystem.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface TaskActionListener {
        void onEdit(int position);
        void onDelete(int position);
    }

    private final Context context;
    private final List<Task> taskList;
    private final TaskActionListener listener;

    public TaskAdapter(Context context, List<Task> taskList, TaskActionListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_task_manager, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        Task task = taskList.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText("Details: " + task.getDescription());
        holder.tvAssigned.setText("Assigned to: " + task.getAssignedTo());
        holder.tvStatus.setText("Status: " + task.getStatus());
        holder.tvCreateDate.setText("Created: " + task.getCreateDate());

        // ✅ GUNA getAdapterPosition (lebih selamat)
        holder.btnEdit.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onEdit(pos);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onDelete(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription, tvAssigned, tvStatus, tvCreateDate;
        Button btnEdit, btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDescription = itemView.findViewById(R.id.tvTaskDesc);
            tvAssigned = itemView.findViewById(R.id.tvAssignedTo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreateDate = itemView.findViewById(R.id.createDate);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
