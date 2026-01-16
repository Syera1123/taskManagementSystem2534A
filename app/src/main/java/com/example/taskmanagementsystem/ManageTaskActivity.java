package com.example.taskmanagementsystem;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
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
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageTaskActivity extends AppCompatActivity {
    private TaskService taskService;
    private String token;
    private Spinner spEmployee;
    private EditText etTitle, etDesc, etCreatedBy, etCreateDate, etFinishDate;
    private RecyclerView rvTasks;
    private List<String> staffNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_task);

        // 1. Initialize UI Views
        etTitle = findViewById(R.id.etTaskTitle);
        etDesc = findViewById(R.id.etTaskDesc);
        etCreatedBy = findViewById(R.id.etCreatedBy);
        etCreateDate = findViewById(R.id.etCreateDate);
        etFinishDate = findViewById(R.id.etFinishDate);
        spEmployee = findViewById(R.id.spEmployee);
        rvTasks = findViewById(R.id.rvTasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        // 2. Setup DatePickers
        etCreateDate.setOnClickListener(v -> showDatePicker(etCreateDate));
        etFinishDate.setOnClickListener(v -> showDatePicker(etFinishDate));

        token = new SharedPrefManager(this).getUser().getToken();
        taskService = ApiUtils.getTaskService();

        fetchStaffList();
        loadAllTasks();

        findViewById(R.id.btnAssignTask).setOnClickListener(v -> handleAddTask());
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            editText.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void fetchStaffList() {
        taskService.getAllUsers(token).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    staffNames.clear();
                    for (User u : response.body()) {
                        if (u.getRole() != null && u.getRole().equalsIgnoreCase("staff")) {
                            staffNames.add(u.getUsername());
                        }
                    }
                    ArrayAdapter<String> adp = new ArrayAdapter<>(ManageTaskActivity.this, android.R.layout.simple_spinner_item, staffNames);
                    adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spEmployee.setAdapter(adp);
                }
            }
            @Override public void onFailure(Call<List<User>> call, Throwable t) {}
        });
    }

    private void handleAddTask() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String createdBy = etCreatedBy.getText().toString().trim();
        String finishDate = etFinishDate.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || createdBy.isEmpty() || finishDate.isEmpty() || spEmployee.getSelectedItem() == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String assignedTo = spEmployee.getSelectedItem().toString();

        // Adding task with finishDate
        taskService.addTask(token, title, desc, assignedTo, createdBy, "Pending", finishDate)
                .enqueue(new Callback<TaskList>() {
                    @Override
                    public void onResponse(Call<TaskList> call, Response<TaskList> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ManageTaskActivity.this, "Task assigned!", Toast.LENGTH_SHORT).show();
                            clearForm();
                            loadAllTasks();
                        }
                    }
                    @Override public void onFailure(Call<TaskList> call, Throwable t) {}
                });
    }

    private void clearForm() {
        etTitle.setText(""); etDesc.setText(""); etCreatedBy.setText("");
        etCreateDate.setText(""); etFinishDate.setText("");
    }

    private void loadAllTasks() {
        taskService.getAllTask(token).enqueue(new Callback<List<TaskList>>() {
            @Override
            public void onResponse(Call<List<TaskList>> call, Response<List<TaskList>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TaskAdapter adapter = new TaskAdapter(response.body(), new TaskAdapter.OnTaskClickListener() {
                        @Override public void onEdit(TaskList task) { showEditDialog(task); }
                        @Override public void onDelete(TaskList task) { showDeleteDialog(task); }
                    });
                    rvTasks.setAdapter(adapter);
                }
            }
            @Override public void onFailure(Call<List<TaskList>> call, Throwable t) {}
        });
    }

    private void showEditDialog(TaskList task) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_task, null);
        EditText edtT = view.findViewById(R.id.editT);
        EditText edtD = view.findViewById(R.id.editD);
        EditText edtCBy = view.findViewById(R.id.editCreatedBy);
        EditText edtFDate = view.findViewById(R.id.editFinishDate);
        Spinner spE = view.findViewById(R.id.editSp);
        Button btnSave = view.findViewById(R.id.btnSaveEdit);

        // 1. Populate form with existing task data
        edtT.setText(task.getTitle());
        edtD.setText(task.getDescription());
        edtCBy.setText(task.getCreated_task_by());

        if(edtFDate != null) {
            edtFDate.setText(task.getFinish_date());
            edtFDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePicker(edtFDate);
                }
            });
        }

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, staffNames);
        spE.setAdapter(adp);
        spE.setSelection(staffNames.indexOf(task.getAssigned_to()));

        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 2. Get values from form
                String title = edtT.getText().toString();
                String description = edtD.getText().toString();
                String createdBy = edtCBy.getText().toString();
                String assignedTo = spE.getSelectedItem().toString();
                String finishDate = "";

                if (edtFDate != null) {
                    finishDate = edtFDate.getText().toString();
                } else {
                    finishDate = task.getFinish_date();
                }

                // 3. Log old task info (EXACTLY like lecturer's example)
                Log.d("TaskUpdate", "Old Task info: " + task.toString());

                // 4. Update the task object with new data (EXACTLY like lecturer's example)
                task.setTitle(title);
                task.setDescription(description);
                task.setCreated_task_by(createdBy);
                task.setAssigned_to(assignedTo);
                task.setFinish_date(finishDate);
                // Keep the existing status (since we're not changing it in this dialog)
                // task.setStatus(task.getStatus());

                // 5. Log new task info (EXACTLY like lecturer's example)
                Log.d("TaskUpdate", "New Task info: " + task.toString());

                // 6. Get user info from SharedPreferences (EXACTLY like lecturer's example)
                SharedPrefManager spm = new SharedPrefManager(ManageTaskActivity.this);
                String token = spm.getUser().getToken();

                // 7. Send request to update the task record to the REST API
                Call<TaskList> call = taskService.updateTask(
                        token,
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getAssigned_to(),
                        task.getCreated_task_by(),
                        task.getFinish_date()
                );

                // 8. Execute (EXACTLY like lecturer's example)
                call.enqueue(new Callback<TaskList>() {
                    @Override
                    public void onResponse(Call<TaskList> call, Response<TaskList> response) {
                        // For debug purpose (EXACTLY like lecturer's example)
                        Log.d("TaskUpdate", "Update Request Response: " + response.raw().toString());

                        if (response.code() == 200 || response.code() == 201) {
                            // Server return success code for update request

                            // Get updated task object from response
                            TaskList updatedTask = response.body();

                            // Display message
                            Toast.makeText(ManageTaskActivity.this,
                                    "Task '" + updatedTask.getTitle() + "' updated successfully.",
                                    Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                            loadAllTasks();
                        }
                        else if (response.code() == 401) {
                            // Unauthorized error - invalid token, ask user to relogin
                            Toast.makeText(ManageTaskActivity.this,
                                    "Invalid session. Please login again",
                                    Toast.LENGTH_LONG).show();
                            // You might want to add clearSessionAndRedirect() here
                        }
                        else {
                            // Server return other error
                            Toast.makeText(ManageTaskActivity.this,
                                    "Error: " + response.message(),
                                    Toast.LENGTH_LONG).show();
                            Log.e("TaskUpdate", response.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<TaskList> call, Throwable t) {
                        Toast.makeText(ManageTaskActivity.this,
                                "Error [" + t.getMessage() + "]",
                                Toast.LENGTH_LONG).show();

                        // For debug purpose
                        Log.d("TaskUpdate", "Error: " + t.getMessage());
                    }
                });
            }
        });

        dialog.show();
    }

    private void showDeleteDialog(TaskList task) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete", (d, w) -> {
                    taskService.deleteTask(token, task.getId()).enqueue(new Callback<Void>() {
                        @Override public void onResponse(Call<Void> call, Response<Void> response) {
                            loadAllTasks();
                        }
                        @Override public void onFailure(Call<Void> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}