package com.example.taskmanagementsystem;

import com.google.android.material.snackbar.Snackbar;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.graphics.Color;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import com.example.taskmanagementsystem.adapter.TaskAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
                new AlertDialog.Builder(ManageTaskActivity.this)
                        .setTitle("Confirm Deletion")
                        .setMessage("Are you sure you want to delete this task?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            allTasks.remove(position);
                            adapter.notifyDataSetChanged();
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
                // --- GET CURRENT DATE HERE ---
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                // Use 'currentDate' instead of hardcoded "2026-01-04"
                Task newTask = new Task(title, currentDate, "Pending", assigned);

                allTasks.add(newTask);
                adapter.notifyDataSetChanged();
                etTaskTitle.setText("");

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

        Spinner spEditEmployee = new Spinner(this);
        ArrayAdapter<String> editEmpAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, employees);
        editEmpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEditEmployee.setAdapter(editEmpAdapter);

        for (int i = 0; i < employees.length; i++) {
            if (employees[i].equals(task.getAssignedTo())) {
                spEditEmployee.setSelection(i);
                break;
            }
        }

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(etTitle);
        layout.addView(spEditEmployee);
        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            task.setTitle(etTitle.getText().toString().trim());
            task.setAssignedTo(spEditEmployee.getSelectedItem().toString());
            adapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}