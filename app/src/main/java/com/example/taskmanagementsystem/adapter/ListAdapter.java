package com.example.taskmanagementsystem.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<TaskList> list;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onUpdateStatus(TaskList task);
    }

    public ListAdapter(Context context, List<TaskList> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the clean task item layout
        View view = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskList task = list.get(position);

        // 1. Set Title and Description
        holder.tvTitle.setText(task.getTitle());
        holder.tvDesc.setText(task.getDescription());

        // 2. Set Assigned Info
        holder.tvCreatedBy.setText(task.getCreated_task_by());

        // 3. Set Status Badge and Color logic
        String status = task.getStatus();
        holder.tvStatus.setText(status.toUpperCase());

        if (status.equalsIgnoreCase("Completed")) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#22C55E")); // Green
        } else if (status.equalsIgnoreCase("In Progress")) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#EAB308")); // Yellow
        } else {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#3B82F6")); // Blue (Pending)
        }

        // 4. Format Deadline (Day/Month/Year)
        holder.tvFinishDate.setText(formatDate(task.getFinish_date()));

        // 5. Update Status Button
        holder.btnUpdateStatus.setOnClickListener(v -> listener.onUpdateStatus(task));
    }

    /**
     * Helper function to convert DB date (yyyy-MM-dd) to UI date (dd/MM/yyyy)
     */
    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty() || dateStr.equalsIgnoreCase("null")) return "-";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            try {
                SimpleDateFormat inputFormatWithTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = inputFormatWithTime.parse(dateStr);
                return outputFormat.format(date);
            } catch (ParseException e2) {
                return dateStr; // Return as-is if parsing fails
            }
        }
    }

    @Override
    public int getItemCount() { return list == null ? 0 : list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvStatus, tvCreatedBy, tvFinishDate;
        Button btnUpdateStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreatedBy = itemView.findViewById(R.id.tvCreatedBy);
            tvFinishDate = itemView.findViewById(R.id.tvFinishDate);
            btnUpdateStatus = itemView.findViewById(R.id.btnItemUpdate);
        }
    }
}