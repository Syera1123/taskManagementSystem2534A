package com.example.taskmanagementsystem;

import android.content.Intent;
import android.os.Bundle;import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
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

        // Apply Window Insets for Edge-to-Edge display
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize UI and API Service
        rvTaskList = findViewById(R.id.rvTaskList);
        rvTaskList.setLayoutManager(new LinearLayoutManager(this));

        taskService = ApiUtils.getTaskService();

        // Get logged-in user details
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        User user = spm.getUser();
        token = user.getToken();
        currentUsername = user.getUsername();

        // Initial Load
        loadTasks();
    }

    /**
     * Fetches all tasks from the server and filters them for the current staff member.
     */
    private void loadTasks() {
        taskService.getAllTask(token).enqueue(new Callback<List<TaskList>>() {
            @Override
            public void onResponse(Call<List<TaskList>> call, Response<List<TaskList>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TaskList> allTasks = response.body();
                    List<TaskList> myFilteredTasks = new ArrayList<>();

                    // Logic: Filter tasks where assigned_to matches the logged-in staff
                    for (TaskList task : allTasks) {
                        if (task.getAssigned_to() != null &&
                                task.getAssigned_to().equalsIgnoreCase(currentUsername)) {
                            myFilteredTasks.add(task);
                        }
                    }

                    // Set up the adapter with a click listener for the "UPDATE STATUS" button
                    adapter = new ListAdapter(EmployeeTaskActivity.this, myFilteredTasks, task -> {
                        Intent intent = new Intent(EmployeeTaskActivity.this, TaskDetailsActivity.class);
                        intent.putExtra("task_id", task.getId());
                        startActivity(intent);
                    });

                    rvTaskList.setAdapter(adapter);
                } else if (response.code() == 401) {
                    Toast.makeText(EmployeeTaskActivity.this, "Session expired. Please login again.", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Log.e("MyApp", "Server Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<TaskList>> call, Throwable t) {
                Toast.makeText(EmployeeTaskActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list every time the staff returns to this screen
        // (e.g., after updating a status in TaskDetailsActivity)
        loadTasks();
    }

    public void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Optional: Context Menu Logic if you still want to use Long-Press
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Since we are using an Interface for the button click,
        // this is mostly for secondary options like "View Only"
        if (item.getItemId() == R.id.menu_details) {
            // Note: You would need to store the last long-pressed item in the adapter
            Toast.makeText(this, "Use the Update Button for better experience", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
    }
}