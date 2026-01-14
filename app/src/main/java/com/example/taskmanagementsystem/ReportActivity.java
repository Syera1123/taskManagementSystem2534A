package com.example.taskmanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementsystem.adapter.TaskAdapter;
import com.example.taskmanagementsystem.model.TaskList;
import com.example.taskmanagementsystem.model.User;
import com.example.taskmanagementsystem.remote.ApiUtils;
import com.example.taskmanagementsystem.remote.TaskService;
import com.example.taskmanagementsystem.sharedpref.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {

    private RecyclerView rvReport;
    private ProgressBar progressBar;
    private TextView tvTotalCount, tvCompletedCount, tvInProgressCount, tvPendingCount;
    private TaskAdapter adapter;
    private List<TaskList> taskList = new ArrayList<>();
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize UI
        rvReport = findViewById(R.id.rvReport);
        progressBar = findViewById(R.id.progressBar);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvInProgressCount = findViewById(R.id.tvInProgressCount);
        tvPendingCount = findViewById(R.id.tvPendingCount);

        rvReport.setLayoutManager(new LinearLayoutManager(this));

        // Get User Session from SharedPrefManager
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        token = user.getToken();

        // Mode Read-Only (true)
        adapter = new TaskAdapter(taskList, true);
        rvReport.setAdapter(adapter);

        if (token != null && !token.isEmpty()) {
            fetchAllTasks();
        } else {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAllTasks() {
        progressBar.setVisibility(View.VISIBLE);
        TaskService taskService = ApiUtils.getTaskService();

        taskService.getAllTask(token).enqueue(new Callback<List<TaskList>>() {
            @Override
            public void onResponse(Call<List<TaskList>> call, Response<List<TaskList>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    taskList.clear();
                    taskList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    updateDashboardStats(taskList);
                } else {
                    Toast.makeText(ReportActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TaskList>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReportActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDashboardStats(List<TaskList> tasks) {
        int total = tasks.size();
        int pending = 0;
        int inProgress = 0;
        int completed = 0;

        for (TaskList task : tasks) {
            String status = task.getStatus();
            if (status != null) {
                if (status.equalsIgnoreCase("Pending")) pending++;
                else if (status.equalsIgnoreCase("In Progress")) inProgress++;
                else if (status.equalsIgnoreCase("Completed")) completed++;
            }
        }

        tvTotalCount.setText(String.valueOf(total));
        tvCompletedCount.setText(String.valueOf(completed));
        tvInProgressCount.setText(String.valueOf(inProgress));
        tvPendingCount.setText(String.valueOf(pending));
    }
}