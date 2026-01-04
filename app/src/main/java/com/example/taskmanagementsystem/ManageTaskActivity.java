package com.example.taskmanagementsystem;

import com.google.android.material.snackbar.Snackbar;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import com.example.taskmanagementsystem.adapter.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class ManageTaskActivity extends AppCompatActivity {

    private List<Task> allTasks;
    private RecyclerView rvTasks;
    private TaskAdapter adapter;
    private String[] employees = {"Azim","Ilham","Hanif","Syera","Farah","Hakim","Danial","Nadia"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_task);

        allTasks = new ArrayList<>();

        rvTasks = findViewById(R.id.rvTasks);
        Button btnAssignTask = findViewById(R.id.btnAssignTask);
        EditText etTaskTitle = findViewById(R.id.etTaskTitle);
        Spinner spEmployee = findViewById(R.id.spEmployee);

        ArrayAdapter<String> empAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, employees);
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEmployee.setAdapter(empAdapter);

        adapter = new TaskAdapter(this, allTasks, new TaskAdapter.TaskActionListener() {
            @Override
            public void onEdit(int position) {
                editTask(position);
            }

            @Override
            public void onDelete(int position) {
                // alert dialoog success delete with logo
                new AlertDialog.Builder(ManageTaskActivity.this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this task?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            allTasks.remove(position);
                            adapter.notifyDataSetChanged();

                            // Inform user with a simple Snackbar
                            Snackbar.make(rvTasks, "Task removed", Snackbar.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(adapter);

        btnAssignTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            String assigned = spEmployee.getSelectedItem().toString();
            if (!title.isEmpty()) {
                Task newTask = new Task(title, "2026-01-04", "Pending", assigned);
                allTasks.add(newTask);
                adapter.notifyDataSetChanged();
                etTaskTitle.setText(""); // clear input

                //snackbar success add task/ cannot be empty task title
                Snackbar snackbar = Snackbar.make(v, "Task assigned to " + assigned, Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", view -> {
                    allTasks.remove(newTask);
                    adapter.notifyDataSetChanged();
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            } else {
                etTaskTitle.setError("Please enter a title");
            }
        });
    }

    private void editTask(int position) {
        Task task = allTasks.get(position);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        EditText etTitle = new EditText(this);
        etTitle.setText(task.getTitle());

        Spinner spEmployee = new Spinner(this);
        ArrayAdapter<String> empAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, employees);
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEmployee.setAdapter(empAdapter);

        // set spinner to current employee
        for (int i = 0; i < employees.length; i++) {
            if (employees[i].equals(task.getAssignedTo())) {
                spEmployee.setSelection(i);
                break;
            }
        }

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        etTitle.setLayoutParams(params);
        spEmployee.setLayoutParams(params);

        layout.addView(etTitle);
        layout.addView(spEmployee);
        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            task.setTitle(etTitle.getText().toString().trim());
            task.setAssignedTo(spEmployee.getSelectedItem().toString());
            adapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
