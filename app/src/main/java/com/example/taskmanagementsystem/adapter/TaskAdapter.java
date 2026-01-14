package com.example.taskmanagementsystem.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanagementsystem.R;
import com.example.taskmanagementsystem.model.TaskList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<TaskList> list;
    private OnTaskClickListener listener;
    private boolean isReadOnly; // Tambah ini

    public interface OnTaskClickListener {
        void onEdit(TaskList task);
        void onDelete(TaskList task);
    }

    // Constructor asal (untuk kegunaan Manager/Admin biasa)
    public TaskAdapter(List<TaskList> list, OnTaskClickListener listener) {
        this.list = list;
        this.listener = listener;
        this.isReadOnly = false;
    }

    // Constructor baru (untuk Report - Read Only)
    public TaskAdapter(List<TaskList> list, boolean isReadOnly) {
        this.list = list;
        this.isReadOnly = isReadOnly;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_manager, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskList task = list.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDesc.setText(task.getDescription());
        holder.tvCreatedBy.setText(task.getCreated_task_by());
        holder.tvAssigned.setText(task.getAssigned_to());
        holder.tvCreateDate.setText(formatDate(task.getCreate_date()));

        // Status Styling
        String status = task.getStatus();
        holder.tvStatus.setText(status);
        if (status != null) {
            if (status.equalsIgnoreCase("Completed")) {
                holder.tvStatus.setTextColor(Color.parseColor("#166534"));
            } else if (status.equalsIgnoreCase("In Progress")) {
                holder.tvStatus.setTextColor(Color.parseColor("#854d0e"));
            } else {
                holder.tvStatus.setTextColor(Color.parseColor("#1e40af"));
            }
        }

        // Deadline Styling
        if (task.getFinish_date() == null || task.getFinish_date().isEmpty() || task.getFinish_date().equalsIgnoreCase("null")) {
            holder.tvFinishDate.setText("Not set");
            holder.tvFinishDate.setTextColor(Color.parseColor("#DC2626"));
        } else {
            holder.tvFinishDate.setText(formatDate(task.getFinish_date()));
            holder.tvFinishDate.setTextColor(Color.parseColor("#1E293B"));
        }

        // --- LOGIK READ ONLY ---
        if (isReadOnly) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> { if (listener != null) listener.onEdit(task); });
            holder.btnDelete.setOnClickListener(v -> { if (listener != null) listener.onDelete(task); });
        }
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || dateStr.equalsIgnoreCase("null")) return "-";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvAssigned, tvStatus, tvCreateDate, tvCreatedBy, tvFinishDate;
        View btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDesc = itemView.findViewById(R.id.tvTaskDesc);
            tvAssigned = itemView.findViewById(R.id.tvAssignedTo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreateDate = itemView.findViewById(R.id.createDate);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
            tvFinishDate = itemView.findViewById(R.id.finishDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}