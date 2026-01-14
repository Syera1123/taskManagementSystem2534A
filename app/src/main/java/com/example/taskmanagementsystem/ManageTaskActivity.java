package com.example.taskmanagementsystem;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementsystem.adapter.TaskAdapter;
import com.example.taskmanagementsystem.remote.ApiUtils;
import com.example.taskmanagementsystem.remote.TaskService;
import com.example.taskmanagementsystem.sharedpref.SharedPrefManager;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageTaskActivity extends AppCompatActivity {

    private List<Task> allTasks;
    private TaskAdapter taskAdapter;
    private TaskService taskService;
    private String token;

    private final String[] employees = {
            "Azim", "Ilham", "Hanif", "Syera",
            "Farah", "Hakim", "Danial", "Nadia"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_task);

        // API initialization
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        token = spm.getUser().getToken();
        taskService = ApiUtils.getTaskService();

        EditText etTaskTitle = findViewById(R.id.etTaskTitle);
        EditText etTaskDesc = findViewById(R.id.etTaskDesc);
        Spinner spEmployee = findViewById(R.id.spEmployee);
        Button btnAssignTask = findViewById(R.id.btnAssignTask);
        RecyclerView rvTasks = findViewById(R.id.rvTasks);

        allTasks = new ArrayList<>();

        // Spinner Employee
        ArrayAdapter<String> empAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employees);
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEmployee.setAdapter(empAdapter);

        // RecyclerView Setup
        taskAdapter = new TaskAdapter(this, allTasks, new TaskAdapter.TaskActionListener() {
            @Override
            public void onEdit(int position) {
                editTask(position);
            }

            @Override
            public void onDelete(int position) {
                deleteTask(position);
            }
        });

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);

        // Step 1: Fetch data from database on load
        refreshData();

        // Step 2: Add Task to database
        btnAssignTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            String desc = etTaskDesc.getText().toString().trim();
            String assigned = spEmployee.getSelectedItem().toString();

            if (title.isEmpty()) {
                etTaskTitle.setError("Required");
                return;
            }

            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            taskService.addTask(token, title, desc, assigned, date, "Pending").enqueue(new Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful()) {
                        etTaskTitle.setText("");
                        etTaskDesc.setText("");
                        // Refresh from database to show the new record at the bottom
                        refreshData();
                        Toast.makeText(ManageTaskActivity.this, "Task assigned to " + assigned, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    Log.e("MyApp", "Add Error: " + t.getMessage());
                }
            });
        });
    }

    private void refreshData() {
        taskService.getAllTasks(token).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allTasks.clear();
                    allTasks.addAll(response.body());
                    taskAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {}
        });
    }

    private void editTask(int position) {
        Task task = allTasks.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        EditText etTitle = new EditText(this);
        etTitle.setText(task.getTitle());

        Spinner spEditEmployee = new Spinner(this);
        spEditEmployee.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employees));

        // Set spinner to current assigned employee
        for (int i = 0; i < employees.length; i++) {
            if (employees[i].equals(task.getAssignedTo())) {
                spEditEmployee.setSelection(i);
                break;
            }
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 10);
        layout.addView(etTitle);
        layout.addView(spEditEmployee);
        builder.setView(layout);

        builder.setPositiveButton("Save", (d, w) -> {
            String newTitle = etTitle.getText().toString();
            String newAssign = spEditEmployee.getSelectedItem().toString();

            // Step 3: Update Task in Database
            taskService.updateTask(token, task.getId(), newTitle, newAssign).enqueue(new Callback<Task>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful()) {
                        task.setTitle(newTitle);
                        task.setAssignedTo(newAssign);
                        taskAdapter.notifyItemChanged(position);
                    }
                }
                @Override
                public void onFailure(Call<Task> call, Throwable t) {}
            });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteTask(int position) {
        Task task = allTasks.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Delete this task?")
                .setPositiveButton("Delete", (d, w) -> {
                    // Step 4: Delete Task from Database
                    taskService.deleteTask(token, task.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                allTasks.remove(position);
                                taskAdapter.notifyItemRemoved(position);
                                Toast.makeText(ManageTaskActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}