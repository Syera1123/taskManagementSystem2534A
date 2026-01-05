package com.example.taskmanagementsystem;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagementsystem.adapter.TaskAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManageTaskActivity extends AppCompatActivity {

    private List<Task> allTasks;
    private TaskAdapter taskAdapter;

    private final String[] employees = {
            "Azim", "Ilham", "Hanif", "Syera",
            "Farah", "Hakim", "Danial", "Nadia"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_task);

        EditText etTaskTitle = findViewById(R.id.etTaskTitle);
        EditText etTaskDesc = findViewById(R.id.etTaskDesc);
        Spinner spEmployee = findViewById(R.id.spEmployee);
        Button btnAssignTask = findViewById(R.id.btnAssignTask);
        RecyclerView rvTasks = findViewById(R.id.rvTasks);

        allTasks = new ArrayList<>();

        // Spinner Employee
        ArrayAdapter<String> empAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                employees
        );
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEmployee.setAdapter(empAdapter);

        // RecyclerView
        taskAdapter = new TaskAdapter(this, allTasks, new TaskAdapter.TaskActionListener() {
            @Override
            public void onEdit(int position) {
                editTask(position);
            }

            @Override
            public void onDelete(int position) {
                new AlertDialog.Builder(ManageTaskActivity.this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Delete this task?")
                        .setPositiveButton("Delete", (d, w) -> {
                            allTasks.remove(position);
                            taskAdapter.notifyItemRemoved(position);
                            Snackbar.make(rvTasks, "Task deleted", Snackbar.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);

        // Add Task
        btnAssignTask.setOnClickListener(v -> {

            String title = etTaskTitle.getText().toString().trim();
            String desc = etTaskDesc.getText().toString().trim();
            String assigned = spEmployee.getSelectedItem().toString();

            if (title.isEmpty()) {
                etTaskTitle.setError("Required");
                return;
            }

            String date = new SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
            ).format(new Date());

            Task task = new Task(
                    title,
                    desc,
                    date,
                    "Pending",
                    assigned
            );

            allTasks.add(task);
            taskAdapter.notifyItemInserted(allTasks.size() - 1);

            etTaskTitle.setText("");
            etTaskDesc.setText("");

            Snackbar snackbar = Snackbar.make(
                    v,
                    "Task assigned to " + assigned,
                    Snackbar.LENGTH_LONG
            );

            snackbar.setAction("UNDO", undo -> {
                allTasks.remove(task);
                taskAdapter.notifyDataSetChanged();
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        });
    }

    private void editTask(int position) {

        Task task = allTasks.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        EditText etTitle = new EditText(this);
        etTitle.setText(task.getTitle());

        EditText etDescription = new EditText(this);
        etDescription.setText(task.getDescription());

        Spinner spEditEmployee = new Spinner(this);
        ArrayAdapter<String> empAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                employees
        );
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEditEmployee.setAdapter(empAdapter);

        for (int i = 0; i < employees.length; i++) {
            if (employees[i].equals(task.getAssignedTo())) {
                spEditEmployee.setSelection(i);
                break;
            }
        }

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 10);
        layout.addView(etTitle);
        layout.addView(spEditEmployee);

        builder.setView(layout);

        builder.setPositiveButton("Save", (d, w) -> {
            task.setTitle(etTitle.getText().toString());
            task.setAssignedTo(spEditEmployee.getSelectedItem().toString());
            taskAdapter.notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
