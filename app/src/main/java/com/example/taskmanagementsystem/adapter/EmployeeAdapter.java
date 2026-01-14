package com.example.taskmanagementsystem.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.taskmanagementsystem.R;
import com.example.taskmanagementsystem.model.User;
import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
    private List<User> employeeList;

    public EmployeeAdapter(List<User> employeeList) { this.employeeList = employeeList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = employeeList.get(position);
        holder.tvName.setText(user.getUsername());
        holder.tvRole.setText("Role: " + user.getRole());
        holder.tvBadge.setText(user.getRole().toUpperCase());

        String role = user.getRole().toLowerCase();
        if (role.equals("staff")) {
            holder.tvBadge.setBackgroundColor(Color.parseColor("#2196F3")); // Biru
        } else if (role.equals("manager")) {
            holder.tvBadge.setBackgroundColor(Color.parseColor("#F44336")); // Merah
        } else {
            holder.tvBadge.setBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() { return employeeList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole, tvBadge;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEmployeeName);
            tvRole = itemView.findViewById(R.id.tvEmployeeRole);
            tvBadge = itemView.findViewById(R.id.tvRoleBadge);
        }
    }



}