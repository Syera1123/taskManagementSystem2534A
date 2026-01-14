package com.example.taskmanagementsystem;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.taskmanagementsystem.model.TaskList;
import com.example.taskmanagementsystem.remote.ApiUtils;
import com.example.taskmanagementsystem.remote.TaskService;
import com.example.taskmanagementsystem.sharedpref.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailsActivity extends AppCompatActivity {
    private TaskService taskService;
    private int taskId;
    private String token;
    private Spinner spStatus;
    private String[] statusOptions = {"Pending", "In Progress", "Completed"};
    private String currentTitle, currentDesc, currentAssignedTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskId = getIntent().getIntExtra("task_id", -1);
        token = new SharedPrefManager(this).getUser().getToken();
        taskService = ApiUtils.getTaskService();

        spStatus = findViewById(R.id.spStatus);
        Button btnUpdate = findViewById(R.id.btnUpdateStatus);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(adapter);

        loadTaskDetails();

        btnUpdate.setOnClickListener(v -> {
            String selectedStatus = spStatus.getSelectedItem().toString();
            // Panggil fungsi update khusus status
            updateStatusOnServer(selectedStatus);
        });
    }

    private void loadTaskDetails() {
        taskService.getTask(token, taskId).enqueue(new Callback<TaskList>() {
            @Override
            public void onResponse(Call<TaskList> call, Response<TaskList> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TaskList task = response.body();

                    // Simpan data asal untuk kegunaan update/delete nanti
                    currentTitle = task.getTitle();
                    currentDesc = task.getDescription();
                    currentAssignedTo = task.getAssigned_to();

                    ((TextView) findViewById(R.id.tvTitle)).setText(currentTitle);
                    ((TextView) findViewById(R.id.tvDescription)).setText(currentDesc);
                    ((TextView) findViewById(R.id.tvStatus)).setText("Current Status: " + task.getStatus());

                    for (int i = 0; i < statusOptions.length; i++) {
                        if (statusOptions[i].equalsIgnoreCase(task.getStatus())) {
                            spStatus.setSelection(i);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<TaskList> call, Throwable t) {}
        });
    }

    private void updateStatusOnServer(String status) {SharedPrefManager spm = new SharedPrefManager(this);
        String currentUsername = spm.getUser().getUsername();

        // Panggil API dengan 4 parameter (api-key, id, status, assigned_to)
        taskService.updateTaskStatus(token, taskId, status, currentUsername).enqueue(new Callback<TaskList>() {
            @Override
            public void onResponse(Call<TaskList> call, Response<TaskList> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TaskDetailsActivity.this, "Status Updated!", Toast.LENGTH_SHORT).show();
                    finish(); // Tutup page balik ke list
                } else {
                    Toast.makeText(TaskDetailsActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TaskList> call, Throwable t) {
                Toast.makeText(TaskDetailsActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteOldTask(String token, int id) {
        taskService.deleteTask(token, id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Rekod lama berjaya dipadam
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) { }
        });
    }
}