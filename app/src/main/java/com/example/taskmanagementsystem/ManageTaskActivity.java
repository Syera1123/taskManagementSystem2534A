package com.example.taskmanagementsystem;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
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
        // NEW: Added a way to edit finish date in dialog
        EditText edtFDate = view.findViewById(R.id.editFinishDate);
        Spinner spE = view.findViewById(R.id.editSp);
        Button btnSave = view.findViewById(R.id.btnSaveEdit);

        edtT.setText(task.getTitle());
        edtD.setText(task.getDescription());
        edtCBy.setText(task.getCreated_task_by());

        // If you don't have editFinishDate in dialog XML yet,
        // it will use the current one from the database
        if(edtFDate != null) {
            edtFDate.setText(task.getFinish_date());
            edtFDate.setOnClickListener(v -> showDatePicker(edtFDate));
        }

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, staffNames);
        spE.setAdapter(adp);
        spE.setSelection(staffNames.indexOf(task.getAssigned_to()));

        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        btnSave.setOnClickListener(v -> {
            String updatedFDate = (edtFDate != null) ? edtFDate.getText().toString() : task.getFinish_date();

            taskService.updateTask(token, task.getId(), edtT.getText().toString(), edtD.getText().toString(),
                            spE.getSelectedItem().toString(), edtCBy.getText().toString(), updatedFDate)
                    .enqueue(new Callback<TaskList>() {
                        @Override
                        public void onResponse(Call<TaskList> call, Response<TaskList> response) {
                            Toast.makeText(ManageTaskActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadAllTasks();
                        }
                        @Override public void onFailure(Call<TaskList> call, Throwable t) {}
                    });
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