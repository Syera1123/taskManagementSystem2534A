package com.example.taskmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementsystem.adapter.ListAdapter;
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

public class EmployeeTaskActivity extends AppCompatActivity {

    private TaskService taskService;
    private RecyclerView rvTaskList;
    private ListAdapter adapter;
    private String token;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee);

        // Apply Window Insets
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize RecyclerView
        rvTaskList = findViewById(R.id.rvTaskList);
        rvTaskList.setLayoutManager(new LinearLayoutManager(this));

        // Get user session info
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        token = user.getToken();
        currentUsername = user.getUsername();

        taskService = ApiUtils.getTaskService();

        // Initial task load
        loadTasks();
    }

    /**
     * Fetches tasks and filters them specifically for the logged-in staff member.
     */
    private void loadTasks() {
        taskService.getAllTask(token).enqueue(new Callback<List<TaskList>>() {
            @Override
            public void onResponse(Call<List<TaskList>> call, Response<List<TaskList>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TaskList> allTasks = response.body();
                    List<TaskList> myFilteredTasks = new ArrayList<>();

                    for (TaskList task : allTasks) {
                        if (task.getAssigned_to() != null &&
                                task.getAssigned_to().equalsIgnoreCase(currentUsername)) {
                            myFilteredTasks.add(task);
                        }
                    }

                    //Pass 3 arguments: Context, List, and Listener
                    adapter = new ListAdapter(EmployeeTaskActivity.this, myFilteredTasks, task -> {
                        //When "UPDATE STATUS" is clicked
                        Intent intent = new Intent(EmployeeTaskActivity.this, TaskDetailsActivity.class);
                        intent.putExtra("task_id", task.getId());
                        startActivity(intent);
                    });

                    rvTaskList.setAdapter(adapter);
                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Session expired. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                }
            }

            @Override
            public void onFailure(Call<List<TaskList>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list to show updated statuses immediately
        loadTasks();
    }

    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    //Long-press context menu logic
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Handle long-press menu actions if needed
        return super.onContextItemSelected(item);
    }
}