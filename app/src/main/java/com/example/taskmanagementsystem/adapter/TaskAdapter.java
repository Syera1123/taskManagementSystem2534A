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

    public interface OnTaskClickListener {
        void onEdit(TaskList task);
        void onDelete(TaskList task);
    }

    public TaskAdapter(List<TaskList> list, OnTaskClickListener listener) {
        this.list = list;
        this.listener = listener;
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

        // Format Create Date (yyyy-mm-dd -> dd/mm/yyyy)
        holder.tvCreateDate.setText(formatDate(task.getCreate_date()));

        // Format Status Badge & Color
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

        // Logic for Finish Deadline & Date Formatting
        if (task.getFinish_date() == null || task.getFinish_date().isEmpty() || task.getFinish_date().equalsIgnoreCase("null")) {
            holder.tvFinishDate.setText("Not set");
            holder.tvFinishDate.setTextColor(Color.parseColor("#DC2626"));
        } else {
            holder.tvFinishDate.setText(formatDate(task.getFinish_date()));
            holder.tvFinishDate.setTextColor(Color.parseColor("#1E293B"));
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(task);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(task);
        });
    }

    // Fungsi Helper untuk tukar format tarikh (DIBETULKAN)
    private String formatDate(String dateStr) {
        // Semakan: Menggunakan dateStr yang betul
        if (dateStr == null || dateStr.isEmpty() || dateStr.equalsIgnoreCase("null")) return "-";

        try {
            // Format asal dari database
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            // Format baru: Hari/Bulan/Tahun
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            // Jika format asal ada jam (cth: 2024-01-14 10:00:00), cuba parse format ini
            try {
                SimpleDateFormat inputFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormatWithTime.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e2) {
                return dateStr; // Jika gagal juga, pulangkan string asal (cth: dd/mm/yyyy sedia ada)
            }
        }
    }

    public void setList(List<TaskList> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

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